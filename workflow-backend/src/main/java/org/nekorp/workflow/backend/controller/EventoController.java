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
import javax.servlet.http.HttpServletResponse;

import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.springframework.validation.BindingResult;

import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;

/**
 * @author Nekorp
 */
@Deprecated
public interface EventoController {

    Page<List<EventoOfy>> getEventos(Long idServicio, PaginationDataLong pagination, BindingResult resultPagination, HttpServletRequest request);
    
    void crearEvento(Long idServicio, EventoOfy dato, BindingResult result, HttpServletRequest request, HttpServletResponse response);
    
    EventoOfy getEvento(Long idServicio, Long idEvento);
    
    void actualizarEvento(Long idServicio, Long idEvento, EventoOfy dato, BindingResult result);
    
    void borrarEvento(Long idServicio, Long idEvento);
}
