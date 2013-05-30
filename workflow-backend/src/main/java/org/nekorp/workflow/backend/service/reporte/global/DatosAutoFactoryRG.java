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

package org.nekorp.workflow.backend.service.reporte.global;

import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.model.auto.Auto;
import org.nekorp.workflow.backend.model.reporte.global.DatosAutoRG;
import org.nekorp.workflow.backend.model.servicio.Servicio;
/**
 *
 */
public class DatosAutoFactoryRG implements DataFactoryRG<DatosAutoRG> {

    private AutoDAO autoDAO;
    @Override
    public DatosAutoRG build(Servicio data) {
        DatosAutoRG r = new DatosAutoRG();
        Auto auto = autoDAO.consultar(data.getIdAuto());
        r.setColor(auto.getColor());
        r.setMarca(auto.getMarca());
        r.setModelo(auto.getModelo());
        r.setPlacas(auto.getPlacas());
        r.setSerie(auto.getNumeroSerie());
        r.setTipo(auto.getTipo());
        r.setVersion(auto.getVersion());
        return r;
    }
    public void setAutoDAO(AutoDAO autoDAO) {
        this.autoDAO = autoDAO;
    }
}
