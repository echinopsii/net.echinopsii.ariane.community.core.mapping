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
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxEndpointSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class EndpointSceImpl extends SProxEndpointSceAbs<EndpointImpl> {

    private static final Logger log = LoggerFactory.getLogger(EndpointSceImpl.class);

    private MappingSceImpl sce = null;

    public EndpointSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public EndpointImpl createEndpoint(String url, String parentNodeID) throws MappingDSException {
        EndpointImpl ret = null;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = createEndpoint(session, url, parentNodeID);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            ret = sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(url);
            if (ret == null) {
                NodeImpl parentNode = sce.getGlobalRepo().getNodeRepo().findNodeByID(parentNodeID);
                if (parentNode != null) {
                    ret = new EndpointImpl();
                    ret.setEndpointURL(url);
                    sce.getGlobalRepo().getEndpointRepo().save(ret);
                    ret.setEndpointParentNode(parentNode);
                } else {
                    throw new MappingDSException("Endpoint creation failed : provided parent node " + parentNodeID + " doesn't exists.");
                }
            }
        }
        return ret;
    }

    @Override
    public void deleteEndpoint(String endpointID) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) deleteEndpoint(session, endpointID);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            EndpointImpl remove = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(endpointID);
            if (remove != null) {
                sce.getGlobalRepo().getEndpointRepo().delete(remove);
            } else {
                throw new MappingDSException("Unable to remove endpoint with id " + endpointID + ": endpoint not found.");
            }
        }
    }

    @Override
    public EndpointImpl getEndpoint(String id) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getEndpoint(session, id);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getEndpointRepo().findEndpointByID(id);
    }

    @Override
    public EndpointImpl getEndpointByURL(String URL) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getEndpointByURL(session, URL);//
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(URL);
    }

    @Override
    public Set<EndpointImpl> getEndpoints(String selector) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getEndpoints(session, selector);//
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            Set<EndpointImpl> ret = null;
            if (selector != null) ret = sce.getGlobalRepo().getEndpointRepo().findEndpointsBySelector(selector);
            else ret = sce.getGlobalRepo().getEndpointRepo().getAllEndpoints();
            return ret;
        }
    }

    @Override
    public Set<EndpointImpl> getEndpoints(String key, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = sce.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) return getEndpoints(session, key, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else return sce.getGlobalRepo().getEndpointRepo().findEndpointsByProperties(key, value);
    }
}