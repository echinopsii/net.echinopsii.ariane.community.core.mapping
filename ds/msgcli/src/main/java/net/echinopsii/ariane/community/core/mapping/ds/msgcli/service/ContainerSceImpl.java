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
package net.echinopsii.ariane.community.core.mapping.ds.msgcli.service;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.domain.ContainerImpl;
import net.echinopsii.ariane.community.core.mapping.ds.msgcli.momsp.MappingMsgcliMomSP;
import net.echinopsii.ariane.community.core.mapping.ds.service.ContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.MappingSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxContainerSceAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.proxy.SProxMappingSce;
import net.echinopsii.ariane.community.messaging.api.AppMsgWorker;
import net.echinopsii.ariane.community.messaging.api.MomMsgTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContainerSceImpl extends SProxContainerSceAbs<ContainerImpl> {

    private static final Logger log = LoggerFactory.getLogger(ContainerSceImpl.class);

    @Override
    public Container createContainer(String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_CREATE_CONTAINER);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_NAME, primaryAdminGateName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    @Override
    public Container createContainer(String name, String primaryAdminURL, String primaryAdminGateName) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_CREATE_CONTAINER);
        message.put(ContainerSce.PARAM_CONTAINER_NAME, name);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_NAME, primaryAdminGateName);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    @Override
    public Container createContainer(String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_CREATE_CONTAINER);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_NAME, primaryAdminGateName);
        message.put(ContainerSce.PARAM_CONTAINER_PCO_ID, parentContainer.getContainerID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    @Override
    public Container createContainer(String name, String primaryAdminURL, String primaryAdminGateName, Container parentContainer) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_CREATE_CONTAINER);
        message.put(ContainerSce.PARAM_CONTAINER_NAME, name);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_NAME, primaryAdminGateName);
        message.put(ContainerSce.PARAM_CONTAINER_PCO_ID, parentContainer.getContainerID());
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    @Override
    public void deleteContainer(String primaryAdminURL) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_DELETE_CONTAINER);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
    }

    @Override
    public Container getContainer(String id) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_GET_CONTAINER);
        message.put(MappingSce.GLOBAL_PARAM_OBJ_ID, id);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    @Override
    public Container getContainerByPrimaryAdminURL(String primaryAdminURL) throws MappingDSException {
        ContainerImpl container = new ContainerImpl();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxContainerSce.OP_GET_CONTAINER_BY_PRIMARY_ADMIN_URL);
        message.put(ContainerSce.PARAM_CONTAINER_PAG_URL, primaryAdminURL);

        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);
        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, container.getContainerReplyWorker());

        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");

        return container;
    }

    class getContainerWorker implements AppMsgWorker {
        @Override
        public Map<String, Object> apply(Map<String, Object> message) {
            Set<ContainerImpl> clusters = null;
            int rc = (int) message.get(MomMsgTranslator.MSG_RC);
            if (rc == 0) {
                try {
                    String body = null;
                    if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof String)
                        body = (String) message.get(MomMsgTranslator.MSG_BODY);
                    else if (message.get(MomMsgTranslator.MSG_BODY) != null && message.get(MomMsgTranslator.MSG_BODY) instanceof byte[])
                        body = new String((byte[]) message.get(MomMsgTranslator.MSG_BODY));

                    clusters = new HashSet<>();
                    for (ContainerJSON.JSONDeserializedContainer jsonDeserializedCluster : ContainerJSON.JSON2Containers(body)) {
                        ContainerImpl container = new ContainerImpl();
                        container.setClusterID(jsonDeserializedCluster.getContainerID());
                        container.setContainerName(jsonDeserializedCluster.getContainerName());
                        //container.setClusterContainersID(jsonDeserializedCluster.getClusterContainersID());
                        clusters.add(container);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else ContainerSceImpl.log.error("Error returned by Ariane Mapping Service ! " + message.get(MomMsgTranslator.MSG_ERR));
            message.put("RET", clusters);
            return message;
        }
    }

    @Override
    public Set getContainers(String selector) throws MappingDSException {
        Set<Container> ret = new HashSet<>();

        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);

        Map<String, Object> message = new HashMap<>();
        message.put(MappingSce.GLOBAL_OPERATION_FDN, SProxClusterSce.OP_GET_CLUSTERS);
        //message.put(MappingSce.GLOBAL_PARAM_SELECTOR, selector);
        if (clientThreadSessionID!=null) message.put(SProxMappingSce.SESSION_MGR_PARAM_SESSION_ID, clientThreadSessionID);

        Map<String, Object> retMsg = MappingMsgcliMomSP.getSharedMoMReqExec().RPC(message, ContainerSce.Q_MAPPING_CONTAINER_SERVICE, new getContainerWorker());
        int rc = (int)retMsg.get(MomMsgTranslator.MSG_RC);
        if (rc != 0) throw new MappingDSException("Ariane server raised an error... Check your logs !");
        ret.addAll((Collection<? extends Container>) retMsg.get("RET"));

        return ret;
    }
}
