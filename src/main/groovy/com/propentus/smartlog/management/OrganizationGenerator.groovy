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

package com.propentus.smartlog.management

import com.propentus.common.util.file.FileUtil
import com.propentus.iot.BlockchainConnector
import com.propentus.iot.ConfigEditor
import com.propentus.iot.certificates.CertificateGenerator
import com.propentus.iot.configs.ConfigGenerator
import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.iot.configs.OrganisationConfiguration.Eventhub
import com.propentus.iot.configs.OrganisationConfiguration.Orderer
import com.propentus.iot.configs.OrganisationConfiguration.Organisation
import com.propentus.iot.configs.OrganisationConfiguration.OrganisationConfigurationBuilder
import com.propentus.iot.configs.OrganisationConfiguration.Peer
import com.propentus.iot.configs.OrganisationConfiguration.PeerType
import com.propentus.smartlog.common.configs.AdminClientConfig
import com.propentus.smartlog.configtxlator.ConfigTxLatorService
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.commons.io.FileUtils
import org.hyperledger.fabric.sdk.UpdateChannelConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Wrapper class for organization management stuff.
 *
 * Contains methods for certificate generation, adding organization to channel,
 * generating configs for organization and generating configs for client.
 *
 */
class OrganizationGenerator {

    BlockchainConnector connector
    GrailsParameterMap orgParams

    private static final Logger logger = LoggerFactory.getLogger(OrganizationGenerator.class)
    private static final String ORDERER_SERVER_CERT_NAME = "server.crt"


    public OrganizationGenerator() {}

    /**
     *
     * @param params
     * @param connector
     */
    public OrganizationGenerator(GrailsParameterMap params, BlockchainConnector connector) {
        logger.info("Initializing OrganizationGenerator with params: " + params)

        this.orgParams = params
        this.connector = connector
    }

    /**
     * Generates needed configuration and certificates and joins organization to channel
     */
    public void generateCertsAndConfigsAndAddToChannel() {

        logger.info("generateCertsAndConfigsAndAddToChannel for " + orgParams.orgMsp)

        // Load current config from channel and get new organization configuration
        String config = FabricConfigLoader.getConfigFile()
        String newOrgJson = generateConfigsAndCerts()

        // Create new config from config and newOrgJson
        ConfigEditor ce = new ConfigEditor(config, connector)
        String newConfig = ce.addOrganisation(orgParams.orgMsp, newOrgJson)

        copyServerCertToOrganizationFolder()

        // Get new config-block from configtxlator
        byte[] updateChannelConfigurationBytes = ConfigTxLatorService.wrapNewAndOldConfig(config, newConfig)
        UpdateChannelConfiguration ucc = new UpdateChannelConfiguration(updateChannelConfigurationBytes)

        // Sign config-block with admin-user
        byte[] signerOrg1 = connector.settingInitializer.getHFClient().getUpdateChannelConfigurationSignature(ucc, connector.getUser())
        byte[][] signers = new byte[1][]
        signers[0] = signerOrg1

        generateClientJson()

        //  Send channel update
        connector.getChannel().updateChannelConfiguration(ucc, signers)
    }

    /**
     * Generate new crypto-config for organization, generate new certificates for organization and
     * get new organization JSON from configtxgen
     * @return
     */
    private String generateConfigsAndCerts() {

        logger.info("Generating configs and certificates")

        ConfigGenerator cg = new ConfigGenerator(orgParams.orgName, orgParams.orgDomain, orgParams.orgMsp, orgParams.peerDomain, connector)
        cg.generateCryptoConfig()

        //	Run cryptogen
        CertificateGenerator certGen = new CertificateGenerator(orgParams.orgDomain, orgParams.orgMsp, connector)
        certGen.generateCertificatesForOrganization()
        certGen.generateMessagingCertificates(connector)
        String newOrgJson = cg.generateOrgJson()

        return newOrgJson
    }

    /**
     * Generates client-JSON from given attributes and saves it to disk
     */
    private void generateClientJson() {

        logger.info("Generating client configuration")

        List<Peer> peers = new ArrayList<Peer>();
        peers.add(new Peer(orgParams.peerUrl, orgParams.peerDomain))
        OrganisationConfiguration conf =
                        new OrganisationConfigurationBuilder()
                                .setPeers(peers)
                                .setOrderer(new Orderer(orgParams.ordererUrl, orgParams.ordererDomain))
                                .setEventhub(new Eventhub(orgParams.eventhubUrl, orgParams.eventhubDomain))
                                .setOrganisation(new Organisation(orgParams.orgName, orgParams.orgDomain, orgParams.orgMsp))
                                .setFabricEnvPath(orgParams.envpath)
                                .setCaUrl(orgParams.caUrl)
                                .setPeerType(PeerType.valueOf(orgParams.peerType))
                                .setChannelName(connector.getConfig().channel)
                                .setCouchDbUrl(orgParams.couchDbUrl)
                                .setPrivateKey(orgParams.envpath + "crypto-config/private.key")
                                .setPublicKey(orgParams.envpath + "crypto-config/public.key")

                                //  TODO: generate random username and password, so user doesn't need to handle these
                                .setCouchDbUsername("ADD_USERNAME_HERE")
                                .setCouchDbPassword("ADD_PASSWORD_HERE")
                                .build()

        String outputPath = new AdminClientConfig(connector).generatedOrgPath

        outputPath = outputPath.replace("{domain}", orgParams.orgDomain.toString())

        FileUtil.writeFile(outputPath, AdminClientConfig.CLIENT_JSON_FILENAME, conf.toJson(), true)
    }

    /**
     * Copies server certificate to new organizations crypto-config -folder.
     * Certificate is used to connect to the orderer, if certificate is not in place, connection can't be
     * established to network and client won't start.
     */
    private void copyServerCertToOrganizationFolder() {

        logger.info("Copying orderer's server certificate to organizations crypto-config -directory")

        String ordererDomain = connector.config.orderer.domainName
        String mainDomain = getDomainFromOrdererDomain(ordererDomain)

        String ordererServerCertPath = "/crypto-config/ordererOrganizations/" +
                mainDomain + "/orderers/" +
                ordererDomain + "/tls/"

        String ordererServerCertFullPath = ordererServerCertPath + ORDERER_SERVER_CERT_NAME
        String envPath = connector.config.fabricEnvPath
        String serverCertPath = envPath + ordererServerCertFullPath
        String outputPath = envPath + "/generated/" + orgParams.orgDomain + ordererServerCertFullPath

        File serverCert = new File(serverCertPath)
        File copyOfServerCert = new File(outputPath)

        FileUtils.copyFile(serverCert, copyOfServerCert)
    }

    /**
     * Get domain from orderer domain
     * @param ordererDomain
     * @return
     */
    private static String getDomainFromOrdererDomain(String ordererDomain) {
        int firstDotIndex = ordererDomain.indexOf(".")
        String domain = ordererDomain.substring(firstDotIndex + 1)

        logger.info("Got orderer domain: " + domain)

        return domain
    }
}
