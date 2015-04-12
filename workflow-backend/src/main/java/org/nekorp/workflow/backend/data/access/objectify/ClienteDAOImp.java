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
package org.nekorp.workflow.backend.data.access.objectify;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * 
 */
public class ClienteDAOImp implements ClienteDAO {
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getClientes(java.util.Map, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Cliente> consultarTodos(final FiltroCliente filtro, final PaginationData<Long> pagination) {
        List<Cliente> result;
        Query<Cliente> query =  ofy().load().type(Cliente.class);
        //Por limitaciones de appengine no se pueden hacer queries con desigualdades en dos propiedades
        if (!StringUtils.isEmpty(filtro.getFiltroNombre())) {
            String nombreBuscado = filtro.getFiltroNombre();
            query = query.filter("nombreEstandar >=", nombreBuscado).filter("nombreEstandar < ", nombreBuscado + "\uFFFD");
            if (pagination.getMaxResults() != 0) {
                query = query.limit(pagination.getMaxResults());
            }
            result = query.list();
            return result;
        } else {
            if (pagination.getSinceId() != null) {
                Key<Cliente> key = Key.create(Cliente.class, pagination.getSinceId());
                query = query.filterKey(">=", key);
            }
            if (pagination.getMaxResults() != 0) {
                //se trae uno de mas para indicar cual es la siguiente pagina
                query = query.limit(pagination.getMaxResults() + 1);
            }
            result = query.list();
            if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
                Cliente ultimo = result.get(pagination.getMaxResults());
                pagination.setNextId(ultimo.getId());
                result.remove(pagination.getMaxResults());
            }
            return result;
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#nuevoCliente(org.nekorp.workflow.backend.model.cliente.Cliente)
     */
    @Override
    public void guardar(final Cliente nuevo) {
        try {
            ofy().save().entity(nuevo).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ClienteDAO#getCliente(java.lang.String)
     */
    @Override
    public Cliente consultar(final Long id) {
        Key<Cliente> key = Key.create(Cliente.class, id);
        Cliente respuesta = ofy().load().key(key).now();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.EntityDAO#borrar(java.lang.Object)
     */
    @Override
    public boolean borrar(Cliente dato) {
        ofy().delete().entity(dato);
        return true;
    }
}
