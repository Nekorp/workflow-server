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
package org.nekorp.workflow.backend.batch.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.service.ServicioMetadataFactory;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import technology.tikal.gae.pagination.model.PaginationDataLong;
import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * @author Nekorp
 */
@RestController
public class BatchController extends RestControllerTemplate {

    private static final Logger log = Logger.getLogger(BatchController.class.getName());
    
    private ServicioDAO servicioDAO;
    private CostoDAO costoDAO;
    private ServicioMetadataFactory servicioMetadataFactory;
    
    @RequestMapping(value="/actualizarServicioMetadata", method = RequestMethod.GET)
    public @ResponseBody void actualizarServicioMetadata() {
        BatchController.log.info("Iniciando Proceso batch de actualizacion del metadata de los servicios");
        PaginationDataLong p = new PaginationDataLong();
        List<ServicioOfy> servicios = servicioDAO.consultarTodos(null, p);
        BatchController.log.info("Se procesaran " + servicios.size() + " servicos");
        int countOk = 0;
        for (ServicioOfy x: servicios) {
            try {
                x.setMetadata(servicioMetadataFactory.calcularMetadata(x));
                servicioDAO.actualizarMetadata(x);
                countOk = countOk + 1;
            } catch (Exception e) {
                BatchController.log.severe("error al actualizar el servicio: " + x.getId() + " " + e.getMessage());
            }
        }
        BatchController.log.info("se procesaron exitosamente " + countOk + " servicios");
    }
    
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/actualizarCostoTotal", method = RequestMethod.GET)
    public PaginationDataLong actualizarCostoTotal(@Valid @ModelAttribute final PaginationDataLong pagination) {
        BatchController.log.warning("Iniciando Proceso batch de actualizacion de los costos Totales");
        int countOk = 0;
        List<ServicioOfy> servicios = servicioDAO.consultarTodos(null, pagination);
        for (ServicioOfy x : servicios) {
            try {
                servicioMetadataFactory.calcularCostoMetaData(x, costoDAO.consultar(x));
                servicioDAO.actualizarMetadata(x);
                countOk = countOk + 1;
            } catch (Exception e) {
                BatchController.log.severe("error al actualizar el servicio: " + x.getId() + " " + e.getMessage());
                break;
            }
        }
        BatchController.log.warning("se procesaron exitosamente " + countOk + " servicios");
        return pagination;
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setCostoDAO(CostoDAO costoDAO) {
        this.costoDAO = costoDAO;
    }

    public void setServicioMetadataFactory(
            ServicioMetadataFactory servicioMetadataFactory) {
        this.servicioMetadataFactory = servicioMetadataFactory;
    }
}
