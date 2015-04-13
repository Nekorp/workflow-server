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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.controller.ServicioController;
import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.data.access.DamageDetailDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroServicio;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationDataLong;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import org.nekorp.workflow.backend.model.servicio.auto.damage.DamageDetail;
import org.nekorp.workflow.backend.model.servicio.bitacora.Evento;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCosto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/servicios")
public class ServicioControllerImp implements ServicioController {

    private ServicioDAO servicioDAO;
    private BitacoraDAO bitacoraDAO;
    private CostoDAO costoDAO;
    private DamageDetailDAO damageDetailDAO;
    private PaginationModelFactory pagFactory;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Servicio, Long> getServicios(@ModelAttribute final FiltroServicio filtro,
        @Valid @ModelAttribute final PaginationDataLong pagination, final HttpServletResponse response) {
        List<Servicio> datos = servicioDAO.consultarTodos(filtro, pagination);
        Page<Servicio, Long> r = pagFactory.getPage();
        r.setTipoItems("servicio");
        r.setLinkPaginaActual(armaUrl("/servicios", filtro, pagination.getSinceId(), pagination.getMaxResults()));
        if (pagination.hasNext()) {
            r.setLinkSiguientePagina(armaUrl("/servicios", filtro, pagination.getNextId(), pagination.getMaxResults()));
            r.setSiguienteItem(pagination.getNextId());
        }
        r.setItems(datos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public void crearServicio(@Valid @RequestBody final Servicio servicio, final HttpServletResponse response) {
        servicio.setId(null);
        this.servicioDAO.guardar(servicio);
        //TODO el evento que marca la hora en la que se creo el servicio podria generarse aqui y no en el cliente.
        //bitacoraDAO.guardar(servicio.getId(), new LinkedList<Evento>());
        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", "/servicios/" + servicio.getId());
    }

    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public @ResponseBody Servicio getServicio(@PathVariable final Long id, final HttpServletResponse response) {
        Servicio respuesta = this.servicioDAO.consultar(id);
        if (respuesta == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return respuesta;
    }

    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public void actualizarServicio(@PathVariable final Long id, @Valid @RequestBody final Servicio datos, final HttpServletResponse response) {
        datos.setId(id);
        if (servicioDAO.consultar(id) == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            servicioDAO.guardar(datos);
        }
    }

    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void borrarServicio(@PathVariable final Long id, final HttpServletResponse response) {
        Servicio dato = this.servicioDAO.consultar(id);
        if (dato == null) {
            //no hay nada que responder
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        //hay que borrar costos y bitacora primero
        this.saveBitacora(id, new LinkedList<Evento>(), response);
        this.saveCosto(id, new LinkedList<RegistroCosto>(), response);
        //se borra servicio y listo
        servicioDAO.borrar(dato);
        //se acepto la peticion de borrado, no quiere decir que sucede de inmediato.
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    
    @Override
    @RequestMapping(value="/{idServicio}/bitacora", method = RequestMethod.GET)
    public @ResponseBody List<Evento> getBitacora(@PathVariable final Long idServicio, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<Evento> r = bitacoraDAO.consultar(idServicio);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }
    
    @Override
    @RequestMapping(value="/{idServicio}/bitacora", method = RequestMethod.POST)
    public @ResponseBody List<Evento> saveBitacora(@PathVariable final Long idServicio,
        @Valid @RequestBody final List<Evento> eventos, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<Evento> datos = bitacoraDAO.guardar(idServicio, eventos);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return datos;
    }
    
    @Override
    @RequestMapping(value="/{idServicio}/costo", method = RequestMethod.GET)
    public @ResponseBody List<RegistroCosto> getCosto(@PathVariable final Long idServicio, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<RegistroCosto> r = costoDAO.consultar(idServicio);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    @Override
    @RequestMapping(value="/{idServicio}/costo", method = RequestMethod.POST)
    public @ResponseBody List<RegistroCosto> saveCosto(@PathVariable final Long idServicio, 
        @Valid @RequestBody final List<RegistroCosto> registros, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<RegistroCosto> datos = costoDAO.guardar(servicio, registros);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return datos;
    }
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{idServicio}/damage", method = RequestMethod.GET)
    public @ResponseBody List<DamageDetail> getInventarioDamage(@PathVariable final Long idServicio, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<DamageDetail> r = damageDetailDAO.consultar(idServicio);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/{idServicio}/damage", method = RequestMethod.POST)
    public @ResponseBody List<DamageDetail> saveInventarioDamage(@PathVariable final Long idServicio,
        @Valid @RequestBody final List<DamageDetail> registros, final HttpServletResponse response) {
        Servicio servicio = this.servicioDAO.consultar(idServicio);
        if (servicio == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        List<DamageDetail> datos = damageDetailDAO.guardar(idServicio, registros);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return datos;
    }
    
    private String armaUrl(final String base, final FiltroServicio filtro, final Long sinceId, final int maxResults) {
        String r = base;
        r = addUrlParameter(r,"fechaInicial", filtro.getFechaInicial());
        r = addUrlParameter(r,"fechaFinal", filtro.getFechaFinal());
        if (sinceId != null && sinceId > 0) {
            r = addUrlParameter(r,"sinceId", sinceId + "");
        }
        if (maxResults > 0) {
            r = addUrlParameter(r,"maxResults", maxResults + "");
        }
        return r;
    }
    
    private String addUrlParameter(final String url, final String name, final String value) {
        String response = url;
        if (!StringUtils.isEmpty(value)) {
            if (!StringUtils.contains(response, '?')) {
                response = response + "?";
            } else {
                response = response + "&";
            }
            response = response + name + "=" + value;
        }
        return response;
    }

    public void setServicioDAO(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public void setBitacoraDAO(BitacoraDAO bitacoraDAO) {
        this.bitacoraDAO = bitacoraDAO;
    }

    public void setCostoDAO(CostoDAO costoDAO) {
        this.costoDAO = costoDAO;
    }

    public void setDamageDetailDAO(DamageDetailDAO damageDetailDAO) {
        this.damageDetailDAO = damageDetailDAO;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }
}
