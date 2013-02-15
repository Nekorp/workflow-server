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

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.nekorp.workflow.backend.controller.ClienteController;
import org.nekorp.workflow.backend.data.access.ClienteDAO;
import org.nekorp.workflow.backend.data.access.util.FiltroFactory;
import org.nekorp.workflow.backend.data.pagination.PaginationModelFactory;
import org.nekorp.workflow.backend.data.pagination.model.Page;
import org.nekorp.workflow.backend.data.pagination.model.PaginationData;
import org.nekorp.workflow.backend.model.cliente.Cliente;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
@RequestMapping("/cliente")
public class ClienteControllerImp implements ClienteController {

    private static final int DEFAULT_LIMIT_RESULTS = 100;
    private ClienteDAO clienteDao;
    private PaginationModelFactory pagFactory;
    private int resultsLimit = ClienteControllerImp.DEFAULT_LIMIT_RESULTS;
    /* (non-Javadoc)
     * @see org.nekorp.workflow.backend.controller.ClienteController#getClientes(java.lang.String, java.lang.String, int)
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page<Cliente> getClientes(
            @RequestParam(value="filtroNombre", required=false) final String filtroNombre, 
            @RequestParam(value="sinceId", required=false) final String sinceId, 
            @RequestParam(value="maxResults", defaultValue="0") final int maxResults) {
        //se preparan los datos para el filtrado de informacion
        Map<String, Object> filter = FiltroFactory.getFilter();
        if (!StringUtils.isEmpty(filtroNombre)) {
            filter.put("filtroNombre", filtroNombre);
        }
        //se preparan datos para paginacion
        PaginationData<String> pagination = pagFactory.getPaginationData();
        pagination.setOffsetRecordId(sinceId);
        int numeroDeRegistros = calculaNumeroResultados(maxResults);
        pagination.setMaxResult(numeroDeRegistros + 1);
        
        //se obtienen los datos y se crea la pagina para colocarlos
        List<Cliente> datos = clienteDao.getClientes(filter, pagination);
        Cliente siguiente = null;
        if (datos.size() > numeroDeRegistros) {
            siguiente = datos.get(datos.size() - 1);
            datos.remove(datos.size() - 1);
        }
        Page<Cliente> r = pagFactory.getPage();
        r.setTipoItems("cliente");
        r.setLinkPaginaActual(armaUrl(filtroNombre,sinceId, numeroDeRegistros));
        if (siguiente != null) {
            r.setLinkSiguientePagina(armaUrl(filtroNombre, siguiente.getId(), numeroDeRegistros));
            r.setSiguienteItem(siguiente.getId());
        }
        r.setItems(datos);
        return r;
    }
    
    private String armaUrl(final String filtroNombre, final String sinceId, final int maxResults) {
        String r = "/cliente";
        r = addUrlParameter(r,"filtroNombre", filtroNombre);
        r = addUrlParameter(r,"sinceId", sinceId);
        if (maxResults != 0 && maxResults != ClienteControllerImp.DEFAULT_LIMIT_RESULTS) {
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

    /**
     * se calcula el numero de registros maximos que se recuperaran
     * si es mayor al maximo se limita y si es igual a 0 se coloca el maximo.
     * @param maxResults el numero maximo de resultados que se solicito.
     * @return el numero maximo de resultados que se procesara.
     */
    private int calculaNumeroResultados(final int maxResults) {
        if (maxResults > resultsLimit || maxResults == 0) {
            return resultsLimit;
        }
        return maxResults;
    }

    public void setClienteDao(ClienteDAO clienteDao) {
        this.clienteDao = clienteDao;
    }

    public void setPagFactory(PaginationModelFactory pagFactory) {
        this.pagFactory = pagFactory;
    }

    public void setResultsLimit(int resultsLimit) {
        this.resultsLimit = resultsLimit;
    }
}
