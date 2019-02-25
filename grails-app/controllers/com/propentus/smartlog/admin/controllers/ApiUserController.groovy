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

import com.propentus.common.util.http.BasicAuthenticationParser
import com.propentus.iot.configs.ConfigReader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.iot.configs.OrganisationConfiguration.AuthType
import com.propentus.smartlog.datasource.couchdb.CouchDBProvider
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser
import com.propentus.smartlog.datasource.couchdb.repositories.ApiUserRepository
import com.propentus.smartlog.management.ClientCertificate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Handles adding and deleting of the API users.
 */
class ApiUserController {

    private static final Logger logger = LoggerFactory.getLogger(this)

    def connectorHolderService

    /**
     * Adds API user to CouchDB.
     *
     * DomainName must be the same as the organizations domain name so we know where to create the certificates.
     *
     * Generates client certificate for the ApiUser which enables connection to cloud version with that certificate.
     *
     * @return
     */
    def addUser() {

        logger.info("Adding ApiUser with parameters: " + params)

        String name = params.name
        String mspid = params.mspid
        String domainName = params.domainName

        //Auth token params
        String username = params.username
        String password = params.password
        String authToken = params.authToken

        try {

            OrganisationConfiguration config = connectorHolderService.getConnector().getConfig()
            String sha1 = ""

            //  Create certificates only if the configured AuthType is CERT
            if (config.authType.equals(AuthType.CERT)) {

                logger.info("AdminClient is configured with certificate authentication, so generate one for the " +
                        "organization: " + domainName)

                //  Generate client certificate for the organization
                ClientCertificate certificate = new ClientCertificate(domainName)
                certificate.generate()

                //  Get SHA1 from the certificate so we can add that information to API User for authorization
                sha1 = certificate.getSha1()
            }

            ApiUser user = new ApiUser()
            user.setName(name)
            user.setOrganisation(mspid)
            user.setDomainName(domainName)
            user.setUsername(username)
            user.setPassword(password)
            user.setSha1(sha1)
            user.setAuthToken(authToken)

            //Read configuration for CouchDB and try to add new ApiUser entity
            CouchDBProvider provider = new CouchDBProvider()
            ApiUserRepository repository = new ApiUserRepository(provider.getConnector())
            repository.add(user)

        } catch (Exception e) {
            logger.error(e.getMessage(), e)
            flash.error = "Couldn't add new ApiUser."
            redirect(controller: "management", action: "apiUsers")
            return
        }
        flash.success = "Saved new ApiUser succesfully"
        redirect(controller: "management", action: "apiUsers")
    }

    /**
     * Deletes selected ApiUser from CouchDB.
     *
     * If admin client is configured to use certificate authentication, removes ApiUser's client certificate and revokes
     * it so it can no longer be used to connect to cloud API's.
     *
     * @return
     */
    def deleteUser() {

        logger.info("Deleting user with params: " + params)

        String id = params.id

        try {

            //Read configuration for CouchDB and try to remove ApiUser entity
            ConfigReader configReader = new ConfigReader()
            CouchDBProvider provider = new CouchDBProvider()
            ApiUserRepository repository = new ApiUserRepository(provider.getConnector())
            ApiUser user = repository.get(id)

            if(user != null) {
                repository.remove(user)

                //  If AdminClient is configured with AuthType CERT, revoke and delete client certificate
                if (configReader.getOrganisationConfiguration().authType.equals(AuthType.CERT)) {

                    //  Delete and revoke ApiUser's certificate so it can no longer be used to connect to cloud API's.
                    ClientCertificate certificate = new ClientCertificate(user.domainName)
                    certificate.revoke()
                }

            } else {
                logger.error("No user found with given ID: " + id)
                flash.error = "No user found with given ID: " + id
                redirect(controller: "management", action: "apiUsers")
                return
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e)
            flash.error = "Error while removing ApiUser"
            redirect(controller: "management", action: "apiUsers")
            return
        }

        flash.success = "Removed ApiUser succesfully"
        redirect(controller: "management", action: "apiUsers")
        return
    }

    /**
     * Generate basic authentication token
     * @return
     */
    def generateAuthToken() {
        String username = params.username
        String password = params.password

        String authToken = BasicAuthenticationParser.createAuthnToken(username, password)
        render authToken
    }
}
