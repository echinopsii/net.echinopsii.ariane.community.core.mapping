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

public abstract class LinkAbs implements Link {
    private String linkID = null;
    private Transport linkTransport = null;
    private Endpoint linkEndpointSource = null;
    private Endpoint linkEndpointTarget = null;
    //private Set<Link> linkSubLinks = new HashSet<Link>();
    //private Link linkUpLink = null;

    @Override
    public String getLinkID() {
        return this.linkID;
    }

    @Override
    public void setLinkID(String ID) {
        this.linkID = ID;
    }

    @Override
    public Transport getLinkTransport() {
        return this.linkTransport;
    }

    @Override
    public void setLinkTransport(Transport transport) throws MappingDSException {
        this.linkTransport = transport;
    }

    @Override
    public Endpoint getLinkEndpointSource() {
        return this.linkEndpointSource;
    }

    @Override
    public void setLinkEndpointSource(Endpoint source) throws MappingDSException {
        this.linkEndpointSource = source;
    }

    @Override
    public Endpoint getLinkEndpointTarget() {
        return this.linkEndpointTarget;
    }

    @Override
    public void setLinkEndpointTarget(Endpoint target) throws MappingDSException {
        this.linkEndpointTarget = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (getClass() != o.getClass() && !o.getClass().isAssignableFrom(getClass()))) {
            return false;
        }

        Link tmp = (Link) o;
        if (this.getLinkID() == null) {
            return super.equals(o);
        }
        return (this.getLinkID().equals(tmp.getLinkID()));
    }

    @Override
    public int hashCode() {
        return ((this.getLinkID() != null && !this.getLinkID().equals("")) ? this.getLinkID().hashCode() : super.hashCode());
    }

    @Override
    public String toString() {
        return String.format("Link{ID='%s'}", this.getLinkID());
    }
}
