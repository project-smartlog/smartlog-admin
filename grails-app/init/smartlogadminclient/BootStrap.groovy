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

package smartlogadminclient

import com.propentus.common.utils.GrailsUtil
import com.propentus.iot.BlockchainConnector
import com.propentus.smartlog.blockchain.BlockchainInfoUtil
import com.propentus.smartlog.blockchain.ConnectorHolderService

class BootStrap {

    def init = { servletContext ->

        //Start blockchain connector on application startup and save reference to singleton grails bean
        BlockchainConnector connector = new BlockchainConnector()
        ConnectorHolderService holder = GrailsUtil.getBean(ConnectorHolderService.class)
        holder.connector = connector

        //  Set connector to BlockchainInfoUtil for easier connection handling
        BlockchainInfoUtil biu = BlockchainInfoUtil.getInstance().setConnector(connector)
    }
    def destroy = {
    }
}
