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
package org.nekorp.workflow.backend.model.index.servicio;

import java.util.Date;

/**
 * 
 */
public class ServicioIndex {

    private Long id;
    private String status;
    private String descripcion;
    private Date fechaInicio;
    private ServicioIndexClienteData clienteData;
    private ServicioIndexAutoData autoData;
    public ServicioIndex() {
        clienteData = new ServicioIndexClienteData();
        autoData = new ServicioIndexAutoData();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Date getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public ServicioIndexClienteData getClienteData() {
        return clienteData;
    }
    public void setClienteData(ServicioIndexClienteData clienteData) {
        this.clienteData = clienteData;
    }
    public ServicioIndexAutoData getAutoData() {
        return autoData;
    }
    public void setAutoData(ServicioIndexAutoData autoData) {
        this.autoData = autoData;
    }
    
}
