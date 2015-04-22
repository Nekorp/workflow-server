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

import org.apache.commons.lang.StringUtils;

import org.nekorp.workflow.backend.controller.ClienteController;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.model.cliente.ClienteOfy;

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
@RequestMapping("/clientes")
@Deprecated
public class ClienteControllerImp extends RestControllerTemplate implements ClienteController {

    private ClienteDAO clienteDao;
    private StringStandarizer stringStandarizer;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getClientes(java.lang.String, java.lang.String, int)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<ClienteOfy>> getClientes(@ModelAttribute final FiltroCliente filtro, 
            @Valid @ModelAttribute final PaginationDataLong pagination, final BindingResult resultPagination, final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        filtro.setFiltroNombre(this.stringStandarizer.standarize(filtro.getFiltroNombre()));
        List<ClienteOfy> datos = clienteDao.consultarTodos(filtro, pagination);
        Page<List<ClienteOfy>> r = PaginationModelFactory.getPage(datos,"cliente",  request.getRequestURI() , filtro, pagination);
        return r;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#crearCliente(org.nekorp.workflow.backend.model.cliente.Cliente)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crearCliente(@Valid @RequestBody ClienteOfy cliente, final BindingResult result, 
            final HttpServletRequest request, HttpServletResponse response) {
        cliente.setId(null);
        preprocesaCliente(cliente);
        this.clienteDao.guardar(cliente);
        response.setHeader("Location",  request.getRequestURI() +"/" + cliente.getId());
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getCliente(java.lang.String)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{id}", method = RequestMethod.GET)
    public ClienteOfy getCliente(@PathVariable Long id) {
        ClienteOfy respuesta = this.clienteDao.consultar(id);
        return respuesta;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#actualizarCliente(org.nekorp.workflow.backend.model.cliente.Cliente, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public void actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteOfy datos, final BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        clienteDao.consultar(id);
        preprocesaCliente(datos);
        datos.setId(id);
        clienteDao.guardar(datos);
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#borrarCliente(java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void borrarCliente(@PathVariable final Long id) {
        ClienteOfy respuesta = this.clienteDao.consultar(id);
        clienteDao.borrar(respuesta);
    }
    /**
     * cambios que se aplican a los datos del cliente independientemente de lo que envien los sistemas.
     * @param cliente el cliente a modificar.
     */
    private void preprocesaCliente(final ClienteOfy cliente) {
        //pasa el rfc a mayusculas
        cliente.setRfc(StringUtils.upperCase(cliente.getRfc()));
        //genera el nombre estandar para posteriores busquedas
        cliente.setNombreEstandar(this.stringStandarizer.standarize(cliente.getNombre()));
    }
    
    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }
    
    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }
}
