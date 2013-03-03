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
package org.nekorp.workflow.backend.memcache;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.nekorp.workflow.backend.data.access.util.FiltroCliente;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.memcache.pojo.ClienteCacheKey;
import org.nekorp.workflow.backend.memcache.pojo.FiltroClienteCacheKey;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * 
 */
public class ClienteDAOCache {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ClienteDAOCache.class.getName());
    void notifyUpdate(Cliente cliente) {
        if (cliente.getId() != null) {
            //log.info("borrando cliente del cache");
            AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
            asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
            ClienteCacheKey key = new ClienteCacheKey();
            key.setId(cliente.getId());
            asyncCache.delete(key);
            // se quita del cache el resultado de la busqueda por nombre que comienze con esta letra
            //TODO hacer configurable el numero de letras minimo para comenzar la busqueda
            String filtroNombre = StringUtils.substring(cliente.getNombreEstandar(), 0, 1);
            FiltroClienteCacheKey keyTodos = new FiltroClienteCacheKey();
            keyTodos.setFiltro(filtroNombre);
            asyncCache.delete(keyTodos);
        }
    }

    public Object notifyQuery(ProceedingJoinPoint pjp, Long id) throws Throwable{
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        ClienteCacheKey key = new ClienteCacheKey();
        key.setId(id);
        Cliente value = (Cliente) syncCache.get(key);
        //log.info("sacando cliente del cache");
        if (value == null) {
            //log.info("el cliente no estaba en el cache sacando del dao");
            value = (Cliente) pjp.proceed();
            syncCache.put(key, value);
        }
        return value;
    }

    public Object notifyQueryTodos(ProceedingJoinPoint pjp, FiltroCliente filtro, PaginationData<Long> pagination) throws Throwable {
        if (!StringUtils.isEmpty(filtro.getFiltroNombre())) {
            //esto es trampa, ya que si se permiten busquedas de diferentes longitudes no se van a poner 
            //en el cache resultados de diferente longitud
            pagination.setMaxResults(0);
            String filtroOriginal = filtro.getFiltroNombre();
            //se filtrara todo con una sola letra realmente.
            //TODO hacer configurable el numero de letras minimo para comenzar la busqueda
            String nuevoFiltro = StringUtils.substring(filtroOriginal, 0, 1);
            MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
            syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
            FiltroClienteCacheKey key = new FiltroClienteCacheKey();
            key.setFiltro(nuevoFiltro);
            //log.info("sacando lista de clientes del cache");
            @SuppressWarnings("unchecked")
            List<Cliente> value = (List<Cliente>) syncCache.get(key);
            if (value == null) {
                value = new LinkedList<Cliente>();
                //log.info("la lista de clientes no estaba en el cache dejando hacer la busqueda al dao");
                filtro.setFiltroNombre(nuevoFiltro);
                @SuppressWarnings("unchecked")
                List<Cliente> data = (List<Cliente>) pjp.proceed();
                for (Cliente x: data) {
                    value.add(x);
                }
                syncCache.put(key, value);
                filtro.setFiltroNombre(filtroOriginal);
            }
            List<Cliente> respuesta = value;
            //En caso de que el cliente quiera filtrar por mas de una letra
            //TODO hacer configurable el numero de letras minimo para comenzar la busqueda
            if (filtroOriginal.length() > 1) {
                respuesta = new LinkedList<Cliente>();
                for (Cliente x: value) {
                    if (StringUtils.startsWith(x.getNombreEstandar(), filtroOriginal)) {
                        respuesta.add(x);
                    }
                }
            }
            return respuesta;
        } else {
            return pjp.proceed();
        }
    }
}
