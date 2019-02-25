/*
 * Copyright 2016-2019
 *
 * Interreg Central Baltic 2014-2020 funded project
 * Smart Logistics and Freight Villages Initiative, CB426
 *
 * Kouvola Innovation Oy, FINLAND
 * Region Örebro County, SWEDEN
 * Tallinn University of Technology, ESTONIA
 * Foundation Valga County Development Agency, ESTONIA
 * Transport and Telecommunication Institute, LATVIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.propentus.smartlog.management;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.crypto.CertificateUtil;
import com.propentus.common.util.file.FileUtil;
import com.propentus.iot.cmd.CommandLineRunner;
import com.propentus.iot.configs.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This class handles the lifecycle of client certificates for the organizations.
 *
 * - Generating new certificates
 * - Revoking certificates
 */
public class ClientCertificate {

    private static final Logger logger = LoggerFactory.getLogger(ClientCertificate.class);
    private static final String KEY_STRENGTH = "4096";

    private static String opensslPath = "openssl";
    private static String adminPath = "";
    private static String caKeyPath = "";
    private static String caCertPath = "";
    private static String crlPath = ""; //  Certificate revocation list
    private static String caPassword = "";
    private static String opensslConfigPath = "";

    private String domainName = "";

    private ClientCertificate() {}

    static {
        setOpensslPathFromSystemProperties();
        setCaKeyPathFromSystemProperties();
        setCaCertPathFromSystemProperties();
        setCrlPathFromSystemProperties();
        setCaPasswordFromSystemProperties();
        setOpensslConfigPathFromSystemProperties();

        //  Get base path from ConfigReader
        try {
            ConfigReader reader = new ConfigReader();
            adminPath = reader.getOrganisationConfiguration().fabricEnvPath;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Public constructor that gets organizations domainName as a parameter. DomainName is used to generate paths for
     * organizations key, certificate signing request and client certificate.
     *
     * @param domainName
     */
    public ClientCertificate(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Runs the whole process for generating certificates using openssl.
     *  - Generates new key for the organization.
     *  - Generates certificate signing request for the CA.
     *  - CA generates client certificate using CSR.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void generate() throws IOException, InterruptedException {
        generateKey();
        generateCsr();
        generateClientCertificate();
    }

    /**
     * Reads certificate from the filesystem and gets it's SHA1-fingerprint.
     * @return
     */
    public String getSha1() throws IOException, CertificateException, NoSuchAlgorithmException {
        String certificatePath = getClientCertificateOutputPath();
        byte[] certificateAsBytes = FileUtil.readFileBytes(certificatePath);
        ByteArrayInputStream is = new ByteArrayInputStream(certificateAsBytes);
        X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);

        if (certificate != null) {
            CertificateUtil certificateUtil = new CertificateUtil();
            return certificateUtil.getFingerprint(certificate);
        }

        throw new RuntimeException("Couldn't parse client certificate from disk to X509Certificate. Path: "
                + certificatePath);
    }

    /**
     * Revokes this organizations client certificate.
     *
     * Adds certificate to CRL (Certificate revocation list) and removes generated certificate.
     * HAProxy reads the same CRL and uses that to recognize if this client certificate needs to be blocked.
     */
    public void revoke() throws IOException, InterruptedException {
        logger.info("Revoking certificate for organization: " + domainName);

        String clientCertificatePath = getClientCertificateOutputPath();
        String crlCommand = opensslPath + " ca"
                + " -config " + opensslConfigPath
                + " -passin " + caPassword
                + " -cert " + caCertPath
                + " -keyfile " + caKeyPath
                + " -gencrl -out " + crlPath;

        File crlFile = new File(crlPath);

        //  If CRL file doesn't exist, create it using openssl.
        //  Should be only one time thing.
        if (!crlFile.exists()) {
            logger.info("Certificate revocation list created");
            CommandLineRunner.executeCommand(null, "{0}", crlCommand);
        }

        //  Assemble revokeCommand and do revoke
        String revokeCommand = opensslPath + " ca"
                + " -config " + opensslConfigPath
                + " -passin " + caPassword
                + " -cert " + caCertPath
                + " -keyfile " + caKeyPath
                + " -revoke " + clientCertificatePath;

        CommandLineRunner.executeCommand(null, "{0}", revokeCommand);

        //  Update CRL so HAProxy knows to block this certificate
        CommandLineRunner.executeCommand(null, "{0}", crlCommand);

        //  Remove certificates from organizations directory
        FileUtil.deleteFile(clientCertificatePath);
        FileUtil.deleteFile(getKeyOutputPath());
        FileUtil.deleteFile(getCsrOutputPath());
    }

    /**
     * Generates new key for the organization using openssl.
     *
     * Outputs key to organizations /crypto-config/client-certificate/ -folder.
     */
    private void generateKey() throws IOException, InterruptedException {
        logger.info("Generating new key for organization: " + domainName);

        String command = opensslPath + " genrsa"
                + " -out " + getKeyOutputPath()
                + " " + KEY_STRENGTH;

        CommandLineRunner.executeCommand(null, "{0}", command);
    }

    /**
     * Generate certificate signing request for the CA using openssl.
     *
     * Outputs CSR to organizations /crypto-config/client-certificate/ -folder.
     */
    private void generateCsr() throws IOException, InterruptedException {
        logger.info("Generating certificate signing request for organization: " + domainName);

        String command = opensslPath + " req -new"
                + " -key " + getKeyOutputPath()
                + " -out " + getCsrOutputPath()
                + " -subj " + getSubjectForCertificate();

        CommandLineRunner.executeCommand(null, "{0}", command);
    }


    /**
     * Uses CA's key and certificate to sign the previously generated certificate signing request.
     *
     * Outputs client certificate to organizations /crypto-config/client-certificate/ -folder.
     */
    private void generateClientCertificate() throws IOException, InterruptedException {
        logger.info("Generating new client certificate for organization: " + domainName);

        String command = opensslPath + " x509"
                + " -req -days 1825"
                + " -CAcreateserial -CAserial projectsmartlog_com.seq"
                + " -CAkey " + caKeyPath
                + " -CA " + caCertPath
                + " -passin " + caPassword
                + " -out " + getClientCertificateOutputPath()
                + " -in " + getCsrOutputPath();

        CommandLineRunner.executeCommand(null, "{0}", command);
    }

    /**
     * Returns path to selected organizations client.key.
     * @return
     */
    private String getKeyOutputPath() {
        String outputPath = adminPath + "generated/{domain}/crypto-config/client.key";
        outputPath = outputPath.replace("{domain}", domainName);
        return outputPath;
    }

    /**
     * Returns path to selected organizations client.csr.
     * @return
     */
    private String getCsrOutputPath() {
        String outputPath = adminPath + "generated/{domain}/crypto-config/client.csr";
        outputPath = outputPath.replace("{domain}", domainName);
        return outputPath;
    }

    /**
     * Returns path to selected organizations client.crt.
     * @return
     */
    private String getClientCertificateOutputPath() {
        String outputPath = adminPath + "generated/{domain}/crypto-config/client.crt";
        outputPath = outputPath.replace("{domain}", domainName);
        return outputPath;
    }

    /**
     * Returns subject for the certificate.
     *
     * Uses domainName as a CN.
     *
     * @return
     */
    private String getSubjectForCertificate() {
        return "/CN=" + domainName;
    }

    /**
     * Path to openssl can be overridden using system properties.
     */
    private static void setOpensslPathFromSystemProperties() {
        String newPath = System.getProperty("smartlog.openssl");

        if (newPath != null) {
            opensslPath = newPath;
        }
    }

    /**
     * Path to CA key can be overridden using system properties.
     */
    private static void setCaKeyPathFromSystemProperties() {
        String newPath = System.getProperty("smartlog.cakey");

        if (newPath != null) {
            caKeyPath = newPath;
        }
    }

    /**
     * Path to CA certificate can be overridden using system properties.
     */
    private static void setCaCertPathFromSystemProperties() {
        String newPath = System.getProperty("smartlog.cacert");

        if (newPath != null) {
            caCertPath = newPath;
        }
    }

    /**
     * Path to CRL can be overriden using system properties.
     */
    private static void setCrlPathFromSystemProperties() {
        String newPath = System.getProperty("smartlog.crl");

        if (newPath != null) {
            crlPath = newPath;
        }
    }

    /**
     * CA-key password can be overriden using system properties.
     *
     * CA-key can be type pass:password or file:/path/to/file.
     */
    private static void setCaPasswordFromSystemProperties() {
        String newPassword = System.getProperty("smartlog.capwd");

        if (newPassword != null) {
            caPassword = newPassword;
        }
    }

    /**
     * Set openssl config path so we know what config to use when revoking certificates.
     * Configuration contains settings for the certificate extensions, CA-key path, CA-certificate path etc..
     */
    private static void setOpensslConfigPathFromSystemProperties() {
        String newPath = System.getProperty("smartlog.openssl.config");

        if (newPath != null) {
            opensslConfigPath = newPath;
        }
    }
}
