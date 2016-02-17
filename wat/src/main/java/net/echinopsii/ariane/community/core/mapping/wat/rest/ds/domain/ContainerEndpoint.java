/**
 * Mapping Web Service :
 * provide a mapping DS Web Service and REST Service
 *
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
package net.echinopsii.ariane.community.core.mapping.wat.rest.ds.domain;

import net.echinopsii.ariane.community.core.mapping.ds.MappingDSException;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Cluster;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Container;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Gate;
import net.echinopsii.ariane.community.core.mapping.ds.domain.Node;
import net.echinopsii.ariane.community.core.mapping.ds.json.PropertiesJSON;
import net.echinopsii.ariane.community.core.mapping.wat.MappingBootstrap;
import net.echinopsii.ariane.community.core.mapping.ds.json.domain.ContainerJSON;
import net.echinopsii.ariane.community.core.mapping.ds.json.ToolBox;
import net.echinopsii.ariane.community.core.mapping.wat.rest.ds.JSONDeserializationResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Path("/mapping/domain/containers")
public class ContainerEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ContainerEndpoint.class);

    public static JSONDeserializationResponse jsonFriendlyToMappingFriendly(ContainerJSON.JSONDeserializedContainer jsonDeserializedContainer) throws MappingDSException {
        JSONDeserializationResponse ret = new JSONDeserializationResponse();

        // DETECT POTENTIAL QUERIES ERROR FIRST
        Gate reqPrimaryAdminGate = null;
        Cluster reqContainerCluster = null;
        Container reqContainerParent = null;
        List<Container> reqContainerChildContainers = new ArrayList<>();
        List<Node> reqContainerChildNodes = new ArrayList<>();
        List<Gate> reqContainerChildGates = new ArrayList<>();
        HashMap<String, Object> reqProperties = new HashMap<>();

        if (jsonDeserializedContainer.getContainerPrimaryAdminGateID()!=0) {
            reqPrimaryAdminGate = MappingBootstrap.getMappingSce().getGateSce().getGate(jsonDeserializedContainer.getContainerPrimaryAdminGateID());
            if (reqPrimaryAdminGate == null) ret.setErrorMessage("Request Error : gate with provided ID " + jsonDeserializedContainer.getContainerPrimaryAdminGateID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerClusterID()!=0) {
            reqContainerCluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(jsonDeserializedContainer.getContainerClusterID());
            if (reqContainerCluster == null) ret.setErrorMessage("Request Error: cluster with provided ID " + jsonDeserializedContainer.getContainerClusterID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerParentContainerID()!=0) {
            reqContainerParent = MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedContainer.getContainerParentContainerID());
            if (reqContainerParent == null) ret.setErrorMessage("Request Error: parent container with provided ID " + jsonDeserializedContainer.getContainerParentContainerID() + " was not found.");
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerChildContainersID()!=null && jsonDeserializedContainer.getContainerChildContainersID().size() > 0) {
            for (long id : jsonDeserializedContainer.getContainerChildContainersID()) {
                Container childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
                if (childContainer!=null) reqContainerChildContainers.add(childContainer);
                else {
                    ret.setErrorMessage("Request Error : child container with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerNodesID()!=null && jsonDeserializedContainer.getContainerNodesID().size()>0) {
            for (long id : jsonDeserializedContainer.getContainerNodesID()) {
                Node childNode = MappingBootstrap.getMappingSce().getNodeSce().getNode(id);
                if (childNode != null) reqContainerChildNodes.add(childNode);
                else {
                    ret.setErrorMessage("Request Error : child node with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerGatesID()!=null && jsonDeserializedContainer.getContainerGatesID().size()>0) {
            for (long id : jsonDeserializedContainer.getContainerGatesID()) {
                Gate childGate = MappingBootstrap.getMappingSce().getGateSce().getGate(id);
                if (childGate != null) reqContainerChildGates.add(childGate);
                else {
                    ret.setErrorMessage("Request Error : child gate with provided ID " + id + " was not found.");
                    break;
                }
            }
        }
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerProperties()!=null && jsonDeserializedContainer.getContainerProperties().size() > 0) {
            for (PropertiesJSON.JSONDeserializedProperty deserializedProperty : jsonDeserializedContainer.getContainerProperties()) {
                try {
                    Object oValue = ToolBox.extractPropertyObjectValueFromString(deserializedProperty.getPropertyValue(), deserializedProperty.getPropertyType());
                    reqProperties.put(deserializedProperty.getPropertyName(), oValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret.setErrorMessage("Request Error : invalid property " + deserializedProperty.getPropertyName() + ".");
                    break;
                }
            }
        }
        // LOOK IF CONTAINER MAYBE UPDATED OR CREATED
        Container deserializedContainer = null;
        if (ret.getErrorMessage() == null && jsonDeserializedContainer.getContainerID()!=0) {
            deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedContainer.getContainerID());
            if (deserializedContainer==null)
                ret.setErrorMessage("Request Error : container with provided ID " + jsonDeserializedContainer.getContainerID() + " was not found.");
        }

        if (ret.getErrorMessage() == null && deserializedContainer == null && jsonDeserializedContainer.getContainerGateURI() != null)
            deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(jsonDeserializedContainer.getContainerGateURI());

        /*
        if (ret.getErrorMessage() == null && deserializedContainer!=null) {
            if (!deserializedContainer.getContainerPrimaryAdminGateURL().equals(jsonDeserializedContainer.getContainerGateURI()) ||
                !deserializedContainer.getContainerPrimaryAdminGate().getNodeName().equals(jsonDeserializedContainer.getContainerGateName())
                    ) {
                ret.setErrorMessage("Request Error : gate definition doesn't match with container " + jsonDeserializedContainer.getContainerID() + " !");
            }
        }
        */

        // APPLY REQ IF NO ERRORS
        if (ret.getErrorMessage() == null) {
            String reqContainerName = jsonDeserializedContainer.getContainerName();
            String reqContainerCompany = jsonDeserializedContainer.getContainerCompany();
            String reqContainerProduct = jsonDeserializedContainer.getContainerProduct();
            String reqContainerType = jsonDeserializedContainer.getContainerType();

            if (deserializedContainer == null) {
                String reqContainerGURI = jsonDeserializedContainer.getContainerGateURI();
                String reqContainerGName = jsonDeserializedContainer.getContainerGateName();
                if (reqContainerName == null)
                    if (reqContainerParent != null)
                        deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(reqContainerGURI, reqContainerGName, reqContainerParent);
                    else
                        deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(reqContainerGURI, reqContainerGName);
                else
                    if (reqContainerParent != null)
                        deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(reqContainerName, reqContainerGURI, reqContainerGName, reqContainerParent);
                    else
                        deserializedContainer = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(reqContainerName, reqContainerGURI, reqContainerGName);
            } else {
                if (reqContainerName != null) deserializedContainer.setContainerName(reqContainerName);
                if (reqPrimaryAdminGate != null) deserializedContainer.setContainerPrimaryAdminGate(reqPrimaryAdminGate);
            }

            if (reqContainerCluster != null) deserializedContainer.setContainerCluster(reqContainerCluster);
            if (reqContainerCompany != null) deserializedContainer.setContainerCompany(reqContainerCompany);
            if (reqContainerProduct != null) deserializedContainer.setContainerProduct(reqContainerProduct);
            if (reqContainerType != null) deserializedContainer.setContainerType(reqContainerType);

            if (jsonDeserializedContainer.getContainerChildContainersID() != null) {
                List<Container> childContainersToDelete = new ArrayList<>();
                for (Container containerToDel : deserializedContainer.getContainerChildContainers())
                    if (!reqContainerChildContainers.contains(containerToDel))
                        childContainersToDelete.add(containerToDel);
                for (Container containerToDel : childContainersToDelete)
                    deserializedContainer.removeContainerChildContainer(containerToDel);
                for (Container containerToAdd : reqContainerChildContainers)
                    deserializedContainer.addContainerChildContainer(containerToAdd);
            }

            if (jsonDeserializedContainer.getContainerNodesID() != null) {
                List<Node> nodesToDelete = new ArrayList<>();
                for (Node nodeToDel : deserializedContainer.getContainerNodes(0))
                    if (!reqContainerChildNodes.contains(nodeToDel))
                        nodesToDelete.add(nodeToDel);
                for (Node nodeToDel : nodesToDelete)
                    deserializedContainer.removeContainerNode(nodeToDel);
                for (Node nodeToAdd : reqContainerChildNodes)
                    deserializedContainer.addContainerNode(nodeToAdd);
            }

            if (jsonDeserializedContainer.getContainerGatesID() != null) {
                List<Gate> gatesToDelete = new ArrayList<>();
                for (Gate gateToDel : deserializedContainer.getContainerGates())
                    if (!reqContainerChildGates.contains(gateToDel))
                        gatesToDelete.add(gateToDel);
                for (Gate gateToDel : gatesToDelete)
                    deserializedContainer.removeContainerGate(gateToDel);
                for (Gate gateToAdd : reqContainerChildGates)
                    deserializedContainer.addContainerGate(gateToAdd);
            }

            if (jsonDeserializedContainer.getContainerProperties()!=null) {
                if (deserializedContainer.getContainerProperties()!=null) {
                    List<String> propertiesToDelete = new ArrayList<>();
                    for (String propertyKey : deserializedContainer.getContainerProperties().keySet())
                        if (!reqProperties.containsKey(propertyKey))
                            propertiesToDelete.add(propertyKey);
                    for (String propertyToDelete : propertiesToDelete)
                        deserializedContainer.removeContainerProperty(propertyToDelete);
                }

                for (String propertyKey : reqProperties.keySet())
                    deserializedContainer.addContainerProperty(propertyKey, reqProperties.get(propertyKey));
            }

            ret.setDeserializedObject(deserializedContainer);
        }
        return ret;
    }

    @GET
    @Path("/{param:[0-9][0-9]*}")
    public Response displayContainer(@PathParam("param") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get container : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (cont != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                try {
                    ContainerJSON.oneContainer2JSON(cont, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Container with id " + id + " not found").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllContainers() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get containers", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            String result = "";
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ContainerJSON.manyContainers2JSON((HashSet<Container>) MappingBootstrap.getMappingSce().getContainerSce().getContainers(null), outStream);
                result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                return Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                result = e.getMessage();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getContainer(@QueryParam("primaryAdminURL") String primaryAdminURL, @QueryParam("ID") long id) {
        if (id!=0) {
            return displayContainer(id);
        } else if (primaryAdminURL != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get container: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL});
            if (subject.hasRole("mappingreader") || subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:read") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Container cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().getContainer(primaryAdminURL);
                if (cont != null) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    try {
                        ContainerJSON.oneContainer2JSON(cont, outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        return Response.status(Status.OK).entity(result).build();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                        String result = e.getMessage();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Container with primary admin url " + primaryAdminURL + " not found").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to read mapping db. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("MappingDSLRegistryRequest error: primaryAdminURL and id are not defined. You must define one of these parameters").build();
        }
    }

    @GET
    @Path("/create")
    public Response createContainer(@QueryParam("name") String name, @QueryParam("primaryAdminURL") String primaryAdminURL, @QueryParam("primaryAdminGateName") String primaryAdminGateName) {
        try {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL, primaryAdminGateName});
            if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                Container cont = null;
                if (name != null)
                    cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(name, primaryAdminURL, primaryAdminGateName);
                else
                    cont = (Container) MappingBootstrap.getMappingSce().getContainerSce().createContainer(primaryAdminURL, primaryAdminGateName);
                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    ContainerJSON.oneContainer2JSON(cont, outStream);
                    String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                    return Response.status(Status.OK).entity(result).build();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
            }
        } catch (MappingDSException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            String result = e.getMessage();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }

    @POST
    public Response postContainer(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create container", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (payload != null) {
                try {
                    Response ret;
                    JSONDeserializationResponse deserializationResponse = jsonFriendlyToMappingFriendly(ContainerJSON.JSON2Container(payload));
                    if (deserializationResponse.getErrorMessage()!=null) {
                        String result = deserializationResponse.getErrorMessage();
                        ret = Response.status(Status.BAD_REQUEST).entity(result).build();
                    } else if (deserializationResponse.getDeserializedObject()!=null) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        ContainerJSON.oneContainer2JSON((Container) deserializationResponse.getDeserializedObject(), outStream);
                        String result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                        ret = Response.status(Status.OK).entity(result).build();
                    } else {
                        String result = "ERROR while deserializing !";
                        ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                    }
                    return ret ;
                } catch (MappingDSException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
            } else {
                return Response.status(Status.BAD_REQUEST).entity("No payload attached to this POST").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteContainer(@QueryParam("primaryAdminURL") String primaryAdminURL) {
        try {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete container : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), primaryAdminURL});
            if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                MappingBootstrap.getMappingSce().getContainerSce().deleteContainer(primaryAdminURL);
                return Response.status(Status.OK).entity("Container (" + primaryAdminURL + ") has been successfully deleted !").build();
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
            }
        } catch (MappingDSException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while deleting container with primary admin URL " + primaryAdminURL).build();
        }
    }

    @GET
    @Path("/update/name")
    public Response setContainerName(@QueryParam("ID") long id, @QueryParam("name") String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container name : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                container.setContainerName(name);
                return Response.status(Status.OK).entity("Container (" + id + ") company successfully updated to " + name + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") company " + name + " : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/company")
    public Response setContainerCompany(@QueryParam("ID") long id, @QueryParam("company") String company) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container company : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, company});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                container.setContainerCompany(company);
                return Response.status(Status.OK).entity("Container (" + id + ") company successfully updated to " + company + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") company " + company + " : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/product")
    public Response setContainerProduct(@QueryParam("ID") long id, @QueryParam("product") String product) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container product : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, product});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                container.setContainerProduct(product);
                return Response.status(Status.OK).entity("Container (" + id + ") product successfully updated to " + product + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") product " + product + " : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/type")
    public Response setContainerType(@QueryParam("ID") long id, @QueryParam("type") String type) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container type : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                container.setContainerType(type);
                return Response.status(Status.OK).entity("Container (" + id + ") type successfully updated to " + type + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") type " + type + " : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/primaryAdminGate")
    public Response setContainerPrimaryAdminGate(@QueryParam("ID") long id, @QueryParam("paGateID") long paGateID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container primary admin gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, paGateID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(paGateID);
                if (gate != null) {
                    container.setContainerPrimaryAdminGate(gate);
                    return Response.status(Status.OK).entity("Container (" + id + ") primary admin gate successfully updated to " + gate.getNodeName() + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") primary admin gate : gate " + paGateID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") primary admin gate " + paGateID + " : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/cluster")
    public Response setContainerCluster(@QueryParam("ID") long id, @QueryParam("clusterID") long clusterID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container cluster : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, clusterID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Cluster cluster = MappingBootstrap.getMappingSce().getClusterSce().getCluster(clusterID);
                if (cluster != null) {
                    container.setContainerCluster(cluster);
                    return Response.status(Status.OK).entity("Container (" + id + ") cluster successfully updated to " + clusterID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating container " + id + " cluster : cluster " + clusterID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") cluster " + clusterID + ": container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/parentContainer")
    public Response setContainerParentContainer(@QueryParam("ID") long id, @QueryParam("parentContainerID") long parentContainerID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container parent container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, parentContainerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Container parentContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(parentContainerID);
                if (parentContainer != null) {
                    parentContainer.setContainerParentContainer(parentContainer);
                    return Response.status(Status.OK).entity("Container (" + id + ") parent container successfully updated to " + parentContainerID + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") parent container " + parentContainerID + ": parent container " + parentContainerID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while updating container (" + id + ") parent container " + parentContainerID + ": container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/childContainers/add")
    public Response addContainerChildContainer(@QueryParam("ID") long id, @QueryParam("childContainerID") long childContainerID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding child container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childContainerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Container childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(childContainerID);
                if (childContainer != null) {
                    container.addContainerChildContainer(childContainer);
                    return Response.status(Status.OK).entity("Child container " + childContainerID + " successfully added to container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding child container into container " + id + " : child container " + childContainerID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding child container (" + childContainerID + ") to container " + id + ": container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/childContainers/delete")
    public Response deleteContainerChildContainer(@QueryParam("ID") long id, @QueryParam("childContainerID") long childContainerID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing child container : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, childContainerID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Container childContainer = MappingBootstrap.getMappingSce().getContainerSce().getContainer(childContainerID);
                if (childContainer != null) {
                    container.removeContainerChildContainer(childContainer);
                    return Response.status(Status.OK).entity("Child container " + childContainerID + " successfully deleted from container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting child container from container " + id + " : child container " + childContainerID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting child container (" + childContainerID + ") from container " + id + ": container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/nodes/add")
    public Response addContainerNode(@QueryParam("ID") long id, @QueryParam("nodeID") long nodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, nodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);
                if (node != null) {
                    container.addContainerNode(node);
                    return Response.status(Status.OK).entity("Node " + nodeID + " successfully added to container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding node into container " + id + " : node " + nodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding node " + nodeID + " into container (" + id + ") : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/nodes/delete")
    public Response deleteContainerNode(@QueryParam("ID") long id, @QueryParam("nodeID") long nodeID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing node : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, nodeID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Node node = MappingBootstrap.getMappingSce().getNodeSce().getNode(nodeID);
                if (node != null) {
                    container.removeContainerNode(node);
                    return Response.status(Status.OK).entity("Node " + nodeID + " successfully deleted from container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting node from container " + id + " : node " + nodeID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deletinging node " + nodeID + " into container : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/gates/add")
    public Response addContainerGate(@QueryParam("ID") long id, @QueryParam("gateID") long gateID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by adding gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, gateID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);
                if (gate != null) {
                    container.addContainerGate(gate);
                    return Response.status(Status.OK).entity("Gate " + gateID + " successfully added to container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while adding gate into container " + id + " : gate " + gateID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding gate " + gateID + " into container (" + id + ") : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/gates/delete")
    public Response deleteContainerGate(@QueryParam("ID") long id, @QueryParam("nodeID") long gateID) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update container by removing gate : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, gateID});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Gate gate = MappingBootstrap.getMappingSce().getGateSce().getGate(gateID);
                if (gate != null) {
                    container.removeContainerGate(gate);
                    return Response.status(Status.OK).entity("Gate " + gateID + " successfully deleted from container " + id + ".").build();
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Error while deleting gate from container " + id + " : gate " + gateID + " not found.").build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting gate " + gateID + " from container (" + id + ") : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/add")
    public Response addContainerProperty(@QueryParam("ID") long id, @QueryParam("propertyName") String name, @QueryParam("propertyValue") String value,
                                         @DefaultValue("String") @QueryParam("propertyType") String type) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}] update container by adding a property : ({},({},{},{}))", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name, value, type});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                Object oValue;
                try {
                    oValue = ToolBox.extractPropertyObjectValueFromString(value, type);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    String result = e.getMessage();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }
                container.addContainerProperty(name, oValue);
                return Response.status(Status.OK).entity("Property (" + name + "," + value + ") successfully added to container " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while adding property (" + name + "," + value + ") into container : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/properties/delete")
    public Response deleteContainerProperty(@QueryParam("ID") long id, @QueryParam("propertyName") String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] update container by removing a property : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
        if (subject.hasRole("mappinginjector") || subject.isPermitted("mappingDB:write") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            Container container = MappingBootstrap.getMappingSce().getContainerSce().getContainer(id);
            if (container != null) {
                container.removeContainerProperty(name);
                return Response.status(Status.OK).entity("Property (" + name + ") successfully deleted from container " + id + ".").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Error while deleting property (" + name + ") from container : container " + id + " not found.").build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to write on mapping db. Contact your administrator.").build();
        }
    }
}