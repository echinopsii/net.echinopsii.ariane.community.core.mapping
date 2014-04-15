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

package com.spectral.cc.core.mapping.ds.blueprintsimpl.domain;

import com.spectral.cc.core.mapping.ds.MappingDSGraphPropertyNames;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.*;
import com.spectral.cc.core.mapping.ds.domain.Cluster;
import com.spectral.cc.core.mapping.ds.domain.Container;
import com.spectral.cc.core.mapping.ds.domain.Gate;
import com.spectral.cc.core.mapping.ds.domain.Node;
import com.tinkerpop.blueprints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContainerImpl implements Container, MappingDSCacheEntity {

	private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);
	
	private long                   containerID               = 0;
	private String                 containerCompany          = null;
    private String                 containerProduct          = null;
    private String                 containerType             = null;
	private GateImpl               containerPrimaryAdminGate = null;
	
	private ClusterImpl            containerCluster          = null;

    private ContainerImpl          containerParentContainer  = null;
    private Set<ContainerImpl>     containerChildContainers  = new HashSet<ContainerImpl>();
	
	private HashMap<String,Object> containerProperties       = null;
	
	private Set<NodeImpl>          containerNodes            = new HashSet<NodeImpl>();
	private Set<GateImpl>          containerGates            = new HashSet<GateImpl>();
	
	private Vertex                 containerVertex           = null;
	private boolean                isBeingSyncFromDB         = false;
	
	@Override
	public long getContainerID() {
		return this.containerID;
	}

    @Override
    public String getContainerCompany() {
        return containerCompany;
    }

    @Override
    public void setContainerCompany(String company) {
        if (this.containerCompany == null || !this.containerCompany.equals(company)) {
            this.containerCompany = company;
            this.synchronizeCompanyToDB();
            log.debug("Set container {} type to {}.", new Object[]{((this.containerVertex!=null)?this.containerVertex.getId():0),this.containerType});
        }
    }

    @Override
    public String getContainerProduct() {
        return this.containerProduct;
    }

    @Override
    public void setContainerProduct(String product) {
        if (this.containerProduct == null || !this.containerProduct.equals(product)) {
            this.containerProduct = product;
            this.synchronizeProductToDB();
            log.debug("Set container {} product to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), this.containerProduct});
        }
    }

    @Override
    public String getContainerType() {
        return this.containerType;
    }

    @Override
    public void setContainerType(String type) {
        if (this.containerType == null || !this.containerType.equals(type)) {
            this.containerType = type;
            this.synchronizeTypeToDB();
            log.debug("Set container {} type to {}.", new Object[]{((this.containerVertex != null) ? this.containerVertex.getId() : 0), this.containerType});
        }
    }

	@Override
	public String getContainerPrimaryAdminGateURL() {
		String ret = null;
		if (this.containerPrimaryAdminGate!=null)
			ret = containerPrimaryAdminGate.getNodePrimaryAdminEndpoint().getEndpointURL();
		return ret;
	}

	@Override
	public GateImpl getContainerPrimaryAdminGate() {
		return this.containerPrimaryAdminGate;
	}

	@Override
	public void setContainerPrimaryAdminGate(Gate gate) {
		if (this.containerPrimaryAdminGate==null || !this.containerPrimaryAdminGate.equals(gate)) {
			if (gate instanceof GateImpl) {
				this.containerPrimaryAdminGate = (GateImpl) gate;
				synchronizePrimaryAdminGateToDB();
			}
			log.debug("Set container {} gate to {}.", new Object[]{this.containerID,this.containerPrimaryAdminGate.toString()});
		}
	}
	
	@Override
	public ClusterImpl getContainerCluster() {
		return this.containerCluster;
	}

	@Override
	public void setContainerCluster(Cluster cluster) {
		if (this.containerCluster == null || !this.containerCluster.equals(cluster)) {
			if (cluster instanceof ClusterImpl) {
				this.containerCluster = (ClusterImpl) cluster ;
				synchronizeClusterToDB();
			}
		}
	}

	@Override
	public HashMap<String, Object> getContainerProperties() {
		return containerProperties ;
	}
	
	@Override
	public void addContainerProperty(String propertyKey, Object value){
        if (containerProperties == null)
            containerProperties = new HashMap<String, Object>();
        containerProperties.put(propertyKey,value);
        synchronizePropertyToDB(propertyKey, value);
        log.debug("Set container {} property : ({},{})", new Object[]{this.containerID,
                                                                             propertyKey,
                                                                             this.containerProperties.get(propertyKey)});
	}

    @Override
    public void removeContainerProperty(String propertyKey) {
        if (containerProperties!=null) {
            containerProperties.remove(propertyKey);
            removePropertyFromDB(propertyKey);
        }
    }

    @Override
    public Container getContainerParentContainer() {
        return this.containerParentContainer;
    }

    @Override
    public void setContainerParentContainer(Container container) {
        if (this.containerParentContainer==null || !this.containerParentContainer.equals(container)) {
            if (container instanceof ContainerImpl) {
                this.containerParentContainer = (ContainerImpl) container;
            }
        }
    }

    @Override
    public Set<ContainerImpl> getContainerChildContainers() {
        return this.containerChildContainers;
    }

    @Override
    public boolean addContainerChildContainer(Container container) {
        boolean ret = false;
        if (container instanceof ContainerImpl) {
            try {
                ret = this.containerChildContainers.add((ContainerImpl)container);
                if (ret)
                    synchronizeChildContainersToDB();
            } catch (MappingDSGraphDBException E) {
                E.printStackTrace();
                log.error("Exception while adding child container {}...", new Object[]{container.getContainerID()});
                this.containerNodes.remove((ContainerImpl)container);
                MappingDSGraphDB.autorollback();
            }
        }
        return ret;
    }

    @Override
    public boolean removeContainerChildContainer(Container container) {
        boolean ret = false;
        if (container instanceof ContainerImpl) {
            ret = containerChildContainers.remove(container);
            if (ret)
                removeChildContainerFromDB((ContainerImpl)container);
        }
        return ret;
    }

    @Override
	public Set<NodeImpl> getContainerNodes(long depth) {
		Set<NodeImpl> ret = null;
		if (depth==1) {
			ret = this.containerNodes;
		} else {
			ret = new HashSet<NodeImpl>();
			Iterator<NodeImpl> iter = this.containerNodes.iterator();
			while (iter.hasNext()) {
				NodeImpl tmp = iter.next();
				if (tmp.getNodeDepth()==depth)
					ret.add(tmp);
			}
		}
		return ret;
	}

	@Override
	public boolean addContainerNode(Node node) {
		if (node instanceof NodeImpl) {
			boolean ret = false; 
			try {
				ret = this.containerNodes.add((NodeImpl)node);
				if (ret)
					synchronizeNodeToDB((NodeImpl)node);
			} catch (MappingDSGraphDBException E) {
				E.printStackTrace();
				log.error("Exception while adding node {}...", new Object[]{node.getNodeID()});
				this.containerNodes.remove((NodeImpl)node);
				MappingDSGraphDB.autorollback();
			}
			return ret;
		}
		else
			return false;
	}
	
	@Override
	public boolean removeContainerNode(Node node) {
		if (node instanceof NodeImpl) {
			boolean ret = this.containerNodes.remove((NodeImpl)node);
			if (ret)
				removeNodeFromDB((NodeImpl)node); 
			return ret;
		} else
			return false;
	}

	@Override
	public Set<GateImpl> getContainerGates() {
		return this.containerGates;
	}

	@Override
	public boolean addContainerGate(Gate gate) {
		// a gate is also a node
		if (gate instanceof GateImpl) {
			boolean addToNodes = false;
			boolean addToGates = false;
			try {
                addToNodes = this.containerNodes.add((NodeImpl) gate);
                addToGates = this.containerGates.add((GateImpl) gate);
                if (addToNodes && addToGates) {
                    synchronizeNodeToDB((NodeImpl) gate);
                    synchronizeGateToDB((GateImpl) gate);
                } else {
					if (addToNodes) this.containerNodes.remove((NodeImpl)gate);
					if (addToGates) this.containerGates.remove((GateImpl)gate);
				}
			} catch (MappingDSGraphDBException E) {
				E.printStackTrace();
				log.error("Exception while adding gate {}...", new Object[]{gate.getNodeID()});				
				if (addToNodes) this.containerNodes.remove((NodeImpl)gate);
				if (addToGates) this.containerGates.remove((GateImpl)gate);
				MappingDSGraphDB.autorollback();
			}
			return addToNodes && addToGates;
		} else {
			return false;
		}			
	}

    @Override
    public boolean removeContainerGate(Gate gate) {
        // a gate is also a node
        if (gate instanceof GateImpl) {
            boolean removedFromNodes = this.containerNodes.remove(gate);
            boolean removedFromGates = this.containerGates.remove(gate);
            if (removedFromGates && removedFromNodes) {
                removeNodeFromDB((NodeImpl)gate);
                removeGateFromDB((GateImpl)gate);
            } else {
                if (removedFromNodes) this.containerNodes.add((NodeImpl)gate);
                if (removedFromGates) this.containerGates.add((GateImpl)gate);
            }
            return removedFromGates && removedFromNodes;
        } else {
            return true;
        }
    }

    public Vertex getElement() {
		return containerVertex;
	}

	public void setElement(Element containerVertex) {
		this.containerVertex = (Vertex)containerVertex;
		this.containerVertex.setProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, MappingDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE);
		this.containerID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
		log.debug("Container vertex has been initialized ({},{}).", new Object[]{this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
																				 this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
	}
	
	public void synchronizeToDB() throws MappingDSGraphDBException {
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

    private void synchronizeCompanyToDB() {
        if (containerVertex!=null) {
            if (this.containerCompany!=null) {
                log.debug("Synchronize container company {}...", new Object[]{this.containerCompany});
                containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY, this.containerCompany);
            }
        }
    }

    private void synchronizeProductToDB() {
        if (containerVertex!=null) {
            if (this.containerProduct!=null) {
                log.debug("Synchronize container product {}...", new Object[]{this.containerProduct});
                containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY, this.containerProduct);
            }
        }
    }

	private void synchronizeTypeToDB() {
		if (containerVertex!=null) {
			if (this.containerType!=null) {
				log.debug("Synchronize container type {}...", new Object[]{this.containerType});
				containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY, this.containerType);
			}
		}
	}
	
	private void synchronizePropertiesToDB() {
		if (containerProperties!=null && containerVertex!=null) {
			Iterator<String> iterK = this.containerProperties.keySet().iterator(); 
			while (iterK.hasNext()) {
				String key = iterK.next();
				Object value = containerProperties.get(key);				
				synchronizePropertyToDB(key, value);
			}
		}
	}
	
	private void synchronizePropertyToDB(String key, Object value) {
		if (containerVertex!=null)
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyToDB(containerVertex, key, value, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
	}
	
	private void synchronizePrimaryAdminGateToDB() {
		if (containerVertex!=null && containerPrimaryAdminGate!=null && containerPrimaryAdminGate.getElement()!=null) {
			log.debug("Synchronize container primary admin gate {}...", new Object[]{this.containerPrimaryAdminGate.getNodeID()});
			containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY,
									    this.containerPrimaryAdminGate.getNodeID());
		}
	}
	
	private void synchronizeClusterToDB() {
		if (containerVertex!=null && containerCluster!=null && containerCluster.getElement()!=null) {
			log.debug("Synchronize container cluster {}...", new Object[]{this.containerCluster.getClusterID()});
			containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY,
									    this.containerCluster.getClusterID());
		}
	}

    private void synchronizeParentContainerToDB() {
        if (containerVertex!=null && containerParentContainer!=null && containerParentContainer.getElement()!=null) {
            log.debug("Synchronize container parent container {}...", new Object[]{this.containerParentContainer.getContainerID()});
            containerVertex.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY,
                                               this.containerParentContainer.getContainerID());
        }
    }

    private void synchronizeChildContainersToDB() throws MappingDSGraphDBException {
        if (containerVertex!=null) {
            Iterator<ContainerImpl> iterCCC = this.containerChildContainers.iterator();
            while (iterCCC.hasNext()) {
                ContainerImpl aContainer = iterCCC.next();
                synchronizeChildContainerToDB(aContainer);
            }
        }
    }

    private void synchronizeChildContainerToDB(ContainerImpl container) throws MappingDSGraphDBException {
        if (containerVertex!=null) {
            VertexQuery query = this.containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            for (Vertex vertex : query.vertices()) {
                if ((long)vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)==container.getContainerID())
                    return;
            }
            log.debug("Synchronize container child container {}...", new Object[]{container.getContainerID()});
            Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, container.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
        }
    }

	private void synchronizeNodesToDB() throws MappingDSGraphDBException {
		if (containerVertex!=null) {
			Iterator<NodeImpl> iterCN = this.containerNodes.iterator();
			while (iterCN.hasNext()) {
				NodeImpl aNode = iterCN.next();
				synchronizeNodeToDB(aNode);
			}
		}		
	}
	
	private void synchronizeNodeToDB(NodeImpl node) throws MappingDSGraphDBException {
		if (containerVertex!=null) {
			if (node instanceof GateImpl)
				return;
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if ((long)vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)==node.getNodeID())
					return;
			}
			log.debug("Synchronize container node {}...", new Object[]{node.getNodeID()});
			Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, node.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
		}
	}
	
	private void synchronizeGatesToDB() throws MappingDSGraphDBException {
		if (containerVertex!=null) {
			Iterator<GateImpl> iterCG = this.containerGates.iterator();
			while (iterCG.hasNext()) {
				GateImpl aGate = iterCG.next();
				synchronizeGateToDB(aGate);
			}
		}
	}
	
	private void synchronizeGateToDB(GateImpl gate) throws MappingDSGraphDBException {
		if (containerVertex!=null) {
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if ((long)vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)==gate.getNodeID())
					return;
			}
			log.debug("Synchronize container gate {}...", new Object[]{gate.getNodeID()});
			Edge owns = MappingDSGraphDB.createEdge(this.containerVertex, gate.getElement(), MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			owns.setProperty(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
		}
	}
	
	public void synchronizeFromDB() {
		if (!isBeingSyncFromDB) {
			isBeingSyncFromDB = true;
			synchronizeIDFromDB();
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
			this.containerID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
	}

    private void synchronizeCompanyFromDB() {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY);
            if (ret!=null)
                this.containerCompany = (String) ret;
        }
    }

    private void synchronizeProductFromDB() {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY);
            if (ret!=null)
                this.containerProduct = (String) ret;
        }
    }

	private void synchronizeTypeFromDB() {
		if (containerVertex!=null) {
			Object ret = containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY);
			if (ret!=null)
				this.containerType = (String) ret;
		}
	}
		
	private void synchronizePropertiesFromDB() {
		if (containerVertex!=null) {
			if (containerProperties==null) {
				containerProperties=new HashMap<String,Object>();
			} else {
                containerProperties.clear();
            }
            MappingDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(containerVertex, containerProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
		}
	}

    private void removePropertyFromDB(String key) {
        if (containerVertex != null) {
            log.debug("Remove container property {} from db...", new Object[]{key});
            MappingDSGraphDBObjectProps.removeObjectPropertyFromDB(containerVertex, key, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        }
    }

	private void synchronizePrimaryAdminGateFromDB() {
		if (containerVertex!=null) {
			Object paGateID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY);
			if (paGateID!=null) {
				MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) paGateID);
				if (entity!=null) {
					if (entity instanceof GateImpl)
						containerPrimaryAdminGate = (GateImpl) entity;
					else
						log.error("CONSISTENCY ERROR : entity {} is not a gate.", entity.getElement().getId());
				}
			}
		}
	}
	
	private void synchronizeClusterFromDB() {
		if (containerVertex!=null) {
			Object clusterID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY);
			if (clusterID!=null) {
				MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) clusterID);
				if (entity!=null) {
					if (entity instanceof ClusterImpl)
						containerCluster = (ClusterImpl) entity;
					else
						log.error("CONSISTENCY ERROR : entity {} is not a cluster.", entity.getElement().getId());
				}
			}
		}
	}

    private void synchronizeParentContainerFromDB() {
        if (containerVertex!=null) {
            Object containerID = this.containerVertex.getProperty(MappingDSGraphPropertyNames.DD_CONTAINER_PCONTER_KEY);
            if (containerID!=null) {
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) containerID);
                if (entity!=null) {
                    if (entity instanceof ContainerImpl)
                        containerParentContainer = (ContainerImpl) entity;
                    else
                        log.error("CONSISTENCY ERROR : entity {} is not a container.", entity.getElement().getId());
                }
            }
        }
    }

    private void synchronizeChildContainersFromDB() {
        if (containerVertex!=null) {
            VertexQuery query = containerVertex.query();
            query.direction(Direction.OUT);
            query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
            query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_CHILD_CONTAINER_KEY, true);
            this.containerChildContainers.clear();
            for (Vertex vertex : query.vertices()) {
                ContainerImpl container = null;
                MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
                if (entity!=null) {
                    if (entity instanceof ContainerImpl) {
                        container = (ContainerImpl)entity;
                    } else {
                        log.error("CONSISTENCY ERROR : entity {} is not a container.", entity.getElement().getId());
                    }
                }
                if (container!=null)
                    this.containerChildContainers.add(container);
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
                if (edge.getVertex(Direction.OUT).equals(container.getElement())) {
                    MappingDSGraphDB.getDDgraph().removeEdge(edge);
                }
            }
        }
    }
	
	private void synchronizeNodesFromDB() {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
            this.containerNodes.clear();
			for (Vertex vertex : query.vertices()) {
				NodeImpl node = null;
				MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
				if (entity!=null) {
					if (entity instanceof NodeImpl) {
						node = (NodeImpl)entity;
					} else {
						log.error("CONSISTENCY ERROR : entity {} is not a node.", entity.getElement().getId());
					}
				}
				if (node!=null)
					this.containerNodes.add(node);
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
				if (edge.getVertex(Direction.OUT).equals(node.getElement())) {
					MappingDSGraphDB.getDDgraph().removeEdge(edge);
				}						
			}
		}
	}

	private void synchronizeGatesFromDB() {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(MappingDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
            this.containerGates.clear();
			for (Vertex vertex : query.vertices()) {
				GateImpl gate = null;
				MappingDSCacheEntity entity = MappingDSGraphDB.getVertexEntity((long) vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
				if (entity!=null) {
					if (entity instanceof GateImpl) {
						gate = (GateImpl)entity;
					} else {
						log.error("CONSISTENCY ERROR : entity {} is not a gate.", entity.getElement().getId());
					}
				}
				if (gate!=null) {
					this.containerNodes.add(gate);
					this.containerGates.add(gate);
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
                if (edge.getVertex(Direction.OUT).equals(node.getElement())) {
                    MappingDSGraphDB.getDDgraph().removeEdge(edge);
                }
            }
        }
    }
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContainerImpl tmp = (ContainerImpl) o;
        if (this.containerID == 0) {
            return super.equals(o);
        }
        return (this.containerID == tmp.getContainerID());
    }

    @Override
    public int hashCode() {
        return this.containerVertex != null ? new Long(this.containerID).hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        String adminUrl = null;
        if (this.containerPrimaryAdminGate != null && this.containerPrimaryAdminGate.getNodePrimaryAdminEndpoint() != null) {
            adminUrl = this.containerPrimaryAdminGate.getNodePrimaryAdminEndpoint().getEndpointURL();
        }
        return String.format("Container{ID='%d', Primary Admin URL='%s'}", this.containerID, adminUrl);
    }
}