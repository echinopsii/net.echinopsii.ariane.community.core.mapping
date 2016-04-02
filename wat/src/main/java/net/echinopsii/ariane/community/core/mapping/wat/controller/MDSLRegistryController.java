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
import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryRequest;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.user.UsersListController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import java.util.Set;

public class MDSLRegistryController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryController.class);
    private MappingDSLRegistryDirectory rootD;

    private TreeNode root;
    private TreeNode selectedDirectoryOrRequestNode;
    private TreeNode selectedRequestNode;
    private String   selectedRequestReq;
    private String   selectedRequestDesc;
    private String   selectedFolderDesc;

    private Subject subject;
    private User       user;

    public final static String FACES_CONTEXT_APPMAP_SELECTED_REQ  = "MAPPING_REGISTRY_SELECTED_REQUEST";
    public final static String FACES_CONTEXT_APPMAP_SELECTED_NODE = "MAPPING_REGISTRY_SELECTED_NODE";
    public final static String FACES_CONTEXT_APPMAP_OPS_ON_FOLDER = "MAPPING_REGISTRY_OPS_ON_FOLDER";

    private boolean hasRight(EntityManager em, Object registryObject, String right) {
        boolean emToClose = false;
        User              regObjUser;
        Group             regObjGroup;
        Set<UXPermission> regObjPermissions;

        if (subject.hasRole("Jedi"))
            return true;

        if (em == null) {
            em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            emToClose = true;
        }
        user = em.find(User.class, user.getId());

        if (registryObject instanceof MappingDSLRegistryDirectory) {
            regObjUser = ((MappingDSLRegistryDirectory)registryObject).getUser();
            regObjGroup = ((MappingDSLRegistryDirectory)registryObject).getGroup();
            regObjPermissions = ((MappingDSLRegistryDirectory)registryObject).getUxPermissions();
        } else if (registryObject instanceof MappingDSLRegistryRequest) {
            regObjUser = ((MappingDSLRegistryRequest)registryObject).getUser();
            regObjGroup = ((MappingDSLRegistryRequest)registryObject).getGroup();
            regObjPermissions = ((MappingDSLRegistryRequest)registryObject).getUxPermissions();
        } else {
            if (emToClose)
                em.close();
            return false;
        }

        for (UXPermission perm : regObjPermissions) {
            if (perm.getName().contains(right)) {
                if (perm.getName().contains(UXPermission.UX_LIKE_U_ACTOR_TYPE) && regObjUser.equals(user)) {
                    if (emToClose)
                        em.close();
                    return true;
                } else if (perm.getName().contains(UXPermission.UX_LIKE_G_ACTOR_TYPE) &&
                                   user.getGroups().contains(regObjGroup)) {
                    if (emToClose)
                        em.close();
                    return true;
                } else if (perm.getName().contains(UXPermission.UX_LIKE_O_ACTOR_TYPE)) {
                    if (emToClose)
                        em.close();
                    return true;
                }
            }
        }

        if (emToClose)
            em.close();
        return false;
    }

    public boolean hasReadAccess() {
        if (selectedDirectoryOrRequestNode!=null) {
            Object registryObject = selectedDirectoryOrRequestNode.getData();
            return hasRight(null, registryObject, UXPermission.UX_LIKE_RD_PERM);
        } else {
            return false;
        }
    }

    public boolean hasWriteAccess() {
        if (this.selectedDirectoryOrRequestNode!=null) {
            Object registryObject = selectedDirectoryOrRequestNode.getData();
            return hasRight(null, registryObject, UXPermission.UX_LIKE_WR_PERM);
        } else {
            return false;
        }
    }

    public boolean hasCHAccess() {
        if (selectedDirectoryOrRequestNode!=null) {
            Object registryObject = selectedDirectoryOrRequestNode.getData();
            return hasRight(null, registryObject, UXPermission.UX_LIKE_CH_PERM);
        } else {
            return false;
        }
    }

    private void buildTree(EntityManager em, MappingDSLRegistryDirectory rootDir, TreeNode rootNode) {
        for (IUXResource child : rootDir.getOrderedChildsList()) {
            if (child instanceof MappingDSLRegistryDirectory) {
                if (subject.hasRole("Jedi") || hasRight(em, child, UXPermission.UX_LIKE_RD_PERM)) {
                    TreeNode subNode;
                    if (subject.hasRole("Jedi") || (hasRight(em, child, UXPermission.UX_LIKE_WR_PERM) && hasRight(em, child, UXPermission.UX_LIKE_CH_PERM))) {
                        subNode = new DefaultTreeNode("FolderRWCH", child, rootNode);
                    } else if (hasRight(em, child, UXPermission.UX_LIKE_WR_PERM)) {
                        subNode = new DefaultTreeNode("FolderRW", child, rootNode);
                    } else {
                        subNode = new DefaultTreeNode("Folder", child, rootNode);
                    }
                    buildTree(em, (MappingDSLRegistryDirectory)child, subNode);
                }
            } else if (child instanceof MappingDSLRegistryRequest) {
                if (subject.hasRole("Jedi") || hasRight(em, child, UXPermission.UX_LIKE_RD_PERM)) {
                    if (subject.hasRole("Jedi") || (hasRight(em, child, UXPermission.UX_LIKE_WR_PERM) && hasRight(em, child, UXPermission.UX_LIKE_CH_PERM))) {
                        new DefaultTreeNode(((MappingDSLRegistryRequest)child).isTemplate() ? "TemplateRWCH" : "RequestRWCH", child, rootNode);
                    } else if (hasRight(em, child, UXPermission.UX_LIKE_WR_PERM)) {
                        new DefaultTreeNode(((MappingDSLRegistryRequest)child).isTemplate() ? "TemplateRW" : "RequestRW", child, rootNode);
                    } else {
                        new DefaultTreeNode(((MappingDSLRegistryRequest)child).isTemplate() ? "Template" : "Request", child, rootNode);
                    }
                }
            }
        }
    }

    @PostConstruct
    public void init() {
        subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();

            user  = UsersListController.getUserByUserName(em, subject.getPrincipal().toString());

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

            if (subject.hasRole("Jedi") || hasRight(em, rootD, UXPermission.UX_LIKE_RD_PERM)) {
                root = new DefaultTreeNode(rootD, null);
                buildTree(em, rootD, root);
            }

            em.close();
        }
    }

    public void reloadTree() {
        if (subject.isAuthenticated()) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            rootD = em.find(rootD.getClass(), rootD.getId());
            if (subject.hasRole("Jedi") || hasRight(em, rootD, UXPermission.UX_LIKE_RD_PERM)) {
                root = new DefaultTreeNode(rootD, null);
                buildTree(em, rootD, root);
            }
            em.close();
            this.selectedDirectoryOrRequestNode = null;
            this.selectedRequestNode = null;
            this.selectedRequestReq  = "";
            this.selectedRequestDesc = "";
            this.selectedFolderDesc  = "";
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_REQ, null);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_NODE, null);
        }
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
            this.selectedFolderDesc  = ((MappingDSLRegistryRequest)this.selectedRequestNode.getData()).getRootDirectory().getDescription();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_REQ, this.selectedRequestReq);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_SELECTED_NODE, this.selectedDirectoryOrRequestNode.getData());
        } else {
            this.selectedRequestNode = null;
            this.selectedRequestReq  = "";
            this.selectedRequestDesc = "";
            this.selectedFolderDesc  = ((MappingDSLRegistryDirectory)this.selectedDirectoryOrRequestNode.getData()).getDescription();
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

    public String getSelectedFolderDesc() {
        return selectedFolderDesc;
    }

    public void createNewFolder() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_OPS_ON_FOLDER, false);
    }

    public void operationOnSelectedFolder() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FACES_CONTEXT_APPMAP_OPS_ON_FOLDER, true);
    }

    public void warnOnSave() {
        Object resource = selectedDirectoryOrRequestNode.getData();
        if (this.selectedRequestNode!=null && hasWriteAccess()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                                "Warning!",
                                                                                "Are you sure to erase " +
                                                                                ((MappingDSLRegistryRequest)resource).getName() +
                                                                                " request ?"));
        } else if (!hasWriteAccess()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                                                                                       "Warning!",
                                                                                       "You don't have permission to write on " +
                                                                                               ((resource instanceof MappingDSLRegistryRequest) ?
                                                                                               " request " + ((MappingDSLRegistryRequest)resource).getName() :
                                                                                               " folder " + ((MappingDSLRegistryDirectory)resource).getName())));
        }
    }
}