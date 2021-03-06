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
package org.nekorp.workflow.backend.model.servicio.auto.damage;

import org.nekorp.workflow.backend.model.servicio.ServicioOfy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * @author Nekorp
 */
@Entity(name="DamageDetail")
public class DamageDetailOfy {

    @Parent
    @JsonIgnore // se ignora por que no se requiere mandar al cliente.
    private Key<ServicioOfy> parent;
    @Id
    private Long id;
    private String esquema;
    private String caracteristica;
    private String categoria;
    private int x;
    private int y;
    
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
    public String getEsquema() {
        return esquema;
    }
    public void setEsquema(String esquema) {
        this.esquema = esquema;
    }
    public String getCaracteristica() {
        return caracteristica;
    }
    public void setCaracteristica(String caracteristica) {
        this.caracteristica = caracteristica;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
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
        DamageDetailOfy other = (DamageDetailOfy) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    
}
