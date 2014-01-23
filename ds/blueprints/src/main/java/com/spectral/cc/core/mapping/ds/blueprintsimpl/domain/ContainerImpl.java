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

public class ContainerImpl implements Container, TopoDSCacheEntity {

	private static final Logger log = LoggerFactory.getLogger(ContainerImpl.class);
	
	private long                   containerID               = 0;
	private String                 containerCompany          = null;
    private String                 containerProduct          = null;
    private String                 containerType             = null;
	private GateImpl               containerPrimaryAdminGate = null;
	
	private ClusterImpl            containerCluster          = null;
	
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
	public void setContainerProperty(String propertyKey,Object value){
        if (containerProperties == null)
            containerProperties = new HashMap<String, Object>();
        containerProperties.put(propertyKey,value);
        synchronizePropertyToDB(propertyKey, value);
        log.debug("Set container {} property : ({},{})", new Object[]{this.containerID,
                                                                             propertyKey,
                                                                             this.containerProperties.get(propertyKey)});
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
				if (ret==true)
					synchronizeNodeToDB((NodeImpl)node);
			} catch (TopoDSGraphDBException E) {
				E.printStackTrace();
				log.error("Exception while adding node {}...", new Object[]{node.getNodeID()});
				this.containerNodes.remove((NodeImpl)node);
				TopoDSGraphDB.autorollback();
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
				addToNodes = this.containerNodes.add((NodeImpl)gate);
				addToGates = this.containerGates.add((GateImpl)gate);
				if (addToNodes && addToGates)
					synchronizeGateToDB((GateImpl)gate);
				else {
					if (addToNodes) this.containerNodes.remove((NodeImpl)gate);
					if (addToGates) this.containerGates.remove((GateImpl)gate);
				}
			} catch (TopoDSGraphDBException E) {
				E.printStackTrace();
				log.error("Exception while adding gate {}...", new Object[]{gate.getNodeID()});				
				if (addToNodes) this.containerNodes.remove((NodeImpl)gate);
				if (addToGates) this.containerGates.remove((GateImpl)gate);
				TopoDSGraphDB.autorollback();
			}
			return addToNodes && addToGates;
		} else {
			return false;
		}			
	}
	
	public Vertex getElement() {
		return containerVertex;
	}

	public void setElement(Element containerVertex) {
		this.containerVertex = (Vertex)containerVertex;
		this.containerVertex.setProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY, TopoDSGraphPropertyNames.DD_TYPE_CONTAINER_VALUE);
		this.containerID = this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
		log.debug("Container vertex has been initialized ({},{}).", new Object[]{this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID),
																				 this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_TYPE_KEY)});
	}
	
	public void synchronizeToDB() throws TopoDSGraphDBException {
        synchronizeCompanyToDB();
        synchronizeProductToDB();
        synchronizeTypeToDB();
		synchronizePropertiesToDB();
		synchronizePrimaryAdminGateToDB();
		synchronizeClusterToDB();
		synchronizeNodesToDB();
		synchronizeGatesToDB();
	}

    private void synchronizeCompanyToDB() {
        if (containerVertex!=null) {
            if (this.containerCompany!=null) {
                log.debug("Synchronize container company {}...", new Object[]{this.containerCompany});
                containerVertex.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY, this.containerCompany);
            }
        }
    }

    private void synchronizeProductToDB() {
        if (containerVertex!=null) {
            if (this.containerProduct!=null) {
                log.debug("Synchronize container product {}...", new Object[]{this.containerProduct});
                containerVertex.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY, this.containerProduct);
            }
        }
    }

	private void synchronizeTypeToDB() {
		if (containerVertex!=null) {
			if (this.containerType!=null) {
				log.debug("Synchronize container type {}...", new Object[]{this.containerType});
				containerVertex.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY, this.containerType);
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
            TopoDSGraphDBObjectProps.synchronizeObjectPropertyToDB(containerVertex,key,value,TopoDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
	}
	
	private void synchronizePrimaryAdminGateToDB() {
		if (containerVertex!=null && containerPrimaryAdminGate!=null && containerPrimaryAdminGate.getElement()!=null) {
			log.debug("Synchronize container primary admin gate {}...", new Object[]{this.containerPrimaryAdminGate.getNodeID()});
			containerVertex.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY, 
									    this.containerPrimaryAdminGate.getNodeID());
		}
	}
	
	private void synchronizeClusterToDB() {
		if (containerVertex!=null && containerCluster!=null && containerCluster.getElement()!=null) {
			log.debug("Synchronize container cluster {}...", new Object[]{this.containerCluster.getClusterID()});
			containerVertex.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY, 
									    this.containerCluster.getClusterID());
		}
	}
	
	private void synchronizeNodesToDB() throws TopoDSGraphDBException {
		if (containerVertex!=null) {
			Iterator<NodeImpl> iterCN = this.containerNodes.iterator();
			while (iterCN.hasNext()) {
				NodeImpl aNode = iterCN.next();
				synchronizeNodeToDB(aNode);
			}
		}		
	}
	
	private void synchronizeNodeToDB(NodeImpl node) throws TopoDSGraphDBException {
		if (containerVertex!=null) {
			if (node instanceof GateImpl)
				return;
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if ((long)vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)==node.getNodeID())
					return;
			}
			log.debug("Synchronize container node {}...", new Object[]{node.getNodeID()});
			Edge owns = TopoDSGraphDB.createEdge(this.containerVertex, node.getElement(), TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY); 
			owns.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
		}
	}
	
	private void synchronizeGatesToDB() throws TopoDSGraphDBException {
		if (containerVertex!=null) {
			Iterator<GateImpl> iterCG = this.containerGates.iterator();
			while (iterCG.hasNext()) {
				GateImpl aGate = iterCG.next();
				synchronizeGateToDB(aGate);
			}
		}
	}
	
	private void synchronizeGateToDB(GateImpl gate) throws TopoDSGraphDBException {
		if (containerVertex!=null) {
			VertexQuery query = this.containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
			for (Vertex vertex : query.vertices()) {
				if ((long)vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID)==gate.getNodeID())
					return;
			}
			log.debug("Synchronize container gate {}...", new Object[]{gate.getNodeID()});
			Edge owns = TopoDSGraphDB.createEdge(this.containerVertex, gate.getElement(), TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY); 
			owns.setProperty(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
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
			synchronizeNodesFromDB();
			synchronizeGatesFromDB();
			isBeingSyncFromDB = false;
		}
	}

	private void synchronizeIDFromDB() {
		if (this.containerVertex!=null)
			this.containerID = this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID);
	}

    private void synchronizeCompanyFromDB() {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(TopoDSGraphPropertyNames.DD_CONTAINER_COMPANY_KEY);
            if (ret!=null)
                this.containerCompany = (String) ret;
        }
    }

    private void synchronizeProductFromDB() {
        if (containerVertex!=null) {
            Object ret = containerVertex.getProperty(TopoDSGraphPropertyNames.DD_CONTAINER_PRODUCT_KEY);
            if (ret!=null)
                this.containerProduct = (String) ret;
        }
    }

	private void synchronizeTypeFromDB() {
		if (containerVertex!=null) {
			Object ret = containerVertex.getProperty(TopoDSGraphPropertyNames.DD_CONTAINER_TYPE_KEY);
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
            TopoDSGraphDBObjectProps.synchronizeObjectPropertyFromDB(containerVertex,containerProperties,TopoDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
		}
	}	

	private void synchronizePrimaryAdminGateFromDB() {
		if (containerVertex!=null) {
			Object paGateID = this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_CONTAINER_PAGATE_KEY);
			if (paGateID!=null) {
				TopoDSCacheEntity entity = TopoDSGraphDB.getVertexEntity((long)paGateID);
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
			Object clusterID = this.containerVertex.getProperty(TopoDSGraphPropertyNames.DD_CONTAINER_CLUSTER_KEY);
			if (clusterID!=null) {
				TopoDSCacheEntity entity = TopoDSGraphDB.getVertexEntity((long)clusterID);
				if (entity!=null) {
					if (entity instanceof ClusterImpl)
						containerCluster = (ClusterImpl) entity;
					else
						log.error("CONSISTENCY ERROR : entity {} is not a cluster.", entity.getElement().getId());
				}
			}
		}
	}
	
	private void synchronizeNodesFromDB() {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_NODE_KEY, true);
            this.containerNodes.clear();
			for (Vertex vertex : query.vertices()) {
				NodeImpl node = null;
				TopoDSCacheEntity entity = TopoDSGraphDB.getVertexEntity((long)vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
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
			query.labels(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(TopoDSGraphPropertyNames.DD_NODE_EDGE_CHILD_KEY, true);
			for (Edge edge : query.edges()) {
				if (edge.getVertex(Direction.OUT).equals(node.getElement())) {
					TopoDSGraphDB.getDDgraph().removeEdge(edge);
				}						
			}
		}
	}

	private void synchronizeGatesFromDB() {
		if (containerVertex!=null) {
			VertexQuery query = containerVertex.query();
			query.direction(Direction.OUT);
			query.labels(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_OWNS_LABEL_KEY);
			query.has(TopoDSGraphPropertyNames.DD_CONTAINER_EDGE_GATE_KEY, true);
            this.containerGates.clear();
			for (Vertex vertex : query.vertices()) {
				GateImpl gate = null;
				TopoDSCacheEntity entity = TopoDSGraphDB.getVertexEntity((long)vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID));
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