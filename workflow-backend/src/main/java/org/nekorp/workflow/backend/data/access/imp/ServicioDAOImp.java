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

import java.util.List;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class ServicioDAOImp implements ServicioDAO {
    
    //private static Logger LOGGER = Logger.getAnonymousLogger();
    private ObjectifyFactory objectifyFactory;

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#getServicios(org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Servicio> getServicios(PaginationData<Long> pagination) {
        List<Servicio> result;
        Objectify ofy = objectifyFactory.begin();
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
    public void nuevoServicio(Servicio nuevo) {
        try {
            Objectify ofy = objectifyFactory.begin();
            ofy.save().entity(nuevo).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#getServicio(java.lang.Long)
     */
    @Override
    public Servicio getServicio(Long id) {
        Objectify ofy = objectifyFactory.begin();
        Key<Servicio> key = Key.create(Servicio.class, id);
        Servicio respuesta = ofy.load().key(key).get();
        return respuesta;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.ServicioDAO#actualizaServicio(org.nekorp.workflow.backend.model.servicio.Servicio)
     */
    @Override
    public boolean actualizaServicio(Servicio servicio) {
        if (getServicio(servicio.getId()) == null) {
            return false;
        }
        try {
            Objectify ofy = objectifyFactory.begin();
            ofy.save().entity(servicio).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public void setObjectifyFactory(ObjectifyFactory objectifyFactory) {
        this.objectifyFactory = objectifyFactory;
    }
}
