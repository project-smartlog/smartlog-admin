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

/**
 * Main JS for transportation chain view
 */
$(function() {

    /**
     * Insert organization msp to chain.
     *
     * If organization msp is already found from chain, do nothing.
     *
     * Chain is defined as a CSV (comma separated values)
     */
    $(".js-organization").click(function() {

        // Get the old value
        var value = $("#transport-chain-input").val();
        var org = $(this).text();

        // Check if already found
        if (value.includes(org)) {
            alert("Organization already found from list");
            return;
        }

        if (value !== "") {
            value += ",";
        }
        value += org;

        // Set new value to input
        $("#transport-chain-input").val(value);
    });


    /**
     * Insert chain info to edit form.
     *
     * If chain gets selected from the list, it gets moved to edit form.
     */
    $(".js-transport-chain").click(function() {

        var id = $(this).find(".transport-chain--id").text();
        var chain = $(this).find(".transport-chain--chain").val();

        $("#transport-chain-name-edit-input").val(id);
        $("#transport-chain-edit-input").val(chain);

        //  Remove success class from every other
        $(".js-transport-chain").each(function(index) {
            $(this).removeClass("btn-success").addClass("btn-primary");
        });

        //  Add success class
        $(this).removeClass("btn-primary").addClass("btn-success");
    });
});