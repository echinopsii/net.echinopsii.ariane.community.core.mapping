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

package net.echinopsii.ariane.community.core.mapping.ds.dsl.registry;

import net.echinopsii.ariane.community.core.idm.base.model.jpa.Group;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.UXPermission;
import net.echinopsii.ariane.community.core.idm.base.model.jpa.User;
import net.echinopsii.ariane.community.core.idm.base.proxy.IDMJPAProvider;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.group.GroupsListController;
import net.echinopsii.ariane.community.core.portal.idmwat.controller.user.UsersListController;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Set;

@Component
@Instantiate
public class MappingDSLRegistryBootstrap {
    private static final Logger log = LoggerFactory.getLogger(MappingDSLRegistryBootstrap.class);
    private static final String MAPPING_DSL_REGISTRY = "Ariane Mapping DSL Registry";

    public static final String MAPPING_DSL_REGISTRY_ROOT_DIR_NAME = "MappingDSLRegistry";

    @Requires
    private IDMJPAProvider        idmJpaProvider = null;
    private static IDMJPAProvider IDMJPAProvider = null;

    @Bind
    public void bindJPAProvider(IDMJPAProvider r) {
        log.debug("Bound to IDM JPA provider...");
        idmJpaProvider = r;
        IDMJPAProvider = idmJpaProvider;
    }

    @Unbind
    public void unbindJPAProvider() {
        log.debug("Unbound from IDM JPA provider...");
        idmJpaProvider = null;
        IDMJPAProvider = idmJpaProvider;
    }

    public static IDMJPAProvider getIDMJPAProvider() {
        return IDMJPAProvider;
    }

    @Validate
    public void validate() throws Exception {
        idmJpaProvider.addSubPersistenceBundle(FrameworkUtil.getBundle(MappingDSLRegistryBootstrap.class));
        EntityManager em = idmJpaProvider.createEM();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<MappingDSLRegistryDirectory> root = countCriteria.from(MappingDSLRegistryDirectory.class);
        countCriteria = countCriteria.select(builder.count(root));
        int rowCount = (int) (long) em.createQuery(countCriteria).getSingleResult();

        if (rowCount == 0) {
            User usryoda = UsersListController.getUserByUserName(em, "yoda");
            Group grpyoda = GroupsListController.getGroupByName(em,"yoda");
            Set<UXPermission> uxDefaultPermissions = new HashSet<UXPermission>();
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_CH_PERM, UXPermission.UX_LIKE_U_ACTOR_TYPE));
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_WR_PERM, UXPermission.UX_LIKE_G_ACTOR_TYPE));
            uxDefaultPermissions.add(MappingDSLRegistryBootstrap.getIDMJPAProvider().getUXLikeResourcesPermissionsFromName(UXPermission.UX_LIKE_RD_PERM, UXPermission.UX_LIKE_O_ACTOR_TYPE));

            MappingDSLRegistryDirectory rootDir = new MappingDSLRegistryDirectory().setNameR(MAPPING_DSL_REGISTRY_ROOT_DIR_NAME).
                                                                                    setDescriptionR("The Mapping DSL Root Registry Directory").
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>()).
                                                                                    setUserR(usryoda).
                                                                                    setGroupR(grpyoda).
                                                                                    setUxPermissionsR(uxDefaultPermissions);
            em.getTransaction().begin();
            em.persist(rootDir);

            MappingDSLRegistryDirectory reqDir  = new MappingDSLRegistryDirectory().setNameR("Samples").
                                                                                    setDescriptionR("The Mapping DSL Samples Directory").
                                                                                    setRootDirectoryR(rootDir).
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>()).
                                                                                    setUserR(usryoda).
                                                                                    setGroupR(grpyoda).
                                                                                    setUxPermissionsR(uxDefaultPermissions);
            rootDir.getSubDirectories().add(reqDir);
            em.persist(reqDir);

            MappingDSLRegistryDirectory tplDir  = new MappingDSLRegistryDirectory().setNameR("Users").
                                                                                    setDescriptionR("The Mapping DSL Users Directory").
                                                                                    setRootDirectoryR(rootDir).
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>()).
                                                                                    setUserR(usryoda).
                                                                                    setGroupR(grpyoda).
                                                                                    setUxPermissionsR(uxDefaultPermissions);
            rootDir.getSubDirectories().add(tplDir);
            em.persist(tplDir);

            em.getTransaction().commit();
        }

        em.close();
        log.info("{} is started", MAPPING_DSL_REGISTRY);
    }

    @Invalidate
    public void invalidate() throws Exception {
        //TODO : hibernate plugin unplug
        log.info("{} is stopped", MAPPING_DSL_REGISTRY);
    }
}