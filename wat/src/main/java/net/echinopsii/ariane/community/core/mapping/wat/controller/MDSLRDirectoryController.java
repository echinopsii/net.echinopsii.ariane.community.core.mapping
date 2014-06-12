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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import java.util.HashSet;

public class MDSLRDirectoryController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRDirectoryController.class);
    private Long      id;
    private String    name;
    private String    description;
    private MappingDSLRegistryDirectory rootDirectory;
    private boolean operationOnSelectedFolder;

    public Long getId() {
        if (isOperationOnSelectedFolder()) {
            Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
            if (selectedDirOrReqNode instanceof MappingDSLRegistryDirectory)
                id = ((MappingDSLRegistryDirectory) selectedDirOrReqNode).getId();
            else
                id = new Long(0);
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOperationOnSelectedFolder() {
        Object opsOnSelectedFolder = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_OPS_ON_FOLDER);
        if (opsOnSelectedFolder!=null)
            operationOnSelectedFolder = (boolean)opsOnSelectedFolder;
        else
            operationOnSelectedFolder = false;
        return operationOnSelectedFolder;
    }

    public void setOperationOnSelectedFolder(boolean operationOnSelectedFolder) {
        this.operationOnSelectedFolder = operationOnSelectedFolder;
    }

    public String getName() {
        if (isOperationOnSelectedFolder()) {
            Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
            if (selectedDirOrReqNode instanceof MappingDSLRegistryDirectory)
                name = ((MappingDSLRegistryDirectory) selectedDirOrReqNode).getName();
            else
                name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (isOperationOnSelectedFolder()) {
            Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
            if (selectedDirOrReqNode instanceof MappingDSLRegistryDirectory)
                description = ((MappingDSLRegistryDirectory) selectedDirOrReqNode).getDescription();
            else
                description = "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MappingDSLRegistryDirectory getRootDirectory() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryDirectory) {
            if (isOperationOnSelectedFolder()) {
                rootDirectory = ((MappingDSLRegistryDirectory) selectedDirOrReqNode).getRootDirectory();
            } else {
                rootDirectory = (MappingDSLRegistryDirectory) selectedDirOrReqNode;
            }
        } else {
            rootDirectory = null;
        }
        return rootDirectory;
    }

    public void setRootDirectory(MappingDSLRegistryDirectory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void save() {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryDirectory entity = null;
        try {
            em.getTransaction().begin();
            if (id == null || id == 0) {
                entity = new MappingDSLRegistryDirectory().setNameR(this.name).setDescriptionR(this.description).setRootDirectoryR(this.rootDirectory);
                MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                entity.setRootDirectory(rootD);
                rootD.getSubDirectories().add(entity);
                em.persist(entity);
            } else {
                entity = em.find(MappingDSLRegistryDirectory.class, id);
                entity.setNameR(name).setDescriptionR(this.description);
                if (!entity.getRootDirectory().equals(this.rootDirectory)) {
                    entity.getRootDirectory().getSubDirectories().remove(entity);
                    MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                    rootD.getSubDirectories().add(entity);
                }
            }
            em.getTransaction().commit();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                "Mapping DSL registry folder created successfully !",
                                                "Mapping DSL registry folder name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Throwable raised while creating mapping dsl registry folder " + ((entity!=null) ? entity.getName() : "null") + " !",
                                                "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void delete(){
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryDirectory entity = null;
        try {
            em.getTransaction().begin();
            entity = em.find(MappingDSLRegistryDirectory.class, this.id);
            entity.getRootDirectory().getSubDirectories().remove(entity);
            em.remove(entity);
            em.getTransaction().commit();

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Mapping DSL registry folder deleted successfully !",
                                                       "Mapping DSL registry folder name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating mapping dsl registry folder " + ((entity!=null) ? entity.getName() : "null") + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}