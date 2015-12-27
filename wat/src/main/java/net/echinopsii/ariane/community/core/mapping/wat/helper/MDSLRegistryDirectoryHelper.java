/**
 * Registry Directory Helper class :
 * Helper class for directory registry
 *
 * Copyright (C) 2015  Echinopsii
 *
 * Author : Sagar Ghuge
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

package net.echinopsii.ariane.community.core.mapping.wat.helper;

import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.user.UsersListController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.Set;

public class MDSLRegistryDirectoryHelper {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryDirectoryHelper.class);
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

    private EntityManager em;

    public MDSLRegistryDirectoryHelper() {
    }

    public void initRoot() {
        subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();

            user  = UsersListController.getUserByUserName(em, subject.getPrincipal().toString());

            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MappingDSLRegistryDirectory> rootDCriteria = builder.createQuery(MappingDSLRegistryDirectory.class);
            Root<MappingDSLRegistryDirectory> rootDRoot = rootDCriteria.from(MappingDSLRegistryDirectory.class);
            rootDRoot.fetch("uxPermissions", JoinType.LEFT);
            rootDRoot.fetch("subDirectories",JoinType.LEFT);
            rootDRoot.fetch("requests", JoinType.LEFT);
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
            }

            em.close();
        }
    }

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

    public MappingDSLRegistryDirectory getRootD(){
        initRoot();
        return rootD;
    }

    public MappingDSLRegistryDirectory getChild(int subDirID){
        em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<MappingDSLRegistryDirectory> rootDCriteria = builder.createQuery(MappingDSLRegistryDirectory.class);
        Root<MappingDSLRegistryDirectory> rootDRoot = rootDCriteria.from(MappingDSLRegistryDirectory.class);
        rootDRoot.fetch("uxPermissions", JoinType.LEFT);
        rootDRoot.fetch("subDirectories",JoinType.LEFT);
        rootDRoot.fetch("requests", JoinType.LEFT);
        rootDCriteria.select(rootDRoot).where(builder.equal(rootDRoot.get("id"), subDirID));
        TypedQuery<MappingDSLRegistryDirectory> rootDQuery = em.createQuery(rootDCriteria);
        MappingDSLRegistryDirectory dir = null;
        try {
            dir = rootDQuery.getSingleResult();
        } catch (NoResultException e) {
            log.error("Mapping DSL Registry directory has not been defined correctly ! You may have some problem during installation... ");
        } catch (Exception e) {
            throw e;
        }
        em.close();
        return dir;
    }

    public Boolean deleteDirectory(long directoryID){
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryDirectory entity = null;
        try {
            em.getTransaction().begin();
            entity = em.find(MappingDSLRegistryDirectory.class, directoryID);
            entity.getRootDirectory().getSubDirectories().remove(entity);
            em.remove(entity);
            em.getTransaction().commit();
/*
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Mapping DSL registry folder deleted successfully !",
                    "Mapping DSL registry folder name : " + entity.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);*/
            return Boolean.TRUE;
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
        return Boolean.FALSE;
    }
}
