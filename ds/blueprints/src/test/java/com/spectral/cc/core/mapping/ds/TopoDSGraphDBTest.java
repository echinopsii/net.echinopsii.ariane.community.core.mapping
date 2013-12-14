//package com.spectral.cc.core.mapping.ds.blueprintsimpl;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.tinkerpop.blueprints.Vertex;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.IOException;
//
//import static org.junit.Assert.assertTrue;
//
//public class TopoDSGraphDBTest {
//
//    @BeforeClass
//    public static void testSetup() {
//        try {
//            TopoDSGraphDB.init(null);
//            TopoDSGraphDB.start();
//        } catch (JsonParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @AfterClass
//    public static void testCleanup() {
//        TopoDSGraphDB.clear();
//        TopoDSGraphDB.stop();
//    }
//
//    @Test
//    public void testInitiatedCountVertex() {
//        assertTrue(TopoDSGraphDB.getDDgraph().getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, 0).iterator().hasNext());
//        int i = 0;
//        for (Vertex vertex : TopoDSGraphDB.getDDgraph().getVertices(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, 0)) {
//            i++;
//            long maxVertex = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
//            long maxEdge = vertex.getProperty(TopoDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
//            assertTrue(maxVertex == TopoDSGraphDB.getVertexMaxCursor());
//            assertTrue(maxEdge == TopoDSGraphDB.getEdgeMaxCursor());
//        }
//        assertTrue(i == 1);
//    }
//}