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

package org.nekorp.workflow.backend.service.reporte.global;

import java.util.Date;

import org.joda.time.DateTime;
import org.nekorp.workflow.backend.model.reporte.global.RenglonRG;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
/**
 * @author Nekorp
 */
public class RenglonFactoryRG implements DataFactoryRG<RenglonRG> {

    private DatosAutoFactoryRG factoryAuto;
    private DatosBitacoraFactoryRG factoryBitacora;
    private DatosClienteFactoryRG factoryCliente;
    private DatosCostoFactoryRG factoryCosto;
    private DatosServicioFactoryRG factoryServicio;
    
    @Override
    public RenglonRG build(ServicioOfy data) {
        RenglonRG r = new RenglonRG();
        r.setDatosAuto(factoryAuto.build(data));
        r.setDatosBitacora(factoryBitacora.build(data));
        r.setDatosCliente(factoryCliente.build(data));
        r.setDatosCosto(factoryCosto.build(data));
        r.setDatosServicio(factoryServicio.build(data));
        Date entradaAutoRaw = r.getDatosBitacora().getFechaIngresoAuto();
        if (entradaAutoRaw != null) {
            DateTime entradaAuto = new DateTime(entradaAutoRaw);
            entradaAuto = new DateTime(entradaAuto.getYear(), entradaAuto.getMonthOfYear(), entradaAuto.getDayOfMonth(),
                entradaAuto.hourOfDay().getMinimumValue(), entradaAuto.minuteOfHour().getMinimumValue(), 
                entradaAuto.secondOfMinute().getMinimumValue(), entradaAuto.millisOfSecond().getMinimumValue(),
                entradaAuto.getZone());
            DateTime iniServ = new DateTime(data.getMetadata().getFechaInicio());
            iniServ = new DateTime(iniServ.getYear(), iniServ.getMonthOfYear(), iniServ.getDayOfMonth(),
                iniServ.hourOfDay().getMinimumValue(), iniServ.minuteOfHour().getMinimumValue(), 
                iniServ.secondOfMinute().getMinimumValue(), iniServ.millisOfSecond().getMinimumValue(),
                iniServ.getZone());
            if (iniServ.isBefore(entradaAuto)) {
                r.getDatosServicio().setProgramado("X");
            }
        }   
        return r;
    }

    public void setFactoryAuto(DatosAutoFactoryRG factoryAuto) {
        this.factoryAuto = factoryAuto;
    }

    public void setFactoryBitacora(DatosBitacoraFactoryRG factoryBitacora) {
        this.factoryBitacora = factoryBitacora;
    }

    public void setFactoryCliente(DatosClienteFactoryRG factoryCliente) {
        this.factoryCliente = factoryCliente;
    }

    public void setFactoryCosto(DatosCostoFactoryRG factoryCosto) {
        this.factoryCosto = factoryCosto;
    }

    public void setFactoryServicio(DatosServicioFactoryRG factoryServicio) {
        this.factoryServicio = factoryServicio;
    }

}
