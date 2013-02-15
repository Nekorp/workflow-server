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
package org.nekorp.workflow.backend.data.pagination.model;

import java.util.List;

/**
 * 
 */
public class Page<T> {

    private String tipoItems;
    private String siguienteItem;
    private String linkPaginaActual;
    private String linkSiguientePagina;
    private List<T> items;
    public String getTipoItems() {
        return tipoItems;
    }
    public void setTipoItems(String tipoItems) {
        this.tipoItems = tipoItems;
    }
    public String getSiguienteItem() {
        return siguienteItem;
    }
    public void setSiguienteItem(String siguienteItem) {
        this.siguienteItem = siguienteItem;
    }
    public String getLinkPaginaActual() {
        return linkPaginaActual;
    }
    public void setLinkPaginaActual(String linkPaginaActual) {
        this.linkPaginaActual = linkPaginaActual;
    }
    public String getLinkSiguientePagina() {
        return linkSiguientePagina;
    }
    public void setLinkSiguientePagina(String linkSiguientePagina) {
        this.linkSiguientePagina = linkSiguientePagina;
    }
    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }
}
