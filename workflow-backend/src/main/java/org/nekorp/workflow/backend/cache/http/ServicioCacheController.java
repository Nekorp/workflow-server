/**
 *   Copyright 2015 Tikal-Technology
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
package org.nekorp.workflow.backend.cache.http;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.nekorp.workflow.backend.model.servicio.ServicioOfy;

import technology.tikal.gae.http.cache.AbstractCacheController;
import technology.tikal.gae.http.cache.UpdatePair;
import technology.tikal.taller.automotriz.model.cobranza.PagoCobranza;
import technology.tikal.taller.automotriz.model.servicio.moneda.Moneda;

/**
 * Controla el cache de los servicios
 * @author Nekorp
 *
 */
public class ServicioCacheController extends AbstractCacheController<ServicioOfy>  {
    //private static final Log LOGGER = LogFactory.getLog(ServicioCacheController.class);
    @Override
    public boolean haveChanges(UpdatePair<ServicioOfy> pair) {
        ServicioOfy updated = pair.getUpdated();
        ServicioOfy original = pair.getOriginal();
        if (!safeEqualsLong(original.getId(), updated.getId())) {
            throw new IllegalArgumentException();
        }
        //datos generales del servicio
        if (!StringUtils.equals(original.getIdAuto(), updated.getIdAuto())) {
            //ServicioCacheController.LOGGER.info("cambio el id del auto");
            return true;
        }
        if (!safeEqualsLong(original.getIdCliente(), updated.getIdCliente())) {
            //ServicioCacheController.LOGGER.info("cambio el id del cliente");
            return true;
        }
        if (!StringUtils.equals(original.getDescripcion(), updated.getDescripcion())) {
            //ServicioCacheController.LOGGER.info("cambio la descripcion");
            return true;
        }
        //metadata
        if (!safeEqualsDates(original.getMetadata().getFechaInicio(), updated.getMetadata().getFechaInicio())) {
            //ServicioCacheController.LOGGER.info("cambio la fecha inicio del metadata");
            return true;
        }
        if (!StringUtils.equals(original.getMetadata().getStatus(), updated.getMetadata().getStatus())) {
            //ServicioCacheController.LOGGER.info("cambio el status del metadata");
            return true;
        }
        if (!StringUtils.equals(original.getMetadata().getCostoTotal().getValue(), updated.getMetadata().getCostoTotal().getValue())) {
            //ServicioCacheController.LOGGER.info("cambio el costo total:" +original.getMetadata().getCostoTotal().getValue()+":"+updated.getMetadata().getCostoTotal().getValue());
            return true;
        }
        //cobranza
        if (!safeEqualsDates(original.getCobranza().getInicio(), updated.getCobranza().getInicio())) {
            //ServicioCacheController.LOGGER.info("cambio el inicio de la cobranza");
            return true;
        }
        if (original.getCobranza().getPagos().size() != updated.getCobranza().getPagos().size()) {
            //ServicioCacheController.LOGGER.info("cambio la cantidad de pagos:"+ original.getCobranza().getPagos().size() + " " + updated.getCobranza().getPagos().size());
            return true;
        }
        for (int i = 0; i < original.getCobranza().getPagos().size(); i++) {
            PagoCobranza pagoOriginal = original.getCobranza().getPagos().get(i);
            PagoCobranza pagoActualizado = updated.getCobranza().getPagos().get(i);
            if (!safeEqualsDates(pagoOriginal.getFecha(), pagoActualizado.getFecha())) {
                //ServicioCacheController.LOGGER.info("el pago" + i + "es diferente en la fecha");
                return true;
            }
            if (!StringUtils.equals(pagoOriginal.getMonto().getValue(), pagoActualizado.getMonto().getValue())) {
                //ServicioCacheController.LOGGER.info("el pago" + i + "es diferente en el monto");
                return true;
            }
        }
        return false;
    }
    
    @Override
    public UpdatePair<ServicioOfy> cloneObject(Object retVal) {
        ServicioOfy updated = (ServicioOfy) retVal;
        ServicioOfy original = new ServicioOfy();
        original.setId(updated.getId());
        //datos generales del servicio
        original.setIdAuto(updated.getIdAuto());
        original.setIdCliente(updated.getIdCliente());
        original.setDescripcion(updated.getDescripcion());
        //metadata
        original.getMetadata().setFechaInicio(updated.getMetadata().getFechaInicio());
        original.getMetadata().setStatus(updated.getMetadata().getStatus());
        //aqui es donde me arrepiento de no hacer las monedas inmutables
        //ServicioCacheController.LOGGER.info("costo total original:" + updated.getMetadata().getCostoTotal().getValue());
        original.getMetadata().setCostoTotal(new Moneda(updated.getMetadata().getCostoTotal().getValue()));
        //cobranza
        original.getCobranza().setInicio(updated.getCobranza().getInicio());
        List<PagoCobranza> pagos = new LinkedList<>();
        for(PagoCobranza x: updated.getCobranza().getPagos()) {
            PagoCobranza originalPago = new PagoCobranza();
            originalPago.setFecha(x.getFecha());
            originalPago.setMonto(new Moneda(x.getMonto().getValue()));
            pagos.add(originalPago);
        }
        original.getCobranza().setPagos(pagos);
        return new UpdatePair<ServicioOfy>(original, updated);
    }
    
    public boolean isTypeOf(Object retVal) {
        return retVal instanceof ServicioOfy;
    }
}
