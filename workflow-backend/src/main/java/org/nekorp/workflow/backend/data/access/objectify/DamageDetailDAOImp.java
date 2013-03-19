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

import java.util.LinkedList;
import java.util.List;
import org.nekorp.workflow.backend.data.access.DamageDetailDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.auto.damage.DamageDetail;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class DamageDetailDAOImp extends ObjectifyDAOTemplate implements DamageDetailDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DamageDetail> consultar(final Long idServicio) {
        Key<Servicio> parentKey = Key.create(Servicio.class, idServicio);
        List<DamageDetail> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<DamageDetail> query =  ofy.load().type(DamageDetail.class);
        query = query.ancestor(parentKey);
        result = query.list();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DamageDetail> guardar(final Long idServicio, final List<DamageDetail> damage) {
        List<DamageDetail> oldList = this.consultar(idServicio);
        for (DamageDetail x: oldList) {
            if (!damage.contains(x)) {
                borrar(x);
            }
        }
        List<Result<Key<DamageDetail>>> nuevos = new LinkedList<Result<Key<DamageDetail>>>();
        Key<Servicio> parentKey = Key.create(Servicio.class, idServicio);
        Result<Key<DamageDetail>> result;
        Objectify ofy = getObjectifyFactory().begin();
        for (DamageDetail x: damage) {
            x.setParent(parentKey);
            result = ofy.save().entity(x);
            if (x.getId() == null) {
                nuevos.add(result);
            }
        }
        for (Result<Key<DamageDetail>> x: nuevos) {
            x.now();
        }
        return damage;
    }
    
    public void borrar(final DamageDetail dmg) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dmg);
    }
}
