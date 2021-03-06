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
package org.nekorp.workflow.backend.data.access.objectify;

import java.util.List;

import org.nekorp.workflow.backend.data.access.RegistroCostoDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;

import technology.tikal.gae.dao.template.FiltroBusqueda;
import technology.tikal.gae.pagination.model.PaginationData;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class RegistroCostoDAOImp implements RegistroCostoDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultarTodos(java.lang.Object, java.lang.Object, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<RegistroCostoOfy> consultarTodos(ServicioOfy parent, FiltroBusqueda filtro, PaginationData<Long> pagination) {
        Key<ServicioOfy> parentKey = Key.create(parent);
        List<RegistroCostoOfy> result;
        Query<RegistroCostoOfy> query = ofy().load().type(RegistroCostoOfy.class);
        query = query.ancestor(parentKey);
        if (pagination.getSinceId() != null) {
            Key<RegistroCostoOfy> key = Key.create(parentKey, RegistroCostoOfy.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            RegistroCostoOfy ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#guardar(java.lang.Object, java.lang.Object)
     */
    @Override
    public void guardar(ServicioOfy parent, RegistroCostoOfy nuevo) {
        Key<ServicioOfy> parentKey = Key.create(parent);
        nuevo.setParent(parentKey);
        ofy().save().entity(nuevo).now();
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#consultar(java.lang.Object, java.lang.Object)
     */
    @Override
    public RegistroCostoOfy consultar(ServicioOfy parent, Long id, Class<?>... group) {
        Key<ServicioOfy> parentKey = Key.create(parent);
        Key<RegistroCostoOfy> key = Key.create(parentKey, RegistroCostoOfy.class, id);
        RegistroCostoOfy respuesta = ofy().load().key(key).safe();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.ChildEntityDAO#borrar(java.lang.Object, java.lang.Object)
     */
    @Override
    public void borrar(ServicioOfy parent, RegistroCostoOfy dato) {
        ofy().delete().entity(dato).now();
    }

}
