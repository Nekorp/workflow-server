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
import org.nekorp.workflow.backend.model.servicio.metadata.ServicioMetadata;

/**
 * 
 */
public interface ServicioMetadataFactory {

    /**
     * Calcula el metadata de un servicio.
     * @param servicio el servicio al que se van a calcular su metadata.
     * @return los datos calculados.
     */
    ServicioMetadata calcularMetadata(Servicio servicio);
    
    /**
     * Calcula el metadata de un servicio.
     * @param servicio el servicio al que se le va a calcular el metadata.
     * @param eventos los eventos de la bitacora. (si ya se tienen cargados para no hacer doble busqueda).
     * @return los datos calculados.
     */
    ServicioMetadata calcularMetadata(Servicio servicio,  List<Evento> eventos);
}
