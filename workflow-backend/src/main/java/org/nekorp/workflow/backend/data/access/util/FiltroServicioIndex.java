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
package org.nekorp.workflow.backend.data.access.util;

import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;

/**
 * 
 */
public class FiltroServicioIndex implements FiltroBusqueda {

    private String statusServicio;
    private String numeroSerieAuto;
    public String getStatusServicio() {
        return statusServicio;
    }
    public void setStatusServicio(String statusServicio) {
        this.statusServicio = statusServicio;
    }
    public String getNumeroSerieAuto() {
        return numeroSerieAuto;
    }
    public void setNumeroSerieAuto(String numeroSerieAuto) {
        this.numeroSerieAuto = numeroSerieAuto;
    }
    
    
}