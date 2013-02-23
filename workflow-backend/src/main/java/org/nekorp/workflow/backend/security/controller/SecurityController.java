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
package org.nekorp.workflow.backend.security.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.nekorp.workflow.backend.security.data.access.UserSecurityDAO;
import org.nekorp.workflow.backend.security.model.WorkflowGrantedAuthority;
import org.nekorp.workflow.backend.security.model.WorkflowUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 */
@Controller
@RequestMapping("/aplication/users")
public class SecurityController {

    private UserSecurityDAO userSecurityDAO;
    /**
     * crea nuevo usuario.
     * get por que me da hueva el post
     * @param userDetails los datos del nuevo usuario
     */
    @RequestMapping(method = RequestMethod.GET)
    void createUser(@Valid @ModelAttribute final WorkflowUserDetails userDetails, final HttpServletResponse response) {
        //abilida el usuario
        userDetails.setEnabled(true);
        //setea permisos por default
        List<WorkflowGrantedAuthority> permisos = new LinkedList<WorkflowGrantedAuthority>();
        WorkflowGrantedAuthority permiso = new WorkflowGrantedAuthority();
        permiso.setAuthority("ROLE_USER");
        permisos.add(permiso);
        userDetails.setPermisos(permisos);
        //guarda el usuario
        userSecurityDAO.saveUserDetails(userDetails);
        response.setStatus(HttpStatus.CREATED.value());
    }
    public void setUserSecurityDAO(UserSecurityDAO userSecurityDAO) {
        this.userSecurityDAO = userSecurityDAO;
    }
}
