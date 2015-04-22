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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class BitacoraDAOImp implements BitacoraDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.EventoDAO#getEventos(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<EventoOfy> consultar(final ServicioOfy servicio) {
        Key<ServicioOfy> parentKey = Key.create(servicio);
        List<EventoOfy> result;
        Query<EventoOfy> query =  ofy().load().type(EventoOfy.class);
        query = query.ancestor(parentKey);
        result = query.list();
        Collections.sort(result);
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.EventoDAO#saveEventos(java.lang.Long, java.util.List)
     */
    @Override
    public List<EventoOfy> guardar(ServicioOfy servicio, final List<EventoOfy> eventos) {
        List<EventoOfy> oldEventos = this.consultar(servicio);
        for (EventoOfy x: oldEventos) {
            if (!eventos.contains(x)) {
                borrarEvento(x);
            }
        }
        List<Result<Key<EventoOfy>>> nuevos = new LinkedList<Result<Key<EventoOfy>>>();
        Key<ServicioOfy> parentKey = Key.create(servicio);
        Result<Key<EventoOfy>> result;
        for (EventoOfy x: eventos) {
            x.setParent(parentKey);
            result = ofy().save().entity(x);
            if (x.getId() == null) {
                nuevos.add(result);
            }
        }
        for (Result<Key<EventoOfy>> x: nuevos) {
            x.now();
        }
        return eventos;
    }
    
    public void borrarEvento(final EventoOfy evento) {
        ofy().delete().entity(evento);
    }
}
