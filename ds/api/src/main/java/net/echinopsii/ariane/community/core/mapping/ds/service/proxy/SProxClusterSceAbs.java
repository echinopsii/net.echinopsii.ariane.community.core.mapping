/**
 * Mapping Datastore Interface :
 * provide a Mapping DS domain, repository and service interfaces
 * Copyright (C) 2016  echinopsii
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
package net.echinopsii.ariane.community.core.mapping.ds.service.proxy;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxCluster;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ClusterJSON;
import net.echinopsii.ariane.community.core.mapping.ds.service.ClusterSce;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.DeserializedPushResponse;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SProxClusterSceAbs<CL extends Cluster> implements SProxClusterSce {

    public static DeserializedPushResponse pushDeserializedCluster(ClusterJSON.JSONDeserializedCluster jsonDeserializedCluster,
                                                                   Session mappingSession,
                                                                   SProxMappingSce mappingSce) throws MappingDSException {
        DeserializedPushResponse ret = new DeserializedPushResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        List<Container> reqContainers = new ArrayList<>();
        if (jsonDeserializedCluster.getClusterContainersID()!=null && jsonDeserializedCluster.getClusterContainersID().size()>0) {
            for (String id : jsonDeserializedCluster.getClusterContainersID()) {
                Container container;

                if (mappingSession!=null)
                    container = mappingSce.getContainerSce().getContainer(mappingSession, id);
                else container = mappingSce.getContainerSce().getContainer(id);

                if (container != null) reqContainers.add(container);
                else {
                    ret.setErrorMessage("Request Error : container with provided ID " + id + " was not found.");
                    break;
                }
            }
        }

        // LOOK IF CLUSTER MAYBE UPDATED OR CREATED
        Cluster deserializedCluster = null;
        if (ret.getErrorMessage()==null && jsonDeserializedCluster.getClusterID()!=null) {
            if (mappingSession!=null)
                deserializedCluster = mappingSce.getClusterSce().getCluster(mappingSession, jsonDeserializedCluster.getClusterID());
            else deserializedCluster = mappingSce.getClusterSce().getCluster(jsonDeserializedCluster.getClusterID());
            if (deserializedCluster == null) ret.setErrorMessage("Request Error : cluster with provided ID " + jsonDeserializedCluster.getClusterID() + " was not found.");
        }

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage()==null) {
            if (deserializedCluster==null)
                if (mappingSession!=null) deserializedCluster = mappingSce.getClusterSce().createCluster(mappingSession, jsonDeserializedCluster.getClusterName());
                else deserializedCluster = mappingSce.getClusterSce().createCluster(jsonDeserializedCluster.getClusterName());
            else if (jsonDeserializedCluster.getClusterName()!=null)
                if (mappingSession!=null) ((SProxCluster)deserializedCluster).setClusterName(mappingSession, jsonDeserializedCluster.getClusterName());
                else deserializedCluster.setClusterName(jsonDeserializedCluster.getClusterName());

            if (jsonDeserializedCluster.getClusterContainersID() != null) {
                List<Container> containersToDelete = new ArrayList<>();
                for (Container containerToDel : deserializedCluster.getClusterContainers())
                    if (!reqContainers.contains(containerToDel))
                        containersToDelete.add(containerToDel);
                for (Container containerToDel : containersToDelete)
                    if (mappingSession!=null) ((SProxCluster)deserializedCluster).removeClusterContainer(mappingSession, containerToDel);
                    else deserializedCluster.removeClusterContainer(containerToDel);
                for (Container containerToAdd : reqContainers)
                    if (mappingSession!=null) ((SProxCluster)deserializedCluster).addClusterContainer(mappingSession, containerToAdd);
                    else deserializedCluster.addClusterContainer(containerToAdd);
            }

            ret.setDeserializedObject(deserializedCluster);
        }

        return ret;
    }

    @Override
    public CL createCluster(Session session, String clusterName) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_CREATE_CLUSTER, new Object[]{clusterName});
        return ret;
    }

    @Override
    public void deleteCluster(Session session, String clusterName) throws MappingDSException {
        if (session!=null && session.isRunning())
            session.execute(this, ClusterSce.OP_DELETE_CLUSTER, new Object[]{clusterName});
    }

    @Override
    public CL getCluster(Session session, String clusterID) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_GET_CLUSTER, new Object[]{clusterID});
        return ret;
    }

    @Override
    public CL getClusterByName(Session session, String clusterName) throws MappingDSException {
        CL ret = null;
        if (session != null && session.isRunning())
            ret = (CL) session.execute(this, ClusterSce.OP_GET_CLUSTER_BY_NAME, new Object[]{clusterName});
        return ret;
    }

    @Override
    public Set<CL> getClusters(Session session, String selector) throws MappingDSException {
        Set<CL> ret = null;
        if (session != null && session.isRunning())
            ret = (Set<CL>)session.execute(this, ClusterSce.OP_GET_CLUSTERS, new Object[]{selector});
        return ret;
    }
}
