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
    String Q_MAPPING_LINK_SERVICE = "ARIANE_MAPPING_LINK_SERVICE_Q";

    String OP_CREATE_LINK = "createLink";
    String OP_DELETE_LINK = "deleteLink";
    String OP_GET_LINK = "getLink";
    String OP_GET_LINKS = "getLinks";

    String PARAM_LINK_SEPID = "SEPID";
    String PARAM_LINK_TEPID = "TEPID";

    L createLink(String sourceEndpointID, String targetEndpointID, String transportID) throws MappingDSException;

    void deleteLink(String linkID) throws MappingDSException;

    L getLink(String id) throws MappingDSException;

    Set<L> getLinks(String selector) throws MappingDSException;
}