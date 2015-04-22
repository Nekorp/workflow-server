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

import org.nekorp.workflow.backend.controller.RegistroCostoController;
import org.nekorp.workflow.backend.data.access.RegistroCostoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
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
@RequestMapping("/servicios/{idServicio}/costo/registros")
@Deprecated
public class RegistroCostoControllerImp extends RestControllerTemplate implements RegistroCostoController {

    private ServicioDAO servicioDAO;
    private RegistroCostoDAO registroCostoDAO;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#getRegistros(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<RegistroCostoOfy>> getRegistros(@PathVariable final Long idServicio,
        @Valid @ModelAttribute final PaginationDataLong pagination, final BindingResult resultPagination, 
        final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        List<RegistroCostoOfy> datos = registroCostoDAO.consultarTodos(servicio, null, pagination);
        Page<List<RegistroCostoOfy>> r = PaginationModelFactory.getPage(datos, "registroCosto", request.getRequestURI() , null, pagination);
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#crearRegistro(java.lang.Long, org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crearRegistro(@PathVariable final Long idServicio, @Valid @RequestBody final RegistroCostoOfy dato,
            final BindingResult result, final HttpServletRequest request, final HttpServletResponse response) {
        dato.setId(null);
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        this.registroCostoDAO.guardar(servicio, dato);
        response.setHeader("Location", request.getRequestURI() + "/" + dato.getId());
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#getRegistro(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idRegistro}", method = RequestMethod.GET)
    public RegistroCostoOfy getRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro) {
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        RegistroCostoOfy respuesta = this.registroCostoDAO.consultar(servicio, idRegistro);
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#actualizarRegistro(java.lang.Long, java.lang.Long, org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idRegistro}", method = RequestMethod.POST)
    public void actualizarRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro,
        @Valid @RequestBody final RegistroCostoOfy datos, BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        datos.setId(idRegistro);
        registroCostoDAO.consultar(servicio, idRegistro);
        this.registroCostoDAO.guardar(servicio, datos);
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ServicioRegistroCosto#borrarRegistro(java.lang.Long, java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{idRegistro}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void borrarRegistro(@PathVariable final Long idServicio, @PathVariable final Long idRegistro) {
        ServicioOfy servicio = servicioDAO.consultar(idServicio);
        RegistroCostoOfy dato = registroCostoDAO.consultar(servicio, idRegistro);
        registroCostoDAO.borrar(servicio, dato);
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setRegistroCostoDAO(RegistroCostoDAO registroCostoDAO) {
        this.registroCostoDAO = registroCostoDAO;
    }
}
