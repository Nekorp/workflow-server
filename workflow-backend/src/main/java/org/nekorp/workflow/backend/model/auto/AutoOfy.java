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
package org.nekorp.workflow.backend.model.auto;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import technology.tikal.taller.automotriz.model.auto.Equipamiento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
/**
 * Se usara para identificar como unico a aun auto, para el historia.
 * para el tema de datos del auto en un servicio en especifico se usara el termino datos auto.
 * 
 * MMMM esta duplicado el codigo de auto y las demas entidades que tienen anotaciones de objectify
 * @author Nekorp
 */
@Entity(name="Auto")
@Cache
public class AutoOfy {
    @JsonIgnore // se ignora por que no se requiere mandar al cliente.
    @Id
    private String vin;
    //se duplica la propiedad vin con numeroSerie para hacer busquedas, muahahahahahaha
    @Size(min=1, max=17)
    @NotNull
    @Index //se indexa para realizar busquedas sobre este campo
    private String numeroSerie;
    @Size(min=1)
    @NotNull
    private String marca;
    @Size(min=1)
    @NotNull
    private String tipo;
    @Size(min=1)
    @NotNull
    private String version;
    @Size(min=1)
    @NotNull
    private String modelo;
    @Size(min=1)
    @NotNull
    private String color;
    @Size(min=1, max=10)
    @NotNull
    private String placas;
    @Valid
    private Equipamiento equipamiento;
    
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public Equipamiento getEquipamiento() {
        return equipamiento;
    }

    public void setEquipamiento(Equipamiento equipamiento) {
        this.equipamiento = equipamiento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AutoOfy) {
            AutoOfy other = (AutoOfy) obj;
            return Objects.equals(vin, other.vin);
        } 
        return false;
    }

    
}
