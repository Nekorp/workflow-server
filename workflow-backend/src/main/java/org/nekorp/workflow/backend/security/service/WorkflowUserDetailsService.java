/**
 *   Copyright 2013 Nekorp
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */
package org.nekorp.workflow.backend.security.service;

import org.nekorp.workflow.backend.security.data.access.UserSecurityDAO;
import org.nekorp.workflow.backend.security.model.WorkflowUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 */
public class WorkflowUserDetailsService implements UserDetailsService {

    private UserSecurityDAO userSecurityDAO;
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WorkflowUserDetails userDetails = userSecurityDAO.loadUserByName(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("");
        }
        return userDetails;
    }
    public void setUserSecurityDAO(UserSecurityDAO userSecurityDAO) {
        this.userSecurityDAO = userSecurityDAO;
    }
}
