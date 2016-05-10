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
package net.echinopsii.ariane.community.core.mapping.ds.service.tools;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;

import java.lang.reflect.Method;

public interface Session {
    public String getSessionID();

    public Session stop();

    public Session start();

    public boolean isRunning();

    public Object execute(Object o, String methodName, Object[] args) throws MappingDSException;

    public Session commit() throws MappingDSException;

    public Session rollback() throws MappingDSException;
}