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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.cfg;

public class TopoDSCfgEntity {
	private String       blueprintsImplementation = null;
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
	public String toString() {
		return "blueprintsConfiguration: \n\t" + blueprintsImplementation + 
			   " ; \n\tblueprintsURL: " + blueprintsURL + 
			   " ; \n\tblueprintsUser: " + blueprintsUser +
			   " ; \n\tblueprintsPasswd : " + ((blueprintsPassword!=null) ? "******" : "null");
	}
}