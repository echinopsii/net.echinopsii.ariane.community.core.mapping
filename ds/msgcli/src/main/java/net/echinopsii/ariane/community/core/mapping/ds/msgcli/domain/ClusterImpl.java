/**
 * Mapping Datastore Messaging Driver Implementation :
 * provide a Mapping DS domain, repository and service messaging driver implementation
 * Copyright (C) 2016 echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxClusterAbs;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ClusterImpl extends SProxClusterAbs {

    class ClusterReplyWorker implements AppMsgWorker {
        private ClusterImpl cluster = null;

        public ClusterReplyWorker(ClusterImpl cluster) {
            this.cluster = cluster;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            return null;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);
    private AppMsgWorker clusterReplyWorker = new ClusterReplyWorker(this);
    private List<String> clusterContainersID;

    public AppMsgWorker getClusterReplyWorker() {
        return clusterReplyWorker;
    }

    public List<String> getClusterContainersID() {
        return clusterContainersID;
    }

    public void setClusterContainersID(List<String> clusterContainersID) {
        this.clusterContainersID = clusterContainersID;
    }

    @Override
    public void setClusterName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setClusterName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            // check change needs before requesting msg operation
            if (super.getClusterName() == null || !super.getClusterName().equals(name)) {
                //
            }
        }
    }

    @Override
    public boolean addClusterContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addClusterContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                //
            }
        }
        return ret;
    }

    @Override
    public boolean removeClusterContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeClusterContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                //
            }
        }
        return ret;
    }
}
