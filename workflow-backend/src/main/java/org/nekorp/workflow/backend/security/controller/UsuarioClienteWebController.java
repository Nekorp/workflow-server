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

import javax.servlet.http.HttpServletResponse;

import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataString;
import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;

/**
 * 
 */
public interface UsuarioClienteWebController {

    Page<UsuarioClienteWeb, String> listar(PaginationDataString pagination, HttpServletResponse response);
    void crear(UsuarioClienteWeb datos, HttpServletResponse response);
    UsuarioClienteWeb consultar(String alias, HttpServletResponse response);
    void actualizar(String alias, UsuarioClienteWeb datos, HttpServletResponse response);
    void borrar(String alias, HttpServletResponse response);
}
