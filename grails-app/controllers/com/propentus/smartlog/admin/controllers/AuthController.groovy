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

import com.propentus.smartlog.common.configs.AdminClientConfig

/**
 * Controller to handle the authentication to the admin client.
 */
class AuthController {

    /**
     * Handle login for application.
     *
     * Only POST is supported.
     *
     * If username or password is incorrect, render login view with error message.
     *
     * If login is successful, add loggedIn-parameter to session and redirect user to management page.
     *
     * @return
     */
    def login() {

        //  If user has already logged in, just redirect to management page
        if (session.loggedIn) {
            redirect(controller: "management", action: "index")
            return
        }

        if (request.post) {

            //  If username and password doesn't match, show error message
            if (!checkUsernameAndPassword()) {
                flash.error = "Username or password is wrong"
                return
            }

            session.loggedIn = true
            redirect(controller: "management", action: "index")
            return
        }
    }

    /**
     * Logs user out from the application.
     *
     * Removes loggedIn-parameter from session and redirects user to auth page.
     *
     * @return
     */
    def logout() {

        session.loggedIn = false
        redirect(controller: "auth", action: "login")
    }

    /**
     * Checks if username and password matches to configured values.
     *
     * @return
     */
    private boolean checkUsernameAndPassword() {

        String username = params.username
        String password = params.password

        if (username.equals(AdminClientConfig.USERNAME) && password.equals(AdminClientConfig.PASSWORD)) {
            return true
        }

        return false
    }
}
