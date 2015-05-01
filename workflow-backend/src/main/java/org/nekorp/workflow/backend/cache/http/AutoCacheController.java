/**
 *   Copyright 2015 Tikal-Technology
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
package org.nekorp.workflow.backend.cache.http;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.model.auto.AutoOfy;

import technology.tikal.gae.http.cache.AbstractCacheController;
import technology.tikal.gae.http.cache.UpdatePair;

/**
 * Controla el cache del auto.
 * @author Nekorp
 *
 */
public class AutoCacheController extends AbstractCacheController<AutoOfy>  {

    @Override
    public boolean haveChanges(UpdatePair<AutoOfy> pair) {
        AutoOfy original = pair.getOriginal();
        AutoOfy actualizado = pair.getUpdated();
        if (!StringUtils.equals(original.getNumeroSerie(), actualizado.getNumeroSerie())) {
            throw new IllegalArgumentException();
        }
        if (!StringUtils.equals(original.getPlacas(), actualizado.getPlacas())) {
            return true;
        }
        if (!StringUtils.equals(original.getTipo(), actualizado.getTipo())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isTypeOf(Object retVal) {
        return retVal instanceof AutoOfy;
    }

    @Override
    public UpdatePair<AutoOfy> cloneObject(Object retVal) {
        AutoOfy actual = (AutoOfy) retVal;
        AutoOfy copia = new AutoOfy();
        copia.setNumeroSerie(actual.getNumeroSerie());
        copia.setPlacas(actual.getPlacas());
        copia.setTipo(actual.getTipo());
        return new UpdatePair<AutoOfy>(copia, actual);
    }    
}
