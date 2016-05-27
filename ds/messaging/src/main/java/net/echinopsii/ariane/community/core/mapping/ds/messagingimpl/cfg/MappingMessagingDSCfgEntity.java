/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016  echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.messagingimpl.cfg;

import net.echinopsii.ariane.community.core.mapping.ds.cfg.MappingDSCfgEntity;

import java.util.Dictionary;

public class MappingMessagingDSCfgEntity extends MappingDSCfgEntity {

    private String momc = null;

    private Dictionary<Object, Object> momc_conf = null;

    public String getMomc() {
        return momc;
    }

    public void setMomc(String momc) {
        this.momc = momc;
    }

    public Dictionary<Object, Object> getMomc_conf() {
        return momc_conf;
    }

    public void setMomc_conf(Dictionary<Object, Object> momc_conf) {
        this.momc_conf = momc_conf;
    }

    public String toString() {
        return super.toString() +
                "\nmom cli implementation : " + momc +
                "\nmom cli configuration: " + momc_conf.toString();
    }
}
