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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class BitacoraDAOImp extends ObjectifyDAOTemplate implements BitacoraDAO {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.EventoDAO#getEventos(java.lang.Long, org.nekorp.workflow.backend.data.pagination.model.PaginationData)
     */
    @Override
    public List<Evento> consultar(final Long idServicio) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idServicio);
        List<Evento> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<Evento> query =  ofy.load().type(Evento.class);
        query = query.ancestor(parentKey);
        result = query.list();
        Collections.sort(result);
        return result;
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.access.EventoDAO#saveEventos(java.lang.Long, java.util.List)
     */
    @Override
    public List<Evento> guardar(final Long idServicio, final List<Evento> eventos) {
        List<Evento> oldEventos = this.consultar(idServicio);
        for (Evento x: oldEventos) {
            if (!eventos.contains(x)) {
                borrarEvento(x);
            }
        }
        List<Result<Key<Evento>>> nuevos = new LinkedList<Result<Key<Evento>>>();
        Key<Servicio> parentKey = Key.create(Servicio.class, idServicio);
        Result<Key<Evento>> result;
        Objectify ofy = getObjectifyFactory().begin();
        for (Evento x: eventos) {
            x.setParent(parentKey);
            result = ofy.save().entity(x);
            if (x.getId() == null) {
                nuevos.add(result);
            }
        }
        for (Result<Key<Evento>> x: nuevos) {
            x.now();
        }
        return eventos;
    }
    
    public void borrarEvento(final Evento evento) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(evento);
    }
}
