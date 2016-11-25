/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.SessionRegistry;

import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistryImpl implements SessionRegistry{

    private static ConcurrentHashMap<String, Session> sessionRegistry = new ConcurrentHashMap<>();

    @Override
    public Session get(String sessionID) {
        return sessionRegistry.get(sessionID);
    }

    @Override
    public Session put(Session session) {
        return sessionRegistry.put(session.getSessionID(), session);
    }

    @Override
    public Session remove(Session session) {
        return sessionRegistry.remove(session.getSessionID());
    }

    public static ConcurrentHashMap<String, Session> getSessionRegistry() {
        return  sessionRegistry;
    }
}
