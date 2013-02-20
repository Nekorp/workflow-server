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
package org.nekorp.workflow.backend.security.data.access.objectify;

import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.security.data.access.UserSecurityDAO;
import org.nekorp.workflow.backend.security.model.WorkflowGrantedAuthority;
import org.nekorp.workflow.backend.security.model.WorkflowUserDetails;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class UserSecurityDAOImp extends ObjectifyDAOTemplate implements UserSecurityDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.security.data.access.UserSecurityDAO#saveUserDetails(org.nekorp.workflow.backend.security.model.WorkflowUserDetails)
     */
    @Override
    public void saveUserDetails(WorkflowUserDetails userDetails) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.save().entity(userDetails).now();
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.security.data.access.UserSecurityDAO#loadUserByName(java.lang.String)
     */
    @Override
    public WorkflowUserDetails loadUserByName(String userName) {
        List<WorkflowUserDetails> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<WorkflowUserDetails> query =  ofy.load().type(WorkflowUserDetails.class);
        result = query.list();
        if (result.size() == 0 && userName.equals("user")) { //no existe ningun usuario hay que crear el de default
            Logger.getLogger(UserSecurityDAOImp.class).info("Creando Usuario default");
            return crearUsuarioDefault();
        }
        for (WorkflowUserDetails x: result) { //se buscan en memoria para no hacer un count y luego un query
            if (x.getUsername().equals(userName)) {
                return x;
            }
        }
        return null;
    }
    
    private WorkflowUserDetails crearUsuarioDefault() {
        WorkflowUserDetails defaultUser = new WorkflowUserDetails();
        defaultUser.setEnabled(true);
        defaultUser.setUsername("user");
        defaultUser.setPassword("user");
        List<WorkflowGrantedAuthority> permisos = new LinkedList<WorkflowGrantedAuthority>();
        WorkflowGrantedAuthority permiso = new WorkflowGrantedAuthority();
        permiso.setAuthority("ROLE_USER");
        permisos.add(permiso);
        defaultUser.setPermisos(permisos);
        this.saveUserDetails(defaultUser);
        return defaultUser;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.security.data.access.UserSecurityDAO#deleteUserDetails(java.lang.String)
     */
    @Override
    public void deleteUserDetails(String userName) {
        Objectify ofy = getObjectifyFactory().begin();
        Key<WorkflowUserDetails> key = Key.create(WorkflowUserDetails.class, userName);
        ofy.delete().entity(key).now();
    }

}
