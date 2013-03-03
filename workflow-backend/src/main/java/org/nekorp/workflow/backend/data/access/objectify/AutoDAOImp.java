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
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.auto.Auto;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class AutoDAOImp extends ObjectifyDAOTemplate implements AutoDAO {
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#getAutos(org.nekorp.workflow.backend.data.access.util.FiltroAuto, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Auto> consultarTodos(FiltroAuto filtro, PaginationData<String> pagination) {
        List<Auto> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<Auto> query =  ofy.load().type(Auto.class);
        if (!StringUtils.isEmpty(filtro.getFiltroNumeroSerie())) {
            String serieBuscado = filtro.getFiltroNumeroSerie();
            if (StringUtils.isEmpty(pagination.getSinceId())) {
                query = query.filter("numeroSerie >=", serieBuscado).filter("numeroSerie < ", serieBuscado + "\uFFFD");
            } else {
                query = query.filter("numeroSerie >=", pagination.getSinceId()).filter("numeroSerie < ", serieBuscado + "\uFFFD");
            }
        } else {
            if (!StringUtils.isEmpty(pagination.getSinceId())) {
                query = query.filter("numeroSerie >=", pagination.getSinceId());
            }
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            Auto ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getVin());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#nuevoAuto(org.nekorp.workflow.backend.model.auto.Auto)
     */
    @Override
    public void guardar(Auto nuevo) {
        try {
            Objectify ofy = getObjectifyFactory().begin();
            ofy.save().entity(nuevo).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#getAuto(java.lang.String)
     */
    @Override
    public Auto consultar(String numerSerie) {
        Objectify ofy = getObjectifyFactory().begin();
        Key<Auto> key = Key.create(Auto.class, numerSerie);
        Auto respuesta = ofy.load().key(key).get();
        return respuesta;
    }    
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.EntityDAO#borrar(java.lang.Object)
     */
    @Override
    public boolean borrar(Auto dato) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dato);
        return true;
    }
}
