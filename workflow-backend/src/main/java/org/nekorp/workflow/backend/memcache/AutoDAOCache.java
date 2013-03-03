/**
 * Copyright 2013 Nekorp
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License
 */
package org.nekorp.workflow.backend.memcache;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.nekorp.workflow.backend.memcache.pojo.AutoCacheKey;
import org.nekorp.workflow.backend.model.auto.Auto;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * 
 */
public class AutoDAOCache {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AutoDAOCache.class.getName());
    
    void notifyUpdate(Auto auto) {
        //log.info("borrando auto del cache");
        AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
        asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        AutoCacheKey key = new AutoCacheKey();
        key.setVin(auto.getVin());
        asyncCache.delete(key);
    }

    public Object notifyQuery(ProceedingJoinPoint pjp, String id) throws Throwable{
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        AutoCacheKey key = new AutoCacheKey();
        key.setVin(id);
        Auto value = (Auto) syncCache.get(key);
        //log.info("sacando auto del cache");
        if (value == null) {
            //log.info("el auto no estaba en el cache sacando del dao");
            value = (Auto) pjp.proceed();
            syncCache.put(key, value);
        }
        return value;
    }
}
