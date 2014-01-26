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

import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.EndpointImpl;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.NodeImpl;
import com.spectral.cc.core.mapping.ds.service.EndpointSce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class EndpointSceImpl implements EndpointSce<EndpointImpl> {

    private static final Logger log = LoggerFactory.getLogger(EndpointSceImpl.class);

    private MappingSceImpl sce = null;

    public EndpointSceImpl(MappingSceImpl sce_) {
        sce = sce_;
    }

    @Override
    public EndpointImpl createEndpoint(String url, long parentNodeID) {
        EndpointImpl ret = sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(url);
        if (ret == null) {
            NodeImpl parentNode = sce.getGlobalRepo().getNodeRepo().findNodeByID(parentNodeID);
            if (parentNode != null) {
                ret = new EndpointImpl();
                ret.setEndpointURL(url);
                ret.setEndpointParentNode(parentNode);
                sce.getGlobalRepo().getEndpointRepo().save(ret);
                parentNode.addEnpoint(ret);
            } else {
                log.error("Endpoint parent node {} is not in the node repository !");
            }
        } else {
            // TODO: raise exception !
        }
        return ret;
    }

    @Override
    public void deleteEndpoint(long endpointID) {
        EndpointImpl remove = sce.getGlobalRepo().getEndpointRepo().findEndpointByID(endpointID);
        if (remove != null) {
            sce.getGlobalRepo().getEndpointRepo().delete(remove);
        } else {
            // TODO: raise exception
        }
    }

    @Override
    public EndpointImpl getEndpoint(long id) {
        return sce.getGlobalRepo().getEndpointRepo().findEndpointByID(id);
    }

    @Override
    public EndpointImpl getEndpoint(String URL) {
        return sce.getGlobalRepo().getEndpointRepo().findEndpointByURL(URL);
    }

    @Override
    public Set<EndpointImpl> getEndpoints(String selector) {
        Set<EndpointImpl> ret = null;
        if (selector != null) {

        } else {
            ret = sce.getGlobalRepo().getEndpointRepo().getAllEndpoints();
        }
        return ret;
    }

    @Override
    public Set<EndpointImpl> getEndpoints(String key, Object value) {
        return sce.getGlobalRepo().getEndpointRepo().findEndpointsByProperties(key, value);
    }
}