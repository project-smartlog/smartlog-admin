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

import com.propentus.common.exception.ConfigurationException
import com.propentus.iot.chaincode.KeystoreChaincodeService
import com.propentus.iot.chaincode.TransportChainChaincode
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO
import com.propentus.iot.chaincode.model.TransportChaincodeTO
import com.propentus.iot.configs.ConfigReader
import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.smartlog.blockchain.BlockchainInfoUtil
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.datasource.couchdb.CouchDBProvider
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser
import com.propentus.smartlog.datasource.couchdb.repositories.ApiUserRepository
import com.propentus.smartlog.management.OrganizationService
import com.propentus.smartlog.uimodel.Organization
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(this)

    def connectorHolderService

    /**
     * Main view for management overview.
     *
     * Shows basic information from blockchain.
     * Information is rendered asynchronously as view loads.
     */
    def index() {
        return
    }

    /**
     * Main view for channel configurations
     */
    def channel() {

        //  Load channel configuration and parse information from it
        String config = FabricConfigLoader.getConfigFile()
        boolean oneOrgSignEnabled = BlockchainInfoUtil.getInstance().isOneOrgSigningConfigured(config)

        String mspid = connectorHolderService.connector.getConfig().organisation.mspid
        KeystoreChaincodeService service = new KeystoreChaincodeService(connectorHolderService.connector)
        OrganisationChaincodeTO org = service.getOrganisation(mspid)
        boolean adminOrgCryptoAdded = org != null

        return [config: config, oneOrgSignEnabled: oneOrgSignEnabled, adminOrgCryptoAdded: adminOrgCryptoAdded]
    }

    /**
     * Main view for add organization.
     */
    def addOrganization() {
        return
    }

    /**
     * Main view for organizations list
     */
    def organizations() {

        try {

            OrganizationService os = new OrganizationService()
            List<Organization> organizations = os.getOrganizations()

            return [organizations: organizations]

        } catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error("Couldn't load organizations")
            flash.error = "There was an error loading organizations"
            return
        }
    }

    /**
     * Main view for transport chain management
     */
    def transportChain() {

        //  Get list of organizations which are configured to the channel
        List<String> orgs = BlockchainInfoUtil.getInstance().getOrganizations()

        //  Get list of chains that are inserted into the blockchain
        TransportChainChaincode service = new TransportChainChaincode(connectorHolderService.connector)
        TransportChaincodeTO[] chains = service.getTransportChain("")

        return [chains: chains, orgs: orgs]
    }

    /**
     * Main view for API user management.
     *
     * Shows a list of all ApiUsers and form from where you can add new ApiUsers.
     */
    def apiUsers() {

        try {

            //Read configuration for CouchDB and try to add new ApiUser entity
            ConfigReader configReader = new ConfigReader()
            CouchDBProvider provider = new CouchDBProvider()
            ApiUserRepository repository = new ApiUserRepository(provider.getConnector())
            List<ApiUser> users = repository.getAll()
            return [apiUsers: users]

        } catch (ConfigurationException ce) {

            logger.error(ce.getMessage(), ce)
            flash.error = "Configuration exception, are CouchDB username and password configured properly?"
            return

        } catch (Exception e) {

            logger.error(e.getMessage(), e)
            flash.error = "There was an error when reading API Users from CouchDB"
            return

        }

    }

}
