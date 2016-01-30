/**
 * Registry Request Helper class :
 * Helper class for request registry
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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.MappingDSLRegistryBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.group.GroupsListController;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.user.UsersListController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;

public class MDSLRegistryRequestHelper {

    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryRequestHelper.class);

    private Set<UXPermission> uxPermissions;
    private Set<UXPermission> uxDefaultPermissions;
    private List<String> UXresourcesLikePermissions;

    public MDSLRegistryRequestHelper() {
        UXresourcesLikePermissions = new ArrayList<String>();
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_RD_PERM);
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_WR_PERM);
        UXresourcesLikePermissions.add(UXPermission.UX_LIKE_CH_PERM);

        uxPermissions = new HashSet<UXPermission>();

        uxDefaultPermissions = new HashSet<UXPermission>();
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_CH_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
        uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_O_ACTOR_TYPE));

    }

    public Boolean deleteRequest(long requestID) {
        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        MappingDSLRegistryRequest entity = null;
        try {
            em.getTransaction().begin();
            entity = em.find(MappingDSLRegistryRequest.class, requestID);
            entity.getRootDirectory().getRequests().remove(entity);
            em.remove(entity);
            em.getTransaction().commit();
            return Boolean.TRUE;
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Throwable raised while deleting mapping dsl request " + ((entity != null) ? entity.getName() : "null") + " !",
                    "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return Boolean.FALSE;
    }

    public long saveRequest(String payload, String username) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> postData = mapper.readValue(payload, Map.class);
        // if id is null then create new directory else update with that id
        long requestId = Long.valueOf((String) postData.get("requestId"));
        long rootId = Long.valueOf((String) postData.get("rootId"));
        String name = (String) postData.get("name");
        String description = (String) postData.get("description");
        String request = (String) postData.get("request");

        EntityManager em = MappingDSLRegistryBootstrap.getIDMJPAProvider().createEM();
        User reqUser = UsersListController.getUserByUserName(em, username);
        Group reqGroup = GroupsListController.getGroupByName(em, username);
        MappingDSLRegistryRequest entity = null;
        try {
            em.getTransaction().begin();
            MappingDSLRegistryDirectory rootDirectory = em.find(MappingDSLRegistryDirectory.class, rootId);
            if (requestId == 0) {
                entity = new MappingDSLRegistryRequest().setNameR(name).setDescriptionR(description).setRequestR(request).setTemplateR(false);
                entity.setUser(reqUser);
                entity.setGroup(reqGroup);
                entity.setUxPermissions(uxDefaultPermissions);
                entity.setRootDirectory(rootDirectory);
                rootDirectory.getRequests().add(entity);
                em.persist(entity);
            } else {
                entity = em.find(MappingDSLRegistryRequest.class, requestId);
                if (name != null) entity.setName(name);
                if (description != null) entity.setDescription(description);
                if (request != null) entity.setRequest(request);
                entity.setTemplate(entity.isTemplate());
                entity.setUser(rootDirectory.getUser());
                entity.setGroup(rootDirectory.getGroup());
                if (entity.getUxPermissions().size() == 0) entity.setUxPermissions(rootDirectory.getUxPermissions());
                if (entity.getRootDirectory() != null && rootDirectory != null && !entity.getRootDirectory().equals(rootDirectory)) {
                    entity.getRootDirectory().getRequests().remove(entity);
                    rootDirectory.getRequests().add(entity);
                }
            }
            em.getTransaction().commit();
            return entity.getId();
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return 0;
    }
}