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
package org.nekorp.workflow.backend.model.servicio;

import java.util.ArrayList;
import java.util.List;

import org.nekorp.workflow.backend.model.servicio.metadata.ServicioMetadataOfy;

import technology.tikal.taller.automotriz.model.cobranza.DatosCobranza;
import technology.tikal.taller.automotriz.model.servicio.auto.DatosAuto;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * @author Nekorp
 *
 */
@Entity(name="Servicio")
@Cache
public class ServicioOfy {
    @Id
    private Long id;
    @Index
    private Long idCliente;
    @Index
    private String idAuto;
    private String descripcion;
    private DatosAuto datosAuto;
    private ServicioMetadataOfy metadata;
    private DatosCobranza cobranza;
    private List<String> gruposCosto;
    public ServicioOfy() {
        this.cobranza = new DatosCobranza();
        this.metadata = new ServicioMetadataOfy();
        this.gruposCosto = new ArrayList<>();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdAuto() {
        return idAuto;
    }

    public void setIdAuto(String idAuto) {
        this.idAuto = idAuto;
    }   
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public DatosAuto getDatosAuto() {
        return datosAuto;
    }

    public void setDatosAuto(DatosAuto datosAuto) {
        this.datosAuto = datosAuto;
    }

    public ServicioMetadataOfy getMetadata() {
        return metadata;
    }

    public void setMetadata(ServicioMetadataOfy metadata) {
        this.metadata = metadata;
    }
    public DatosCobranza getCobranza() {
        return cobranza;
    }
    public void setCobranza(DatosCobranza cobranza) {
        this.cobranza = cobranza;
    }
    public List<String> getGruposCosto() {
        ArrayList<String> r = new ArrayList<>();
        for (String x: this.gruposCosto) {
            r.add(x);
        }
        return r;
    }

    public void setGruposCosto(List<String> gruposCosto) {
        this.gruposCosto = new ArrayList<>();
        for (String x: gruposCosto) {
            this.gruposCosto.add(x);
        }
    }
}
