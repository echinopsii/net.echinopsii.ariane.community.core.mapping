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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.repository;

import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSCacheEntity;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.domain.LinkImpl;
import com.spectral.cc.core.mapping.ds.repository.LinkRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkRepoImpl implements LinkRepo<LinkImpl> {

    private final static Logger log = LoggerFactory.getLogger(LinkRepoImpl.class);

    @Override
    public LinkImpl save(LinkImpl link) {
        if (link.getLinkEndpointSource() != null && link.getLinkEndpointSource().getElement() != null &&
                    link.getLinkEndpointTarget() != null && link.getLinkEndpointTarget().getElement() != null) {
            MappingDSGraphDB.saveEdgeEntity(link, link.getLinkEndpointSource().getElement(), link.getLinkEndpointTarget().getElement(),
                                                   MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY);
            log.debug("Added unicast link {} to graph({}).", new Object[]{link.toString(), MappingDSGraphDB.getEdgeMaxCursor()});
        } else if (link.getLinkEndpointSource() != null && link.getLinkEndpointSource().getElement() != null &&
                    link.getLinkTransport() != null && link.getLinkTransport().getElement() != null) {
            MappingDSGraphDB.saveEdgeEntity(link, link.getLinkEndpointSource().getElement(), link.getLinkTransport().getElement(),
                                                   MappingDSGraphPropertyNames.DD_GRAPH_EDGE_LINK_LABEL_KEY);
            log.debug("Added multicast link {} to graph({}).", new Object[]{link.toString(), MappingDSGraphDB.getEdgeMaxCursor()});
        }
        return link;
    }

    @Override
    public void delete(LinkImpl link) {
        MappingDSGraphDB.deleteEntity(link);
        log.debug("Deleted link {} from graph({}).", new Object[]{link.toString(), MappingDSGraphDB.getVertexMaxCursor()});
    }

    @Override
    public LinkImpl findLinkByID(long id) {
        LinkImpl ret = null;
        MappingDSCacheEntity entity = MappingDSGraphDB.getLink(id);
        if (entity != null) {
            if (entity instanceof LinkImpl) {
                ret = (LinkImpl) entity;
            } else {
                log.error("CONSISTENCY ERROR : entity {} is not a link.", entity.getElement().getId());
            }
        }
        return ret;
    }
}
