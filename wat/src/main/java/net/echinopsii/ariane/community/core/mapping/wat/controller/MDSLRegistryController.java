/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 03/06/14 echinopsii
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

import net.echinopsii.ariane.community.core.idm.base.model.IUXResource;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class MDSLRegistryController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryController.class);
    private MappingDSLRegistryDirectory rootD;

    private TreeNode root;
    private TreeNode selectedDirectoryOrRequestNode;
    private TreeNode selectedRequestNode;
    private String   selectedRequestReq;
    private String   selectedRequestDesc;

    public final static String FACES_CONTEXT_APPMAP_SELECTED_REQ  = "MAPPING_SELECTED_REQUEST";
    public final static String FACES_CONTEXT_APPMAP_SELECTED_NODE = "MAPPING_SELECTED_NODE";

    private void buildTree(MappingDSLRegistryDirectory rootDir, TreeNode rootNode) {
        for (IUXResource child : rootDir.getOrderedChildsList()) {
            if (child instanceof MappingDSLRegistryDirectory) {
                TreeNode subNode = new DefaultTreeNode("Folder", child, rootNode);
                buildTree((MappingDSLRegistryDirectory)child, subNode);
            } else if (child instanceof MappingDSLRegistryRequest) {
                new DefaultTreeNode(((MappingDSLRegistryRequest)child).isTemplate() ? "Template" : "Request", child, rootNode);
            }
        }
    }

    @PostConstruct
    public void init() {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<MappingDSLRegistryDirectory> rootDCriteria = builder.createQuery(MappingDSLRegistryDirectory.class);
        Root<MappingDSLRegistryDirectory> rootDRoot = rootDCriteria.from(MappingDSLRegistryDirectory.class);
        rootDCriteria.select(rootDRoot).where(builder.equal(rootDRoot.<String>get("name"), MappingDSLRegistryBootstrap.MAPPING_DSL_REGISTRY_ROOT_DIR_NAME));
        TypedQuery<MappingDSLRegistryDirectory> rootDQuery = em.createQuery(rootDCriteria);

        try {
            rootD = rootDQuery.getSingleResult();
        } catch (NoResultException e) {
            log.error("Mapping DSL Registry root directory has not been defined correctly ! You may have some problem during installation... ");
            return;
        } catch (Exception e) {
            throw e;
        }

        root = new DefaultTreeNode(rootD, null);
        buildTree(rootD, root);

        em.close();
    }

    public void reloadTree() {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        rootD = em.find(rootD.getClass(), rootD.getId());
        root = new DefaultTreeNode(rootD, null);
        buildTree(rootD, root);
        em.close();
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedDirectoryOrRequestNode() {
        return selectedDirectoryOrRequestNode;
    }

    public void setSelectedDirectoryOrRequestNode(TreeNode selectedDirectoryOrRequestNode) {
        this.selectedDirectoryOrRequestNode = selectedDirectoryOrRequestNode;
        if (selectedDirectoryOrRequestNode.getData() instanceof MappingDSLRegistryRequest) {
            this.selectedRequestNode = selectedDirectoryOrRequestNode;
            this.selectedRequestReq  = ((MappingDSLRegistryRequest)this.selectedRequestNode.getData()).getRequest();
            this.selectedRequestDesc = ((MappingDSLRegistryRequest)this.selectedRequestNode.getData()).getDescription();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_REQ, this.selectedRequestReq);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_NODE, this.selectedDirectoryOrRequestNode.getData());
        } else {
            this.selectedRequestNode = null;
            this.selectedRequestReq  = "";
            this.selectedRequestDesc = "";
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_REQ, this.selectedRequestReq);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_NODE, this.selectedDirectoryOrRequestNode.getData());
        }
    }

    public TreeNode getSelectedRequestNode() {
        return selectedRequestNode;
    }

    public String getSelectedRequestReq() {
        return selectedRequestReq;
    }

    public String getSelectedRequestDesc() {
        return selectedRequestDesc;
    }

    public void warnOnReadyToEraseExistingRequest() {
        if (this.selectedRequestNode!=null)
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                                "Warning!",
                                                                                "Are you sure to erase " +
                                                                                ((MappingDSLRegistryRequest)selectedDirectoryOrRequestNode.getData()).getName() +
                                                                                " request ?"));
    }
}