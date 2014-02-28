/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 14/01/14 echinopsii
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

package com.spectral.cc.core.mapping.wat.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PropertiesJSON {

    private final static Logger log = LoggerFactory.getLogger(PropertiesJSON.class);

    public static void propertiesToJSON(HashMap<String, Object> props, JsonGenerator jgenerator) throws IOException {
        if (props != null) {
            log.debug("Read properties {}", new Object[]{props.toString()});
            Iterator<Map.Entry<String, Object>> iterP = props.entrySet().iterator();
            while (iterP.hasNext()) {
                Map.Entry<String, Object> current = iterP.next();
                String objectName = current.getKey();
                Object obj = current.getValue();
                log.debug("Property {}", new Object[]{objectName});
                if (obj instanceof String) {
                    jgenerator.writeStringField(objectName, (String) obj);
                } else if (obj instanceof Boolean) {
                    jgenerator.writeBooleanField(objectName, (Boolean) obj);
                } else if (obj instanceof Long) {
                    jgenerator.writeNumberField(objectName, (Long) obj);
                } else if (obj instanceof Integer) {
                    jgenerator.writeNumberField(objectName, (Integer) obj);
                } else if (obj instanceof Double) {
                    jgenerator.writeNumberField(objectName, (Double) obj);
                } else if (obj instanceof HashMap<?, ?>) {
                    log.debug("Property {} value is an object", new Object[]{objectName});
                    jgenerator.writeObjectFieldStart(objectName);
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> hobj = (HashMap<String, Object>) obj;
                    Iterator<String> iterHK = hobj.keySet().iterator();
                    while (iterHK.hasNext()) {
                        String key = iterHK.next();
                        Object value = hobj.get(key);
                        if (value instanceof String) {
                            log.debug("Property Object {} value {}:{}", new Object[]{objectName, key, (String) value});
                            jgenerator.writeStringField(key, (String) value);
                        } else if (value instanceof Long) {
                            log.debug("Property Object {} value {}:{}", new Object[]{objectName, key, (Long) value});
                            jgenerator.writeNumberField(key, (Long) value);
                        } else if (value instanceof Integer) {
                            jgenerator.writeNumberField(key, (Integer) value);
                            log.debug("Property Object {} value {}:{}", new Object[]{objectName, key, (Integer) value});
                        } else if (value instanceof Double) {
                            jgenerator.writeNumberField(key, (Double) value);
                            log.debug("Property Object {} value {}:{}", new Object[]{objectName, key, (Double) value});
                        } else if (value instanceof Boolean) {
                            jgenerator.writeBooleanField(key, (Boolean) value);
                        }
                    }
                    jgenerator.writeEndObject();
                } else if (obj instanceof ArrayList<?>) {
                    jgenerator.writeArrayFieldStart(objectName);
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> aobj = (ArrayList<Object>) obj;
                    Iterator<Object> iterAK = aobj.iterator();
                    while (iterAK.hasNext()) {
                        Object value = iterAK.next();
                        if (value instanceof String) {
                            jgenerator.writeString((String) value);
                        } else if (value instanceof Long) {
                            jgenerator.writeNumber((Long) value);
                        } else if (value instanceof Integer) {
                            jgenerator.writeNumber((Integer) value);
                        } else if (value instanceof Double) {
                            jgenerator.writeNumber((Double) value);
                        } else if (value instanceof Boolean) {
                            jgenerator.writeBoolean((Boolean) value);
                        }
                    }
                    jgenerator.writeEndArray();
                } else if (obj instanceof String[]) {
                    jgenerator.writeArrayFieldStart(objectName);
                    for (String value : (String[])obj) {
                        jgenerator.writeString(value);
                    }
                    jgenerator.writeEndArray();
                } else if (obj instanceof Long[]) {
                    jgenerator.writeArrayFieldStart(objectName);
                    for (Long value : (Long[])obj) {
                        jgenerator.writeNumber(value);
                    }
                    jgenerator.writeEndArray();
                } else if (obj instanceof Integer[]) {
                    jgenerator.writeArrayFieldStart(objectName);
                    for (Integer value : (Integer[])obj) {
                        jgenerator.writeNumber(value);
                    }
                    jgenerator.writeEndArray();
                } else if (obj instanceof Double[]) {
                    jgenerator.writeArrayFieldStart(objectName);
                    for (Double value : (Double[])obj) {
                        jgenerator.writeNumber(value);
                    }
                    jgenerator.writeEndArray();
                } else if (obj instanceof Boolean[]) {
                    jgenerator.writeArrayFieldStart(objectName);
                    for (Boolean value : (Boolean[])obj) {
                        jgenerator.writeBoolean(value);
                    }
                    jgenerator.writeEndArray();
                } else {
                    log.error("Property {} type is not managed...", new Object[]{objectName});
                }
            }
        }
    }
}