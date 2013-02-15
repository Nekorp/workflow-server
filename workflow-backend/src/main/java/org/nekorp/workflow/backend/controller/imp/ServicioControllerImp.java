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
package org.nekorp.workflow.backend.controller.imp;

import java.util.LinkedList;
import java.util.List;

import org.nekorp.workflow.backend.controller.ServicioController;
import org.nekorp.workflow.backend.model.Servicio;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/servicios")
public class ServicioControllerImp implements ServicioController {

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Servicio> getServicios() {
        List<Servicio> respuesta = new LinkedList<Servicio>();
        Servicio nuevo = new Servicio();
        nuevo.setId("1");
        nuevo.setDescripcion("algunaDescripcion");
        respuesta.add(nuevo);
        return respuesta;
    }
}
