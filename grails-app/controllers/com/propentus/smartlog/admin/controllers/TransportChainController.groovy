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


import com.propentus.common.validators.TransportChainParamsValidator
import com.propentus.iot.chaincode.TransportChainChaincode
import com.propentus.iot.chaincode.model.TransportChaincodeTO
import com.propentus.smartlog.blockchain.ConnectorHolderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Contains transport chain specific actions
 */
class TransportChainController {

    private static final Logger logger = LoggerFactory.getLogger(this)

    def connectorHolderService

    /**
     * Edit transportChain
     *
     * If transportChain doesn't exist it gets created
     *
     * @return
     */
    def edit() {

        logger.info("Editing transportation chain with params: " + params)

        String chainId = params.chainId
        String chain = params.chain

        TransportChainParamsValidator validator = new TransportChainParamsValidator(params)

        if (!validator.validate()) {

            logger.error("Validation unsuccessful, error: " + validator.getError())
            response.setStatus(400)
            flash.error = "Error: " + validator.getError()

            //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
            //  all of those again.
            redirect(controller: "management", action: "transportChain", params: [newChainId: chainId, newChain: chain])
            return
        }

        try {

            //  Initialize TransportChainChaincode with blockchain connections and add transport chain to blockchain.
            TransportChainChaincode service = new TransportChainChaincode(connectorHolderService.connector)
            TransportChaincodeTO transportChain = new TransportChaincodeTO()
            transportChain.setId(chainId)
            transportChain.setParticipants(chain.split(',').toList())
            if (!service.addTransportChain(transportChain)) {
                logger.error("Couldn't add new transport chain to blockchain")
                response.setStatus(400)
                flash.error = "Error: Couldn't add transport chain to blockchain."

                //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
                //  all of those again.
                redirect(controller: "management", action: "transportChain", params: [newChainId: chainId, newChain: chain])
                return
            }

        } catch(Exception e) {
            logger.error(e.getMessage(), e)
            response.setStatus(400)
            flash.error = "Error: Couldn't add transportChain to blockchain."

            //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
            //  all of those again.
            redirect(controller: "management", action: "transportChain", params: [newChainId: chainId, newChain: chain])
            return
        }

        flash.success = "New chain added to blockchain"
        redirect(controller: "management", action: "transportChain")
        return
    }

    /**
     * Removes transport chain from blockchain
     * @return
     */
    def remove() {

        String chainId = params.chainId

        if (!chainId) {
            response.setStatus(400)
            flash.error = "Error: No chainId given"

            //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
            //  all of those again.
            redirect(controller: "management", action: "transportChain", params: [chainId: chainId])
            return
        }

        try {

            TransportChainChaincode service = new TransportChainChaincode(connectorHolderService.connector)
            TransportChaincodeTO transportChain = new TransportChaincodeTO()
            transportChain.setId(chainId)

            //  TODO: service.removeTransportChain()

        } catch(Exception e) {
            logger.error(e.getMessage(), e)
            response.setStatus(400)
            flash.error = "Couldn't remove transport chain due to some error"

            //  Redirect to same page where request came and put parameters back to url, so user don't need to insert
            //  all of those again.
            redirect(controller: "management", action: "transportChain", params: [chainId: chainId])
            return
        }

        flash.success = "Removed transport chain " + chainId + " from blockchain"
        redirect(controller: "management", action: "transportChain")
        return
    }
}
