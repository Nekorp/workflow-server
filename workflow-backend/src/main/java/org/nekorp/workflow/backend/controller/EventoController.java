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
package org.nekorp.workflow.backend.controller;

import javax.servlet.http.HttpServletResponse;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;

/**
 * 
 */
public interface EventoController {

    Page<Evento, Long> getEventos(Long idServicio, PaginationDataLong pagination, HttpServletResponse response);
    
    void crearEvento(Long idServicio, Evento dato, HttpServletResponse response);
    
    Evento getEvento(Long idServicio, Long idEvento, HttpServletResponse response);
    
    void actualizarEvento(Long idServicio, Long idEvento, Evento dato, HttpServletResponse response);
    
    void borrarEvento(Long idServicio, Long idEvento, HttpServletResponse response);
}
