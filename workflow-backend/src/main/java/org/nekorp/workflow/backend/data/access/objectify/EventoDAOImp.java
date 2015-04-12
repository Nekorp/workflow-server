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
import org.nekorp.workflow.backend.data.access.EventoDAO;
import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * 
 */
public class EventoDAOImp implements EventoDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultarTodos(java.lang.Object, java.lang.Object, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Evento> consultarTodos(Long idParent, FiltroBusqueda filtro,
            PaginationData<Long> pagination) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        List<Evento> result;
        Query<Evento> query = ofy().load().type(Evento.class);
        query = query.ancestor(parentKey);
        if (pagination.getSinceId() != null) {
            Key<Evento> key = Key.create(parentKey, Evento.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            Evento ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#guardar(java.lang.Object, java.lang.Object)
     */
    @Override
    public void guardar(Long idParent, Evento nuevo) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        nuevo.setParent(parentKey);
        ofy().save().entity(nuevo).now();
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultar(java.lang.Object, java.lang.Object)
     */
    @Override
    public Evento consultar(Long idParent, Long id) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        Key<Evento> key = Key.create(parentKey, Evento.class, id);
        Evento respuesta = ofy().load().key(key).now();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#borrar(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean borrar(Long idParent, Evento dato) {
        ofy().delete().entity(dato);
        return true;
    }
}
