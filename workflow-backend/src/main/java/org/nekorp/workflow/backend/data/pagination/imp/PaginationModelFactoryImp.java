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
package org.nekorp.workflow.backend.data.pagination.imp;

import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;

/**
 * 
 */
public class PaginationModelFactoryImp implements PaginationModelFactory {

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.pagination.PaginationModelFactory#getPage()
     */
    @Override
    public <T> Page<T> getPage() {
        return new Page<T>();
    }

    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.data.pagination.PaginationModelFactory#getPaginationData()
     */
    @Override
    public PaginationData getPaginationData() {
        return new PaginationData();
    }

}
