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
package org.nekorp.workflow.backend.service.reporte.cliente;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.AutoDAO;
import org.nekorp.workflow.backend.data.access.BitacoraDAO;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.CostoDAO;
import org.nekorp.workflow.backend.model.auto.AutoOfy;
import org.nekorp.workflow.backend.model.cliente.ClienteOfy;
import org.nekorp.workflow.backend.model.reporte.cliente.AutoRC;
import org.nekorp.workflow.backend.model.reporte.cliente.EventoRC;
import org.nekorp.workflow.backend.model.reporte.cliente.RegistroCostoRC;
import org.nekorp.workflow.backend.model.reporte.cliente.ReporteCliente;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;
import org.nekorp.workflow.backend.model.servicio.bitacora.EventoOfy;
import org.nekorp.workflow.backend.model.servicio.costo.RegistroCostoOfy;
import org.nekorp.workflow.backend.util.MonedaHalfUpRound;

/**
 * Hay cosas repetidas o similares en el cliente de escritorio.
 * Lo que esta aqui se usa para generar el reporte pdf del cliente web
 * lo que esta repetido en el cliente es para generar el reporte xml con la informacion que se tiene en memoria en el cliente de escritorio.
 * se podrian unificar en uno solo.
 * @author Nekorp
 */
public class ReporteClienteDataFactoryImp implements ReporteClienteDataFactory {

    private ClienteDAO clienteDao;
    private BitacoraDAO bitacoraDao;
    private AutoDAO autoDao;
    private CostoDAO costoDao;
    /**{@inheritDoc}*/
    @Override
    //otra cosa horriblemente repetida, en el cliente lo realiza similar pero con la informacion que tiene cargada en memoria.
    public ReporteCliente getData(ServicioOfy servicio) {
        ClienteOfy cliente = clienteDao.consultar(servicio.getIdCliente());
        ReporteCliente dato = new ReporteCliente();
        dato.setNumeroDeServicio(servicio.getId() + "");
        dato.setNombreDelCliente(cliente.getNombre());
        dato.setDescripcionServicio(servicio.getDescripcion());
        
        List<EventoOfy> bitacora = bitacoraDao.consultar(servicio);
        dato.setTiempoReparacion(calculaTiempoServicio(bitacora));
        
        AutoOfy autoRaw = autoDao.consultar(servicio.getIdAuto());
        AutoRC auto = new AutoRC();
        auto.setMarca(autoRaw.getMarca());
        auto.setTipo(autoRaw.getTipo());
        auto.setVersion(autoRaw.getVersion());
        auto.setSerie(autoRaw.getNumeroSerie());
        auto.setModelo(autoRaw.getModelo());
        auto.setColor(autoRaw.getColor());
        auto.setPlacas(autoRaw.getPlacas());
        auto.setKilometraje(servicio.getDatosAuto().getKilometraje());
        dato.setAuto(auto);
        
        List<RegistroCostoRC> mecanica = new LinkedList<RegistroCostoRC>();
        List<RegistroCostoRC> hojalateria = new LinkedList<RegistroCostoRC>();
        List<RegistroCostoOfy> costos = costoDao.consultar(servicio);
        MonedaHalfUpRound totalMecanica = new MonedaHalfUpRound();
        MonedaHalfUpRound totalHojalateria = new MonedaHalfUpRound();
        for (RegistroCostoOfy x: costos) {
            if (StringUtils.equals("Mecanica", x.getTipo()) && !StringUtils.equals("Insumo", x.getSubtipo())) {
                RegistroCostoRC registro = new RegistroCostoRC();
                registro.setTipo(x.getSubtipo());
                registro.setDescripcion(x.getConcepto());
                MonedaHalfUpRound precioCliente = MonedaHalfUpRound.valueOf(x.getPrecioCliente().getValue());
                MonedaHalfUpRound costo = precioCliente.multiplica(x.getCantidad());
                registro.setCosto(costo.doubleValue());
                mecanica.add(registro);
                totalMecanica = totalMecanica.suma(costo);
            }
            if (StringUtils.equals("Hojalateria y Pintura", x.getTipo()) && !StringUtils.equals("Insumo", x.getSubtipo())) {
                RegistroCostoRC registro = new RegistroCostoRC();
                registro.setTipo(x.getSubtipo());
                registro.setDescripcion(x.getConcepto());
                MonedaHalfUpRound precioCliente = MonedaHalfUpRound.valueOf(x.getPrecioCliente().getValue());
                MonedaHalfUpRound costo = precioCliente.multiplica(x.getCantidad());
                registro.setCosto(costo.doubleValue());
                hojalateria.add(registro);
                totalHojalateria = totalHojalateria.suma(costo);
            }
        }
        dato.setRegistroMecanica(mecanica);
        dato.setRegistroHojalateriaPintura(hojalateria);
        dato.setTotalMecanica(totalMecanica.doubleValue());
        dato.setTotalHojalateria(totalHojalateria.doubleValue());
        dato.setTotalServicio(totalMecanica.suma(totalHojalateria).doubleValue());
        
        List<EventoRC> eventos = new LinkedList<EventoRC>();
        EventoRC evento;
        for (EventoOfy x: bitacora) {
            evento = new EventoRC();
            if (x.getTipo().equals("EventoEntrega")) {
                evento.setNombreEvento(x.getEtiqueta());
                evento.setDetalle(x.getResponsable());
                evento.setFecha(x.getFecha());
                evento.setEtiqueta("");
            }
            if (x.getTipo().equals("EventoFinServicio")) {
                evento.setNombreEvento(x.getEtiqueta());
                evento.setDetalle(x.getResponsable());
                evento.setFecha(x.getFechaCreacion());
                evento.setEtiqueta("");
            }
            if (x.getTipo().equals("EventoGeneral")) {
                evento.setNombreEvento(x.getEtiqueta());
                evento.setDetalle(x.getDescripcion());
                evento.setFecha(x.getFecha());
                evento.setEtiqueta("");
            }
            if (x.getTipo().equals("EventoReclamacion")) {
                evento.setNombreEvento("Reclamaciones");
                evento.setDetalle(x.getDescripcion());
                if (x.getEtiqueta().equals("fundada")) {
                    evento.setEtiqueta("Fundada");
                } else {
                    evento.setEtiqueta("Infundada");
                }
                evento.setFecha(x.getFechaCreacion());
            }
            if (x.getTipo().equals("EventoSistema")) {
                evento.setNombreEvento(x.getEtiqueta());
                evento.setDetalle("");
                evento.setFecha(x.getFechaCreacion());
                evento.setEtiqueta("");
            }
            if (x.getTipo().equals("EventoDiagnostico")) {
                evento.setNombreEvento("Diagnostico");
                evento.setDetalle(x.getDescripcion());
                evento.setEtiqueta("");
                evento.setFecha(x.getFechaCreacion());
            }
            if (x.getTipo().equals("EventoObservaciones")) {
                evento.setNombreEvento("Observaciones");
                evento.setDetalle(x.getDescripcion());
                evento.setEtiqueta("");
                evento.setFecha(x.getFechaCreacion());
            }
            eventos.add(evento);
        }
        dato.setBitacora(eventos);
        return dato;
    }

    //horrible smell a cosas repetidas
    //pero en el cliente lo calcula constantemente para refrescar cada segundo lo que pinta en pantalla
    //BitacoraAnalyzerImp en el cliente desktop tambien tiene esta logica
    private String calculaTiempoServicio(List<EventoOfy> bitacora) {
        Date fechaEntrada = getFechaInicioServicio(bitacora);
        Date fechaSalida = getFechaFinServicio(bitacora);
        if (fechaSalida == null) {
            fechaSalida = new Date();
        }
        if (fechaEntrada != null) {
            long ms = fechaSalida.getTime() - fechaEntrada.getTime();
            long x;
            x = ms / 1000;
            long segundos = x % 60;
            x /= 60;
            long minutes = x % 60;
            x /= 60;
            long hours = x % 24;
            x /= 24;
            long days = x;
            return(days+"D "+hours +"H "+minutes+ "m "+segundos+"s");
        } else {
            return "";
        }
    }
   
    private Date getFechaInicioServicio(List<EventoOfy> bitacora) {
        for (EventoOfy x: bitacora) {
            if (StringUtils.equals("EventoSistema", x.getTipo()) && StringUtils.equals("Inicio del Servicio", x.getEtiqueta())) {
                return x.getFechaCreacion();
            }
        }
        return null;
    }
    
    private Date getFechaFinServicio(List<EventoOfy> bitacora) {
        for (EventoOfy x: bitacora) {
            if (StringUtils.equals("EventoFinServicio", x.getTipo())) {
                return x.getFechaCreacion();
            }
        }
        return null;
    }

    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }

    public void setBitacoraDao(BitacoraDAO bitacoraDao) {
        this.bitacoraDao = bitacoraDao;
    }

    public void setAutoDao(AutoDAO autoDao) {
        this.autoDao = autoDao;
    }

    public void setCostoDao(CostoDAO costoDao) {
        this.costoDao = costoDao;
    }
}
