/**
 * Mapping Datastore Runtime Injectection Manager :
 * provide a mapping DS configuration parser, factories and registry to inject
 * Mapping DS interface implementation dependencies.
 *   
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

package com.spectral.cc.core.mapping.ds.rim.cfg;

public class TopoDSCfgEntity {

	private String       bundleName    = null;
	private String       bundleCfgFile = null;
	
	public String getBundleName() {
		return bundleName;
	}
	
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	
	public String getBundleCfgFile() {
		return bundleCfgFile;
	}
	
	public void setBundleCfgFile(String bundleCfgFile) {
		this.bundleCfgFile = bundleCfgFile;
	}
	
	public String toString() {
		return "bundleName: " + bundleName + " ; bundleCfgFile: " + bundleCfgFile;
	}
}