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

package com.propentus.common.validators;

import com.propentus.smartlog.blockchain.BlockchainInfoUtil;
import grails.web.servlet.mvc.GrailsParameterMap;

public class OrganizationParamsValidator extends AbstractValidator {

    public OrganizationParamsValidator(GrailsParameterMap params) {
        super(params);
    }

    /**
     * Main validation method
     *
     * Check if proper parameters were given
     * Check if organization is already found from channel config
     *
     * @return boolean
     */
    @Override
    public boolean validate() {

        //  Get basic from map
        String name = this.params.getProperty("orgName").toString();
        String domain = this.params.getProperty("orgDomain").toString();
        String msp = this.params.getProperty("orgMsp").toString();
        String peerUrl = this.params.getProperty("peerUrl").toString();
        String peerDomain = this.params.getProperty("peerDomain").toString();
        String ordererUrl = this.params.getProperty("ordererUrl").toString();
        String ordererDomain = this.params.getProperty("ordererDomain").toString();
        String eventhubUrl = this.params.getProperty("eventhubUrl").toString();
        String eventhubDomain = this.params.getProperty("eventhubDomain").toString();
        String envpath = this.params.getProperty("envpath").toString();
        String caUrl = this.params.getProperty("caUrl").toString();
        String peerType = this.params.getProperty("peerType").toString();
        String couchDbUrl = this.params.getProperty("couchDbUrl").toString();

        String[] parameters = {name, domain, msp, peerUrl, peerDomain, ordererUrl,
                ordererDomain, eventhubUrl, eventhubDomain, envpath, caUrl, peerType, couchDbUrl};

        //  Check if all required params are found
        for (String param : parameters) {

            if (param.equals("")) {
                setError("Required parameters are missing");
                return false;
            }
        }

        //  If organization is already found from channel then add error and return false
        try {
            if (BlockchainInfoUtil.getInstance().orgAlreadyOnChannel(msp)) {
                setError("Organization already configured to channel");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            setError("Could not parse orgAlreadyOnChannel-information from channel config");
            return false;
        }

        return true;
    }
}
