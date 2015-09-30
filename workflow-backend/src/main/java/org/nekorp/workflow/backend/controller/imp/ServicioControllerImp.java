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
package org.nekorp.workflow.backend.controller.imp;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.nekorp.workflow.backend.controller.ServicioController;
import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.data.access.DamageDetailDAO;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroServicio;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.auto.damage.DamageDetailOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import technology.tikal.gae.error.exceptions.MessageSourceResolvableException;
import technology.tikal.gae.error.exceptions.NotValidException;
import technology.tikal.gae.pagination.PaginationModelFactory;
import technology.tikal.gae.pagination.model.Page;
import technology.tikal.gae.pagination.model.PaginationDataLong;
import technology.tikal.gae.service.template.RestControllerTemplate;

/**
 * 
 * @author Nekorp
 *
 */
@RestController
@RequestMapping("/servicios")
public class ServicioControllerImp extends RestControllerTemplate implements ServicioController {

    private ServicioDAO servicioDAO;
    private BitacoraDAO bitacoraDAO;
    private CostoDAO costoDAO;
    private DamageDetailDAO damageDetailDAO;

    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Page<List<ServicioOfy>> getServicios(@ModelAttribute final FiltroServicio filtro,
        @Valid @ModelAttribute final PaginationDataLong pagination, final BindingResult resultPagination, 
        final HttpServletRequest request) {
        if (resultPagination.hasErrors()) {
            throw new NotValidException(resultPagination);
        }
        List<ServicioOfy> datos = servicioDAO.consultarTodos(filtro, pagination);
        Page<List<ServicioOfy>> r = PaginationModelFactory.getPage(datos, "servicio", request.getRequestURI() , filtro, pagination);
        return r;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void crearServicio(@Valid @RequestBody final ServicioOfy servicio, final BindingResult result, 
            final HttpServletRequest request, final HttpServletResponse response) {
        servicio.setId(null);
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        if (servicio.getId() != null) {
            throw new MessageSourceResolvableException(new DefaultMessageSourceResolvable(
                new String[]{"IllegalId.ServicioControllerImp.crearServicio"}, 
                new String[]{servicio.getId().toString()}, 
                "Se espera que un nuevo servicio no tenga id"));
        }
        this.servicioDAO.guardarNuevo(servicio);
        //TODO el evento que marca la hora en la que se creo el servicio podria generarse aqui y no en el cliente.
        //bitacoraDAO.guardar(servicio.getId(), new LinkedList<Evento>());
        response.setHeader("Location", request.getRequestURI() + "/" + servicio.getId());
    }

    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{id}", method = RequestMethod.GET)
    public ServicioOfy getServicio(@PathVariable final Long id) {
        ServicioOfy respuesta = this.servicioDAO.consultar(id);
        return respuesta;
    }

    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public void actualizarServicio(@PathVariable final Long id, @Valid @RequestBody final ServicioOfy datos, final BindingResult result) {
        if (result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy original = servicioDAO.consultar(id);
        original.setIdCliente(datos.getIdCliente());
        original.setIdAuto(datos.getIdAuto());
        original.setDescripcion(datos.getDescripcion());
        original.setDatosAuto(datos.getDatosAuto());
        original.setCobranza(datos.getCobranza());
        original.setGruposCosto(datos.getGruposCosto());
        servicioDAO.guardar(original);
    }

    @Override
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void borrarServicio(@PathVariable final Long id) {
        ServicioOfy dato = this.servicioDAO.consultar(id);
        //hay que borrar costos y bitacora primero
        this.saveBitacora(id, new LinkedList<EventoOfy>(), null);
        this.saveCosto(id, new LinkedList<RegistroCostoOfy>(), null);
        this.saveInventarioDamage(id, new LinkedList<DamageDetailOfy>(), null);
        //se borra servicio y listo
        servicioDAO.borrar(dato);
    }
    
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/bitacora", method = RequestMethod.GET)
    public List<EventoOfy> getBitacora(@PathVariable final Long idServicio) {
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<EventoOfy> r = bitacoraDAO.consultar(servicio);
        return r;
    }
    
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/bitacora", method = RequestMethod.POST)
    public List<EventoOfy> saveBitacora(@PathVariable final Long idServicio,
        @Valid @RequestBody final List<EventoOfy> eventos, final BindingResult result) {
        if (result != null && result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<EventoOfy> datos = bitacoraDAO.guardar(servicio, eventos);
        return datos;
    }
    
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/costo", method = RequestMethod.GET)
    public List<RegistroCostoOfy> getCosto(@PathVariable final Long idServicio) {
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<RegistroCostoOfy> r = costoDAO.consultar(servicio);
        return r;
    }

    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/costo", method = RequestMethod.POST)
    public List<RegistroCostoOfy> saveCosto(@PathVariable final Long idServicio, 
        @Valid @RequestBody final List<RegistroCostoOfy> registros, final BindingResult result) {
        if (result != null && result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<RegistroCostoOfy> datos = costoDAO.guardar(servicio, registros);
        return datos;
    }
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/damage", method = RequestMethod.GET)
    public List<DamageDetailOfy> getInventarioDamage(@PathVariable final Long idServicio) {
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<DamageDetailOfy> r = damageDetailDAO.consultar(servicio);
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(produces = "application/json;charset=UTF-8", value="/{idServicio}/damage", method = RequestMethod.POST)
    public List<DamageDetailOfy> saveInventarioDamage(@PathVariable final Long idServicio,
        @Valid @RequestBody final List<DamageDetailOfy> registros, final BindingResult result) {
        if (result != null && result.hasErrors()) {
            throw new NotValidException(result);
        }
        ServicioOfy servicio = this.servicioDAO.consultar(idServicio);
        List<DamageDetailOfy> datos = damageDetailDAO.guardar(servicio, registros);
        return datos;
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
}
