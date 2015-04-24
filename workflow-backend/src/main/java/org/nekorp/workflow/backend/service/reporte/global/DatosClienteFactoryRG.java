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

import org.nekorp.workflow.backend.model.reporte.global.DatosClienteRG;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;


/**
 * @author Nekorp
 */
public class DatosClienteFactoryRG implements DataFactoryRG<DatosClienteRG>{

    @Override
    public DatosClienteRG build(ServicioOfy data) {
        DatosClienteRG r = new DatosClienteRG();
        r.setCiudad("");
        r.setColonia("");
        r.setContacto("");
        r.setDireccion("");
        r.setNombre("");
        r.setTelefono("");
        return r;
    }
}
