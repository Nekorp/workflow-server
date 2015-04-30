/**
 *   Copyright 2012-2013 Nekorp
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
package org.nekorp.workflow.backend.model.cliente;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import technology.tikal.taller.automotriz.model.cliente.DomicilioFiscal;
import technology.tikal.taller.automotriz.model.cliente.Telefono;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * @author Nekorp
 *
 */
@Entity(name="Cliente")
@Cache
@Deprecated
public class ClienteOfy {
    @Id
    private Long id;
    @NotNull
    private String nombre;
    /**
     * Esta propiedad se usa para pasar el nombre a mayusculas
     * y quitar caracteres especiales de tal manera que al hacer
     * busquedas por el nombre se obtengan un mayor numero de ocurrencias.
     */
    @JsonIgnore // se ignora por que no se requiere mandar al cliente.
    @Index //se indexa para realizar busquedas sobre este campo
    private String nombreEstandar;
    @Size(max=13)
    private String rfc;
    private DomicilioFiscal domicilio;
    private String email;
    private String contacto;
    @Size(max=3)
    private List<Telefono> telefonoContacto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public DomicilioFiscal getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(DomicilioFiscal domicilio) {
        this.domicilio = domicilio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public List<Telefono> getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(List<Telefono> telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getNombreEstandar() {
        return nombreEstandar;
    }

    public void setNombreEstandar(String nombreEstandar) {
        this.nombreEstandar = nombreEstandar;
    }
    
}
