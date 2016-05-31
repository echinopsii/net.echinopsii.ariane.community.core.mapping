/**
 * Mapping Messaging Server
 * Configuration entity
 * Copyright (C) 27/05/16 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.msgsrv.cfg;

import net.echinopsii.ariane.community.core.mapping.ds.cfg.MappingDSCfgEntity;

import java.util.Dictionary;

public class MappingMsgsrvCfgEntity extends MappingDSCfgEntity {

    private String momc = null;

    private Dictionary<Object, Object> momCliConf = null;

    public String getMomc() {
        return momc;
    }

    public void setMomc(String momc) {
        this.momc = momc;
    }

    public Dictionary<Object, Object> getMomCliConf() {
        return momCliConf;
    }

    public void setMomCliConf(Dictionary<Object, Object> momCliConf) {
        this.momCliConf = momCliConf;
    }

    public String toString() {
        return super.toString() +
                "\nmom cli implementation : " + momc +
                "\nmom cli configuration: " + momCliConf.toString();
    }
}
