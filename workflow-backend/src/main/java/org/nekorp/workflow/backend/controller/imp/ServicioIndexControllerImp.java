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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.nekorp.workflow.backend.controller.ServicioIndexController;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroServicioIndex;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.model.auto.AutoOfy;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import technology.tikal.gae.error.exceptions.NotValidException;
import technology.tikal.gae.pagination.PaginationModelFactory;
import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;
import technology.tikal.gae.service.template.RestControllerTemplate;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndex;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndexAutoData;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndexClienteData;

/**
 * @author Nekorp
 */
@RestController
@RequestMapping("/index")
public class ServicioIndexControllerImp extends RestControllerTemplate implements ServicioIndexController {

    private AutoDAO autoDAO;
    private ServicioDAO servicioDAO;
    private StringStandarizer stringStandarizer;
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value = "/servicio", method = RequestMethod.GET)
    public Page<List<ServicioIndex>> getServicioIndex(@ModelAttribute final FiltroServicioIndex filtro, 
            @Valid @ModelAttribute final PaginationDataLong pagination, final BindingResult resultPagination, 
            final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        filtro.setNumeroSerieAuto(stringStandarizer.standarize(filtro.getNumeroSerieAuto()));
        List<ServicioOfy> servicios = servicioDAO.consultarTodos(filtro, pagination);
        List<ServicioIndex> datosRespuesta = new LinkedList<ServicioIndex>();
        for (ServicioOfy x: servicios) {
            datosRespuesta.add(crearServicioIndex(x));
        }
        Page<List<ServicioIndex>> r = PaginationModelFactory.getPage(datosRespuesta, "ServicioIndex", request.getRequestURI() , filtro, pagination);
        return r;
    }
    
    private ServicioIndex crearServicioIndex(ServicioOfy servicio) {
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
        AutoOfy auto = autoDAO.consultar(idAuto);
        if (auto != null) {
            nuevo.setNumeroSerie(auto.getNumeroSerie());
            nuevo.setPlacas(auto.getPlacas());
            nuevo.setTipo(auto.getTipo());
        }
        return nuevo;
    }
    
    private ServicioIndexClienteData crearServicioIndexClienteData(Long idCliente) {
        ServicioIndexClienteData nuevo = new ServicioIndexClienteData();
        nuevo.setId(idCliente);
        return nuevo;
    }

    public void setAutoDAO(AutoDAO autoDAO) {
        this.autoDAO = autoDAO;
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }    

    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }    
}
