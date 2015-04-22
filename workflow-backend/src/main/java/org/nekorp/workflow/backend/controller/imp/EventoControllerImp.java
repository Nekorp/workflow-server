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
package org.nekorp.workflow.backend.controller.imp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.nekorp.workflow.backend.controller.EventoController;
import org.nekorp.workflow.backend.data.access.EventoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import technology.tikal.gae.error.exceptions.NotValidException;
import technology.tikal.gae.pagination.PaginationModelFactory;
import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;
import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * @author Nekorp
 */
@RestController
@RequestMapping("/servicios/{idServicio}/bitacora/eventos")
@Deprecated
public class EventoControllerImp extends RestControllerTemplate implements EventoController {

    private ServicioDAO servicioDAO;
    private EventoDAO eventoDAO;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#getEventos(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<EventoOfy>> getEventos(@PathVariable final Long idServicio,
            @Valid @ModelAttribute final PaginationDataLong pagination, final BindingResult resultPagination,
            final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        List<EventoOfy> datos = eventoDAO.consultarTodos(servicio, null, pagination);
        Page<List<EventoOfy>> r = PaginationModelFactory.getPage(datos, "evento", request.getRequestURI() , null, pagination);
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#crearEvento(java.lang.Long, org.nekorp.workflow.backend.model.servicio.bitacora.Evento, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crearEvento(@PathVariable final Long idServicio, @Valid @RequestBody final EventoOfy dato, final BindingResult result,
            final HttpServletRequest request, final HttpServletResponse response) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        dato.setId(null);
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        this.eventoDAO.guardar(servicio, dato);
        response.setHeader("Location", request.getRequestURI() + "/" + dato.getId());
        
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#getEvento(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idEvento}", method = RequestMethod.GET)
    public EventoOfy getEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento) {
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        EventoOfy respuesta = this.eventoDAO.consultar(servicio, idEvento);
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#actualizarEvento(java.lang.Long, java.lang.Long, org.nekorp.workflow.backend.model.servicio.bitacora.Evento, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idEvento}", method = RequestMethod.POST)
    public void actualizarEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento, 
        @Valid @RequestBody final EventoOfy dato, final BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        dato.setId(idEvento);
        eventoDAO.consultar(servicio, idEvento);
        eventoDAO.guardar(servicio, dato);
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#borrarEvento(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idEvento}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void borrarEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento) {
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        EventoOfy dato = eventoDAO.consultar(servicio, idEvento);
        eventoDAO.borrar(servicio, dato);
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setEventoDAO(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }    
}
