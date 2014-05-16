/**
 * Mapping Datastore Blueprints Implementation :
 * provide a Mapping DS domain, repository and service blueprints implementation
 * Copyright (C) 2013  Mathilde Ffrench
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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.service;

import com.spectral.cc.core.mapping.ds.MappingDSException;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.GateImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.repository.ContainerRepoImpl;
import com.spectral.cc.core.mapping.ds.service.ContainerSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ContainerSceImpl implements ContainerSce<ContainerImpl> {

    private static final Logger log = LoggerFactory.getLogger(ContainerSceImpl.class);

    private MappingSceImpl sce = null;

    public ContainerSceImpl(MappingSceImpl sce_) {
        this.sce = sce_;
    }

    @Override
    public ContainerImpl createContainer(String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        ContainerImpl ret = sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
        if (ret == null) {
            ret = new ContainerImpl();
            sce.getGlobalRepo().getContainerRepo().save(ret);

            try {
                GateImpl primaryAdminService = sce.getGateSce().createGate(primaryAdminURL, primaryAdminGateName, ret.getContainerID(), true);
                log.debug("Container primary gate ({}) saved !", primaryAdminService.getNodeName());
                ret.setContainerPrimaryAdminGate(primaryAdminService);
            } catch (MappingDSException e) {
                try {
                    deleteContainer(primaryAdminURL);
                    log.error("Unable to create container gate : container has been deleted...");
                } catch (MappingDSException e1) {
                    log.error("Unable to remove previously created erronous container.");
                }
                throw new MappingDSException("Unable to create container " + primaryAdminURL + " : gate creation failed.");
            }
            log.debug("Container {} with primaryAdminURL ({}) has been saved.",
                             new Object[]{ret.getContainerID(), ret.getContainerPrimaryAdminGateURL()});
        } else {
            log.debug("Container for this primaryAdminURL ({}) already exist.", new Object[]{primaryAdminURL});
        }
        return ret;
    }

    @Override
    public void deleteContainer(String primaryAdminURL) throws MappingDSException {
        ContainerImpl remove = sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
        if (remove != null) {
            sce.getGlobalRepo().getContainerRepo().delete(remove);
        } else {
            throw new MappingDSException("Unable to remove container with primary admin URL " + primaryAdminURL + ": container not found .");
        }
    }

    @Override
    public ContainerImpl getContainer(long id) {
        return sce.getGlobalRepo().getContainerRepo().findContainerByID(id);
    }

    @Override
    public ContainerImpl getContainer(String primaryAdminURL) {
        return sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
    }

    @Override
    public Set<ContainerImpl> getContainers(String selector) {
        //TODO : manage selector - check graphdb query
        return ContainerRepoImpl.getRepository();
    }
}