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

package com.propentus.smartlog.taglibs

import com.propentus.iot.configs.OrganisationConfiguration.PeerType

class FormTagLib {
    static defaultEncodeAs = [taglib:'none']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    /**
     * Render select that contains options for PeerTypes
     *
     */
    def peerTypeOptions = { attrs ->

        String optionsHtml = ""
        String selectedValue = attrs.selected != null ? attrs.selected : "NORMAL"

        //  Loop thru PeerTypes and generate <option>-html
        for (PeerType peerType : PeerType.values()) {

            String selected = ""

            try {

                //  If value was given as param
                if (PeerType.valueOf(selectedValue).equals(peerType)) {
                    selected = "selected"
                }

            } catch(IllegalArgumentException iae) {
                //  Just to avoid crashing if parameter was not given or it had wrong value.
                //  Enum.valueOf() needs exactly correct values as a parameter, otherwise it throws
                //  IllegalArgumentException
            }

            optionsHtml += '<option value="' + peerType + '" ' + selected + '>' + peerType + '</option>'
        }

        out << optionsHtml
    }


}
