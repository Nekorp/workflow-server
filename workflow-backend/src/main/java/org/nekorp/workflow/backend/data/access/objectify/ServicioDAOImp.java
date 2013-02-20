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
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class ServicioDAOImp extends ObjectifyDAOTemplate implements ServicioDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#getServicios(org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Servicio> consultarTodos(FiltroBusqueda filtro, PaginationData<Long> pagination) {
        List<Servicio> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<Servicio> query =  ofy.load().type(Servicio.class);
        if (pagination.getSinceId() != null) {
            Key<Servicio> key = Key.create(Servicio.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            Servicio ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#nuevoServicio(org.nekorp.workflow.backend.model.servicio.Servicio)
     */
    @Override
    public void guardar(Servicio nuevo) {
        try {
            Objectify ofy = getObjectifyFactory().begin();
            ofy.save().entity(nuevo).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#getServicio(java.lang.Long)
     */
    @Override
    public Servicio consultar(Long id) {
        Objectify ofy = getObjectifyFactory().begin();
        Key<Servicio> key = Key.create(Servicio.class, id);
        Servicio respuesta = ofy.load().key(key).get();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#actualizaServicio(org.nekorp.workflow.backend.model.servicio.Servicio)
     */
    @Override
    public boolean actualizar(Servicio servicio) {
        if (consultar(servicio.getId()) == null) {
            return false;
        }
        try {
            Objectify ofy = getObjectifyFactory().begin();
            ofy.save().entity(servicio).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.EntityDAO#borrar(java.lang.Object)
     */
    @Override
    public boolean borrar(Servicio dato) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dato);
        return true;
    }
}
