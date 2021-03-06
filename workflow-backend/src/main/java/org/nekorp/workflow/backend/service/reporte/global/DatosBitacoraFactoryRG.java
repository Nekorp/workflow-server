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

package org.nekorp.workflow.backend.service.reporte.global;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.model.reporte.global.DatosBitacoraRG;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
/**
 * @author Nekorp
 */
public class DatosBitacoraFactoryRG implements DataFactoryRG<DatosBitacoraRG> {

    private BitacoraDAO bitacoraDAO;
    
    @Override
    public DatosBitacoraRG build(ServicioOfy data) {
        DatosBitacoraRG r = new DatosBitacoraRG();
        List<EventoOfy> eventos = bitacoraDAO.consultar(data);
        r.setDiagnostico(calcularDiagnostico(eventos));
        r.setFechaIngresoAuto(calcularFechaIngresoAuto(eventos));
        r.setFechaEntregaAuto(calcularFechaEntregaAuto(eventos));
        r.setRecomendaciones(calcularRecomendaciones(eventos));
        return r;
    }

    private String calcularDiagnostico(List<EventoOfy> eventos) {
        String r = "";
        for (EventoOfy x: eventos) {
            if (x.getTipo().equals("EventoDiagnostico") && !StringUtils.isEmpty(x.getDescripcion())) {
                if (!StringUtils.isEmpty(r)) {
                    r = r + "\n";
                }
                r = r + x.getDescripcion();
            }
        }
        return r;
    }
    
    private Date calcularFechaIngresoAuto(List<EventoOfy> eventos) {
        for (EventoOfy x: eventos) {
            if (StringUtils.equals(x.getTipo(), "EventoEntrega") && StringUtils.equals(x.getEtiqueta(), "Entrada de Auto")) {
                return x.getFecha();
            }
        }
        return null;
    }
    
    private Date calcularFechaEntregaAuto(List<EventoOfy> eventos) {
        for (EventoOfy x: eventos) {
            if (StringUtils.equals(x.getTipo(), "EventoEntrega") && StringUtils.equals(x.getEtiqueta(), "Salida de Auto")) {
                return x.getFecha();
            }
        }
        return null;
    }
    
    private String calcularRecomendaciones(List<EventoOfy> eventos) {
        String r = "";
        for (EventoOfy x: eventos) {
            boolean esEventoGeneral = x.getTipo().equals("EventoGeneral");
            boolean esUnaRecomendacion = StringUtils.equalsIgnoreCase(StringUtils.trim(x.getEtiqueta()), "recomendaciones");
            boolean noEstaVaciaLaDescripcion = !StringUtils.isEmpty(x.getDescripcion());
            if (esEventoGeneral && esUnaRecomendacion && noEstaVaciaLaDescripcion) {
                if (!StringUtils.isEmpty(r)) {
                    r = r + "\n";
                }
                r = r + x.getDescripcion();
            }
        }
        return r;
    }

    public void setBitacoraDAO(BitacoraDAO bitacoraDAO) {
        this.bitacoraDAO = bitacoraDAO;
    }

}
