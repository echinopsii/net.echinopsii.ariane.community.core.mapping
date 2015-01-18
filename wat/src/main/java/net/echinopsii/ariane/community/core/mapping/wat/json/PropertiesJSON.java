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

package net.echinopsii.ariane.community.core.mapping.wat.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PropertiesJSON {

    private final static Logger log = LoggerFactory.getLogger(PropertiesJSON.class);


    private static HashMap<String, Object> JSONStringMapToPropertyObject(JsonNode rootTree) throws PropertiesException {
        HashMap<String, Object> ret = null;
        if (rootTree.isObject()) {
            ObjectNode objectNode = (ObjectNode) rootTree;
            Iterator<Entry<String, JsonNode>> iter = objectNode.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> entry = iter.next();
                String objectFieldName = entry.getKey();
                JsonNode objectField = entry.getValue();
                if (objectField.isArray()) {
                    if (ret == null)
                        ret = new HashMap<String, Object>();
                    ArrayNode arrayNode = (ArrayNode) objectField;
                    if (objectField.size() == 2) {
                        String vType = arrayNode.get(0).asText();
                        JsonNode subRootTree = arrayNode.get(1);
                        String value = arrayNode.get(1).asText();
                        switch (vType.toLowerCase()) {
                            case "boolean":
                                ((HashMap<String, Object>) ret).put(objectFieldName, new Boolean(value));
                                break;
                            case "double":
                                ((HashMap<String, Object>) ret).put(objectFieldName, new Double(value));
                                break;
                            case "int":
                            case "integer":
                                ((HashMap<String, Object>) ret).put(objectFieldName, new Integer(value));
                                break;
                            case "long":
                                ((HashMap<String, Object>) ret).put(objectFieldName, new Long(value));
                                break;
                            case "string":
                                ((HashMap<String, Object>) ret).put(objectFieldName, value);
                                break;
                            case "map":
                                HashMap<String, Object> valueHashMap = JSONStringMapToPropertyObject(subRootTree);
                                ((HashMap<String, Object>) ret).put(objectFieldName, valueHashMap);
                                break;
                            case "array":
                                ArrayList<?> valueArray = JSONStringArrayToPropertyObject(subRootTree);
                                ((HashMap<String, Object>) ret).put(objectFieldName, valueArray);
                                break;
                            default:
                                throw new PropertiesException("Unsupported map entry type (" + vType.toLowerCase() + "). Supported types are : boolean, double, integer, long, string");
                        }
                    } else {
                        throw new PropertiesException("Json property map badly defined. Each map entry should be defined with following array : ['value type','value']");
                    }
                } else {
                    throw new PropertiesException("Json property map badly defined. Each map entry should be defined with following array : ['value type','value']");
                }
            }
        } else {
            throw new PropertiesException("Json property badly defined : map should be defined as a Json object.");
        }
        return ret;
    }

    private static ArrayList<?> JSONStringArrayToPropertyObject(JsonNode rootTree) throws PropertiesException {
        Object ret = null;
        if (rootTree.isArray()) {
            ArrayNode arrayNode = (ArrayNode) rootTree;
            if (arrayNode.size() == 2) {
                JsonNode arrayType = arrayNode.get(0);
                if (arrayType.isTextual()) {
                    String arrayTypeValue = arrayType.asText();
                    JsonNode value = arrayNode.get(1);
                    if (value.isArray()) {
                        ArrayNode arrayValue = (ArrayNode) value;
                        Iterator<JsonNode> iter = arrayValue.elements();
                        switch(arrayTypeValue.toLowerCase()) {
                            case "map":
                                ret = new ArrayList<HashMap<String, Object>>();
                                while (iter.hasNext()) {
                                    HashMap<String, Object> valueHashMap = JSONStringMapToPropertyObject(iter.next());
                                    ((ArrayList<HashMap<String, Object>>) ret).add(valueHashMap);
                                }
                                break;
                            case "array":
                                ret = new ArrayList<ArrayList<?>>();
                                while (iter.hasNext()) {
                                    ArrayList<?> valueArray = JSONStringArrayToPropertyObject(iter.next());
                                    ((ArrayList<ArrayList<?>>) ret).add(valueArray);
                                }
                                break;
                            case "boolean":
                                ret = new ArrayList<Boolean>();
                                while (iter.hasNext()) {
                                    JsonNode next = iter.next();
                                    if (next.isBoolean())
                                        ((ArrayList)ret).add(next.asBoolean());
                                    else
                                        throw new PropertiesException("Json property array badly defined. Following array value is not a boolean : " +next.toString() + ".\n" +
                                                                              "Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
                                }
                                break;
                            case "double":
                                ret = new ArrayList<Double>();
                                while (iter.hasNext()) {
                                    JsonNode next = iter.next();
                                    if (next.isDouble())
                                        ((ArrayList)ret).add(next.asDouble());
                                    else
                                        throw new PropertiesException("Json property array badly defined. Following array value is not a double : " +next.toString() + ".\n" +
                                                                              "Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
                                }
                                break;
                            case "int":
                            case "integer":
                                ret = new ArrayList<Integer>();
                                while (iter.hasNext()) {
                                    JsonNode next = iter.next();
                                    if (next.isInt())
                                        ((ArrayList)ret).add(next.asInt());
                                    else
                                        throw new PropertiesException("Json property array badly defined. Following array value is not an integer : " +next.toString() + ".\n" +
                                                                              "Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
                                }
                                break;
                            case "long":
                                ret = new ArrayList<Long>();
                                while (iter.hasNext()) {
                                    JsonNode next = iter.next();
                                    if (next.isLong())
                                        ((ArrayList)ret).add(next.asLong());
                                    else
                                        throw new PropertiesException("Json property array badly defined. Following array value is not a long : " +next.toString() + ".\n" +
                                                                              "Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
                                }
                                break;
                            case "string":
                                ret = new ArrayList<String>();
                                while (iter.hasNext()) {
                                    JsonNode next = iter.next();
                                    if (next.isTextual())
                                        ((ArrayList)ret).add(next.asText());
                                    else
                                        throw new PropertiesException("Json property array badly defined. Following array value is not textual : " +next.toString() + ".\n" +
                                                                              "Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
                                }
                                break;
                            default:
                                throw new PropertiesException("Unsupported array type (" + arrayTypeValue.toLowerCase() + "). Supported types are : boolean, double, integer, long, string");
                        }
                    } else {
                        throw new PropertiesException("Json property array badly defined. Array value is not an array.\nArray entry should be defined with following array : ['array type',['value1','value2' ...]]");
                    }
                } else {
                    throw new PropertiesException("Json property array badly defined. Array type is not textual.\nArray entry should be defined with following array : ['array type',['value1','value2' ...]]");
                }
            } else {
                throw new PropertiesException("Json property array badly defined. Array entry should be defined with following array : ['array type',['value1','value2' ...]]");
            }
        } else {
            throw new PropertiesException("Json property badly defined : array should be defined as a Json array.");
        }
        return (ArrayList<?>) ret;
    }

    public static Object JSONStringToPropertyObject(String type, String json) throws IOException, PropertiesException {
        Object ret = null;
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jp = factory.createJsonParser(json);
        JsonNode rootTree = mapper.readTree(jp);

        if (type.toLowerCase().equals("map"))
            ret = JSONStringMapToPropertyObject(rootTree);
        else if (type.toLowerCase().equals("array")) {
            ret = JSONStringArrayToPropertyObject(rootTree);
        } else {
            throw new PropertiesException("Unsupported json type (" + type + "). Supported types are : array, map");
        }

        return ret;
    }

    private static void arrayListToJSON(ArrayList<Object> aobj, String objectName, JsonGenerator jgenerator) throws IOException {
        if (objectName!=null)
            jgenerator.writeArrayFieldStart(objectName);
        else
            jgenerator.writeStartArray();
        @SuppressWarnings("unchecked")
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
            } else if (value instanceof BigDecimal) {
                jgenerator.writeNumber((BigDecimal) value);
            } else if (value instanceof Boolean) {
                jgenerator.writeBoolean((Boolean) value);
            } else if (value instanceof ArrayList) {
                arrayListToJSON((ArrayList<Object>) value, null, jgenerator);
            } else if (value instanceof String[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((String[])value)), objectName, jgenerator);
            } else if (value instanceof Long[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Long[])value)), objectName, jgenerator);
            } else if (value instanceof Integer[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Integer[])value)), objectName, jgenerator);
            } else if (value instanceof Double[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Double[])value)), objectName, jgenerator);
            } else if (value instanceof BigDecimal[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((BigDecimal[])value)), objectName, jgenerator);
            } else if (value instanceof Boolean[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Boolean[])value)), objectName, jgenerator);
            } else if (value instanceof HashMap) {
                hashMapToJSON((HashMap)value, null, jgenerator);
            }
        }
        jgenerator.writeEndArray();
    }

    private static void hashMapToJSON(HashMap<String, Object> hobj, String objectName, JsonGenerator jgenerator) throws IOException {
        if (objectName!=null)
            jgenerator.writeObjectFieldStart(objectName);
        else
            jgenerator.writeStartObject();
        @SuppressWarnings("unchecked")
        Iterator<String> iterHK = hobj.keySet().iterator();
        while (iterHK.hasNext()) {
            String key = iterHK.next();
            Object value = hobj.get(key);
            if (value instanceof String) {
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (String) value});
                jgenerator.writeStringField(key, (String) value);
            } else if (value instanceof Long) {
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (Long) value});
                jgenerator.writeNumberField(key, (Long) value);
            } else if (value instanceof Integer) {
                jgenerator.writeNumberField(key, (Integer) value);
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (Integer) value});
            } else if (value instanceof Double) {
                jgenerator.writeNumberField(key, (Double) value);
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (Double) value});
            } else if (value instanceof BigDecimal) {
                jgenerator.writeNumberField(key, (BigDecimal) value);
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (BigDecimal) value});
            } else if (value instanceof Boolean) {
                jgenerator.writeBooleanField(key, (Boolean) value);
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, (Boolean) value});
            } else if (value instanceof HashMap) {
                hashMapToJSON((HashMap) value, key, jgenerator);
                log.debug("HashMap key {} value {}:{}", new Object[]{objectName, key, value.toString()});
            } else if (value instanceof ArrayList) {
                arrayListToJSON((ArrayList)value, key, jgenerator);
            } else if (value instanceof String[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((String[])value)), objectName, jgenerator);
            } else if (value instanceof Long[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Long[])value)), objectName, jgenerator);
            } else if (value instanceof Integer[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Integer[])value)), objectName, jgenerator);
            } else if (value instanceof Double[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Double[])value)), objectName, jgenerator);
            } else if (value instanceof BigDecimal[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((BigDecimal[])value)), objectName, jgenerator);
            } else if (value instanceof Boolean[]) {
                arrayListToJSON(new ArrayList<Object>(Arrays.asList((Boolean[]) value)), objectName, jgenerator);
            }
        }
        jgenerator.writeEndObject();
    }

    public static void propertiesToJSON(HashMap<String, Object> props, JsonGenerator jgenerator) throws IOException {
        if (props != null) {
            log.debug("Read properties {}", new Object[]{props.toString()});
            Iterator<Entry<String, Object>> iterP = props.entrySet().iterator();
            while (iterP.hasNext()) {
                Entry<String, Object> current = iterP.next();
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
                } else if (obj instanceof BigDecimal) {
                    jgenerator.writeNumberField(objectName, (BigDecimal) obj);
                } else if (obj instanceof HashMap<?, ?>) {
                    log.debug("Property {} value is an object", new Object[]{objectName});
                    hashMapToJSON((HashMap<String, Object>)obj, objectName, jgenerator);
                } else if (obj instanceof ArrayList<?>) {
                    arrayListToJSON((ArrayList)obj, objectName, jgenerator);
                } else if (obj instanceof String[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((String[]) obj)), objectName, jgenerator);
                } else if (obj instanceof Long[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((Long[]) obj)), objectName, jgenerator);
                } else if (obj instanceof Integer[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((Integer[]) obj)), objectName, jgenerator);
                } else if (obj instanceof Double[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((Double[]) obj)), objectName, jgenerator);
                } else if (obj instanceof BigDecimal[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((BigDecimal[]) obj)), objectName, jgenerator);
                } else if (obj instanceof Boolean[]) {
                    arrayListToJSON(new ArrayList<Object>(Arrays.asList((Boolean[])obj)), objectName, jgenerator);
                } else {
                    log.error("Property {} type is not managed...", new Object[]{objectName});
                }
            }
        }
    }
}