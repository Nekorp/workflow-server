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
package org.nekorp.workflow.backend.security.controller.imp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.security.controller.UsuarioClienteWebController;
import org.nekorp.workflow.backend.security.data.access.UsuarioClienteWebDAO;
import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.googlecode.objectify.NotFoundException;

import technology.tikal.gae.error.exceptions.NotValidException;
import technology.tikal.gae.pagination.PaginationModelFactory;
import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataString;
import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * @author Nekorp
 * TODO validar si el cliente existe.
 */
@RestController
@RequestMapping("/cliente/web/usuarios")
public class UsuarioClienteWebControllerImp extends RestControllerTemplate implements UsuarioClienteWebController {

    private UsuarioClienteWebDAO usuarioClienteWebDAO;
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<UsuarioClienteWeb>> listar(@Valid @ModelAttribute final PaginationDataString pagination, 
            final BindingResult resultPagination, final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        List<UsuarioClienteWeb> datos = usuarioClienteWebDAO.consultarTodos(null, pagination);
        Page<List<UsuarioClienteWeb>> r = PaginationModelFactory.getPage(datos, "usuarioClienteWeb", request.getRequestURI() , null, pagination);
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crear(@Valid @RequestBody final UsuarioClienteWeb datos, final BindingResult result, 
            final HttpServletRequest request, final HttpServletResponse response) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        datos.setAlias(StringUtils.lowerCase(datos.getAlias()));
        try {
            usuarioClienteWebDAO.consultar(datos.getAlias());
            throw new IllegalArgumentException("usuario repetido");
        } catch (NotFoundException e) {
            //Ya no valida si el cliente existe
            this.usuarioClienteWebDAO.guardar(datos);
            response.setHeader("Location",  request.getRequestURI() + "/" + datos.getAlias());
        }
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{alias}", method = RequestMethod.GET)
    public UsuarioClienteWeb consultar(@PathVariable final String alias) {
        String consulta = StringUtils.lowerCase(alias);
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(consulta);
        return resultado;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{alias}", method = RequestMethod.POST)
    public void actualizar(@PathVariable final String alias, @Valid @RequestBody final UsuarioClienteWeb datos, final BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        datos.setAlias(StringUtils.lowerCase(alias));
        usuarioClienteWebDAO.consultar(datos.getAlias());
        //ya no valida si el cliente existe
        usuarioClienteWebDAO.guardar(datos);
    }

    /**{@inheritDoc}*/
    @Override
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value="/{alias}", method = RequestMethod.DELETE)
    public void borrar(@PathVariable final String alias) {
        String buscar = StringUtils.lowerCase(alias);
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(buscar);
        usuarioClienteWebDAO.borrar(resultado);
    }
    
    public void setUsuarioClienteWebDAO(UsuarioClienteWebDAO usuarioClienteWebDAO) {
        this.usuarioClienteWebDAO = usuarioClienteWebDAO;
    }    
}
