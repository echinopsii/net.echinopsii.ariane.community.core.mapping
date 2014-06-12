/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 04/06/14 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.echinopsii.ariane.community.core.mapping.wat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;

public class MDSLRSessionRequestController {
    private static final Logger log = LoggerFactory.getLogger(MDSLRSessionRequestController.class);
    private String sessionRequest ="Define your Mapping DSL request here...";

    public final static String FACES_CONTEXT_APPMAP_SESSION_REQ = "MAPPING_SESSION_REQUEST";

    public String getSessionRequest() {
        return sessionRequest;
    }

    public void setSessionRequest(String sessionRequest) {
        this.sessionRequest = sessionRequest;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SESSION_REQ, this.sessionRequest);
    }

    public void loadSelectedRequest() {
        setSessionRequest((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_REQ));
    }
}