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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.model.auto.AutoOfy;

import technology.tikal.gae.pagination.model.PaginationData;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.ofy;
/**
 * @author Nekorp
 */
public class AutoDAOImp implements AutoDAO {
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#getAutos(org.nekorp.workflow.backend.data.access.util.FiltroAuto, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<AutoOfy> consultarTodos(FiltroAuto filtro, PaginationData<String> pagination) {
        List<AutoOfy> result;
        Query<AutoOfy> query =  ofy().load().type(AutoOfy.class);
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
            AutoOfy ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getVin());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#nuevoAuto(org.nekorp.workflow.backend.model.auto.Auto)
     */
    @Override
    public void guardar(AutoOfy nuevo) {
        try {
            ofy().save().entity(nuevo).now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.AutoDAO#getAuto(java.lang.String)
     */
    @Override
    public AutoOfy consultar(String numerSerie, Class<?>... group) {
        Key<AutoOfy> key = Key.create(AutoOfy.class, numerSerie);
        AutoOfy respuesta = ofy().load().key(key).safe();
        return respuesta;
    }    
    
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.template.EntityDAO#borrar(java.lang.Object)
     */
    @Override
    public void borrar(AutoOfy dato) {
        ofy().delete().entity(dato).now();
    }

    @Override
    public Map<String, AutoOfy> consultaBatch(String[] ids, Class<?>... group) {
        Map<String, AutoOfy> ths = ofy().load().type(AutoOfy.class).ids(ids);
        return ths;
    }
}
