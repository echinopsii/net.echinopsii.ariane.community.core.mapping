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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.GateImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.repository.ContainerRepoImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

import java.util.Set;

public class ContainerSceImpl extends SProxContainerSceAbs<ContainerImpl> {

    private static final Logger log = MomLoggerFactory.getLogger(ContainerSceImpl.class);

    private MappingSceImpl sce = null;

    public ContainerSceImpl(MappingSceImpl sce_) {
        this.sce = sce_;
    }

    @Override
    public ContainerImpl createContainer(String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        ContainerImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createContainer(session, primaryAdminURL, primaryAdminGateName);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ret = sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
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
        }
        return ret;
    }

    @Override
    public ContainerImpl createContainer(String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        ContainerImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createContainer(session, name, primaryAdminURL, primaryAdminGateName);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ret = this.createContainer(primaryAdminURL, primaryAdminGateName);
            ret.setContainerName(name);
        }
        return ret;
    }

    @Override
    public ContainerImpl createContainer(String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        ContainerImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createContainer(session, primaryAdminURL, primaryAdminGateName, parentContainer);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ret = this.createContainer(primaryAdminURL, primaryAdminGateName);
            parentContainer.addContainerChildContainer(ret);
        }
        return ret;
    }

    @Override
    public ContainerImpl createContainer(String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        ContainerImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createContainer(session, primaryAdminURL, primaryAdminGateName, parentContainer);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ret = this.createContainer(primaryAdminURL, primaryAdminGateName);
            ret.setContainerName(name);
            parentContainer.addContainerChildContainer(ret);
        }
        return ret;
    }

    @Override
    public void deleteContainer(String primaryAdminURL) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) deleteContainer(session, primaryAdminURL);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ContainerImpl remove = sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
            if (remove != null) {
                sce.getGlobalRepo().getContainerRepo().delete(remove);
            } else {
                throw new MappingDSException("Unable to remove container with primary admin URL " + primaryAdminURL + ": container not found .");
            }
        }
    }

    @Override
    public ContainerImpl getContainer(String id) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getContainer(session, id);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getContainerRepo().findContainerByID(id);
    }

    @Override
    public ContainerImpl getContainerByPrimaryAdminURL(String primaryAdminURL) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getContainerByPrimaryAdminURL(session, primaryAdminURL);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getContainerRepo().findContainersByPrimaryAdminURL(primaryAdminURL);
    }

    @Override
    public Set<ContainerImpl> getContainers(String selector) throws MappingDSException {
        //TODO : manage selector - check graphdb query
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getContainers(session, selector);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return ContainerRepoImpl.getRepository();
    }
}