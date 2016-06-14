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
import net.echinopsii.ariane.community.core.mapping.ds.domain.Transport;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.TransportImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxTransportSceAbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TransportSceImpl extends SProxTransportSceAbs<TransportImpl> {

    private static final Logger log = LoggerFactory.getLogger(TransportSceImpl.class);

    @Override
    public Transport createTransport(String transportName) throws MappingDSException {
        return null;
    }

    @Override
    public void deleteTransport(String transportID) throws MappingDSException {

    }

    public static Transport internalGetTransport(String transportID) throws MappingDSException {
        return null;
    }

    @Override
    public Transport getTransport(String transportID) throws MappingDSException {
        return internalGetTransport(transportID);
    }

    @Override
    public Set getTransports(String selector) throws MappingDSException {
        return null;
    }
}
