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
package org.nekorp.workflow.backend.service.imp;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import org.nekorp.workflow.backend.model.servicio.metadata.ServicioMetadataOfy;
import org.nekorp.workflow.backend.service.ServicioMetadataFactory;
import org.nekorp.workflow.backend.util.MonedaHalfUpRound;

import technology.tikal.taller.automotriz.model.servicio.bitacora.EventoConstants;
import technology.tikal.taller.automotriz.model.servicio.metadata.ServicioStatusConstants;
import technology.tikal.taller.automotriz.model.servicio.moneda.Moneda;

/**
 * @author Nekorp
 */
public class ServicioMetadataFactoryImp implements ServicioMetadataFactory {

    private static final int CADUCIDAD_DEFAULT = 15;
    private static final int DIAS_MAX_CIERRE_DEFAULT = 15;
    private BitacoraDAO bitacoraDAO;
    private int diasCaducidad = ServicioMetadataFactoryImp.CADUCIDAD_DEFAULT;
    private int diasMaxCierre = ServicioMetadataFactoryImp.DIAS_MAX_CIERRE_DEFAULT;

    @Override
    public ServicioMetadataOfy calcularMetadata(ServicioOfy servicio) {
        return this.calcularMetadata(servicio, bitacoraDAO.consultar(servicio));
    }

    @Override
    public ServicioMetadataOfy calcularMetadata(ServicioOfy servicio, List<EventoOfy> eventos) {
        ServicioMetadataOfy respuesta = new ServicioMetadataOfy();
        respuesta.setCostoTotal(new Moneda(servicio.getMetadata().getCostoTotal().getValue()));
        if (eventos == null) {
            eventos = new LinkedList<EventoOfy>();
        }
        EventoOfy eventoInicio = null;
        EventoOfy eventoEntradaAuto = null;
        EventoOfy eventoSalidaAuto = null;
        EventoOfy eventoCancelar = null;
        EventoOfy eventoTermino = null;
        for (EventoOfy x: eventos) {
            if (x.getEtiqueta().equals(EventoConstants.inicioServicio) && eventoInicio == null) {
                eventoInicio = x;
                respuesta.setFechaInicio(x.getFechaCreacion());
            }
            if (x.getEtiqueta().equals(EventoConstants.entradaAuto) && eventoEntradaAuto == null) {
                eventoEntradaAuto = x;
            }
            if (x.getEtiqueta().equals(EventoConstants.salidaAuto) && eventoSalidaAuto == null) {
                eventoSalidaAuto = x;
            }
            if (x.getEtiqueta().equals(EventoConstants.cancelacion) && eventoCancelar == null) {
                eventoCancelar = x;
            }
            if (x.getEtiqueta().equals(EventoConstants.terminoServicio) && eventoTermino == null) {
                eventoTermino = x;
            }
        }
        if (eventoCancelar != null) {
            respuesta.setStatus(ServicioStatusConstants.cancelado);
        }
        if (eventoTermino != null && StringUtils.isEmpty(respuesta.getStatus())) {
            respuesta.setStatus(ServicioStatusConstants.terminado);
        }
        if (eventoSalidaAuto != null && StringUtils.isEmpty(respuesta.getStatus())) {
            DateTime fechaEntrega= new DateTime(eventoSalidaAuto.getFecha());
            DateTime fechaCaducidad = fechaEntrega.plusDays(diasMaxCierre);
            if (fechaCaducidad.isBeforeNow()) {
                respuesta.setStatus(ServicioStatusConstants.sinCerrar);
            }
        }
        if (eventoEntradaAuto != null && StringUtils.isEmpty(respuesta.getStatus())) {
            respuesta.setStatus(ServicioStatusConstants.activo);
        }
        if (eventoInicio != null && StringUtils.isEmpty(respuesta.getStatus())) {
            DateTime fechaInicio = new DateTime(respuesta.getFechaInicio());
            DateTime fechaCaducidad = fechaInicio.plusDays(diasCaducidad);
            if (fechaCaducidad.isBeforeNow()) {
                respuesta.setStatus(ServicioStatusConstants.vencido);
            } else {
                respuesta.setStatus(ServicioStatusConstants.activo);
            }
        }
        return respuesta;
    }

    @Override
    public void calcularCostoMetaData(ServicioOfy servicio, List<RegistroCostoOfy> registros) {
        if (registros == null) {
            servicio.getMetadata().setCostoTotal(new Moneda());
            return;
        }
        MonedaHalfUpRound total = new MonedaHalfUpRound();
        MonedaHalfUpRound iva = MonedaHalfUpRound.valueOf("0.16");
        for (RegistroCostoOfy x: registros) {
            MonedaHalfUpRound subtotal = null;
            if (StringUtils.equals("Insumo", x.getSubtipo())) {
                MonedaHalfUpRound precioUnitario = MonedaHalfUpRound.valueOf(x.getPrecioUnitario().getValue());
                subtotal = precioUnitario.multiplica(x.getCantidad());
            } else {
                MonedaHalfUpRound precioCliente = MonedaHalfUpRound.valueOf(x.getPrecioCliente().getValue());
                subtotal = precioCliente.multiplica(x.getCantidad());
            }
            if (x.isSubtotalConIVA()) {
                total = total.suma(subtotal.multiplica(iva));
            }
            total = total.suma(subtotal);
        }
        Moneda resultado = new Moneda();
        resultado.setValue(total.toString());
        servicio.getMetadata().setCostoTotal(resultado);
    }
    
    public void setBitacoraDAO(BitacoraDAO bitacoraDAO) {
        this.bitacoraDAO = bitacoraDAO;
    }

    public void setDiasCaducidad(int diasCaducidad) {
        this.diasCaducidad = diasCaducidad;
    }

    public void setDiasMaxCierre(int diasMaxCierre) {
        this.diasMaxCierre = diasMaxCierre;
    }    
}
