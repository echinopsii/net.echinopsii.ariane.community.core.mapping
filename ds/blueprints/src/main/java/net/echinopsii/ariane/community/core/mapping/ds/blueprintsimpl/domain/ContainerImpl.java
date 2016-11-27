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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.domain;

import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Vertex;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSBlueprintsCacheEntity;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDBObjectProps;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.service.tools.SessionRegistryImpl;
import net.echinopsii.ariane.community.core.mapping.ds.cli.ClientThreadSessionRegistry;
import net.echinopsii.ariane.community.core.mapping.ds.domain.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainer;
import com.tinkerpop.blueprints.*;
import net.echinopsii.ariane.community.core.mapping.ds.domain.proxy.SProxContainerAbs;
import net.echinopsii.ariane.community.core.mapping.ds.service.tools.Session;
import net.echinopsii.ariane.community.messaging.common.MomLoggerFactory;
import org.slf4j.Logger;

public class ContainerImpl extends SProxContainerAbs implements SProxContainer, MappingDSBlueprintsCacheEntity {

	private static final Logger log = MomLoggerFactory.getLogger(ContainerImpl.class);
	
	private transient Vertex       containerVertex           = null;
	private boolean                isBeingSyncFromDB         = false;
	
    @Override
    public void setContainerName(String name) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerName(session, name);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerName() == null || !super.getContainerName().equals(name)) {
                super.setContainerName(name);
                this.synchronizeNameToDB();
                log.debug("Set container {} name to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), super.getContainerName()});
            }
        }
    }

    @Override
    public void setContainerCompany(String company) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerCompany(session, company);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerCompany() == null || !super.getContainerCompany().equals(company)) {
                super.setContainerCompany(company);
                this.synchronizeCompanyToDB();
                log.debug("Set container {} company to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), super.getContainerCompany()});
            }
        }
    }

    @Override
    public void setContainerProduct(String product) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerProduct(session, product);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerProduct() == null || !super.getContainerProduct().equals(product)) {
                super.setContainerProduct(product);
                this.synchronizeProductToDB();
                log.debug("Set container {} product to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), super.getContainerProduct()});
            }
        }
    }

    @Override
    public void setContainerType(String type) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerType(session, type);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerType() == null || !super.getContainerType().equals(type)) {
                super.setContainerType(type);
                this.synchronizeTypeToDB();
                log.debug("Set container {} type to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), super.getContainerType()});
            }
        }
    }

    @Override
	public void setContainerPrimaryAdminGate(Gate gate) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerPrimaryAdminGate(session, gate);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerPrimaryAdminGate() == null || !super.getContainerPrimaryAdminGate().equals(gate)) {
                if (gate instanceof GateImpl) {
                    super.setContainerPrimaryAdminGate(gate);
                    synchronizePrimaryAdminGateToDB();
                }
                log.debug("Set container {} gate to {}.", new Object[]{super.getContainerID(),
                        (super.getContainerPrimaryAdminGate()!=null) ? super.getContainerPrimaryAdminGate() : "null"
                });
            }
        }
	}
	
    @Override
	public void setContainerCluster(Cluster cluster) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session != null) this.setContainerCluster(session, cluster);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerCluster() == null || !super.getContainerCluster().equals(cluster)) {
                if (cluster == null || cluster instanceof ClusterImpl) {
                    if (super.getContainerCluster()!=null) super.getContainerCluster().removeClusterContainer(this);
                    super.setContainerCluster(cluster);
                    if (cluster!=null) cluster.addClusterContainer(this);
                    synchronizeClusterToDB();
                }
            }
        }
	}

    @Override
	public void addContainerProperty(String propertyKey, Object value) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session != null) this.addContainerProperty(session, propertyKey, value);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            super.addContainerProperty(propertyKey, value);
            synchronizePropertyToDB(propertyKey, value);
            log.debug("Set container {} property : ({},{})", new Object[]{super.getContainerID(),
                    propertyKey,
                    super.getContainerProperties().get(propertyKey)});
        }
	}

    @Override
    public void removeContainerProperty(String propertyKey) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session != null) removeContainerProperty(session, propertyKey);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            super.removeContainerProperty(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
    public void setContainerParentContainer(Container container) throws MappingDSException {
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) this.setContainerParentContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (super.getContainerParentContainer() == null || !super.getContainerParentContainer().equals(container)) {
                if (container == null || container instanceof ContainerImpl) {
                    Container previousContainer = super.getContainerParentContainer();
                    if (previousContainer!=null) previousContainer.removeContainerChildContainer(this);
                    super.setContainerParentContainer(container);
                    if (container!=null) container.addContainerChildContainer(this);
                    synchronizeParentContainerToDB();
                }
            }
        }
    }

    @Override
    public boolean addContainerChildContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = addContainerChildContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                try {
                    ret = super.addContainerChildContainer(container);
                    container.setContainerParentContainer(this);
                    if (ret)
                        synchronizeChildContainersToDB();
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding child container {}...", new Object[]{container.getContainerID()});
                    super.removeContainerChildContainer(container);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeContainerChildContainer(session, container);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (container instanceof ContainerImpl) {
                ret = super.removeContainerChildContainer(container);
                if (ret) {
                    container.setContainerParentContainer(null);
                    removeChildContainerFromDB((ContainerImpl) container);
                }
            }
        }
        return ret;
    }

    @Override
	public boolean addContainerNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.addContainerNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                try {
                    ret = super.addContainerNode(node);
                    if (!node.getNodeContainer().equals(this)) node.setNodeContainer(this);
                    if (ret) synchronizeNodeToDB((NodeImpl) node);
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding node {}...", new Object[]{node.getNodeID()});
                    super.removeContainerNode(node);
                    node.setNodeContainer(null);
                    MappingDSGraphDB.autorollback();
                }
            }
        }
        return ret;
	}

    @Override
	public boolean removeContainerNode(Node node) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeContainerNode(session, node);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (node instanceof NodeImpl) {
                ret = super.removeContainerNode(node);
                if (ret) {
                    if (node.getNodeContainer().equals(this)) node.setNodeContainer(null);
                    removeNodeFromDB((NodeImpl) node);
                }
            }
        }
        return ret;
	}

    @Override
	public boolean addContainerGate(Gate gate) throws MappingDSException {
		boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = addContainerGate(session, gate);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (gate instanceof GateImpl) {
                boolean addToNodes = false;
                boolean addToGates = false;
                try {
                    addToNodes = (super.getContainerNodes().contains(gate)) || super.addContainerNode(gate);
                    addToGates = (super.getContainerGates().contains(gate)) || super.addContainerGate(gate);
                    if (addToNodes && addToGates) {
                        if (gate.getNodeContainer()==null || !gate.getNodeContainer().equals(this)) gate.setNodeContainer(this);
                        synchronizeNodeToDB((NodeImpl) gate);
                        synchronizeGateToDB((GateImpl) gate);
                    } else {
                        gate.setNodeContainer(null);
                        if (addToNodes) super.removeContainerNode(gate);
                        if (addToGates) super.removeContainerGate(gate);
                    }
                } catch (MappingDSException E) {
                    E.printStackTrace();
                    log.error("Exception while adding gate {}...", new Object[]{gate.getNodeID()});
                    if (addToNodes) super.removeContainerNode(gate);
                    if (addToGates) super.removeContainerGate(gate);
                    gate.setNodeContainer(null);
                    MappingDSGraphDB.autorollback();
                }
                ret = addToNodes && addToGates;
            }
        }
        return ret;
	}

    @Override
    public boolean removeContainerGate(Gate gate) throws MappingDSException {
        boolean ret = false;
        String clientThreadName = Thread.currentThread().getName();
        String clientThreadSessionID = ClientThreadSessionRegistry.getSessionFromThread(clientThreadName);
        if (clientThreadSessionID!=null) {
            Session session = SessionRegistryImpl.getSessionRegistry().get(clientThreadSessionID);
            if (session!=null) ret = this.removeContainerGate(session, gate);
            else throw new MappingDSException("Session " + clientThreadSessionID + " not found !");
        } else {
            if (gate instanceof GateImpl) {
                boolean removedFromNodes = super.removeContainerNode(gate);
                boolean removedFromGates = super.removeContainerGate(gate);
                if (removedFromGates && removedFromNodes) {
                    gate.setNodeContainer(null);
                    removeNodeFromDB((NodeImpl) gate);
                    removeGateFromDB((GateImpl) gate);
                } else {
                    if (removedFromNodes) super.removeContainerNode(gate);
                    if (removedFromGates) super.removeContainerGate(gate);
                }
                ret = removedFromGates && removedFromNodes;
            }
        }
        return ret;
    }

    public Vertex getElement() {
		return containerVertex;
	}

	public void setElement(Element containerVertex) {
		this.containerVertex = (Vertex)containerVertex;
        super.setContainerID((String) this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
        if (this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)==null) {
            if (MappingDSGraphDB.isBlueprintsNeo4j() && this.containerVertex instanceof Neo4j2Vertex)
                ((Neo4j2Vertex) this.containerVertex).addLabel(MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE);
            this.containerVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE);
            log.debug("Container vertex has been initialized ({},{}).", new Object[]{this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
                    this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
        }
	}

    @Override
    public String getEntityCacheID() {
        return "V" + super.getContainerID();
    }

    public void synchronizeToDB() throws MappingDSException {
        synchronizeNameToDB();
        synchronizeCompanyToDB();
        synchronizeProductToDB();
        synchronizeTypeToDB();
		synchronizePropertiesToDB();
		synchronizePrimaryAdminGateToDB();
		synchronizeClusterToDB();
        synchronizeParentContainerToDB();
        synchronizeChildContainersToDB();
		synchronizeNodesToDB();
		synchronizeGatesToDB();
	}

    private void synchronizeNameToDB() {
        if (containerVertex!=null) {
            if (super.getContainerName()!=null) {
                log.debug("Synchronize container name {}...", new Object[]{super.getContainerName()});
                containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_NAME_KEY, super.getContainerName());
                MappingDSGraphDB.autocommit();
                log.debug("Synchronize container name {} done...", new Object[]{super.getContainerName()});
            }
        }
    }

    private void synchronizeCompanyToDB() {
        if (containerVertex!=null) {
            if (super.getContainerCompany()!=null) {
                log.debug("Synchronize container company {}...", new Object[]{super.getContainerCompany()});
                containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY, super.getContainerCompany());
                MappingDSGraphDB.autocommit();
                log.debug("Synchronize container company {} done...", new Object[]{super.getContainerCompany()});
            }
        }
    }

    private void synchronizeProductToDB() {
        if (containerVertex!=null) {
            if (super.getContainerProduct()!=null) {
                log.debug("Synchronize container product {}...", new Object[]{super.getContainerProduct()});
                containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY, super.getContainerProduct());
                MappingDSGraphDB.autocommit();
                log.debug("Synchronize container product {} done...", new Object[]{super.getContainerProduct()});
            }
        }
    }

	private void synchronizeTypeToDB() {
		if (containerVertex!=null) {
			if (super.getContainerType()!=null) {
				log.debug("Synchronize container type {}...", new Object[]{super.getContainerType()});
				containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY, super.getContainerType());
                MappingDSGraphDB.autocommit();
                log.debug("Synchronize container product {} done...", new Object[]{super.getContainerType()});
			}
		}
	}
	
	private void synchronizePropertiesToDB() {
		if (super.getContainerProperties()!=null && containerVertex!=null) {
            for (String key : super.getContainerProperties().keySet()) {
                Object value = super.getContainerProperties().get(key);
                synchronizePropertyToDB(key, value);
            }
		}
	}
	
	private void synchronizePropertyToDB(String key, Object value) {
		if (containerVertex!=null) {
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyToDB(containerVertex, key, value, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
	}
	
	private void synchronizePrimaryAdminGateToDB() {
		if (containerVertex!=null && super.getContainerPrimaryAdminGate()!=null && ((GateImpl)super.getContainerPrimaryAdminGate()).getElement()!=null) {
			log.debug("Synchronize container primary admin gate {}...", new Object[]{super.getContainerPrimaryAdminGate().getNodeID()});
			containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY,
                    super.getContainerPrimaryAdminGate().getNodeID());
            MappingDSGraphDB.autocommit();
            log.debug("Synchronize container primary admin gate {} done...", new Object[]{super.getContainerPrimaryAdminGate().getNodeID()});
		}
	}
	
	private void synchronizeClusterToDB() {
		if (containerVertex!=null && super.getContainerCluster()!=null && ((ClusterImpl)super.getContainerCluster()).getElement()!=null) {
			log.debug("Synchronize container cluster {}...", new Object[]{super.getContainerCluster().getClusterID()});
			containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY,
                    super.getContainerCluster().getClusterID());
            MappingDSGraphDB.autocommit();
            log.debug("Synchronize container cluster {} done...", new Object[]{super.getContainerCluster().getClusterID()});
		} else if (containerVertex!=null && super.getContainerCluster()==null &&
                containerVertex.getPropertyKeys().contains(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY)) {
            containerVertex.removeProperty(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY);
            MappingDSGraphDB.autocommit();
        }
	}

    private void synchronizeParentContainerToDB() {
        if (containerVertex!=null && super.getContainerParentContainer()!=null && ((ContainerImpl)super.getContainerParentContainer()).getElement()!=null) {
            log.debug("Synchronize container parent container {}...", new Object[]{super.getContainerParentContainer().getContainerID()});
            containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY,
                    super.getContainerParentContainer().getContainerID());
            MappingDSGraphDB.autocommit();
            log.debug("Synchronize container parent container {} done...", new Object[]{super.getContainerParentContainer().getContainerID()});
        } else if (containerVertex!=null && super.getContainerParentContainer()==null &&
                containerVertex.getPropertyKeys().contains(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY)) {
            containerVertex.removeProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

    private void synchronizeChildContainersToDB() throws MappingDSException {
        if (containerVertex!=null)
            for (Container aContainer : super.getContainerChildContainers()) synchronizeChildContainerToDB((ContainerImpl) aContainer);
    }

    private void synchronizeChildContainerToDB(ContainerImpl container) throws MappingDSException {
        if (containerVertex!=null) {
            VertexQuery query = this.containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            for (Vertex vertex : query.vertices()) {
                if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(container.getContainerID()))
                    return;
            }
            log.debug("Synchronize container child container {}...", new Object[]{container.getContainerID()});
            Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, container.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            MappingDSGraphDB.autocommit();
        }
    }

	private void synchronizeNodesToDB() throws MappingDSException {
		if (containerVertex!=null)
            for (Node aNode : super.getContainerNodes())
                synchronizeNodeToDB((NodeImpl) aNode);
	}
	
	private void synchronizeNodeToDB(NodeImpl node) throws MappingDSException {
		if (containerVertex!=null) {
			if (node instanceof GateImpl)
				return;
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(node.getNodeID()))
					return;
			}
			log.debug("Synchronize container node {}...", new Object[]{node.getNodeID()});
			Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, node.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
            MappingDSGraphDB.autocommit();
		}
	}
	
	private void synchronizeGatesToDB() throws MappingDSException {
		if (containerVertex!=null)
            for (Gate aGate : super.getContainerGates())
                synchronizeGateToDB((GateImpl) aGate);
	}
	
	private void synchronizeGateToDB(GateImpl gate) throws MappingDSException {
		if (containerVertex!=null) {
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if (vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID).equals(gate.getNodeID()))
					return;
			}
			log.debug("Synchronize container gate {}...", new Object[]{gate.getNodeID()});
			Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, gate.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
            MappingDSGraphDB.autocommit();
		}
	}
	
	public void synchronizeFromDB() throws MappingDSException {
		if (!isBeingSyncFromDB) {
			isBeingSyncFromDB = true;
			synchronizeIDFromDB();
            synchronizeNameFromDB();
            synchronizeCompanyFromDB();
            synchronizeProductFromDB();
			synchronizeTypeFromDB();
			synchronizePropertiesFromDB();
			synchronizePrimaryAdminGateFromDB();
			synchronizeClusterFromDB();
            synchronizeParentContainerFromDB();
            synchronizeChildContainersFromDB();
			synchronizeNodesFromDB();
			synchronizeGatesFromDB();
			isBeingSyncFromDB = false;
		}
	}

	private void synchronizeIDFromDB() {
		if (this.containerVertex!=null)
			super.setContainerID((String) this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
	}

    private void synchronizeNameFromDB() throws MappingDSException {
        if (this.containerVertex!=null) {
            Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_NAME_KEY);
            if (ret!=null)
                super.setContainerName((String) ret);
        }
    }

    private void synchronizeCompanyFromDB() throws MappingDSException {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY);
            if (ret!=null)
                super.setContainerCompany((String) ret);
        }
    }

    private void synchronizeProductFromDB() throws MappingDSException {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY);
            if (ret!=null)
                super.setContainerProduct((String) ret);
        }
    }

	private void synchronizeTypeFromDB() throws MappingDSException {
		if (containerVertex!=null) {
			Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY);
			if (ret!=null)
				super.setContainerType((String) ret);
		}
	}
		
	private void synchronizePropertiesFromDB() {
		if (containerVertex!=null) {
			if (super.getContainerProperties()!=null) super.getContainerProperties().clear();
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(containerVertex, super.getContainerProperties(), MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
		}
	}

    private void removePropertyFromDB(String key) {
        if (containerVertex != null) {
            log.debug("Remove container property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(containerVertex, key, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
            MappingDSGraphDB.autocommit();
        }
    }

	private void synchronizePrimaryAdminGateFromDB() throws MappingDSException {
		if (containerVertex!=null) {
			Object paGateID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY);
			if (paGateID!=null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) paGateID);
				if (entity!=null) {
					if (entity instanceof GateImpl) super.setContainerPrimaryAdminGate((Gate)entity);
					else log.error("CONSISTENCY ERROR : entity {} is not a gate.", entity.getElement().getId());
				}
			}
		}
	}
	
	private void synchronizeClusterFromDB() throws MappingDSException {
		if (containerVertex!=null) {
			Object clusterID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY);
			if (clusterID!=null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) clusterID);
				if (entity!=null) {
					if (entity instanceof ClusterImpl) super.setContainerCluster((Cluster)entity);
					else log.error("CONSISTENCY ERROR : entity {} is not a cluster.", entity.getElement().getId());
				}
			}
		}
	}

    private void synchronizeParentContainerFromDB() throws MappingDSException {
        if (containerVertex!=null) {
            Object containerID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY);
            if (containerID!=null) {
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) containerID);
                if (entity!=null) {
                    if (entity instanceof ContainerImpl) super.setContainerParentContainer((Container) entity);
                    else log.error("CONSISTENCY ERROR : entity {} is not a container.", entity.getElement().getId());
                }
            }
        }
    }

    private void synchronizeChildContainersFromDB() throws MappingDSException {
        if (containerVertex!=null) {
            VertexQuery query = containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            super.getContainerChildContainers().clear();
            for (Vertex vertex : query.vertices()) {
                ContainerImpl container = null;
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity!=null) {
                    if (entity instanceof ContainerImpl) container = (ContainerImpl)entity;
                    else log.error("CONSISTENCY ERROR : entity {} is not a container.", entity.getElement().getId());
                }
                if (container!=null)
                    super.addContainerChildContainer(container);
            }
        }
    }

    private void removeChildContainerFromDB(ContainerImpl container) {
        if (this.containerVertex!=null && container.getElement()!=null) {
            VertexQuery query = this.containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.IN).equals(container.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }
	
	private void synchronizeNodesFromDB() throws MappingDSException {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
            super.getContainerNodes().clear();
			for (Vertex vertex : query.vertices()) {
				NodeImpl node = null;
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
				if (entity!=null) {
					if (entity instanceof NodeImpl) node = (NodeImpl)entity;
					else log.error("CONSISTENCY ERROR : entity {} is not a node.", entity.getElement().getId());
				}
				if (node!=null)
					super.addContainerNode(node);
			}
		}
	}
	
	private void removeNodeFromDB(NodeImpl node) {
		if (this.containerVertex!=null && node.getElement()!=null) {
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
			for (Edge edge : query.edges()) {
				if (edge.getVertex(Direction.IN).equals(node.getElement())) {
					MappingDSGraphDB.getGraph().removeEdge(edge);
				}						
			}
            MappingDSGraphDB.autocommit();
		}
	}

	private void synchronizeGatesFromDB() throws MappingDSException {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
            super.getContainerGates().clear();
			for (Vertex vertex : query.vertices()) {
				GateImpl gate = null;
                MappingDSBlueprintsCacheEntity entity = MappingDSGraphDB.getVertexEntity((String) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
				if (entity!=null) {
					if (entity instanceof GateImpl) gate = (GateImpl)entity;
					else log.error("CONSISTENCY ERROR : entity {} is not a gate.", entity.getElement().getId());
				}
				if (gate!=null) {
					super.addContainerNode(gate);
					super.addContainerGate(gate);
				}
			}
		}
	}

    private void removeGateFromDB(GateImpl node) {
        if (this.containerVertex!=null && node.getElement()!=null) {
            VertexQuery query = this.containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
            for (Edge edge : query.edges()) {
                if (edge.getVertex(Direction.IN).equals(node.getElement())) {
                    MappingDSGraphDB.getGraph().removeEdge(edge);
                }
            }
            MappingDSGraphDB.autocommit();
        }
    }
}