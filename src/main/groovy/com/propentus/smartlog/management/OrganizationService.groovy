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

import com.propentus.common.util.EntityUtil
import com.propentus.common.util.file.FileUtil
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.common.utils.ZipUtil
import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.common.configs.AdminClientConfig
import com.propentus.smartlog.uimodel.Organization

import java.util.zip.ZipOutputStream

class OrganizationService {


    private static final String CONFIG_FILE_NAME = AdminClientConfig.CLIENT_JSON_FILENAME
    List<Organization> organizations = new ArrayList<Organization>()

    ConnectorHolderService holder = GrailsUtil.getBean(ConnectorHolderService.class)
    AdminClientConfig adminConfig = new AdminClientConfig(holder.getConnector())
    private String organizationsConfigPath = adminConfig.generatedPath

    public OrganizationService() {}

    /**
     * Get organization information that are configured to network
     * @return
     */
    public List<Organization> getOrganizations() {

        //  TODO: populate the data using channel config and stuff from disk
        String channelConfig = FabricConfigLoader.getConfigFile()

        //  Get config_path from

        //  Read organizations from disk and populate the data
        File orgsFolder = new File(organizationsConfigPath)

        for (File f : orgsFolder.listFiles()) {

            if (f.isDirectory()) {

                //  Get config
                String conf = FileUtil.readFileAsString(organizationsConfigPath + f.getName() + "/" + CONFIG_FILE_NAME)

                OrganisationConfiguration orgConfig = EntityUtil.JsonToObject(conf, OrganisationConfiguration.class)
                String mspid = orgConfig.organisation.mspid

                Organization o = new Organization()
                o.domain = f.getName()
                o.config = conf
                o.inChannel = channelConfig.contains(mspid)

                organizations.add(o)
            }
        }

        return organizations
    }

    public ZipOutputStream getConfigsZip(ZipOutputStream zos, String orgDomain) {

        String folderName = organizationsConfigPath + orgDomain

        try {

            ZipUtil zip = new ZipUtil(zos)
            zip.setSourceFolder(folderName)
            zip.generateFileList(new File(folderName))
            zip.zipIt()
            return zip.getZip()

        } catch(Exception e) {
            e.printStackTrace()
        }

        return null
    }
}
