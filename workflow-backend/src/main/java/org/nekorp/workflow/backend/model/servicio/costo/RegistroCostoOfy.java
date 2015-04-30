/**
 *   Copyright 2012-2015 Tikal-Technology
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
package org.nekorp.workflow.backend.model.servicio.costo;

import java.util.Date;

import org.nekorp.workflow.backend.model.servicio.ServicioOfy;

import technology.tikal.taller.automotriz.model.servicio.moneda.Moneda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * 
 * @author Nekorp
 *
 */
@Entity(name="RegistroCosto")
public class RegistroCostoOfy {
    @Parent
    @JsonIgnore // se ignora por que no se requiere mandar al cliente.
    private Key<ServicioOfy> parent;
    @Id
    private Long id;
    private String tipo;
    private String subtipo;
    private String concepto;
    private Integer cantidad;
    private Moneda precioUnitario;
    private boolean precioUnitarioConIVA;
    private Moneda precioCliente;
    private boolean subtotalConIVA;
    private Date fechaCreacion;
    
    public RegistroCostoOfy() {
        fechaCreacion = new Date();
    }
    
    public Key<ServicioOfy> getParent() {
        return parent;
    }

    public void setParent(Key<ServicioOfy> parent) {
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }
    
    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Moneda getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Moneda precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Moneda getPrecioCliente() {
        return precioCliente;
    }

    public void setPrecioCliente(Moneda precioCliente) {
        this.precioCliente = precioCliente;
    }    

    public boolean isPrecioUnitarioConIVA() {
        return precioUnitarioConIVA;
    }

    public void setPrecioUnitarioConIVA(boolean precioUnitarioConIVA) {
        this.precioUnitarioConIVA = precioUnitarioConIVA;
    }

    public boolean isSubtotalConIVA() {
        return subtotalConIVA;
    }

    public void setSubtotalConIVA(boolean subtotalConIVA) {
        this.subtotalConIVA = subtotalConIVA;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegistroCostoOfy other = (RegistroCostoOfy) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}