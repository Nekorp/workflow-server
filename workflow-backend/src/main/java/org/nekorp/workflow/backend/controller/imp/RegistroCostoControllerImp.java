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
import org.nekorp.workflow.backend.controller.RegistroCostoController;
import org.nekorp.workflow.backend.data.access.RegistroCostoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
@RequestMapping("/servicios/{idServicio}/costo/registros")
public class RegistroCostoControllerImp implements RegistroCostoController {

    private ServicioDAO servicioDAO;
    private RegistroCostoDAO registroCostoDAO;
    private PaginationModelFactory pagFactory;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#getRegistros(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<RegistroCosto, Long> getRegistros(@PathVariable final Long idServicio,
        @Valid @ModelAttribute final PaginationDataLong pagination, final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<RegistroCosto> datos = registroCostoDAO.consultarTodos(idServicio, null, pagination);
        Page<RegistroCosto, Long> r = pagFactory.getPage();
        r.setTipoItems("registroCosto");
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
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#crearRegistro(java.lang.Long, org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearRegistro(@PathVariable final Long idServicio, @Valid @RequestBody final RegistroCosto dato,
        final HttpServletResponse response) {
        dato.setId(null);
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        this.registroCostoDAO.guardar(idServicio, dato);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/servicios/" + idServicio + "/costo/registros/" + dato.getId());
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#getRegistro(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idRegistro}", method = RequestMethod.GET)
    public @ResponseBody RegistroCosto getRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro,
        final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        RegistroCosto respuesta = this.registroCostoDAO.consultar(idServicio, idRegistro);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#actualizarRegistro(java.lang.Long, java.lang.Long, org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idRegistro}", method = RequestMethod.POST)
    public void actualizarRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro,
        @Valid @RequestBody final RegistroCosto datos, final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        datos.setId(idRegistro);
        if (!this.registroCostoDAO.actualizar(idServicio, datos)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#borrarRegistro(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idRegistro}", method = RequestMethod.DELETE)
    public void borrarRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro,
        final HttpServletResponse response) {
        if (!idServicioValido(idServicio)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        RegistroCosto dato = registroCostoDAO.consultar(idServicio, idRegistro);
        if (dato == null) {
            //no hay nada que responder
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        registroCostoDAO.borrar(idServicio, dato);
        //se acepto la peticion de borrado, no quiere decir que sucede de inmediato.
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    
    private boolean idServicioValido(Long idServicio) {
        return servicioDAO.consultar(idServicio) != null;
    }
    
    private String armaUrl(final Long idParent, final Long sinceId, final int maxResults) {
        String r = "/servicios/" + idParent + "/costo/registros";
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

    public void setRegistroCostoDAO(RegistroCostoDAO registroCostoDAO) {
        this.registroCostoDAO = registroCostoDAO;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }
}
