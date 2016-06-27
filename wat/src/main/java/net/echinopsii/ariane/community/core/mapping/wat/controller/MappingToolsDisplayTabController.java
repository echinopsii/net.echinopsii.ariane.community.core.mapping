/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
 *
 * Copyright (C) 2013  Mathilde Ffrench
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

import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MappingToolsDisplayTabController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MappingToolsDisplayTabController.class);

    private List<String> defaultNotifications = new ArrayList<String>();
    private String mappingLayout = null;
    private String mappingView   = null;
    private String mappingMode   = null;
    private String mappingEPH    = null;
    private String mappingLegend = null;

    @PostConstruct
    public void init() {
        defaultNotifications.add("notifyInfos");
        defaultNotifications.add("notifyErrs");

        mappingLayout = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MappingBootstrap.MAPPING_USER_PREF_LAYOUT);
        mappingMode = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MappingBootstrap.MAPPING_USER_PREF_MODE);
        mappingView = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MappingBootstrap.MAPPING_USER_PREF_VIEW);
        mappingEPH = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MappingBootstrap.MAPPING_USER_PREF_EPH);
        mappingLegend = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MappingBootstrap.MAPPING_USER_PREF_LEGEND);
    }

    public List<String> getDefaultNotifications() {
        return defaultNotifications;
    }

    public void setDefaultNotifications(List<String> defaultNotifications) {
        this.defaultNotifications = defaultNotifications;
    }

    public String getMappingLayout() {
        return mappingLayout;
    }

    public void setMappingLayout(String mappingLayout) {
        this.mappingLayout = mappingLayout;
    }

    public String getMappingView() {
        return mappingView;
    }

    public void setMappingView(String mappingView) {
        this.mappingView = mappingView;
    }

    public String getMappingMode() {
        return mappingMode;
    }

    public void setMappingMode(String mappingMode) {
        this.mappingMode = mappingMode;
    }

    public String getMappingEPH() {
        return mappingEPH;
    }

    public void setMappingEPH(String mappingEPH) {
        this.mappingEPH = mappingEPH;
    }

    public String getMappingLegend() {
        return mappingLegend;
    }

    public void setMappingLegend(String mappingLegend) {
        this.mappingLegend = mappingLegend;
    }
}