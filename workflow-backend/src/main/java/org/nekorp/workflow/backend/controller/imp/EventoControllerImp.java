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
import org.nekorp.workflow.backend.controller.EventoController;
import org.nekorp.workflow.backend.data.access.EventoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@RequestMapping("/servicios/{idServicio}/bitacora/eventos")
public class EventoControllerImp implements EventoController {

    private ServicioDAO servicioDAO;
    private EventoDAO eventoDAO;
    private PaginationModelFactory pagFactory;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#getEventos(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Evento, Long> getEventos(@PathVariable final Long idServicio,
        @Valid @ModelAttribute final PaginationDataLong pagination, final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<Evento> datos = eventoDAO.consultarTodos(idServicio, null, pagination);
        Page<Evento, Long> r = pagFactory.getPage();
        r.setTipoItems("evento");
        r.setLinkPaginaActual(armaUrl(idServicio, pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl(idServicio, pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#crearEvento(java.lang.Long, org.nekorp.workflow.backend.model.servicio.bitacora.Evento, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearEvento(@PathVariable final Long idServicio, @Valid @RequestBody final Evento dato,
        final HttpServletResponse response) {
        dato.setId(null);
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        this.eventoDAO.guardar(idServicio, dato);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/servicios/" + idServicio + "/bitacora/eventos/" + dato.getId());
        
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#getEvento(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idEvento}", method = RequestMethod.GET)
    public @ResponseBody Evento getEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento,
        final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Evento respuesta = this.eventoDAO.consultar(idServicio, idEvento);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#actualizarEvento(java.lang.Long, java.lang.Long, org.nekorp.workflow.backend.model.servicio.bitacora.Evento, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idEvento}", method = RequestMethod.POST)
    public void actualizarEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento, 
        @Valid @RequestBody final Evento dato, final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        dato.setId(idEvento);
        if (eventoDAO.consultar(idServicio, idEvento) == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            eventoDAO.guardar(idServicio, dato);
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioEventoController#borrarEvento(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idEvento}", method = RequestMethod.DELETE)
    public void borrarEvento(@PathVariable final Long idServicio, @PathVariable final Long idEvento,
        final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        Evento dato = eventoDAO.consultar(idServicio, idEvento);
        if (dato == null) {
            //no hay nada que responder
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        eventoDAO.borrar(idServicio, dato);
        //se acepto la peticion de borrado, no quiere decir que sucede de inmediato.
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    
    private boolean idServicioValido(Long idServicio) {
        return servicioDAO.consultar(idServicio) != null;
    }
    
    private String armaUrl(final Long idParent, final Long sinceId, final int maxResults) {
        String r = "/servicios/" + idParent + "/bitacora/eventos";
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
