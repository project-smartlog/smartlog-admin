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

import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.smartlog.blockchain.BlockchainInfoUtil
import com.propentus.smartlog.configtxlator.ConfigTxLatorService
import com.propentus.smartlog.management.ChannelManagement
import org.hyperledger.fabric.sdk.Channel
import org.hyperledger.fabric.sdk.UpdateChannelConfiguration
import org.hyperledger.fabric.sdk.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Contains actions for channel management
 */
class ChannelController {

    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class)

    def connectorHolderService

    /**
     * Enable one org signing for channel.
     * Makes our peer as a admin client which is then used to manage the channel.
     *
     * @return
     */
    def enableOneOrgSign() {

        // If signign is already configured, just do redirect
        if (BlockchainInfoUtil.getInstance().isOneOrgSigningConfigured()) {
            flash.error = "Admin org signing already enabled"
            redirect(controller: "management", action: "channel")
            return
        }

        try {

            ChannelManagement cm = new ChannelManagement(connectorHolderService.connector)
            cm.setOneOrgSign()

        } catch(Exception e) {
            logger.error(e.getMessage(), e)
            logger.error("Something went wrong when trying to edit channel config")
            flash.error = "Something went wrong when trying to edit channel config"
            redirect(controller: "management", action: "channel")
            return
        }

        flash.success = "Admin org signing enabled"
        redirect(controller: "management", action: "channel")
    }

    /**
     * API to set new configuration for the channel.
     *
     * Gets new configuration from the request's body and tries to add that as a new configuration for channel.
     *
     * @return
     */
    def setupNewConfig() {

        try {

            String newConfig = request.reader.text

            if (!newConfig) {
                render "No config found from request"
                return
            }

            String oldConfig = FabricConfigLoader.getConfigFile();
            Channel channel = connectorHolderService.getConnector().getChannel()
            User user = connectorHolderService.getConnector().getUser()

            //	Create proto from modified configs using configtxlator
            byte[] updateChannelConfigurationBytes = ConfigTxLatorService.wrapNewAndOldConfig(oldConfig, newConfig);

            //	Get signature for and sign the UpdateChannelConfiguration
            UpdateChannelConfiguration ucc = new UpdateChannelConfiguration(updateChannelConfigurationBytes);
            byte[] signerOrg1 = connectorHolderService.getConnector().settingInitializer.getHFClient().getUpdateChannelConfigurationSignature(ucc, user);
            byte[][] signers = new byte[1][]
            signers[0] = signerOrg1

            channel.updateChannelConfiguration(ucc, signers);
            render "OK"
            return

        } catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error("Error message: ", e.cause)
            render "FAIL"
            return
        }
    }
}
