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
package org.nekorp.workflow.backend.controller.imp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.controller.ServicioController;
import org.nekorp.workflow.backend.data.access.EventoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/servicio")
public class ServicioControllerImp implements ServicioController {

    private ServicioDAO servicioDAO;
    private EventoDAO eventoDAO;
    private PaginationModelFactory pagFactory;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#getServicios(org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Servicio, Long> getServicios(@Valid @ModelAttribute final PaginationDataLong pagination, final HttpServletResponse response) {
        List<Servicio> datos = servicioDAO.getServicios(pagination);
        Page<Servicio, Long> r = pagFactory.getPage();
        r.setTipoItems("servicio");
        r.setLinkPaginaActual(armaUrl("/servicio", pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl("/servicio", pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#crearServicio(org.nekorp.workflow.backend.model.servicio.Servicio, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearServicio(@Valid @RequestBody final Servicio servicio, final HttpServletResponse response) {
        this.servicioDAO.nuevoServicio(servicio);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/servicio/" + servicio.getId());
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#getServicio(java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public @ResponseBody Servicio getServicio(@PathVariable final Long id, final HttpServletResponse response) {
        Servicio respuesta = this.servicioDAO.getServicio(id);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#actualizarServicio(java.lang.Long, org.nekorp.workflow.backend.model.servicio.Servicio, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public void actualizarServicio(@PathVariable final Long id, @Valid @RequestBody final Servicio datos, final HttpServletResponse response) {
        datos.setId(id);
        if (!this.servicioDAO.actualizaServicio(datos)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#getEventos(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idServicio}/evento", method = RequestMethod.GET)
    public @ResponseBody List<Evento> getEventos(@PathVariable final Long idServicio, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.getServicio(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<Evento> r = eventoDAO.getEventos(idServicio);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioController#saveEventos(java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idServicio}/evento", method = RequestMethod.POST)
    public @ResponseBody List<Evento> saveEventos(@PathVariable final Long idServicio,
        @Valid @RequestBody final List<Evento> eventos, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.getServicio(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<Evento> datos = eventoDAO.saveEventos(idServicio, eventos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return datos;
    }

    private String armaUrl(final String base, final Long sinceId, final int maxResults) {
        String r = base;
        if (sinceId != null && sinceId > 0) {
            r = addUrlParameter(r,"sinceId", sinceId + "");
        }
        if (maxResults > 0) {
            r = addUrlParameter(r,"maxResults", maxResults + "");
        }
        return r;
    }
    
    private String addUrlParameter(final String url, final String name, final String value) {
        String response = url;
        if (!StringUtils.isEmpty(value)) {
            if (!StringUtils.contains(response, '?')) {
                response = response + "?";
            } else {
                response = response + "&";
            }
            response = response + name + "=" + value;
        }
        return response;
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setEventoDAO(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }

}
