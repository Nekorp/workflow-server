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
package org.nekorp.workflow.backend.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nekorp.workflow.backend.data.access.util.FiltroServicioIndex;
import org.springframework.validation.BindingResult;

import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndex;

/**
 * @author Nekorp
 */
public interface ServicioIndexController {

    Page<List<ServicioIndex>> getServicioIndex(FiltroServicioIndex filtro, PaginationDataLong pagination, BindingResult resultPagination, HttpServletRequest request);
}
