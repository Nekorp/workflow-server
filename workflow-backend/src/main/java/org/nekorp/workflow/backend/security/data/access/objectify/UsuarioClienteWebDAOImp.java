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
package org.nekorp.workflow.backend.security.data.access.objectify;

import java.util.List;

import org.nekorp.workflow.backend.security.data.access.UsuarioClienteWebDAO;
import org.nekorp.workflow.backend.security.model.web.UsuarioClienteWeb;

import technology.tikal.gae.dao.template.FiltroBusqueda;
import technology.tikal.gae.pagination.model.PaginationData;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class UsuarioClienteWebDAOImp implements UsuarioClienteWebDAO {

    /**{@inheritDoc}*/
    @Override
    public List<UsuarioClienteWeb> consultarTodos(FiltroBusqueda filtro, PaginationData<String> pagination) {
        List<UsuarioClienteWeb> result;
        Query<UsuarioClienteWeb> query = ofy().load().type(UsuarioClienteWeb.class);
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
            ofy().save().entity(nuevo).now();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public UsuarioClienteWeb consultar(String id, Class<?>... group) {
        Key<UsuarioClienteWeb> key = Key.create(UsuarioClienteWeb.class, id);
        UsuarioClienteWeb respuesta = ofy().load().key(key).safe();
        return respuesta;
    }

    /**{@inheritDoc}*/
    @Override
    public void borrar(UsuarioClienteWeb dato) {
        ofy().delete().entity(dato).now();
    }
}
