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
package org.nekorp.workflow.backend.data.access.mock;

import java.util.LinkedList;
import java.util.List;

import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.model.cliente.ClienteOfy;

import technology.tikal.gae.pagination.model.PaginationData;

/**
 * @author Nekorp
 */
@Deprecated
public class ClienteDAOMock implements ClienteDAO {

    
    private List<ClienteOfy> clientes;
    
    public ClienteDAOMock() {
        clientes = new LinkedList<ClienteOfy>();
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getClientes(java.util.Map, org.nekorp.workflow.backend.data.access.util.PaginationData)
     */
    @Override
    public List<ClienteOfy> consultarTodos(final FiltroCliente filtro, final PaginationData<Long> pagination) {
        LinkedList<ClienteOfy> datos = new LinkedList<ClienteOfy>();
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
    public void guardar(ClienteOfy nuevo) {
        Long id = this.clientes.size() + 1l;
        nuevo.setId(id);
        this.clientes.add(nuevo);
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getCliente(java.lang.String)
     */
    @Override
    public ClienteOfy consultar(Long id, Class<?>... group) {
        for (ClienteOfy x: clientes) {
            if(id == x.getId()) {
                return x;
            }
        }
        return null;
    }    

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.EntityDAO#borrar(java.lang.Object)
     */
    @Override
    public void borrar(ClienteOfy dato) {
        // TODO Auto-generated method stub
        return;
    }
}
