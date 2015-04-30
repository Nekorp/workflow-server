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

import org.nekorp.workflow.backend.controller.AutoIndexController;
import org.nekorp.workflow.backend.controller.ServicioIndexController;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
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
import technology.tikal.gae.pagination.model.PaginationDataString;
import technology.tikal.gae.service.template.RestControllerTemplate;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndex;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndexAutoData;

/**
 * @author Nekorp
 */
@RestController
@RequestMapping("/index")
public class IndexControllerImp extends RestControllerTemplate implements ServicioIndexController, AutoIndexController {

    private ServicioDAO servicioDAO;
    private AutoDAO autoDAO;
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
        nuevo.setIdAuto(servicio.getIdAuto());
        nuevo.setIdCliente(servicio.getIdCliente());
        nuevo.setDescripcion(servicio.getDescripcion());
        nuevo.setFechaInicio(servicio.getMetadata().getFechaInicio());
        nuevo.setId(servicio.getId());
        nuevo.setStatus(servicio.getMetadata().getStatus());
        nuevo.setCobranza(servicio.getCobranza());
        nuevo.setCostoTotal(servicio.getMetadata().getCostoTotal());
        return nuevo;
    }
    
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value = "/auto", method = RequestMethod.GET)
    public Page<List<ServicioIndexAutoData>> getAutoIndex(final FiltroAuto filtro, final PaginationDataString pagination, 
            final BindingResult resultPagination, final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        filtro.setFiltroNumeroSerie(this.stringStandarizer.standarize(filtro.getFiltroNumeroSerie()));
        pagination.setSinceId(this.stringStandarizer.standarize(pagination.getSinceId()));
        List<AutoOfy> datos = autoDAO.consultarTodos(filtro, pagination);
        List<ServicioIndexAutoData> datosRespuesta = new LinkedList<>();
        for (AutoOfy x: datos) {
            datosRespuesta.add(buildAutoIndex(x));
        }
        Page<List<ServicioIndexAutoData>> r = PaginationModelFactory.getPage(datosRespuesta, "ServicioIndexAutoData", request.getRequestURI() , filtro, pagination);
        return r;
    }

    private ServicioIndexAutoData buildAutoIndex(AutoOfy source) {
        ServicioIndexAutoData r = new ServicioIndexAutoData();
        r.setNumeroSerie(source.getNumeroSerie());
        r.setPlacas(source.getPlacas());
        r.setTipo(source.getTipo());
        return r;
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
