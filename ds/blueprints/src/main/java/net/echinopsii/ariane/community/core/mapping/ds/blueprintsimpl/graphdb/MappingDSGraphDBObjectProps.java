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
            flatObjectProperties(vertex, key + "_BigDecimal_Scale", scale, mappingObjPropsKey);
            double otherValue = bigDecimal.doubleValue()*Math.pow(10,scale);
            flatObjectProperties(vertex, key + "_BigDecimal_Value", otherValue, mappingObjPropsKey);
            return;
        } else if (value instanceof ArrayList) {
            for (Object aValue : (ArrayList<Object>)value)
                flatObjectProperties(vertex, key + "_ArrayList_" + ((ArrayList<Object>) value).indexOf(aValue), aValue, mappingObjPropsKey);
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

    public static void unflatVertexPropertiesToObjectProperties(Vertex vertex, HashMap<String, Object> props, String mappingObjPropsKey) {

        HashMap<String, Object> vertexGlobalHashMapProps = new HashMap<String,Object>();
        HashMap<String, Double> vertexGlobalDoubleScale  = new HashMap<String,Double>();
        HashMap<String, Double> vertexGlobalDoubleValue  = new HashMap<String,Double>();

        Iterator<String> iterK = vertex.getPropertyKeys().iterator();
        while (iterK.hasNext()) {
            String key = iterK.next();
            if (key.contains(mappingObjPropsKey)) {
                String subkey = key.split(mappingObjPropsKey + "_")[1];

                if (subkey.contains("HashMap")) {

                    String hashMapPropKey = subkey.split("_HashMap_")[0];
                    Object subkeyObjValue = vertexGlobalHashMapProps.get(hashMapPropKey);
                    if (subkeyObjValue == null) {
                        subkeyObjValue = new HashMap<String, Object>();
                        vertexGlobalHashMapProps.put(hashMapPropKey, subkeyObjValue);
                    }
                    String hKey = subkey.split("_HashMap_")[1];
                    log.debug("Synchronize {} property {} into HashMap {}..", new Object[]{hKey, vertex.getProperty(key).toString(), hashMapPropKey});
                    ((HashMap) subkeyObjValue).put(hKey, vertex.getProperty(key));

                } else if (subkey.contains("BigDecimal")) {

                    String bigDecimalPropKey = subkey.split("_BigDecimal_")[0];
                    if (subkey.contains("Scale"))

                        if (vertexGlobalDoubleValue.get(bigDecimalPropKey) != null)
                            props.put(bigDecimalPropKey, new BigDecimal(vertexGlobalDoubleValue.get(bigDecimalPropKey) / Math.pow(10, (double) vertex.getProperty(key))));
                        else
                            vertexGlobalDoubleScale.put(bigDecimalPropKey, (double) vertex.getProperty(key));

                    else if (subkey.contains("Value"))

                        if (vertexGlobalDoubleScale.get(bigDecimalPropKey) != null)
                            props.put(bigDecimalPropKey, new BigDecimal((double) vertex.getProperty(key) / Math.pow(10, vertexGlobalDoubleScale.get(bigDecimalPropKey))));
                        else
                            vertexGlobalDoubleValue.put(bigDecimalPropKey, (double) vertex.getProperty(key));

                } else if (subkey.contains("ArrayList")) {

                } else {
                    props.put(subkey, vertex.getProperty(key));
                }

                for (String nsubkey :  vertexGlobalHashMapProps.keySet())
                    if (nsubkey.startsWith("MappingDSToPush."))
                        props.put(nsubkey, vertexGlobalHashMapProps.get(nsubkey));
            }
        }
    }

    public static void synchronizeObjectPropertyFromDB(Vertex vertex, HashMap<String,Object> props, String mappingObjPropsKey) {
        if (MappingDSGraphDB.isBlueprintsNeo4j()) {
            unflatVertexPropertiesToObjectProperties(vertex, props, mappingObjPropsKey);
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