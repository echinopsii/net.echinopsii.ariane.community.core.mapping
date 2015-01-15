package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class MappingDSGraphDBObjectPropsTest {

    private static Graph  graph  = null;
    private static Vertex vertexToUnflat = null;

    private static HashMap<String, Object> simpleHashMap1;
    private static HashMap<String, Object> simpleHashMap2;
    private static HashMap<String, Object> simpleHashMap3;

    private static ArrayList<Object> simpleArray1;
    private static ArrayList<Object> simpleArray2;
    private static ArrayList<Object> simpleArray3;

    private static BigDecimal pi;
    private static BigDecimal e;

    private static HashMap<String, HashMap<String, Object>> HMembbededSimpleHashMap;
    private static HashMap<String, ArrayList<Object>> HMembbededSimpleArray;
    private static HashMap<String, BigDecimal> HMembbededBigDecimal;

    private static void buildSimpleHashMap1() {
        simpleHashMap1 = new HashMap<>();
        simpleHashMap1.put("key11", "value11"); simpleHashMap1.put("key12", "value12"); simpleHashMap1.put("key13", "value13");
    }

    private static void defineSimpleHashMap1(Vertex vertex) {
        buildSimpleHashMap1();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleHashMap1", simpleHashMap1, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildSimpleHashMap2() {
        simpleHashMap2 = new HashMap<>();
        simpleHashMap2.put("key21", 21); simpleHashMap2.put("key22", 22); simpleHashMap2.put("key23", 23);
    }

    private static void defineSimpleHashMap2(Vertex vertex) {
        buildSimpleHashMap2();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleHashMap2", simpleHashMap2, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildSimpleHashMap3() {
        simpleHashMap3 = new HashMap<>();
        simpleHashMap3.put("key31", 31); simpleHashMap3.put("key32", false); simpleHashMap3.put("key33", 3.1415);
    }

    private static void defineSimpleHashMap3(Vertex vertex) {
        buildSimpleHashMap3();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleHashMap3", simpleHashMap3, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildSimpleArray1(){
        simpleArray1 = new ArrayList<>();
        simpleArray1.add("value11");simpleArray1.add("value12");simpleArray1.add("value13");
    }

    private static void defineSimpleArray1(Vertex vertex) {
        buildSimpleArray1();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleArray1", simpleArray1, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildSimpleArray2() {
        simpleArray2 = new ArrayList<>();
        simpleArray2.add(21);simpleArray2.add(22);simpleArray2.add(23);
    }

    private static void defineSimpleArray2(Vertex vertex) {
        buildSimpleArray2();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleArray2", simpleArray2, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildSimpleArray3(){
        simpleArray3 = new ArrayList<>();
        simpleArray3.add(31);simpleArray3.add(true);simpleArray3.add(3.1415);
    }

    private static void defineSimpleArray3(Vertex vertex) {
        buildSimpleArray3();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "simpleArray3", simpleArray3, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void definePiBigDecimal(Vertex vertex) {
        pi = new BigDecimal(3.14159265359);
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "piBigDecimal", pi, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void defineEBigDecimal(Vertex vertex) {
        e = new BigDecimal(2.71828182846);
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "eBigDecimal", e, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildHMembeddedSimpleHashMap(){
        buildSimpleHashMap1(); buildSimpleHashMap2(); buildSimpleHashMap3();
        HMembbededSimpleHashMap = new HashMap<>();
        HMembbededSimpleHashMap.put("simpleHashMap1", simpleHashMap1); HMembbededSimpleHashMap.put("simpleHashMap2", simpleHashMap2); HMembbededSimpleHashMap.put("simpleHashMap3", simpleHashMap3);
    }
    private static void defineHMembeddedSimpleHashMap(Vertex vertex) {
        buildHMembeddedSimpleHashMap();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "HMembbededSimpleHashMap", HMembbededSimpleHashMap, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void buildHMembeddedSimpleArray() {
        buildSimpleArray1();buildSimpleArray2();buildSimpleArray3();
        HMembbededSimpleArray = new HashMap<>();
        HMembbededSimpleArray.put("simpleArray1", simpleArray1);HMembbededSimpleArray.put("simpleArray2", simpleArray2);HMembbededSimpleArray.put("simpleArray3", simpleArray3);
    }

    private static void defineHMembeddedSimpleArray(Vertex vertex) {
        buildHMembeddedSimpleArray();
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "HMembbededSimpleArray", HMembbededSimpleArray, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    private static void defineHMembeddedBigDecimal(Vertex vertex) {
        HMembbededBigDecimal = new HashMap<>();
        pi = new BigDecimal(3.14159265359);
        e = new BigDecimal(2.71828182846);
        HMembbededBigDecimal.put("piBigDecimal", pi);HMembbededBigDecimal.put("eBigDecimal", e);
        MappingDSGraphDBObjectProps.flatObjectProperties(vertex, "HMembbededBigDecimal", HMembbededBigDecimal, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
    }

    @BeforeClass
    public static void testSetup() {
        graph          = new TinkerGraph();
    }

    @Test
    public void testFlatSimpleHashMap() {
        Vertex vertex = graph.addVertex(null);
        defineSimpleHashMap1(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key+":"+vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY+"_simpleHashMap1_HashMap"));
            if (key.contains("key11"))
                assertTrue(vertex.getProperty(key).equals("value11"));
            else if (key.contains("key12"))
                assertTrue(vertex.getProperty(key).equals("value12"));
            else if (key.contains("key13"))
                assertTrue(vertex.getProperty(key).equals("value13"));
        }
    }

    @Test
    public void testUnflatSimpleHashMap() {
        Vertex vertex = graph.addVertex(null);
        defineSimpleHashMap1(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("simpleHashMap1"));
        assertTrue(unflattedProperties.get("simpleHashMap1").equals(simpleHashMap1));
    }

    @Test
    public void testFlatSimpleArray() {
        Vertex vertex = graph.addVertex(null);
        defineSimpleArray1(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key + ":" + vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY+"_simpleArray1_ArrayList."));
            int index = new Integer(key.split("_ArrayList\\.")[1]);
            assertTrue(vertex.getProperty(key).equals("value1"+(index+1)));
        }
    }

    @Test
    public void testUnflatSimpleArray() {
        Vertex vertex = graph.addVertex(null);
        defineSimpleArray1(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("simpleArray1"));
        assertTrue(unflattedProperties.get("simpleArray1").equals(simpleArray1));
    }

    @Test
    public void testFlatBigDecimal() {
        Vertex vertex = graph.addVertex(null);
        definePiBigDecimal(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key + ":" + vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY+"_piBigDecimal_BigDecimal"));
            if (key.contains("BigDecimalScale"))
                assertTrue(vertex.getProperty(key).equals(50.0));
            else if (key.contains("BigDecimalValue"))
                assertTrue(vertex.getProperty(key).equals(3.14159265359E50));
        }
    }

    @Test
    public void testUnflatSimpleBigDecimal() {
        Vertex vertex = graph.addVertex(null);
        defineEBigDecimal(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("eBigDecimal"));
        assertTrue(unflattedProperties.get("eBigDecimal").equals(e));
    }

    @Test
    public void testFlatHMembeddedSimpleHashMap() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedSimpleHashMap(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key + ":" + vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY+"_HMembbededSimpleHashMap_HashMap_"));
            String subkey = key.split(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY+"_")[1];
            String embeddedHashMapKey = subkey.split("_HashMap_")[1];
            if (embeddedHashMapKey.equals("simpleHashMap1")) {
                String embeddedHashMapKeyKey = subkey.split("_HashMap_")[2];
                if (embeddedHashMapKeyKey.equals("key11"))
                    assertTrue(vertex.getProperty(key).equals("value11"));
                else if (embeddedHashMapKeyKey.equals("key12"))
                    assertTrue(vertex.getProperty(key).equals("value12"));
                else if (embeddedHashMapKeyKey.equals("key13"))
                    assertTrue(vertex.getProperty(key).equals("value13"));
            } else if (embeddedHashMapKey.equals("simpleHashMap2")) {
                String embeddedHashMapKeyKey = subkey.split("_HashMap_")[2];
                if (embeddedHashMapKeyKey.equals("key21"))
                    assertTrue(vertex.getProperty(key).equals(21));
                else if (embeddedHashMapKeyKey.equals("key22"))
                    assertTrue(vertex.getProperty(key).equals(22));
                else if (embeddedHashMapKeyKey.equals("key23"))
                    assertTrue(vertex.getProperty(key).equals(23));
            } else if (embeddedHashMapKey.equals("simpleHashMap3")) {
                String embeddedHashMapKeyKey = subkey.split("_HashMap_")[2];
                if (embeddedHashMapKeyKey.equals("key31"))
                    assertTrue(vertex.getProperty(key).equals(31));
                else if (embeddedHashMapKeyKey.equals("key32"))
                    assertTrue(vertex.getProperty(key).equals(false));
                else if (embeddedHashMapKeyKey.equals("key33"))
                    assertTrue(vertex.getProperty(key).equals(3.1415));
            }
        }
    }

    @Test
    public void testUnflatHMembeddedSimpleHashMap() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedSimpleHashMap(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("HMembbededSimpleHashMap"));
        assertTrue(unflattedProperties.get("HMembbededSimpleHashMap").equals(HMembbededSimpleHashMap));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).containsKey("simpleHashMap1"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).get("simpleHashMap1").equals(simpleHashMap1));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).containsKey("simpleHashMap2"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).get("simpleHashMap2").equals(simpleHashMap2));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).containsKey("simpleHashMap3"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleHashMap")).get("simpleHashMap3").equals(simpleHashMap3));
    }

    @Test
    public void testFlatHMembeddedSimpleArray() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedSimpleArray(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key + ":" + vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY + "_HMembbededSimpleArray_HashMap_"));
            String subkey = key.split(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY + "_")[1];
            String embeddedArrayKey = subkey.split("_HashMap_")[1];
            int index = new Integer(key.split("_ArrayList\\.")[1]);
            if (embeddedArrayKey.equals("simpleArray1")) {
                assertTrue(vertex.getProperty(key).equals("value1"+(index+1)));
            } else if (embeddedArrayKey.equals("simpleArray2")) {
                assertTrue(vertex.getProperty(key).equals(20+index+1));
            } else if (embeddedArrayKey.equals("simpleArray3")) {
                if (index==0)
                    assertTrue(vertex.getProperty(key).equals(31));
                else if (index==1)
                    assertTrue(vertex.getProperty(key).equals(true));
                else if (index==2)
                    assertTrue(vertex.getProperty(key).equals(3.1415));
            }
        }
    }

    @Test
    public void testUnflatHMembeddedSimpleArray() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedSimpleArray(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("HMembbededSimpleArray"));
        assertTrue(unflattedProperties.get("HMembbededSimpleArray").equals(HMembbededSimpleArray));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).containsKey("simpleArray1"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).get("simpleArray1").equals(simpleArray1));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).containsKey("simpleArray2"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).get("simpleArray2").equals(simpleArray2));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).containsKey("simpleArray3"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededSimpleArray")).get("simpleArray3").equals(simpleArray3));
    }

    @Test
    public void testFlatHMembeddedBigDecimal() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedBigDecimal(vertex);
        for (String key : vertex.getPropertyKeys()) {
            //System.out.println(key + ":" + vertex.getProperty(key));
            assertTrue(key.startsWith(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY + "_HMembbededBigDecimal_HashMap_"));
            String subkey = key.split(MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY + "_")[1];
            String embeddedBigDecimalKey = subkey.split("_ArrayList\\.|_BigDecimal_Value|_BigDecimal_Scale|_HashMap_")[1];
            if (embeddedBigDecimalKey.contains("piBigDecimal")) {
                if (key.contains("BigDecimalScale"))
                    assertTrue(vertex.getProperty(key).equals(50.0));
                else if (key.contains("BigDecimalValue"))
                    assertTrue(vertex.getProperty(key).equals(3.14159265359E50));
            } else if (embeddedBigDecimalKey.contains("eBigDecimal")) {
                if (key.contains("BigDecimalScale"))
                    assertTrue(vertex.getProperty(key).equals(51.0));
                else if (key.contains("BigDecimalValue"))
                    assertTrue(vertex.getProperty(key).equals(2.71828182846E51));
            }
        }
    }

    @Test
    public void testUnflatHMembeddedBigDecimal() {
        Vertex vertex = graph.addVertex(null);
        defineHMembeddedBigDecimal(vertex);
        HashMap<String, Object> unflattedProperties = new HashMap<String, Object>();
        MappingDSGraphDBObjectProps.unflatVertexPropsToObjects(vertex, unflattedProperties, MappingDSGraphPropertyNames.DD_CONTAINER_PROPS_KEY);
        //System.out.println(unflattedProperties);
        assertTrue(unflattedProperties.containsKey("HMembbededBigDecimal"));
        assertTrue(unflattedProperties.get("HMembbededBigDecimal").equals(HMembbededBigDecimal));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededBigDecimal")).containsKey("piBigDecimal"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededBigDecimal")).get("piBigDecimal").equals(pi));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededBigDecimal")).containsKey("eBigDecimal"));
        assertTrue(((HashMap)unflattedProperties.get("HMembbededBigDecimal")).get("eBigDecimal").equals(e));
    }

    @AfterClass
    public static void testCleanup() {
        graph.shutdown();
    }
}