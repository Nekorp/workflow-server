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
package org.nekorp.workflow.backend.data.access.objectify;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.data.access.ServicioDAO;
import org.nekorp.workflow.backend.data.access.objectify.template.ObjectifyDAOTemplate;
import org.nekorp.workflow.backend.data.access.template.FiltroBusqueda;
import org.nekorp.workflow.backend.data.access.util.FiltroServicioIndex;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.secuencia.DatosFoliadorServicio;
import org.nekorp.workflow.backend.model.servicio.Servicio;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

/**
 * 
 */
public class ServicioDAOImp extends ObjectifyDAOTemplate implements ServicioDAO {

    private String idFoliador;
    
    @Override
    public List<Servicio> consultarTodos(FiltroBusqueda filtroRaw, PaginationData<Long> pagination) {
        List<Servicio> result;
        Objectify ofy = getObjectifyFactory().begin();
        Query<Servicio> query =  ofy.load().type(Servicio.class);
        if (pagination.getSinceId() != null) {
            Key<Servicio> key = Key.create(Servicio.class, pagination.getSinceId());
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
            Servicio ultimo = result.get(pagination.getMaxResults());
            pagination.setNextId(ultimo.getId());
            result.remove(pagination.getMaxResults());
        }
        return result;
    }

    @Override
    public void guardar(Servicio nuevo) {
        try {
            if (nuevo.getId() == null) {
                nuevo.setId(obtenerNuevoFolio());
            }
            Objectify ofy = getObjectifyFactory().begin();
            ofy.save().entity(nuevo).now();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Long obtenerNuevoFolio() {
        final Objectify ofy = getObjectifyFactory().begin();
        Long folio = ofy.transact(new Work<Long>() {
            @Override
            public Long run() {
                Key<DatosFoliadorServicio> key = Key.create(DatosFoliadorServicio.class, idFoliador);
                DatosFoliadorServicio datFolio = ofy.load().key(key).get();
                if (datFolio == null) {
                    datFolio = new DatosFoliadorServicio(idFoliador, Long.valueOf(1));
                }
                Long r = datFolio.usarSiguienteFolio();
                ofy.save().entity(datFolio);
                return r;
            }
        });
        return folio;
    }

    @Override
    public Servicio consultar(Long id) {
        Objectify ofy = getObjectifyFactory().begin();
        Key<Servicio> key = Key.create(Servicio.class, id);
        Servicio respuesta = ofy.load().key(key).get();
        return respuesta;
    }

    @Override
    public boolean borrar(Servicio dato) {
        Objectify ofy = getObjectifyFactory().begin();
        ofy.delete().entity(dato);
        return true;
    }

    /**{@inheritDoc}*/
    @Override
    public void actualizarMetadata(Servicio servicio) {
        this.guardar(servicio);
    }

    public void setIdFoliador(String idFoliador) {
        this.idFoliador = idFoliador;
    }
}
