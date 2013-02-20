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
package org.nekorp.workflow.backend.data.access.template;

import java.util.List;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;

/**
 * Template para los daos.
 * @param <T> el tipo de entidad que maneja el dao.
 * @param <K> el tipo de la llave de la entidad que maneja el dao.
 * @param <P> el tipo de la llave del padre.
 * @param <F> el tipo del filtro.
 */
public interface ChildEntityDAO<T,K,P,F> {

    List<T> consultarTodos(P idParent, F filtro, PaginationData<K> pagination);
    
    void guardar(P idParent, T nuevo);
    
    T consultar(P idParent, K id);
    
    boolean actualizar(P idParent, T dato);
    
    boolean borrar(P idParent, T dato);
}
