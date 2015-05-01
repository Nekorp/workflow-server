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
import org.nekorp.workflow.backend.controller.AutoController;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.model.auto.AutoOfy;

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
import technology.tikal.gae.pagination.model.PaginationDataString;
import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * @author Nekorp
 */
@RestController
@RequestMapping("/autos")
public class AutoControllerImp extends RestControllerTemplate implements AutoController {

    private AutoDAO autoDAO;
    private StringStandarizer stringStandarizer;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#getAutos(org.nekorp.workflow.backend.data.pagination.model.PaginationDataString, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<AutoOfy>> getAutos(@ModelAttribute final FiltroAuto filtro, 
            @Valid @ModelAttribute PaginationDataString pagination, final BindingResult resultPagination, 
            final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        filtro.setFiltroNumeroSerie(this.stringStandarizer.standarize(filtro.getFiltroNumeroSerie()));
        pagination.setSinceId(this.stringStandarizer.standarize(pagination.getSinceId()));
        List<AutoOfy> datos = autoDAO.consultarTodos(filtro, pagination);
        Page<List<AutoOfy>> r = PaginationModelFactory.getPage(datos, "auto", request.getRequestURI() , filtro, pagination);
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#crearAuto(org.nekorp.workflow.backend.model.auto.DatosAuto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crearAuto(@Valid @RequestBody AutoOfy auto, final BindingResult result,
            final HttpServletRequest request, final HttpServletResponse response) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        preprocesaAuto(auto);
        this.autoDAO.guardarNuevo(auto);
        response.setHeader("Location", request.getRequestURI() + "/" + auto.getNumeroSerie());
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#getAuto(java.lang.String, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{numeroSerie}", method = RequestMethod.GET)
    public AutoOfy getAuto(@PathVariable String numeroSerie) {
        AutoOfy respuesta = this.autoDAO.consultar(this.stringStandarizer.standarize(numeroSerie));
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#actualizarAuto(java.lang.Long, org.nekorp.workflow.backend.model.auto.DatosAuto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{numeroSerie}", method = RequestMethod.POST)
    public void actualizarAuto(@PathVariable final String numeroSerie, @Valid @RequestBody final AutoOfy datos, final BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        //TODO pasarlo a un update?
        AutoOfy original = autoDAO.consultar(this.stringStandarizer.standarize(numeroSerie));
        preprocesaAuto(datos);
        original.setColor(datos.getColor());
        original.setEquipamiento(datos.getEquipamiento());
        original.setMarca(datos.getMarca());
        original.setModelo(datos.getModelo());
        original.setPlacas(datos.getPlacas());
        original.setTipo(datos.getTipo());
        original.setVersion(datos.getVersion());
        autoDAO.guardar(original);
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#borrarAuto(java.lang.String, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{numeroSerie}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void borrarAuto(@PathVariable final String numeroSerie) {
        AutoOfy dato = this.autoDAO.consultar(this.stringStandarizer.standarize(numeroSerie));
        autoDAO.borrar(dato);
    }
    
    /**
     * cambios que se aplican a los datos del auto independientemente de lo que envien los sistemas.
     * @param auto el auto a modificar.
     */
    private void preprocesaAuto(final AutoOfy auto) {
        //ajusta el numero de serie
        auto.setNumeroSerie(this.stringStandarizer.standarize(auto.getNumeroSerie()));
        auto.setVin(auto.getNumeroSerie());
        //pasa las placas a mayusculas
        auto.setPlacas(StringUtils.upperCase(auto.getPlacas()));
    }

    public void setAutoDAO(AutoDAO autoDAO) {
        this.autoDAO = autoDAO;
    }

    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }
}
