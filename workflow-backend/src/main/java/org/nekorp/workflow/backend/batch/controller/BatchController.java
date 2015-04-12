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
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.service.ServicioMetadataFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Nekorp
 */
@Controller
public class BatchController {

    private static final Logger log = Logger.getLogger(BatchController.class.getName());
    
    private ServicioDAO servicioDAO;
    private ServicioMetadataFactory servicioMetadataFactory;
    
    @RequestMapping(value="/actualizarServicioMetadata", method = RequestMethod.GET)
    public @ResponseBody void actualizarServicioMetadata() {
        BatchController.log.info("Iniciando Proceso batch de actualizacion del metadata de los servicios");
        PaginationDataLong p = new PaginationDataLong();
        List<Servicio> servicios = servicioDAO.consultarTodos(null, p);
        BatchController.log.info("Se procesaran " + servicios.size() + " servicos");
        int countOk = 0;
        for (Servicio x: servicios) {
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

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setServicioMetadataFactory(
            ServicioMetadataFactory servicioMetadataFactory) {
        this.servicioMetadataFactory = servicioMetadataFactory;
    }
}
