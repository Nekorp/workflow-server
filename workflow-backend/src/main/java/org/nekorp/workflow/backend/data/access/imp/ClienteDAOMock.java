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

/**
 * 
 */
public class ClienteDAOMock implements ClienteDAO {

    
    private List<Cliente> clientes;
    
    public ClienteDAOMock() {
        clientes = new LinkedList<Cliente>();
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getClientes(java.util.Map, org.nekorp.workflow.backend.data.access.util.PaginationData)
     */
    @Override
    public List<Cliente> getClientes(final Map<String, Object> filter, final PaginationData pagination) {
        LinkedList<Cliente> datos = new LinkedList<Cliente>();
        if (pagination.getMaxResults() == 0) { //todos 
            return clientes;
        } else {//limitado
            int index = 0;
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId().equals(pagination.getSinceId())) {
                    index = i;
                    break;
                }
            }
            for (int i = index; i < clientes.size() && datos.size() < pagination.getMaxResults(); i++) {
                datos.add(clientes.get(i));
                index = index + 1;
            }
            if (datos.size() == pagination.getMaxResults() && index < datos.size() ) {
                pagination.setNextId(datos.get(index).getId());
            }
        }
        return datos;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#nuevoCliente(org.nekorp.workflow.backend.model.cliente.Cliente)
     */
    @Override
    public void nuevoCliente(Cliente nuevo) {
        int id = this.clientes.size() + 1;
        nuevo.setId(id + "");
        this.clientes.add(nuevo);
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getCliente(java.lang.String)
     */
    @Override
    public Cliente getCliente(String id) {
        for (Cliente x: clientes) {
            if(StringUtils.equals(id, x.getId())) {
                return x;
            }
        }
        return null;
    }
}
