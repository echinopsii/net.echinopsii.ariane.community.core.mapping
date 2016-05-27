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
package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public class SessionImpl implements Session {
    @Override
    public String getSessionID() {
        return null;
    }

    @Override
    public Session stop() {
        return null;
    }

    @Override
    public Session start() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Object execute(Object o, String methodName, Object[] args) throws MappingDSException {
        /*
           SEND REQ LIKE :
           String ObjectType - Mapping Domain or Mapping Service
           String ID - can be null
           String methodName
        */
        return null;
    }

    @Override
    public Session commit() throws MappingDSException {
        return null;
    }

    @Override
    public Session rollback() throws MappingDSException {
        return null;
    }
}
