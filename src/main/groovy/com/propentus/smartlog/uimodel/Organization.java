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

package com.propentus.smartlog.uimodel;

import java.util.UUID;

/**
 * This class is used to collect organization data that is then used to
 */
public class Organization {

    private String domain;
    private String config;
    private boolean inChannel;
    private UUID uuid;

    /**
     * Default constructor
     */
    public Organization() {
        this.uuid = UUID.randomUUID();
    }

    public Organization(String domain, String config, boolean inChannel) {
        this.domain = domain;
        this.config = config;
        this.inChannel = inChannel;
        this.uuid = UUID.randomUUID();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public boolean isInChannel() {
        return inChannel;
    }

    public void setInChannel(boolean inChannel) {
        this.inChannel = inChannel;
    }
}
