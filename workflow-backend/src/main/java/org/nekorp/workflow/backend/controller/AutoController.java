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
package org.nekorp.workflow.backend.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nekorp.workflow.backend.data.access.util.FiltroAuto;
import org.nekorp.workflow.backend.model.auto.AutoOfy;
import org.springframework.validation.BindingResult;

import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataString;

/**
 * @author Nekorp
 */
public interface AutoController {

    Page<List<AutoOfy>> getAutos(FiltroAuto filtro, PaginationDataString pagination, BindingResult resultPagination, HttpServletRequest request);

    void crearAuto(AutoOfy auto, BindingResult result, HttpServletRequest request, HttpServletResponse response);

    AutoOfy getAuto(String numeroSerie);

    void actualizarAuto(String numeroSerie, AutoOfy datos, BindingResult result);
    
    void borrarAuto(String numeroSerie);
}
