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

import com.propentus.iot.BlockchainConnector
import com.propentus.iot.ConfigEditor
import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.smartlog.configtxlator.ConfigTxLatorService
import org.hyperledger.fabric.sdk.Channel
import org.hyperledger.fabric.sdk.UpdateChannelConfiguration
import org.hyperledger.fabric.sdk.User

/**
 * Contains managing methods for channel
 */
class ChannelManagement {

    private BlockchainConnector connector

    public ChannelManagement() {}

    public ChannelManagement(BlockchainConnector connector) {
        this.connector = connector;
    }

    /**
     * Modifies channel config like so that our organization is the only one that can modify the configs.
     */
    public void setOneOrgSign() {

        Channel channel = connector.getChannel()
        User user = connector.getUser()

        //	Get config_block as JSON
        String configJson = FabricConfigLoader.getConfigFile();

        //	Change signing policy for application
        ConfigEditor ce = new ConfigEditor(configJson, connector);
        String newConfig = ce.changeOrganizationPolicyToAdminOrgOnly(connector.getConfig().organisation.mspid);

        //	Create proto from modified configs using configtxlator
        byte[] updateChannelConfigurationBytes = ConfigTxLatorService.wrapNewAndOldConfig(configJson, newConfig);

        //	Get signature for and sign the UpdateChannelConfiguration
        UpdateChannelConfiguration ucc = new UpdateChannelConfiguration(updateChannelConfigurationBytes);
        byte[] signerOrg1 = connector.settingInitializer.getHFClient().getUpdateChannelConfigurationSignature(ucc, user);
        byte[][] signers = new byte[1][]
        signers[0] = signerOrg1

        //  Deploy the update to the network
        channel.updateChannelConfiguration(ucc, signers);
    }
}
