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
package org.nekorp.workflow.backend.service;

import java.util.List;

import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;

/**
 * En algunas ocaciones se tiene que recalcular el metadata del servicio.
 */
public interface ServicioMetadataAutoRefresh {
    
    void actualizarUsandoId(Long idServicio);
    
    void actualizarUsandoIdEventos(Long idServicio, List<Evento> eventos);
    
    /**
     * cambia el contenido del metadata, con un valor calculado en ese momento.
     * @param servicio
     */
    void actualizarServicioMetadataInterceptor(Servicio servicio);
    
    void actualizarUsandoServicioEventos(Servicio servicio, List<Evento> eventos);
}
