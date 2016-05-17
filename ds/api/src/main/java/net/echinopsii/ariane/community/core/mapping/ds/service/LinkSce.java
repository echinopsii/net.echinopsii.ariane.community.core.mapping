/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
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

package net.echinopsii.ariane.community.core.mapping.ds.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Link;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public interface LinkSce<L extends Link> {
    public L createLink(Session session, String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException;
    public L createLink(String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException;

    public void deleteLink(Session session, String linkID) throws MappingDSException;
    public void deleteLink(String linkID) throws MappingDSException;

    public L getLink(Session session, String id) throws MappingDSException;
    public L getLink(String id);

    public Set<L> getLinks(Session session, String selector) throws MappingDSException;
    public Set<L> getLinks(String selector);
}