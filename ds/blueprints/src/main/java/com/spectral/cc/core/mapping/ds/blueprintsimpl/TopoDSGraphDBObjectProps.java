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

package com.spectral.cc.core.mapping.ds.blueprintsimpl;

import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

public class TopoDSGraphDBObjectProps {

    private static final Logger log = LoggerFactory.getLogger(TopoDSGraphDBObjectProps.class);

    public static void synchronizeObjectPropertyToDB(Vertex vertex, String key, Object value, String topoObjPropsKey) {
        if (TopoDSGraphDB.isBlueprintsNeo4j()) {
            if (value instanceof HashMap) {
                HashMap<String, Object> hashMap = (HashMap) value;
                for (String hKey: hashMap.keySet()) {
                    Object hValue = hashMap.get(hKey);
                    log.debug("Synchronize property {}_{}_HashMap_{} : {}...", new Object[]{topoObjPropsKey, key, hKey, hValue});
                    vertex.setProperty(topoObjPropsKey+"_"+key+"_HashMap_"+hKey, hValue);
                }
                return;
            }
        }
        log.debug("Synchronize property {}_{}...", new Object[]{topoObjPropsKey,key});
        vertex.setProperty(topoObjPropsKey+"_"+key, value);
    }

    public static void synchronizeObjectPropertyFromDB(Vertex vertex, HashMap<String,Object> props, String topoObjPropsKey) {
        HashMap<String, Object> neoObjProps = new HashMap<String,Object>();
        Iterator<String> iterK = vertex.getPropertyKeys().iterator();
        while (iterK.hasNext()) {
            String key = iterK.next();
            if (key.contains(topoObjPropsKey)) {
                String subkey = key.split(topoObjPropsKey+"_")[1];
                if (TopoDSGraphDB.isBlueprintsNeo4j()) {
                    if (subkey.contains("HashMap")) {
                        String notTypedSubkey = subkey.split("_HashMap_")[0];
                        Object subkeyObjValue = neoObjProps.get(notTypedSubkey);
                        if (subkeyObjValue==null) {
                            subkeyObjValue = new HashMap<String,Object>();
                            neoObjProps.put(notTypedSubkey,subkeyObjValue);
                        }
                        String hKey = subkey.split("_HashMap_")[1];
                        log.debug("Synchronize {} property {} into HashMap {}..", new Object[]{hKey, vertex.getProperty(key).toString(), notTypedSubkey});
                        ((HashMap)subkeyObjValue).put(hKey,vertex.getProperty(key));
                    } else {
                        props.put(subkey, vertex.getProperty(key));
                    }
                } else {
                    props.put(subkey, vertex.getProperty(key));
                }
            }
        }

        if (TopoDSGraphDB.isBlueprintsNeo4j())
            for (String subkey :  neoObjProps.keySet())
                props.put(subkey, neoObjProps.get(subkey));
    }
}