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
package org.nekorp.workflow.backend.data.access.imp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.nekorp.workflow.backend.model.cliente.DomicilioFiscal;
import org.nekorp.workflow.backend.model.cliente.Telefono;

/**
 * 
 */
public class ClienteDAOMock implements ClienteDAO {

    /**
     * el numero de registros simulados.
     */
    private int numeroDeRegistros = 230;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getClientes(java.util.Map, org.nekorp.workflow.backend.data.access.util.PaginationData)
     */
    @Override
    public List<Cliente> getClientes(final Map<String, Object> filter, final PaginationData pagination) {
        LinkedList<Cliente> datos = new LinkedList<Cliente>();
        int id = 1;
        if (!StringUtils.isEmpty(pagination.getSinceId())) {
            id = Integer.parseInt(pagination.getSinceId()); 
        }
        if (pagination.getMaxResults() == 0) { //TODO
            for (int i = id; i <= numeroDeRegistros; i++) {
                datos.add(generaCliente(i + ""));
            }
        } else {//limitado
            for (int i = id; i <= numeroDeRegistros&& datos.size() < pagination.getMaxResults(); i++) {
                datos.add(generaCliente(id + ""));
                id = id + 1;
            }
            if (datos.size() == pagination.getMaxResults() && id <= numeroDeRegistros) {
                pagination.setNextId(id + "");
            }
        }
        return datos;
    }
    
    private Cliente generaCliente(String id) {
        DomicilioFiscal domicilio = new DomicilioFiscal();
        domicilio.setCalle("la calle");
        domicilio.setCiudad("la ciudad");
        domicilio.setCodigoPostal("el codigo postal");
        domicilio.setColonia("la colonia");
        domicilio.setNumInterior("el numero interior");
        Cliente nuevo = new Cliente();
        nuevo.setId(id);
        nuevo.setNombre("nombre cliente");
        nuevo.setRfc("rfc del cliente");
        nuevo.setDomicilio(domicilio);
        nuevo.setContacto("datos contacto");
        List<Telefono> telefonos = new LinkedList<Telefono>();
        nuevo.setTelefonoContacto(telefonos);
        Telefono tel = new Telefono();
        tel.setLabel("Movil");
        tel.setValor("1234567890");
        telefonos.add(tel);
        tel = new Telefono();
        tel.setLabel("Oficina");
        tel.setValor("0987654321");
        telefonos.add(tel);
        tel = new Telefono();
        tel.setLabel("Radio");
        tel.setValor("5555555555");
        telefonos.add(tel);
        return nuevo;
    }

}
