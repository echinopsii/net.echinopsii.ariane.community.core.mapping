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
package net.echinopsii.ariane.community.core.mapping.ds;

public class MappingDSException extends Exception {

	private static final long serialVersionUID = -5737356297845448334L;

    public static String MAPPING_TIMEOUT = "Mapping Execution Timeout !";
    public static String MAPPING_OVERLOAD = "Mapping Service Overload Detected !";

	public MappingDSException() {
		super();
	}

	public MappingDSException(String message) {
        super(message);
    }

	public MappingDSException(String message, Throwable cause) {
        super(message,cause);
    }

    public MappingDSException(Throwable cause) {
        super(cause);
    }

    protected MappingDSException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
    	super(message, cause, enableSuppression, writableStackTrace);
    }
}
