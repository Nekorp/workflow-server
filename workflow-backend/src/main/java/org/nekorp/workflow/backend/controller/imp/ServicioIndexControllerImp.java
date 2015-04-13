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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.controller.ServicioIndexController;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroServicioIndex;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.auto.Auto;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.nekorp.workflow.backend.model.index.servicio.ServicioIndex;
import org.nekorp.workflow.backend.model.index.servicio.ServicioIndexAutoData;
import org.nekorp.workflow.backend.model.index.servicio.ServicioIndexClienteData;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Nekorp
 */
@Controller
@RequestMapping("/index")
public class ServicioIndexControllerImp implements ServicioIndexController {

    private AutoDAO autoDAO;
    private ServicioDAO servicioDAO;
    private ClienteDAO clienteDao;
    private StringStandarizer stringStandarizer;
    private PaginationModelFactory pagFactory;
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value = "/servicio", method = RequestMethod.GET)
    public @ResponseBody Page<ServicioIndex, Long> getServicioIndex(@ModelAttribute final FiltroServicioIndex filtro, 
            @Valid @ModelAttribute final PaginationDataLong pagination, final HttpServletResponse response) {
        filtro.setNumeroSerieAuto(stringStandarizer.standarize(filtro.getNumeroSerieAuto()));
        Page<ServicioIndex, Long> r = pagFactory.getPage();
        List<Servicio> servicios = servicioDAO.consultarTodos(filtro, pagination);
        List<ServicioIndex> datosRespuesta = new LinkedList<ServicioIndex>();
        for (Servicio x: servicios) {
            datosRespuesta.add(crearServicioIndex(x));
        }
        r.setItems(datosRespuesta);
        r.setLinkPaginaActual(armaUrl("/index/servicio", filtro, pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl("/index/servicio", filtro, pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }
    
    private ServicioIndex crearServicioIndex(Servicio servicio) {
        ServicioIndex nuevo = new ServicioIndex();
        nuevo.setAutoData(crearServicioIndexAutoData(servicio.getIdAuto()));
        nuevo.setClienteData(crearServicioIndexClienteData(servicio.getIdCliente()));
        nuevo.setDescripcion(servicio.getDescripcion());
        nuevo.setFechaInicio(servicio.getMetadata().getFechaInicio());
        nuevo.setId(servicio.getId());
        nuevo.setStatus(servicio.getMetadata().getStatus());
        nuevo.setCobranza(servicio.getCobranza());
        nuevo.setCostoTotal(servicio.getMetadata().getCostoTotal());
        return nuevo;
    }
    
    private ServicioIndexAutoData crearServicioIndexAutoData(String idAuto) {
        ServicioIndexAutoData nuevo = new ServicioIndexAutoData();
        Auto auto = autoDAO.consultar(idAuto);
        if (auto != null) {
            nuevo.setNumeroSerie(auto.getNumeroSerie());
            nuevo.setPlacas(auto.getPlacas());
            nuevo.setTipo(auto.getTipo());
        }
        return nuevo;
    }
    
    private ServicioIndexClienteData crearServicioIndexClienteData(Long idCliente) {
        ServicioIndexClienteData nuevo = new ServicioIndexClienteData();
        Cliente cliente = clienteDao.consultar(idCliente);
        if (cliente != null) {
            nuevo.setId(cliente.getId());
            nuevo.setNombre(cliente.getNombre());
        }
        return nuevo;
    }
    
    private String armaUrl(final String base, final FiltroServicioIndex filtro, final Long sinceId, final int maxResults) {
        String r = base;
        r = addUrlParameter(r,"numeroSerieAuto", filtro.getNumeroSerieAuto());
        r = addUrlParameter(r,"statusServicio", filtro.getStatusServicio());
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

    public void setAutoDAO(AutoDAO autoDAO) {
        this.autoDAO = autoDAO;
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }

    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }
}
