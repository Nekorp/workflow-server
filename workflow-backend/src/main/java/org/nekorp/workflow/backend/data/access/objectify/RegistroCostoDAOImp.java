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
import org.nekorp.workflow.backend.data.access.RegistroCostoDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class RegistroCostoDAOImp extends ObjectifyDAOTemplate implements RegistroCostoDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultarTodos(java.lang.Object, java.lang.Object, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<RegistroCosto> consultarTodos(Long idParent, FiltroBusqueda filtro, PaginationData<Long> pagination) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        List<RegistroCosto> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<RegistroCosto> query = ofy.load().type(RegistroCosto.class);
        query = query.ancestor(parentKey);
        if (pagination.getSinceId() != null) {
            Key<RegistroCosto> key = Key.create(parentKey, RegistroCosto.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            RegistroCosto ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#guardar(java.lang.Object, java.lang.Object)
     */
    @Override
    public void guardar(Long idParent, RegistroCosto nuevo) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        nuevo.setParent(parentKey);
        Objectify ofy = getObjectifyFactory().begin();
        ofy.save().entity(nuevo).now();
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultar(java.lang.Object, java.lang.Object)
     */
    @Override
    public RegistroCosto consultar(Long idParent, Long id) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idParent);
        Key<RegistroCosto> key = Key.create(parentKey, RegistroCosto.class, id);
        Objectify ofy = getObjectifyFactory().begin();
        RegistroCosto respuesta = ofy.load().key(key).now();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#borrar(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean borrar(Long idParent, RegistroCosto dato) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dato);
        return true;
    }

}
