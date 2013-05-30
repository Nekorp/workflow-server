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

import javax.servlet.http.HttpServletResponse;

import org.nekorp.workflow.backend.controller.ReporteGlobalController;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.reporte.global.RenglonRG;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.service.reporte.global.RenglonFactoryRG;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
@RequestMapping("/reportes/global/renglones/servicio")
public class ReporteGlobalControllerImp implements ReporteGlobalController {

    private ServicioDAO servicioDAO;
    private RenglonFactoryRG renglonFactoryRG;
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{idServicio}", method = RequestMethod.GET)
    @ResponseBody public RenglonRG getRenglon(@PathVariable final Long idServicio, final HttpServletResponse response) {
        Servicio servicio = servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        RenglonRG r = renglonFactoryRG.build(servicio);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }
    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }
    public void setRenglonFactoryRG(RenglonFactoryRG renglonFactoryRG) {
        this.renglonFactoryRG = renglonFactoryRG;
    }
}
