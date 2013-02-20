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
import org.nekorp.workflow.backend.controller.ClienteController;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.cliente.Cliente;
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
@RequestMapping("/clientes")
public class ClienteControllerImp implements ClienteController {

    private ClienteDAO clienteDao;
    private StringStandarizer stringStandarizer;
    private PaginationModelFactory pagFactory;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getClientes(java.lang.String, java.lang.String, int)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Cliente, Long> getClientes(@ModelAttribute final FiltroCliente filtro, 
            @Valid @ModelAttribute final PaginationDataLong pagination, HttpServletResponse response) {
        filtro.setFiltroNombre(this.stringStandarizer.standarize(filtro.getFiltroNombre()));
        List<Cliente> datos = clienteDao.consultarTodos(filtro, pagination);
        Page<Cliente, Long> r = pagFactory.getPage();
        r.setTipoItems("cliente");
        r.setLinkPaginaActual(armaUrl(filtro.getFiltroNombre(), pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl(filtro.getFiltroNombre(), pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#crearCliente(org.nekorp.workflow.backend.model.cliente.Cliente)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearCliente(@Valid @RequestBody Cliente cliente, HttpServletResponse response) {
        cliente.setId(null);
        preprocesaCliente(cliente);
        this.clienteDao.guardar(cliente);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/clientes/" + cliente.getId());
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getCliente(java.lang.String)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public @ResponseBody Cliente getCliente(@PathVariable Long id, HttpServletResponse  response) {
        Cliente respuesta = this.clienteDao.consultar(id);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#actualizarCliente(org.nekorp.workflow.backend.model.cliente.Cliente, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public void actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente datos, HttpServletResponse response) {
        preprocesaCliente(datos);
        datos.setId(id);
        if (!this.clienteDao.actualizar(datos)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#borrarCliente(java.lang.Long, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void borrarCliente(@PathVariable final Long id, final HttpServletResponse response) {
        Cliente respuesta = this.clienteDao.consultar(id);
        if (respuesta == null) {
            //no hay nada que responder
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        clienteDao.borrar(respuesta);
        //se acepto la peticion de borrado, no quiere decir que sucede de inmediato.
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    /**
     * cambios que se aplican a los datos del cliente independientemente de lo que envien los sistemas.
     * @param cliente el cliente a modificar.
     */
    private void preprocesaCliente(final Cliente cliente) {
        //pasa el rfc a mayusculas
        cliente.setRfc(StringUtils.upperCase(cliente.getRfc()));
        //genera el nombre estandar para posteriores busquedas
        cliente.setNombreEstandar(this.stringStandarizer.standarize(cliente.getNombre()));
    }
    
    
    private String armaUrl(final String filtroNombre, final Long sinceId, final int maxResults) {
        String r = "/clientes";
        r = addUrlParameter(r,"filtroNombre", filtroNombre);
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

    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }

    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }
}
