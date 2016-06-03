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
package net.echinopsii.ariane.community.core.mapping.ds.domain.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

public abstract class SProxContainerAbs extends ContainerAbs implements SProxContainer {

    @Override
    public void setContainerName(Session session, String name) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_NAME, new Object[]{name});
    }

    @Override
    public void setContainerCompany(Session session, String company) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_COMPANY, new Object[]{company});
    }

    @Override
    public void setContainerProduct(Session session, String product) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_PRODUCT, new Object[]{product});
    }

    @Override
    public void setContainerType(Session session, String type) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_TYPE, new Object[]{type});
    }

    @Override
    public void setContainerPrimaryAdminGate(Session session, Gate gate) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_PRIMARY_ADMIN_GATE, new Object[]{gate});
    }

    @Override
    public void setContainerCluster(Session session, Cluster cluster) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_CLUSTER, new Object[]{cluster});
    }

    @Override
    public void addContainerProperty(Session session, String propertyKey, Object value) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ADD_CONTAINER_PROPERTY, new Object[]{propertyKey, value});
    }

    @Override
    public void removeContainerProperty(Session session, String propertyKey) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, REMOVE_CONTAINER_PROPERTY, new Object[]{propertyKey});
    }

    @Override
    public void setContainerParentContainer(Session session, Container container) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, SET_CONTAINER_PARENT_CONTAINER, new Object[]{container});
    }

    @Override
    public boolean addContainerChildContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_CONTAINER_CHILD_CONTAINER, new Object[]{container});
        return ret;
    }

    @Override
    public boolean removeContainerChildContainer(Session session, Container container) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_CONTAINER_CHILD_CONTAINER, new Object[]{container});
        return ret;
    }

    @Override
    public boolean addContainerNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean)session.execute(this, ADD_CONTAINER_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean removeContainerNode(Session session, Node node) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_CONTAINER_NODE, new Object[]{node});
        return ret;
    }

    @Override
    public boolean addContainerGate(Session session, Gate service) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, ADD_CONTAINER_GATE, new Object[]{service});
        return ret;
    }

    @Override
    public boolean removeContainerGate(Session session, Gate service) throws MappingDSException {
        boolean ret = false;
        if (session!=null && session.isRunning())
            ret = (boolean) session.execute(this, REMOVE_CONTAINER_GATE, new Object[]{service});
        return ret;
    }


}
