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

import net.echinopsii.ariane.community.core.idm.base.proxy.IDMJPAProvider;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryDirectory;
import net.echinopsii.ariane.community.core.mapping.ds.dsl.registry.model.MappingDSLRegistryRequest;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;

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
            MappingDSLRegistryDirectory rootDir = new MappingDSLRegistryDirectory().setNameR(MAPPING_DSL_REGISTRY_ROOT_DIR_NAME).
                                                                                    setDescriptionR("The Mapping DSL Root Registry Directory").
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>());
            em.getTransaction().begin();
            em.persist(rootDir);

            MappingDSLRegistryDirectory reqDir  = new MappingDSLRegistryDirectory().setNameR("Requests").
                                                                                    setDescriptionR("The Mapping DSL Requests Directory").
                                                                                    setRootDirectoryR(rootDir).
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>());
            rootDir.getSubDirectories().add(reqDir);
            em.persist(reqDir);

            MappingDSLRegistryDirectory tplDir  = new MappingDSLRegistryDirectory().setNameR("Templates").
                                                                                    setDescriptionR("The Mapping DSL Templates Directory").
                                                                                    setRootDirectoryR(rootDir).
                                                                                    setSubDirectoriesR(new HashSet<MappingDSLRegistryDirectory>()).
                                                                                    setRequestsR(new HashSet<MappingDSLRegistryRequest>());
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