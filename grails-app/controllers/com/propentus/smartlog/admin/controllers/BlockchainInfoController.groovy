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

import com.propentus.smartlog.blockchain.BlockchainInfoUtil
import com.propentus.smartlog.uimodel.DashboardInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Contains actions that are used to fetch information
 * from blockchain.
 */
class BlockchainInfoController {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainInfoController.class)

    /**
     * Returns block count as a dashboard info
     *
     * Uses channel to query BlockchainInfo and maps block height
     * and description to dasboardInfo-model
     *
     * @render template: "/dashboard/dashboardInfo"
     */
    def getBlockCount() {

        try {
            long height = BlockchainInfoUtil.getInstance().getChainHeight()

            //  Map data to DashboardInfo-model for easier handling of the data in template
            DashboardInfo di = new DashboardInfo()
            di.info = height.toString()
            di.label = "Blocks"
            di.description = "Height of the chain"

            render (template: "/dashboard/dashboardInfo", model: [dashboard: di])
            return

        } catch(Exception e) {
            logger.error(e.getMessage(), e)
            response.setStatus(500)
            logger.error("Error when trying to fetch height information from channel")
            render "Error when trying to fetch height information from channel"
            return
        }


    }

    /**
     * Returns count of the organizations as a dashboard info.
     *
     * Gets peer count from channel and maps it to dashboardInfo model.
     *
     * @render template: "/dashboard/dashboardInfo"
     */
    def getOrganizationCount() {

        try {

            int orgCount = BlockchainInfoUtil.getInstance().getOrgCount()

            //  Map data to DashboardInfo-model for easier handling of the data in template
            DashboardInfo di = new DashboardInfo()
            di.info = orgCount.toString()
            di.label = "Organizations"
            di.description = "Organizations configured to channel"

            render (template: "/dashboard/dashboardInfo", model: [dashboard: di])
            return

        } catch(Exception e) {

            logger.error(e.getMessage(), e)
            response.setStatus(500)
            logger.error("Error when trying to read organization count from channel config")
            render "Error when trying to read organization count from channel config"
            return
        }
    }
}
