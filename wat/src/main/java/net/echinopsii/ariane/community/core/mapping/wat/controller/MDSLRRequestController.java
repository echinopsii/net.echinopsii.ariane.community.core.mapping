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

import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.mdsl.registry.model.MappingDSLRegistryRequest;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.group.GroupsListController;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.user.UsersListController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MDSLRRequestController {

    private static final Logger log = LoggerFactory.getLogger(MDSLRRequestController.class);

    private Long      id;
    private String    name;
    private String    request;
    private String    description;
    private boolean   isTemplate;
    private MappingDSLRegistryDirectory rootDirectory;

    private String userName;
    private User user;

    private String groupName;
    private Group group;

    private List<String> UXresourcesLikePermissions;

    private List<String> userSelectedPermissions;
    private List<String> groupSelectedPermissions;
    private List<String> otherSelectedPermissions;

    private Set<UXPermission> uxPermissions;
    private Set<UXPermission> uxDefaultPermissions;

    @PostConstruct
    public void init() {
        UXresourcesLikePermissions = new ArrayList<String>();
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_RD_PERM);
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_WR_PERM);
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_CH_PERM);

        uxPermissions =  new HashSet<UXPermission>();

        uxDefaultPermissions = new HashSet<UXPermission>();
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_CH_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_O_ACTOR_TYPE));
    }

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


    public List<String> getUXresourcesLikePermissions() {
        return UXresourcesLikePermissions;
    }

    public void setUXresourcesLikePermissions(List<String> UXresourcesLikePermissions) {
        this.UXresourcesLikePermissions = UXresourcesLikePermissions;
    }

    public String getUserName() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            MappingDSLRegistryRequest req = em.find(MappingDSLRegistryRequest.class, ((MappingDSLRegistryRequest)selectedDirOrReqNode).getId());
            user = req.getUser();
            if (user!=null)
                userName = user.getUserName();
            em.close();
        }
        return userName;
    }

    public void setUserName(String userName) {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        this.user = UsersListController.getUserByUserName(em, userName);
        if (this.user!=null)
            this.userName = userName;
        em.close();
    }

    public String getGroupName() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            MappingDSLRegistryRequest req = em.find(MappingDSLRegistryRequest.class, ((MappingDSLRegistryRequest)selectedDirOrReqNode).getId());
            group = req.getGroup();
            if (group!=null)
                groupName = group.getName();
            em.close();
        }
        return groupName;
    }

    public void setGroupName(String groupName) {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        this.group = GroupsListController.getGroupByName(em, groupName);
        if (this.group!=null)
            this.groupName = groupName;
        em.close();
    }

    public List<String> getUserSelectedPermissions() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
            userSelectedPermissions = new ArrayList();
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            MappingDSLRegistryRequest req = em.find(MappingDSLRegistryRequest.class, ((MappingDSLRegistryRequest)selectedDirOrReqNode).getId());
            for(UXPermission perm : req.getUxPermissions()) {
                if (perm.getName().contains(UXPermission.UX_LIKE_U_ACTOR_TYPE)) {
                    userSelectedPermissions.add(perm.getName().split("\\.")[0]);
                }
            }

            em.close();
        }
        return userSelectedPermissions;
    }

    public void setUserSelectedPermissions(List<String> userSelectedPermissions) {
        this.userSelectedPermissions = userSelectedPermissions;
        for (String permName : this.userSelectedPermissions) {
            UXPermission perm = MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(permName, UXPermission.UX_LIKE_U_ACTOR_TYPE);
            if (perm!=null)
                this.uxPermissions.add(perm);
        }
    }

    public List<String> getGroupSelectedPermissions() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
            groupSelectedPermissions = new ArrayList();
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            MappingDSLRegistryRequest req = em.find(MappingDSLRegistryRequest.class, ((MappingDSLRegistryRequest)selectedDirOrReqNode).getId());
            for(UXPermission perm : req.getUxPermissions()) {
                if (perm.getName().contains(UXPermission.UX_LIKE_G_ACTOR_TYPE)) {
                    groupSelectedPermissions.add(perm.getName().split("\\.")[0]);
                }
            }
            em.close();
        }
        return groupSelectedPermissions;
    }

    public void setGroupSelectedPermissions(List<String> groupSelectedPermissions) {
        this.groupSelectedPermissions = groupSelectedPermissions;
        for (String permName : this.groupSelectedPermissions) {
            UXPermission perm = MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(permName, UXPermission.UX_LIKE_G_ACTOR_TYPE);
            if (perm!=null)
                this.uxPermissions.add(perm);
        }
    }

    public List<String> getOtherSelectedPermissions() {
        Object selectedDirOrReqNode = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
        if (selectedDirOrReqNode instanceof MappingDSLRegistryRequest) {
            otherSelectedPermissions = new ArrayList();
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            MappingDSLRegistryRequest req = em.find(MappingDSLRegistryRequest.class, ((MappingDSLRegistryRequest)selectedDirOrReqNode).getId());
            for(UXPermission perm : req.getUxPermissions()) {
                if (perm.getName().contains(UXPermission.UX_LIKE_O_ACTOR_TYPE)) {
                    otherSelectedPermissions.add(perm.getName().split("\\.")[0]);
                }
            }
            em.close();
        }
        return otherSelectedPermissions;
    }

    public void setOtherSelectedPermissions(List<String> otherSelectedPermissions) {
        this.otherSelectedPermissions = otherSelectedPermissions;
        for (String permName : this.otherSelectedPermissions) {
            UXPermission perm = MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(permName, UXPermission.UX_LIKE_O_ACTOR_TYPE);
            if (perm!=null)
                this.uxPermissions.add(perm);
        }
    }

    public void clear() {
        id=null;
        name=null;
        request=null;
        description=null;
        request=null;
        rootDirectory=null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(MDSLRegistryController.FACES_CONTEXT_APPMAP_SELECTED_NODE);
    }

    public void save() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
            User reqUser = UsersListController.getUserByUserName(em, subject.getPrincipal().toString());
            Group reqGroup = GroupsListController.getGroupByName(em, subject.getPrincipal().toString());
            MappingDSLRegistryRequest entity = null;
            try {
                em.getTransaction().begin();
                if (id==null || id == 0) {
                    entity = new MappingDSLRegistryRequest().setNameR(this.name).setDescriptionR(this.description).setRequestR(this.request).setTemplateR(false);
                    entity.setUser(reqUser);
                    entity.setGroup(reqGroup);
                    entity.setUxPermissions(uxDefaultPermissions);
                    MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                    entity.setRootDirectory(rootD);
                    rootD.getRequests().add(entity);
                    em.persist(entity);
                } else {
                    entity = em.find(MappingDSLRegistryRequest.class, this.id);
                    if (name!=null) entity.setName(name);
                    if (description!=null) entity.setDescription(this.description);
                    if (request!=null) entity.setRequest(this.request);
                    entity.setTemplate(this.isTemplate);
                    if (user!=null) entity.setUser(user);
                    if (group!=null) entity.setGroup(group);
                    if (uxPermissions.size()!=0) entity.setUxPermissions(uxPermissions);
                    if (entity.getRootDirectory()!=null && rootDirectory!=null && !entity.getRootDirectory().equals(this.rootDirectory)) {
                        entity.getRootDirectory().getRequests().remove(entity);
                        MappingDSLRegistryDirectory rootD = em.find(rootDirectory.getClass(), rootDirectory.getId());
                        rootD.getRequests().add(entity);
                    }
                }
                em.getTransaction().commit();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                           "Mapping DSL request " + ((id==null || id==0)? "created" : "updated") + " successfully !",
                                                           "Mapping DSL request " + ((entity.getName() != null) ? "name : " + entity.getName() : "id :" + entity.getId()));
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