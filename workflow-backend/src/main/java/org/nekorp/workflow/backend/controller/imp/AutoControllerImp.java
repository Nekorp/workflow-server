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
import org.nekorp.workflow.backend.controller.AutoController;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.data.access.util.StringStandarizer;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataString;
import org.nekorp.workflow.backend.model.auto.Auto;
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
@RequestMapping("/auto")
public class AutoControllerImp implements AutoController {

    private AutoDAO autoDAO;
    private StringStandarizer stringStandarizer;
    private PaginationModelFactory pagFactory;
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#getAutos(org.nekorp.workflow.backend.data.pagination.model.PaginationDataString, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Auto, String> getAutos(
            @ModelAttribute final FiltroAuto filtro, 
            @Valid @ModelAttribute PaginationDataString pagination,
            HttpServletResponse response) {
        String filtroOriginal = filtro.getFiltroNumeroSerie();
        filtro.setFiltroNumeroSerie(this.stringStandarizer.standarize(filtroOriginal));
        List<Auto> datos = autoDAO.getAutos(filtro, pagination);
        Page<Auto, String> r = pagFactory.getPage();
        r.setTipoItems("auto");
        r.setLinkPaginaActual(armaUrl(filtroOriginal, pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl(filtroOriginal, pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#crearAuto(org.nekorp.workflow.backend.model.auto.DatosAuto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearAuto(@Valid @RequestBody Auto auto, HttpServletResponse response) {
        preprocesaAuto(auto);
        this.autoDAO.nuevoAuto(auto);
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/auto/" + auto.getNumeroSerie());
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#getAuto(java.lang.String, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{numeroSerie}", method = RequestMethod.GET)
    public @ResponseBody Auto getAuto(@PathVariable String numeroSerie, HttpServletResponse response) {
        Auto respuesta = this.autoDAO.getAuto(numeroSerie);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.AutoController#actualizarAuto(java.lang.Long, org.nekorp.workflow.backend.model.auto.DatosAuto, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @RequestMapping(value="/{numeroSerie}", method = RequestMethod.POST)
    public void actualizarAuto(
            @PathVariable String numeroSerie, 
            @Valid @RequestBody Auto datos,
            HttpServletResponse response) {
        //no me importa lo que manden lo que vale es lo que viene en el path
        datos.setNumeroSerie(numeroSerie);
        preprocesaAuto(datos);
        if (!this.autoDAO.actualizaAuto(datos)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
    
    /**
     * cambios que se aplican a los datos del auto independientemente de lo que envien los sistemas.
     * @param auto el auto a modificar.
     */
    private void preprocesaAuto(final Auto auto) {
        //ajusta el numero de serie
        auto.setNumeroSerie(this.stringStandarizer.standarize(auto.getNumeroSerie()));
        auto.setVin(auto.getNumeroSerie());
    }
    
    private String armaUrl(final String filtroNumeroSerie, final String sinceId, final int maxResults) {
        String r = "/auto";
        r = addUrlParameter(r, "filtroNumeroSerie", filtroNumeroSerie);
        r = addUrlParameter(r, "sinceId", sinceId);
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

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }

    public void setStringStandarizer(StringStandarizer stringStandarizer) {
        this.stringStandarizer = stringStandarizer;
    }
    
}
