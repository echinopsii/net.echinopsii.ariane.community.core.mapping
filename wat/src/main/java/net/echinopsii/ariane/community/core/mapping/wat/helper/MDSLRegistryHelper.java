package net.echinopsii.ariane.community.core.mapping.wat.helper;

import net.echinopsii.ariane.community.core.idm.base.model.IUXResource;
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
/**
 * Created by sagar on 23/11/15.
 */
public class MDSLRegistryHelper {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryHelper.class);
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

    public MDSLRegistryHelper() {
        subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();

            user = UsersListController.getUserByUserName(em, subject.getPrincipal().toString());

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

    public TreeNode getRoot() {
        return root;
    }

    public MappingDSLRegistryDirectory getRootD(){
        return rootD;
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
}
