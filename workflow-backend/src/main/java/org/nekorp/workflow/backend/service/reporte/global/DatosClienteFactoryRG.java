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

import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.nekorp.workflow.backend.model.reporte.global.DatosClienteRG;
import org.nekorp.workflow.backend.model.servicio.Servicio;


/**
 *
 */
public class DatosClienteFactoryRG implements DataFactoryRG<DatosClienteRG>{

    private ClienteDAO clienteDAO;
    @Override
    public DatosClienteRG build(Servicio data) {
        DatosClienteRG r = new DatosClienteRG();
        Cliente cliente = clienteDAO.consultar(data.getIdCliente());
        r.setCiudad(cliente.getDomicilio().getCiudad());
        r.setColonia(cliente.getDomicilio().getColonia());
        r.setContacto(cliente.getContacto());
        r.setDireccion(cliente.getDomicilio().getCalle());
        r.setNombre(cliente.getNombre());
        if (cliente.getTelefonoContacto().size() > 0) {
            r.setTelefono(cliente.getTelefonoContacto().get(0).getValor());
        }
        return r;
    }
    public void setClienteDAO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
}
