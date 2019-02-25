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

package com.propentus.smartlog.blockchain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.configs.FabricConfigLoader;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BlockchainInfoUtil contains helper methods to query information
 * about the state of the network.
 */
public class BlockchainInfoUtil {

    private static final BlockchainInfoUtil INSTANCE = new BlockchainInfoUtil();
    private static final String MAJORITY_POLICY = "MAJORITY";
    private BlockchainConnector connector = null;

    private BlockchainInfoUtil() {}

    public static BlockchainInfoUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Gets config from channel and uses configtxlator to decode
     * the config to JSON.
     *
     * Then gets count of the organizations from config.
     *
     * @return
     * @throws Exception
     */
    public int getOrgCount() throws Exception {

        String configJson = FabricConfigLoader.getConfigFile();

        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(configJson).getAsJsonObject();

        int count = element.getAsJsonObject("channel_group")
                .getAsJsonObject("groups")
                .getAsJsonObject("Application")
                .getAsJsonObject("groups").size();

        return count;
    }

    /**
     * Gets the height of the chain.
     *
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public long getChainHeight() throws InvalidArgumentException, ProposalException {

        long height = connector.getChannel().queryBlockchainInfo().getHeight();
        return height;
    }

    /**
     * Checks from config if the one-org-signing is already modified to config.
     *
     * @return true if one org signing is configured, false otherwise
     */
    public boolean isOneOrgSigningConfigured(String config) throws Exception {

        if (config == null) {
            config = FabricConfigLoader.getConfigFile();
        }

        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(config).getAsJsonObject();

        JsonObject orgPolicy = element.getAsJsonObject("channel_group")
                .getAsJsonObject("groups")
                .getAsJsonObject("Application")
                .getAsJsonObject("policies")
                .getAsJsonObject("Admins");

        return !orgPolicy.toString().contains(MAJORITY_POLICY);
    }

    /**
     * Check if organization is already configured to channel.
     *
     * Load configuration block from channel, and check if Application-group contains this
     * new org already.
     *
     * @return boolean
     */
    public boolean orgAlreadyOnChannel(String msp) throws Exception {

        String config = FabricConfigLoader.getConfigFile();

        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(config).getAsJsonObject();

        JsonObject orgGroup = element.getAsJsonObject("channel_group")
                .getAsJsonObject("groups")
                .getAsJsonObject("Application");

        return orgGroup.toString().toLowerCase().contains(msp.toLowerCase());
    }

    /**
     * Get all organizations that are configured to the channel
     * @return List of all organizations
     */
    public List<String> getOrganizations() throws Exception {

        String config = FabricConfigLoader.getConfigFile();

        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(config).getAsJsonObject();

        JsonObject orgGroup = element.getAsJsonObject("channel_group")
                .getAsJsonObject("groups")
                .getAsJsonObject("Application")
                .getAsJsonObject("groups");

        List<String> orgs = new ArrayList<String>();

        for (Map.Entry<String, JsonElement> org : orgGroup.entrySet()) {
            orgs.add(org.getKey());
        }

        return orgs;
    }

    public void setConnector(BlockchainConnector bc) {
        this.connector = bc;
    }
}
