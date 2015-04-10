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
package org.nekorp.workflow.backend.model.secuencia;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

/**
 * 
 */
@Entity @Unindex
public class DatosFoliadorServicio {

    @Id
    private String id;
    private Long siguienteFolio;
    
    private DatosFoliadorServicio() {
        super();
    }
    
    public DatosFoliadorServicio(String id, Long folioInicial) {
        this();
        this.id = id;
        this.siguienteFolio = folioInicial;
    }

    public Long usarSiguienteFolio() {
        Long r = siguienteFolio;
        siguienteFolio = siguienteFolio + 1;
        return r;
    }
    
}