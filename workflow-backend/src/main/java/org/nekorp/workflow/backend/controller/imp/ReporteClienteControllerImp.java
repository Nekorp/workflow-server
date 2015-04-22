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

import org.nekorp.workflow.backend.controller.ReporteClienteController;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.reporte.cliente.ReporteCliente;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.service.reporte.cliente.ReporteClienteDataFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.googlecode.objectify.NotFoundException;

import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * @author Nekorp
 */
@RestController
@RequestMapping("/reportes/cliente/{idCliente}")
public class ReporteClienteControllerImp extends RestControllerTemplate implements ReporteClienteController {

    private ServicioDAO servicioDao;
    private ReporteClienteDataFactory dataFactory;
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}", method = RequestMethod.GET)
    public ReporteCliente getDatosReporte(@PathVariable Long idCliente, @PathVariable Long idServicio) {
        ServicioOfy servicio = servicioDao.consultar(idServicio);
        if (servicio.getIdCliente().longValue() != idCliente.longValue()) {
            throw new NotFoundException();
        }
        return dataFactory.getData(servicio);
    }

    public void setServicioDao(ServicioDAO servicioDao) {
        this.servicioDao = servicioDao;
    }

    public void setDataFactory(ReporteClienteDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }    
}
