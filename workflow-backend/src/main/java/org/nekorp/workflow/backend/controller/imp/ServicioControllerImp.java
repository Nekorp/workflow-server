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
@RequestMapping("/servicio")
public class ServicioControllerImp implements ServicioController {

    @Override
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<Servicio> getServicios() {
        List<Servicio> respuesta = new LinkedList<Servicio>();
        Servicio nuevo = new Servicio();
        nuevo.setId("1");
        nuevo.setDescripcion("algunaDescripcion");
        respuesta.add(nuevo);
        return respuesta;
    }
}
