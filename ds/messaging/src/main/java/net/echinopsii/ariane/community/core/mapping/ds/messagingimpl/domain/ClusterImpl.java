/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
 * Author: mathilde.ffrench@echinopsii.net
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
package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxCluster;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.Set;

public class ClusterImpl implements SProxCluster{
    @Override
    public void setClusterName(Session session, String name) throws MappingDSException {

    }

    @Override
    public boolean addClusterContainer(Session session, Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeClusterContainer(Session session, Container container) throws MappingDSException {
        return false;
    }

    @Override
    public String getClusterID() {
        return null;
    }

    @Override
    public void setClusterID(String ID) {

    }

    @Override
    public String getClusterName() {
        return null;
    }

    @Override
    public void setClusterName(String name) {

    }

    @Override
    public Set<? extends Container> getClusterContainers() {
        return null;
    }

    @Override
    public boolean addClusterContainer(Container container) throws MappingDSException {
        return false;
    }

    @Override
    public boolean removeClusterContainer(Container container) throws MappingDSException {
        return false;
    }
}
