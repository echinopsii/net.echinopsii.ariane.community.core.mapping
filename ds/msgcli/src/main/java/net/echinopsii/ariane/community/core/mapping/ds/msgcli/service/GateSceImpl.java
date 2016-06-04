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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.GateImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxGateSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GateSceImpl implements SProxGateSce<GateImpl> {

    private static final Logger log = LoggerFactory.getLogger(GateSceImpl.class);

    @Override
    public GateImpl createGate(Session session, String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
        return null;
    }

    @Override
    public void deleteGate(Session session, String nodeID) throws MappingDSException {

    }

    @Override
    public GateImpl getGate(Session session, String id) throws MappingDSException {
        return null;
    }

    @Override
    public Set<GateImpl> getGates(Session session, String selector) throws MappingDSException {
        return null;
    }

    @Override
    public Gate createGate(String url, String name, String containerid, Boolean isPrimaryAdmin) throws MappingDSException {
        return null;
    }

    @Override
    public void deleteGate(String nodeID) throws MappingDSException {

    }

    @Override
    public Gate getGate(String id) throws MappingDSException {
        return null;
    }

    @Override
    public Set getGates(String selector) throws MappingDSException {
        return null;
    }
}
