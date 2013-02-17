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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.controller.ClienteController;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroFactory;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
@RequestMapping("/cliente")
public class ClienteControllerImp implements ClienteController {

    private ClienteDAO clienteDao;
    private PaginationModelFactory pagFactory;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getClientes(java.lang.String, java.lang.String, int)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Page<Cliente> getClientes(
            @RequestParam(value="filtroNombre", required=false) final String filtroNombre, 
            @Valid @ModelAttribute PaginationData pagination) {
        //se preparan los datos para el filtrado de informacion
        Map<String, Object> filter = FiltroFactory.getFilter();
        if (!StringUtils.isEmpty(filtroNombre)) {
            filter.put("filtroNombre", filtroNombre);
        }
        //se obtienen los datos y se crea la pagina para colocarlos
        List<Cliente> datos = clienteDao.getClientes(filter, pagination);
        Page<Cliente> r = pagFactory.getPage();
        r.setTipoItems("cliente");
        r.setLinkPaginaActual(armaUrl(filtroNombre, pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl(filtroNombre, pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        return r;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#crearCliente(org.nekorp.workflow.backend.model.cliente.Cliente)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void crearCliente(@Valid @RequestBody Cliente cliente, HttpServletResponse  response) {
        cliente.setRfc(StringUtils.upperCase(cliente.getRfc()));
        this.clienteDao.nuevoCliente(cliente);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/cliente/" + cliente.getId());
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getCliente(java.lang.String)
     */
    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Cliente getCliente(@PathVariable String id, HttpServletResponse  response) {
        Cliente respuesta = this.clienteDao.getCliente(id);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return respuesta;
    }
    
    
    private String armaUrl(final String filtroNombre, final String sinceId, final int maxResults) {
        String r = "/cliente";
        r = addUrlParameter(r,"filtroNombre", filtroNombre);
        r = addUrlParameter(r,"sinceId", sinceId);
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
}
