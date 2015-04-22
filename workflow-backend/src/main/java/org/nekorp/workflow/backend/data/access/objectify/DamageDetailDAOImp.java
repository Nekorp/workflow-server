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
import org.nekorp.workflow.backend.data.access.DamageDetailDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.auto.damage.DamageDetailOfy;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class DamageDetailDAOImp implements DamageDetailDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DamageDetailOfy> consultar(final ServicioOfy servicio) {
        Key<ServicioOfy> parentKey = Key.create(servicio);
        List<DamageDetailOfy> result;
        Query<DamageDetailOfy> query =  ofy().load().type(DamageDetailOfy.class);
        query = query.ancestor(parentKey);
        result = query.list();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DamageDetailOfy> guardar(final ServicioOfy servicio, final List<DamageDetailOfy> damage) {
        List<DamageDetailOfy> oldList = this.consultar(servicio);
        for (DamageDetailOfy x: oldList) {
            if (!damage.contains(x)) {
                borrar(x);
            }
        }
        List<Result<Key<DamageDetailOfy>>> nuevos = new LinkedList<Result<Key<DamageDetailOfy>>>();
        Key<ServicioOfy> parentKey = Key.create(servicio);
        Result<Key<DamageDetailOfy>> result;
        for (DamageDetailOfy x: damage) {
            x.setParent(parentKey);
            result = ofy().save().entity(x);
            if (x.getId() == null) {
                nuevos.add(result);
            }
        }
        for (Result<Key<DamageDetailOfy>> x: nuevos) {
            x.now();
        }
        return damage;
    }
    
    public void borrar(final DamageDetailOfy dmg) {
        ofy().delete().entity(dmg);
    }
}
