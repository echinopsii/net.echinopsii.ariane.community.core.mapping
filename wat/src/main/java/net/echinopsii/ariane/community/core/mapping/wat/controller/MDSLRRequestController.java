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

    private static final Logger log = LoggerFactory.getLogger(MDSLRRequestController.class);
    private Long      id;
    private String    name;
    private String    request;
    private String    description;
    private boolean   isTemplate;
    private MappingDSLRegistryDirectory rootDirectory;

    public Long getId() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest)
            id = ((MappingDSLRegistryRequest) selectedDirOrReqNode).getId();
        else
            id = new Long(0);
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest)
            name = ((MappingDSLRegistryRequest) selectedDirOrReqNode).getName();
        else
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequest() {
        request = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRSessionRequestController.FACES_CONTEXT_APPMAP_SESSION_REQ);
        if (request==null || request.equals("")) {
            Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
            if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
                request = ((MappingDSLRegistryRequest) selectedDirOrReqNode).getRequest();
            }
        }
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDescription() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest)
            description = ((MappingDSLRegistryRequest) selectedDirOrReqNode).getDescription();
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
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryDirectory)
            rootDirectory = (MappingDSLRegistryDirectory) selectedDirOrReqNode;
        else if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest)
            rootDirectory = ((MappingDSLRegistryRequest) selectedDirOrReqNode).getRootDirectory();
        else
            rootDirectory = null;
        return rootDirectory;
    }

    public void setRootDirectory(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void clear() {
        name=null;
        request=null;
        description=null;
        request=null;
        rootDirectory=null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
    }

    public void save() {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryRequest entity = null;
        try {
            em.getTransaction().begin();
            if (id == 0) {
                entity = new MappingDSLRegistryRequest().setNameR(this.name).setDescriptionR(this.description).setRequestR(this.request).setTemplateR(false);
                MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                entity.setRootDirectory(rootD);
                rootD.getRequests().add(entity);
                em.persist(entity);
            } else {
                entity = em.find(MappingDSLRegistryRequest.class, this.id);
                entity.setNameR(this.name).setDescriptionR(this.description).setRequestR(this.request).setTemplate(this.isTemplate);
                if (!entity.getRootDirectory().equals(this.rootDirectory)) {
                    entity.getRootDirectory().getRequests().remove(entity);
                    MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                    rootD.getRequests().add(entity);
                }
            }
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Mapping DSL request created successfully !",
                                                       "Mapping DSL request name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while creating mapping dsl request " + ((entity!=null) ? entity.getName() : "null") + " !",
                                                "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void delete() {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryRequest entity = null;
        try {
            em.getTransaction().begin();
            entity = em.find(MappingDSLRegistryRequest.class, this.id);
            entity.getRootDirectory().getRequests().remove(entity);
            em.remove(entity);
            em.getTransaction().commit();

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Mapping DSL request deleted successfully !",
                                                       "Mapping DSL request name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }  catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while deleting mapping dsl request " + ((entity!=null) ? entity.getName() : "null") + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}