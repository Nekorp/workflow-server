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

import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import org.springframework.validation.BindingResult;

import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;

/**
 * @author Nekorp
 */
@Deprecated
public interface RegistroCostoController {

    Page<List<RegistroCostoOfy>> getRegistros(Long idServicio, PaginationDataLong pagination, BindingResult resultPagination, HttpServletRequest request);
    
    void crearRegistro(Long idServicio, RegistroCostoOfy dato, BindingResult result, HttpServletRequest request, HttpServletResponse response);
    
    RegistroCostoOfy getRegistro(Long idServicio, Long idRegistro);
    
    void actualizarRegistro(Long idServicio, Long idRegistro, RegistroCostoOfy dato, BindingResult result);
    
    void borrarRegistro(Long idServicio, Long idRegistro);
}
