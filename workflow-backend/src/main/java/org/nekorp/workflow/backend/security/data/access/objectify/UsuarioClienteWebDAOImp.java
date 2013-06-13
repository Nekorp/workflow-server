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
package org.nekorp.workflow.backend.security.data.access.objectify;

import java.util.List;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.security.data.access.UsuarioClienteWebDAO;
import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class UsuarioClienteWebDAOImp extends ObjectifyDAOTemplate implements UsuarioClienteWebDAO {

    /**{@inheritDoc}*/
    @Override
    public List<UsuarioClienteWeb> consultarTodos(FiltroBusqueda filtro, PaginationData<String> pagination) {
        List<UsuarioClienteWeb> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<UsuarioClienteWeb> query = ofy.load().type(UsuarioClienteWeb.class);
        if (pagination.getSinceId() != null) {
            Key<UsuarioClienteWeb> key = Key.create(UsuarioClienteWeb.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            UsuarioClienteWeb ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getAlias());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    /**{@inheritDoc}*/
    @Override
    public void guardar(UsuarioClienteWeb nuevo) {
        try {
            Objectify ofy = getObjectifyFactory().begin();
            ofy.save().entity(nuevo).now();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public UsuarioClienteWeb consultar(String id) {
        Objectify ofy = getObjectifyFactory().begin();
        Key<UsuarioClienteWeb> key = Key.create(UsuarioClienteWeb.class, id);
        UsuarioClienteWeb respuesta = ofy.load().key(key).now();
        return respuesta;
    }

    /**{@inheritDoc}*/
    @Override
    public boolean borrar(UsuarioClienteWeb dato) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dato);
        return true;
    }
}
