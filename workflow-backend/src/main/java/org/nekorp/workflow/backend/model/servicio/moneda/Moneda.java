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
package org.nekorp.workflow.backend.model.servicio.moneda;

import com.googlecode.objectify.annotation.Embed;


/**
 *
 * gracias java por tu perdida de precision.
 * no usar los getter y setter estan ahi para serializar :a
 */
@Embed
public class Moneda {
    
    private String value;
    
    public Moneda() {
        this.value = "0.00";
    }
    
    public Moneda(String val) {
        this.value = val;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
