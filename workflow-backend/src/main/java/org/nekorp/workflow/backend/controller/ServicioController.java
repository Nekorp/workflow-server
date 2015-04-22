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

import org.nekorp.workflow.backend.data.access.util.FiltroServicio;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.auto.damage.DamageDetailOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import org.springframework.validation.BindingResult;

import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;
/**
 * Para la parte de los eventos y los registos de costos se plantea verlos
 * desde dos puntos de vista diferente
 * El primero es verlos como una unidad
 *      La bitacora como la coleccion de todos los eventos, de esto se desprenden los metodos
 *          getBitacora
 *          saveBitacora
 *      El costo como la coleccion de todos los registros de costo, de esto se desprenden los metodos
 *          getCosto
 *          saveCosto
 * El segundo es ver cada elemento de manera individual
 *      Eventos cada suceso parte de la bitacora, de esto se desprende el control de eventos.
 *      Registro Costo una parte del costo total, de esto se desprende el control de registros del costo.
 *      
 * Desde el punto de vista de REST se tienen uri diferentes para estos cuatro recursos
 *      bitacora
 *      costo
 *      bitacora/eventos
 *      costo/registros
 * @author Nekorp
 */
public interface ServicioController {

    Page<List<ServicioOfy>> getServicios(FiltroServicio filtro, PaginationDataLong pagination, BindingResult resultPagination, HttpServletRequest request);
    void crearServicio(ServicioOfy servicio, BindingResult result, HttpServletRequest request, HttpServletResponse response);
    ServicioOfy getServicio(Long id);
    void actualizarServicio(Long id, ServicioOfy datos, BindingResult result);
    void borrarServicio(Long id);
    
    /**
     * Consulta la bitacora del servicio, es decir la coleccion de todos los eventos,
     * los eventos no estan necesariamente ordenados cronologicamente, estan ordenados por ID.
     * @param idServicio el id del servicio del cual se consulta la bitacora.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de los eventos que conforman la bitacora del servicio.
     */
    List<EventoOfy> getBitacora(Long idServicio);
    /**
     * Guarda la bitacora del servicio, si hay eventos nuevos se crean, los eventos ya existentes
     * se actualizan y los eventos que ya no se recivan se borran.
     * @param idServicio el id del servicio al que se quiere actualizar su bitacora.
     * @param eventos los eventos que conforman la bitacora.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de eventos, los eventos nuevos se regresan con el ID generado.
     */
    List<EventoOfy> saveBitacora(Long idServicio, List<EventoOfy> eventos, BindingResult result);
    /**
     * Consulta el costo del servicio, es decir la coleccion de todos los registros de costo.
     * @param idServicio el id del servicio del cual se consulta el costo.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de registros que forman el costo del servicio.
     */
    List<RegistroCostoOfy> getCosto(Long idServicio);
    /**
     * Guarda el costo del servicio, si hay registros nuevos se crean, los registros ya existentes
     * se actualizan y los registros que ya no se recivan se borran.
     * @param idServicio el id del servicio del cual se consulta el costo.
     * @param registros los registros que producen el costo.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de registros, los registros nuevos se regresan con el ID generado.
     */
    List<RegistroCostoOfy> saveCosto(Long idServicio, List<RegistroCostoOfy> registros, BindingResult result);
    
    /**
     * Consulta los danios del auto.
     * @param idServicio el id del servicio del cual se consulta el inventario de danios.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de registros que forman el inventario de danios.
     */
    List<DamageDetailOfy> getInventarioDamage(Long idServicio);
    /**
     * Guarda el inventario de danios del auto, si hay registros nuevos se crean, los registros ya existentes
     * se actualizan y los registros que ya no se recivan se borran.
     * @param idServicio el id del servicio del cual se modeifica el inventario.
     * @param registros los registros que forman el inventario.
     * @param response se requiere para colocar informacion adicional.
     * @return La lista de registros, los registros nuevos se regresan con el ID generado.
     */
    List<DamageDetailOfy> saveInventarioDamage(Long idServicio, List<DamageDetailOfy> registros, BindingResult result);
    
}
