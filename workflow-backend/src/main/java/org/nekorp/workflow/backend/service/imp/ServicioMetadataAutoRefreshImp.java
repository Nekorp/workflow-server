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
package org.nekorp.workflow.backend.service.imp;

import java.util.List;

import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto;
import org.nekorp.workflow.backend.service.ServicioMetadataAutoRefresh;
import org.nekorp.workflow.backend.service.ServicioMetadataFactory;

/**
 * 
 */
public class ServicioMetadataAutoRefreshImp implements ServicioMetadataAutoRefresh {

    private ServicioDAO servicioDAO;
    private ServicioMetadataFactory servicioMetadataFactory;
    
    /**{@inheritDoc}*/
    @Override
    public void actualizarUsandoId(final Long idServicio) {
        Servicio servicio = servicioDAO.consultar(idServicio);
        servicio.setMetadata(servicioMetadataFactory.calcularMetadata(servicio));
        servicioDAO.actualizarMetadata(servicio);
    }
    
    /**{@inheritDoc}*/
    @Override
    public void actualizarUsandoIdEventos(Long idServicio, List<Evento> eventos) {
        Servicio servicio = servicioDAO.consultar(idServicio);
        servicio.setMetadata(servicioMetadataFactory.calcularMetadata(servicio, eventos));
        servicioDAO.actualizarMetadata(servicio);
    }

    /**{@inheritDoc}*/
    @Override
    public void actualizarServicioMetadataInterceptor(final Servicio servicio) {
        if (servicio.getId() != null) {
            servicio.setMetadata(servicioMetadataFactory.calcularMetadata(servicio));
        } else {
            servicio.setMetadata(servicioMetadataFactory.calcularMetadata(servicio, null));
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void actualizarUsandoServicioEventos(final Servicio servicio, final List<Evento> eventos) {
        servicio.setMetadata(servicioMetadataFactory.calcularMetadata(servicio, eventos));
        servicioDAO.actualizarMetadata(servicio);
    }
    
    @Override
    public void actualizarCostoTotal(final Servicio servicio, final List<RegistroCosto> registros) {
        servicioMetadataFactory.calcularCostoMetaData(servicio, registros);
        servicioDAO.actualizarMetadata(servicio);
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setServicioMetadataFactory(ServicioMetadataFactory servicioMetadataFactory) {
        this.servicioMetadataFactory = servicioMetadataFactory;
    }
}
