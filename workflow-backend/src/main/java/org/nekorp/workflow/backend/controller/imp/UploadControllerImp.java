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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nekorp.workflow.backend.controller.UploadController;
import org.nekorp.workflow.backend.model.upload.ImagenMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * 
 */
@Controller
@RequestMapping("/upload")
public class UploadControllerImp implements UploadController {

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/url", method = RequestMethod.GET)
    public @ResponseBody ImagenMetadata getUploadUrl() {
        ImagenMetadata r = new ImagenMetadata();
        r.setUploadUrl(blobstoreService.createUploadUrl("/api/v1/upload/imagenes/"));
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/imagenes", method = RequestMethod.POST)
    public @ResponseBody ImagenMetadata uploadImage(HttpServletRequest request, HttpServletResponse response) {
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        ImagenMetadata r = new ImagenMetadata();
        for (BlobKey x: blobs.get("myFile")) {
            r.setRawBlobKey(x.getKeyString());
        }
        return r;
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/imagenes/{rawBlobKey}", method = RequestMethod.GET)
    public @ResponseBody void getImage(@PathVariable String rawBlobKey, HttpServletResponse response) {
        try {
            BlobKey blobKey = new BlobKey(rawBlobKey);
            blobstoreService.serve(blobKey, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    /**{@inheritDoc}*/
    @Override
    @RequestMapping(value="/imagenes/{rawBlobKey}", method = RequestMethod.DELETE)
    public void deleteImage(@PathVariable final String rawBlobKey, final HttpServletResponse response) {
        try {
            BlobKey blobKey = new BlobKey(rawBlobKey);
            blobstoreService.delete(blobKey);
            response.setStatus(HttpStatus.ACCEPTED.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
}
