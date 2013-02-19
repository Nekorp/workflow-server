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

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;

public interface ServicioController {

    Page<Servicio, Long> getServicios(PaginationDataLong pagination, HttpServletResponse response);
    
    void crearServicio(Servicio servicio, HttpServletResponse response);

    Servicio getServicio(Long id, HttpServletResponse response);

    void actualizarServicio(Long id, Servicio datos, HttpServletResponse response);
    
    List<Evento> getEventos(Long idServicio, HttpServletResponse response);
    
    List<Evento> saveEventos(Long idServicio, List<Evento> eventos, HttpServletResponse response);
}
