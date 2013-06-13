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
package org.nekorp.workflow.backend.model.auto;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Unindex;

@Embed @Unindex
public class Equipamiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transmision;
    private String elevadores;
    private String bolsasDeAire;
    private boolean aireAcondicionado;
    private List<String> equipoAdicional;
    
    public String getTransmision() {
        return transmision;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public String getElevadores() {
        return elevadores;
    }

    public void setElevadores(String elevadores) {
        this.elevadores = elevadores;
    }

    public String getBolsasDeAire() {
        return bolsasDeAire;
    }

    public void setBolsasDeAire(String bolsasDeAire) {
        this.bolsasDeAire = bolsasDeAire;
    }

    public boolean isAireAcondicionado() {
        return aireAcondicionado;
    }

    public void setAireAcondicionado(boolean aireAcondicionado) {
        this.aireAcondicionado = aireAcondicionado;
    }

    public List<String> getEquipoAdicional() {
        return this.equipoAdicional;
    }

    public void setEquipoAdicional(List<String> equipoAdicional) {
        this.equipoAdicional = equipoAdicional;
    }
}
