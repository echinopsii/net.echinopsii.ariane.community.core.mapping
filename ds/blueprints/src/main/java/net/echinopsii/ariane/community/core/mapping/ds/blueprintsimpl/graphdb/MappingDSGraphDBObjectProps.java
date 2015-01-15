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

package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb;

import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MappingDSGraphDBObjectProps {

    private static final Logger log = LoggerFactory.getLogger(MappingDSGraphDBObjectProps.class);

    public static void flatObjectProperties(Vertex vertex, String key, Object value, String mappingObjPropsKey) {
        if (value instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap)value;
            for (String hKey: hashMap.keySet()) {
                Object hValue = hashMap.get(hKey);
                flatObjectProperties(vertex, key + "_HashMap_" + hKey, hValue, mappingObjPropsKey);
                //vertex.setProperty(mappingObjPropsKey+"_"+key+"_HashMap_"+hKey, hValue);
            }
            return;
        } else if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            double scale = bigDecimal.scale();
            flatObjectProperties(vertex, key + "_BigDecimalScale", scale, mappingObjPropsKey);
            double otherValue = bigDecimal.doubleValue()*Math.pow(10,scale);
            flatObjectProperties(vertex, key + "_BigDecimalValue", otherValue, mappingObjPropsKey);
            return;
        } else if (value instanceof ArrayList) {
            for (Object aValue : (ArrayList<Object>)value)
                flatObjectProperties(vertex, key + "_ArrayList." + ((ArrayList<Object>) value).indexOf(aValue), aValue, mappingObjPropsKey);
            return;
        }
        log.debug("Synchronize property {}_{} : {}...", new Object[]{mappingObjPropsKey,key,value.toString()});
        vertex.setProperty(mappingObjPropsKey+"_"+key, value);
    }

    public static void synchronizeObjectPropertyToDB(Vertex vertex, String key, Object value, String mappingObjPropsKey) {
        if (MappingDSGraphDB.isBlueprintsNeo4j())
            flatObjectProperties(vertex, key, value, mappingObjPropsKey);
        else {
            log.debug("Synchronize property {}_{} : {}...", new Object[]{mappingObjPropsKey,key,value.toString()});
            vertex.setProperty(mappingObjPropsKey+"_"+key, value);
        }
    }

    public static void removeObjectPropertyFromDB(Vertex vertex, String key, String mappingObjPropsKey) {
        Object value = vertex.getProperty(mappingObjPropsKey+"_"+key);
        if (MappingDSGraphDB.isBlueprintsNeo4j() && value == null) {
            for (String pKey : vertex.getPropertyKeys()) {
                if (pKey.startsWith(mappingObjPropsKey+"_"+key+"_HashMap") ||
                    pKey.startsWith(mappingObjPropsKey+"_"+key+"_BigDecimal") ||
                    pKey.startsWith(mappingObjPropsKey+"_"+key+"_ArrayList"))
                    vertex.removeProperty(pKey);
            }
        } else if (value != null)
            vertex.removeProperty(mappingObjPropsKey+"_"+key);
    }

    private static void unflatVertexPropertyToObject(Vertex vertex, String key, String prefixObjKey, String splittedKey, String parentObjKey,
                                                    HashMap<String, Object> props,  HashMap<String, Object> objectsMap) {
        String keyName = splittedKey.split(prefixObjKey + "_|\\.")[1].split("_")[0];
        String type    = null;
        String subKey  = null;

        if (splittedKey.split("_|\\.").length>2) {
            type = splittedKey.split(keyName + "_")[1].split("_|\\.")[0];
            subKey = splittedKey.split(keyName + "_")[1];
        }

        if (type!=null) {
            if (type.equals("HashMap") || type.equals("ArrayList")) {
                Object keyObject = null;
                if (parentObjKey!=null)
                    keyObject = objectsMap.get(parentObjKey+"."+keyName);
                else
                    keyObject = objectsMap.get(keyName);

                if (keyObject==null) {
                    if (type.equals("HashMap"))
                        keyObject = new HashMap<String, Object>();
                    else if (type.equals("ArrayList"))
                        keyObject = new ArrayList<Object>();
                    if (parentObjKey!=null) {
                        Object parentObj = objectsMap.get(parentObjKey);
                        if (parentObj instanceof HashMap)
                            ((HashMap<String, Object>) parentObj).put(keyName, keyObject);
                        else if (parentObj instanceof ArrayList) {
                            int index = new Integer(keyName);
                            while (index >= ((ArrayList<Object>) parentObj).size())
                                ((ArrayList<Object>) parentObj).add(null);
                            ((ArrayList<Object>) parentObj).set(new Integer(keyName), keyObject);
                        } else
                            log.error("Unsupported property type {}", parentObj.getClass().getCanonicalName());

                        objectsMap.put(parentObjKey+"."+keyName, keyObject);
                    } else {
                        props.put(keyName, keyObject);
                        objectsMap.put(keyName, keyObject);
                    }
                }
                unflatVertexPropertyToObject(vertex, key, type, subKey, (parentObjKey!=null)?parentObjKey+"."+keyName:keyName, props, objectsMap);
            } else if (type.contains("BigDecimal")) {
                if (type.equals("BigDecimalValue")) {
                    Object scaleObject = null;
                    if (parentObjKey!=null)
                        scaleObject = objectsMap.get(parentObjKey + "." + keyName + ".Scale");
                    else
                        scaleObject = objectsMap.get(keyName+".Scale");

                    Object valueObject = vertex.getProperty(key);
                    if (scaleObject==null) {
                        if (parentObjKey!=null)
                            objectsMap.put(parentObjKey+"."+keyName+".Value", valueObject);
                        else
                            objectsMap.put(keyName+".Value", valueObject);
                    } else {
                        if (parentObjKey!=null) {
                            Object parentObj = objectsMap.get(parentObjKey);
                            if (parentObj instanceof HashMap)
                                ((HashMap<String, Object>) parentObj).put(keyName, new BigDecimal((Double)valueObject / Math.pow(10, (double) scaleObject)));
                            else if (parentObj instanceof ArrayList) {
                                int index = new Integer(keyName);
                                while (index >= ((ArrayList<Object>) parentObj).size())
                                    ((ArrayList<Object>) parentObj).add(null);
                                ((ArrayList<Object>) parentObj).set(new Integer(keyName), new BigDecimal((Double)valueObject / Math.pow(10, (double) scaleObject)));
                            } else
                                log.error("Unsupported property type {}", parentObj.getClass().getCanonicalName());
                        } else
                            props.put(keyName, new BigDecimal((Double)valueObject / Math.pow(10, (Double) scaleObject)));
                    }
                } else if (type.equals("BigDecimalScale")) {
                    Object valueObject = null;
                    if (parentObjKey!=null)
                        valueObject = objectsMap.get(parentObjKey + "." + keyName + ".Value");
                    else
                        valueObject = objectsMap.get(keyName+".Value");

                    Object scaleObject = vertex.getProperty(key);
                    if (valueObject==null) {
                        if (parentObjKey!=null)
                            objectsMap.put(parentObjKey+"."+keyName+".Scale", scaleObject);
                        else
                            objectsMap.put(keyName+".Scale", scaleObject);
                    } else {
                        if (parentObjKey!=null) {
                            Object parentObj = objectsMap.get(parentObjKey);
                            if (parentObj instanceof HashMap)
                                ((HashMap<String, Object>) parentObj).put(keyName, new BigDecimal((Double)valueObject / Math.pow(10, (double) scaleObject)));
                            else if (parentObj instanceof ArrayList) {
                                int index = new Integer(keyName);
                                while (index >= ((ArrayList<Object>) parentObj).size())
                                    ((ArrayList<Object>) parentObj).add(null);
                                ((ArrayList<Object>) parentObj).set(new Integer(keyName), new BigDecimal((Double)valueObject / Math.pow(10, (double) scaleObject)));
                            } else
                                log.error("Unsupported property type {}", parentObj.getClass().getCanonicalName());
                        } else
                            props.put(keyName, new BigDecimal((Double)valueObject / Math.pow(10, (Double) scaleObject)));
                    }
                }
            }
        } else {
            if (parentObjKey!=null) {
                Object parentObj = objectsMap.get(parentObjKey);
                if (parentObj instanceof HashMap)
                    ((HashMap<String, Object>)parentObj).put(keyName, vertex.getProperty(key));
                else if (parentObj instanceof ArrayList) {
                    int index = new Integer(keyName);
                    while (index >= ((ArrayList<Object>) parentObj).size())
                        ((ArrayList<Object>) parentObj).add(null);
                    ((ArrayList<Object>) parentObj).set(new Integer(keyName), vertex.getProperty(key));
                }
                else
                    log.error("Unsupported property type {}", parentObj.getClass().getCanonicalName());
            } else {
                props.put(keyName, vertex.getProperty(key));
            }
        }
    }

    public static void unflatVertexPropsToObjects(Vertex vertex, HashMap<String, Object> props, String mappingObjPropsKey) {
        HashMap<String, Object> vertexObjectProperties = new HashMap<String,Object>();
        HashMap<String, Object> objectsMap = new HashMap<String, Object>();

        for (String key : vertex.getPropertyKeys())
            if (key.contains(mappingObjPropsKey))
                unflatVertexPropertyToObject(vertex, key, mappingObjPropsKey, key, null, props, objectsMap);
    }

    public static void synchronizeObjectPropertyFromDB(Vertex vertex, HashMap<String,Object> props, String mappingObjPropsKey) {
        if (MappingDSGraphDB.isBlueprintsNeo4j()) {
            unflatVertexPropsToObjects(vertex, props, mappingObjPropsKey);
        } else {
            Iterator<String> iterK = vertex.getPropertyKeys().iterator();
            while (iterK.hasNext()) {
                String key = iterK.next();
                if (key.contains(mappingObjPropsKey)) {
                    String subkey = key.split(mappingObjPropsKey + "_")[1];
                    props.put(subkey, vertex.getProperty(key));
                }
            }
        }
    }
}