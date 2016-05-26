/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
 *
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
package net.echinopsii.ariane.community.core.mapping.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;

import java.util.HashSet;
import java.util.Set;

public abstract class ClusterAbs implements Cluster {
    private String clusterID = null;
    private String clusterName = null;
    private Set<Container> clusterContainers = new HashSet<>();

    @Override
    public String getClusterID() {
        return this.clusterID;
    }

    @Override
    public void setClusterID(String ID) {
        this.clusterID = ID;
    }

    @Override
    public String getClusterName() {
        return this.clusterName;
    }

    @Override
    public void setClusterName(String name) throws MappingDSException {
        this.clusterName = name;
    }

    @Override
    public Set<Container> getClusterContainers() {
        return this.clusterContainers;
    }

    @Override
    public boolean addClusterContainer(Container container) throws MappingDSException {
        return this.clusterContainers.add(container);
    }

    @Override
    public boolean removeClusterContainer(Container container) throws MappingDSException {
        return this.clusterContainers.remove(container);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClusterAbs tmp = (ClusterAbs) o;
        if (this.clusterName == null || this.clusterID == null) {
            return super.equals(o);
        }
        return (clusterName.equals(tmp.getClusterName()) && clusterID.equals(tmp.getClusterID()));
    }

    @Override
    public int hashCode() {
        return (clusterID != null && !clusterID.equals("")) ? clusterID.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Cluster{ID='%s', name='%s'}", this.clusterID, this.clusterName);
    }
}
