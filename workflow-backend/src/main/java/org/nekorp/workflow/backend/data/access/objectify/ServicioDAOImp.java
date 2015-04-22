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
package org.nekorp.workflow.backend.data.access.objectify;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroServicio;
import org.nekorp.workflow.backend.data.access.util.FiltroServicioIndex;
import org.nekorp.workflow.backend.model.secuencia.DatosFoliadorServicio;
import org.nekorp.workflow.backend.model.servicio.ServicioOfy;

import technology.tikal.gae.dao.template.FiltroBusqueda;
import technology.tikal.gae.pagination.model.PaginationData;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author Nekorp
 */
public class ServicioDAOImp implements ServicioDAO {

    private String idFoliador;
    
    @Override
    public List<ServicioOfy> consultarTodos(FiltroBusqueda filtroRaw, PaginationData<Long> pagination) {
        List<ServicioOfy> result;
        Query<ServicioOfy> query = ofy().load().type(ServicioOfy.class);
        if (filtroRaw instanceof FiltroServicio) {
            FiltroServicio filtro = (FiltroServicio) filtroRaw;
            //consulta unicamente por rango de fechas
            //ignora datos de paginacion
            //esto es debido a limitantes de appengine ya que no puede tener desigualdades sobre mas de un campo
            //en este caso el registro inicial de la paginacion y las comparaciones con las fechas
            if (!StringUtils.isEmpty(filtro.getFechaInicial())) {
                return consultarTodosPorFecha(filtro, pagination);
            }
        }
        if (pagination.getSinceId() != null) {
            Key<ServicioOfy> key = Key.create(ServicioOfy.class, pagination.getSinceId());
            query = query.filterKey(">=", key);
        }
        if (pagination.getMaxResults() != 0) {
            //se trae uno de mas para indicar cual es la siguiente pagina
            query = query.limit(pagination.getMaxResults() + 1);
        }
        if (filtroRaw instanceof FiltroServicioIndex) {
            FiltroServicioIndex filtro = (FiltroServicioIndex) filtroRaw;
            if (!StringUtils.isEmpty(filtro.getStatusServicio())) {
                query = query.filter("metadata.status =", filtro.getStatusServicio());
            }
            if (!StringUtils.isEmpty(filtro.getNumeroSerieAuto())) {
                query = query.filter("idAuto =", filtro.getNumeroSerieAuto());
            }
        }
        result = query.list();
        if (pagination.getMaxResults() != 0 && result.size() > pagination.getMaxResults()) {
            ServicioOfy ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }
    
    private List<ServicioOfy> consultarTodosPorFecha(FiltroServicio filtro, PaginationData<Long> pagination) {
        List<ServicioOfy> result;
        try {
            Query<ServicioOfy> query = ofy().load().type(ServicioOfy.class);
            DateTime inicio = new DateTime(filtro.getFechaInicial());
            query = query.filter("metadata.fechaInicio >=", inicio.toDate());
            DateTime fin = new DateTime(filtro.getFechaFinal());
            query = query.filter("metadata.fechaInicio <=", fin.toDate());
            if (filtro.getIdCliente() != null) {
                query = query.filter("idCliente =", filtro.getIdCliente());
            }
            result = query.list();
            return result;
        } catch (IllegalArgumentException e) {
            result = new LinkedList<ServicioOfy>();
            return result;
        }
    }

    @Override
    public void guardar(ServicioOfy nuevo) {
        try {
            if (nuevo.getId() == null) {
                nuevo.setId(obtenerNuevoFolio());
            }
            ofy().save().entity(nuevo).now();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Long obtenerNuevoFolio() {
        Long folio = ofy().transact(new Work<Long>() {
            @Override
            public Long run() {
                Key<DatosFoliadorServicio> key = Key.create(DatosFoliadorServicio.class, idFoliador);
                DatosFoliadorServicio datFolio = ofy().load().key(key).now();
                if (datFolio == null) {
                    datFolio = new DatosFoliadorServicio(idFoliador, Long.valueOf(1));
                }
                Long r = datFolio.usarSiguienteFolio();
                ofy().save().entity(datFolio);
                return r;
            }
        });
        return folio;
    }

    @Override
    public ServicioOfy consultar(Long id, Class<?>... group) {
        Key<ServicioOfy> key = Key.create(ServicioOfy.class, id);
        ServicioOfy respuesta = ofy().load().key(key).safe();
        return respuesta;
    }

    @Override
    public void borrar(ServicioOfy dato) {
        ofy().delete().entity(dato).now();
    }

    /**{@inheritDoc}*/
    @Override
    public void actualizarMetadata(ServicioOfy servicio) {
        this.guardar(servicio);
    }

    public void setIdFoliador(String idFoliador) {
        this.idFoliador = idFoliador;
    }
}
