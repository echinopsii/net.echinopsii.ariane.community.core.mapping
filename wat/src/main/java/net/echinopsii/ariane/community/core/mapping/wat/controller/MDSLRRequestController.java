/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 05/06/14 echinopsii
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

import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

public class MDSLRRequestController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryController.class);
    private String    name;
    private String    request;
    private String    description;
    private boolean   isTemplate;
    private MappingDSLRegistryDirectory rootDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public MappingDSLRegistryDirectory getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void save() {
        MappingDSLRegistryRequest tosave = new MappingDSLRegistryRequest().setNameR(this.name).setDescriptionR(this.description).setTemplateR(this.isTemplate).setRootDirectoryR(this.rootDirectory);
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        try {
            em.getTransaction().begin();
            em.persist(tosave);
            em.getTransaction().commit();
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while creating mapping dsl request " + tosave.getName() + " !",
                                                "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}