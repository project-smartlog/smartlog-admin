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

package com.propentus.smartlog.admin.controllers

import com.propentus.common.validators.OrganizationParamsValidator
import com.propentus.iot.chaincode.KeystoreChaincodeService
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.management.OrganizationGenerator
import com.propentus.smartlog.management.OrganizationService
import com.propentus.smartlog.security.CryptoUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.KeyPair
import java.util.zip.ZipOutputStream

/**
 * Contains actions that are used to handle Organization data
 */
class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(this)

    def connectorHolderService

    /**
     * Gets needed attributes from the UI and validates that everything is given properly.
     *
     * If validator gives no errors, generate new configs and certificates for the given organization and add
     * organization to channel.
     * @return
     */
    def generate() {

        logger.info("Generating certificates and configurations to organization with parameters: " + params)

        String name = params.orgName
        String domain = params.orgDomain
        String msp = params.orgMsp
        String peerUrl = params.peerUrl
        String peerDomain = params.peerDomain
        String ordererUrl = params.ordererUrl
        String ordererDomain = params.ordererDomain
        String eventhubUrl = params.eventhubUrl
        String eventhubDomain = params.eventhubDomain
        String envpath = params.envpath
        String caUrl = params.caUrl
        String peerType = params.peerType
        String couchDbUrl = params.couchDbUrl
        String msg = "Successfully generated certificates"

        OrganizationParamsValidator validator = new OrganizationParamsValidator(params)

        //  Validate parameters and check if organization is already found from channel config
        if (!validator.validate()) {

            logger.warn("Error validating parameters: " + validator.getError())
            response.setStatus(400)
            msg = "Error: " + validator.getError()
            flash.error = msg

            //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
            //  all of those again.
            redirect(controller: "management", action: "addOrganization",
                    params: params)
            return
        }

        try {

            OrganizationGenerator orgManagement = new OrganizationGenerator(params, connectorHolderService.connector)
            orgManagement.generateCertsAndConfigsAndAddToChannel()

            logger.info("Successfully generated certificates and added organization to channel")
            msg = "Successfully generated certificates and added organization to channel"

            flash.success = msg
            redirect(controller: "management", action: "addOrganization")

        } catch(Exception e) {

            logger.error(e.getMessage(), e)
            flash.error = "There was an error when generating certificates"
            redirect(controller: "management", action: "addOrganization",
                    params: params)
        }
    }

    /**
     * Add admin organization's public key to blockchain.
     */
    def addAdminOrgCryptoToChannel() {

        logger.info("Adding admin organization crypto to channel")

        try {

            OrganisationConfiguration config = connectorHolderService.connector.getConfig()
            KeyPair pair = CryptoUtil.generateMessagingCertificates(config.fabricEnvPath + "/crypto-config/", "RSA")
            KeystoreChaincodeService service = new KeystoreChaincodeService(connectorHolderService.connector)
            OrganisationChaincodeTO org = new OrganisationChaincodeTO()
            org.mspID = config.organisation.mspid
            org.publicKey = CryptoUtil.publicKeyToString(pair.getPublic())
            if (!service.addOrganisation(org)) {
                logger.error("Something went wrong when adding admin organizations cryptography to blockchain.")
                response.setStatus(400)

                flash.error = "There was an error adding cryptography to channel"
                redirect(controller: "management", action: "channel")
                return
            }

        } catch(Exception e) {

            logger.error(e.getMessage(), e)
            response.setStatus(400)

            flash.error = "There was an error adding cryptography to channel"
            redirect(controller: "management", action: "channel")
            return
        }

        flash.success = "Added admin organization crypto succesfully to channel"
        redirect(controller: "management", action: "channel")
    }

    /**
     * Download everything about organization that was generated.
     *
     * Zip contains configurations and cryptography for organization.
     *
     * @return
     */
    def downloadConfigs() {

        String orgDomain = params.orgDomain

        response.setContentType('APPLICATION/OCTET-STREAM')
        response.setHeader('Content-Disposition', 'Attachment;Filename="' + orgDomain + '.zip"')
        ZipOutputStream zos = new ZipOutputStream(response.outputStream);

        OrganizationService os = new OrganizationService()
        zos = os.getConfigsZip(zos, orgDomain)
    }
}
