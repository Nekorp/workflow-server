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

import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.data.access.DamageDetailDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataString;
import org.nekorp.workflow.backend.model.auto.Auto;
import org.nekorp.workflow.backend.model.cliente.Cliente;
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
    private BitacoraDAO bitacoraDAO;
    private CostoDAO costoDAO;
    private DamageDetailDAO damageDetailDAO;
    private AutoDAO autoDAO;
    private ClienteDAO clienteDAO;
    
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
    
    @RequestMapping(value="/migrarDatos", method = RequestMethod.GET)
    public @ResponseBody void migrarDatos() {
        int paginationSize = 10;
        BatchController.log.info("Iniciando Proceso de migracion de datos");
        PaginationDataLong p = new PaginationDataLong();
        p.setMaxResults(paginationSize);
        int countOk = 0;
        do {
            p.setSinceId(p.getNextId());
            p.setNextId(null);
            List<Servicio> servicios = servicioDAO.consultarTodos(null, p);
            for (Servicio x: servicios) {
                try {
                    servicioDAO.guardar(x);
                    bitacoraDAO.guardar(x.getId(), bitacoraDAO.consultar(x.getId()));
                    costoDAO.guardar(x.getId(), costoDAO.consultar(x.getId()));
                    damageDetailDAO.guardar(x.getId(), damageDetailDAO.consultar(x.getId()));
                    countOk = countOk + 1;
                } catch (Exception e) {
                    BatchController.log.severe("error al actualizar el servicio: " + x.getId() + " " + e.getMessage());
                }
            }
        } while (p.getNextId() != null);
        BatchController.log.info("se procesaron exitosamente " + countOk + " servicios");
        
        PaginationDataString ps = new PaginationDataString();
        ps.setMaxResults(paginationSize);
        countOk = 0;
        do {
            ps.setSinceId(ps.getNextId());
            ps.setNextId(null);
            List<Auto> autos = autoDAO.consultarTodos(new FiltroAuto(), ps);
            for (Auto x: autos) {
                try {
                    autoDAO.guardar(x);
                    countOk = countOk + 1;
                } catch (Exception e) {
                    BatchController.log.severe("error al actualizar el auto: " + x.getNumeroSerie() + " " + e.getMessage());
                }
            }
        } while (ps.getNextId() != null);
        BatchController.log.info("se procesaron exitosamente " + countOk + " autos");
        
        p = new PaginationDataLong();
        p.setMaxResults(paginationSize);
        countOk = 0;
        do {
            p.setSinceId(p.getNextId());
            p.setNextId(null);
            List<Cliente> clientes = clienteDAO.consultarTodos(new FiltroCliente(), p);
            for (Cliente x: clientes) {
                try {
                    clienteDAO.guardar(x);
                    countOk = countOk + 1;
                } catch (Exception e) {
                    BatchController.log.severe("error al actualizar el cliente: " + x.getId() + " " + e.getMessage());
                }
            }
        } while (p.getNextId() != null);
        BatchController.log.info("se procesaron exitosamente " + countOk + " clientes");
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setServicioMetadataFactory(
            ServicioMetadataFactory servicioMetadataFactory) {
        this.servicioMetadataFactory = servicioMetadataFactory;
    }

    public void setBitacoraDAO(BitacoraDAO bitacoraDAO) {
        this.bitacoraDAO = bitacoraDAO;
    }

    public void setCostoDAO(CostoDAO costoDAO) {
        this.costoDAO = costoDAO;
    }

    public void setDamageDetailDAO(DamageDetailDAO damageDetailDAO) {
        this.damageDetailDAO = damageDetailDAO;
    }

    public void setAutoDAO(AutoDAO autoDAO) {
        this.autoDAO = autoDAO;
    }

    public void setClienteDAO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
}
