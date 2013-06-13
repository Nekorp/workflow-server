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
package org.nekorp.workflow.backend.security.controller.imp;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataString;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.nekorp.workflow.backend.security.controller.UsuarioClienteWebController;
import org.nekorp.workflow.backend.security.data.access.UsuarioClienteWebDAO;
import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;
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
@RequestMapping("/cliente/web/usuarios")
public class UsuarioClienteWebControllerImp implements UsuarioClienteWebController {

    private UsuarioClienteWebDAO usuarioClienteWebDAO;
    private ClienteDAO clienteDao;
    private PaginationModelFactory pagFactory;
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody public Page<UsuarioClienteWeb, String> listar(@Valid @ModelAttribute final PaginationDataString pagination, 
        final HttpServletResponse response) {
        List<UsuarioClienteWeb> datos = usuarioClienteWebDAO.consultarTodos(null, pagination);
        Page<UsuarioClienteWeb, String> r = pagFactory.getPage();
        r.setTipoItems("usuarioClienteWeb");
        r.setLinkPaginaActual(armaUrl("/cliente/web/usuarios", pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl("/cliente/web/usuarios", pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crear(@Valid @RequestBody final UsuarioClienteWeb datos, final HttpServletResponse response) {
        datos.setAlias(StringUtils.lowerCase(datos.getAlias()));
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(datos.getAlias());
        if (resultado != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        Cliente cliente = clienteDao.consultar(datos.getIdCliente());
        if (cliente == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        this.usuarioClienteWebDAO.guardar(datos);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/cliente/web/usuarios/" + datos.getAlias());
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{alias}", method = RequestMethod.GET)
    public @ResponseBody UsuarioClienteWeb consultar(@PathVariable final String alias, final HttpServletResponse response) {
        String consulta = StringUtils.lowerCase(alias);
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(consulta);
        if (resultado == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return resultado;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{alias}", method = RequestMethod.POST)
    public void actualizar(@PathVariable final String alias, @Valid @RequestBody final UsuarioClienteWeb datos, final HttpServletResponse response) {
        datos.setAlias(alias);
        datos.setAlias(StringUtils.lowerCase(datos.getAlias()));
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(datos.getAlias());
        if (resultado == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        Cliente cliente = clienteDao.consultar(datos.getIdCliente());
        if (cliente == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        usuarioClienteWebDAO.guardar(datos);
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{alias}", method = RequestMethod.DELETE)
    public void borrar(@PathVariable final String alias, final HttpServletResponse response) {
        String buscar = StringUtils.lowerCase(alias);
        UsuarioClienteWeb resultado = usuarioClienteWebDAO.consultar(buscar);
        if (resultado == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        } 
        usuarioClienteWebDAO.borrar(resultado);
        //se acepto la peticion de borrado, no quiere decir que sucede de inmediato.
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    
    private String armaUrl(final String base, final String sinceId, final int maxResults) {
        String r = base;
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

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }

    public void setUsuarioClienteWebDAO(UsuarioClienteWebDAO usuarioClienteWebDAO) {
        this.usuarioClienteWebDAO = usuarioClienteWebDAO;
    }

    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }
}
