/**
 *   Copyright 2013-2015 Tikal-Technology
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
package org.nekorp.workflow.backend.security.data.access;

import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;

import technology.tikal.gae.dao.template.EntityDAO;
import technology.tikal.gae.dao.template.FiltroBusqueda;
import technology.tikal.gae.pagination.model.PaginationData;

/**
 * @author Nekorp
 */
public interface UsuarioClienteWebDAO extends EntityDAO<UsuarioClienteWeb, String, FiltroBusqueda, PaginationData<String>>{

}
