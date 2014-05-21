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

package net.echinopsii.ariane.core.mapping.ds.blueprintsimpl.cfg;

public class MappingDSCfgEntity {
    private String       cacheConfigFile          = null;

    private String       blueprintsImplementation = null;

    private String       blueprintsGraphPath      = null;
    private String       blueprintsNeoConfigFile  = null;

	private String       blueprintsURL            = null;
	private String       blueprintsUser           = null;
	private String       blueprintsPassword       = null;

	public String getBlueprintsImplementation() {
		return blueprintsImplementation;
	}
	public void setBlueprintsImplementation(String blueprintsImplementation) {
		this.blueprintsImplementation = blueprintsImplementation;
	}

	public String getBlueprintsURL() {
		return blueprintsURL;
	}
	public void setBlueprintsURL(String blueprintsURL) {
		this.blueprintsURL = blueprintsURL;
	}	

	public String getBlueprintsUser() {
		return blueprintsUser;
	}
	public void setBlueprintsUser(String blueprintsUser) {
		this.blueprintsUser = blueprintsUser;
	}

	public String getBlueprintsPassword() {
		return blueprintsPassword;
	}
	public void setBlueprintsPassword(String blueprintsPassword) {
		this.blueprintsPassword = blueprintsPassword;
	}

    public String getBlueprintsGraphPath() {
        return blueprintsGraphPath;
    }
    public void setBlueprintsGraphPath(String blueprintsGraphPath) {
        this.blueprintsGraphPath = blueprintsGraphPath;
    }

    public String getBlueprintsNeoConfigFile() {
        return blueprintsNeoConfigFile;
    }
    public void setBlueprintsNeoConfigFile(String blueprintsNeoConfigFile) {
        this.blueprintsNeoConfigFile = blueprintsNeoConfigFile;
    }

    public String getCacheConfigFile() {
        return cacheConfigFile;
    }
    public void setCacheConfigFile(String cacheConfigFile) {
        this.cacheConfigFile = cacheConfigFile;
    }

    public String toString() {
		return "cacheConfig File: " + cacheConfigFile +
               "\nblueprintsConfiguration:" +
               "\n\t + blueprintsImplementation:" + blueprintsImplementation +
               " ; \n\tblueprintsGraphPath: " + blueprintsGraphPath +
               " ; \n\tblueprintsNeoConfigFile: " + blueprintsNeoConfigFile +
			   " ; \n\tblueprintsURL: " + blueprintsURL + 
			   " ; \n\tblueprintsUser: " + blueprintsUser +
			   " ; \n\tblueprintsPasswd: " + ((blueprintsPassword!=null) ? "******" : "null");
	}
}