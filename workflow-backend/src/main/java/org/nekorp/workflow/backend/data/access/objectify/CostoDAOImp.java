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

import java.util.LinkedList;
import java.util.List;
import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class CostoDAOImp implements CostoDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.CostoDAO#getEventos(java.lang.Long)
     */
    @Override
    public List<RegistroCostoOfy> consultar(ServicioOfy servicio) {
        Key<ServicioOfy> parentKey = Key.create(servicio);
        List<RegistroCostoOfy> result;
        Query<RegistroCostoOfy> query =  ofy().load().type(RegistroCostoOfy.class);
        query = query.ancestor(parentKey);
        result = query.list();
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.CostoDAO#saveEventos(java.lang.Long, java.util.List)
     */
    @Override
    public List<RegistroCostoOfy> guardar(ServicioOfy servicio, final List<RegistroCostoOfy> registros) {
        List<RegistroCostoOfy> oldRegistros = this.consultar(servicio);
        for (RegistroCostoOfy x: oldRegistros) {
            if (!registros.contains(x)) {
                borrarRegistro(x);
            }
        }
        List<Result<Key<RegistroCostoOfy>>> nuevos = new LinkedList<Result<Key<RegistroCostoOfy>>>();
        Key<ServicioOfy> parentKey = Key.create(ServicioOfy.class, servicio.getId());
        Result<Key<RegistroCostoOfy>> result;
        for (RegistroCostoOfy x: registros) {
            x.setParent(parentKey);
            result = ofy().save().entity(x);
            if (x.getId() == null) {
                nuevos.add(result);
            }
        }
        for (Result<Key<RegistroCostoOfy>> x: nuevos) {
            x.now();
        }
        return registros;
    }   
    
    private void borrarRegistro(final RegistroCostoOfy registro) {
        ofy().delete().entity(registro);
    }
}
