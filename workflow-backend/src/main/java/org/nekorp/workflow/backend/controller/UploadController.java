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
package org.nekorp.workflow.backend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import technology.tikal.taller.automotriz.model.upload.ImagenMetadata;

/**
 * @author Nekorp
 */
public interface UploadController {

    ImagenMetadata getUploadUrl();
    ImagenMetadata uploadImage(HttpServletRequest request, HttpServletResponse response);
    void getImage(String rawBlobKey, HttpServletResponse response);
    void deleteImage(String rawBlobKey, HttpServletResponse response);
}
