package com.spectral.cc.core.mapping.ds;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.spectral.cc.core.mapping.ds.blueprintsimpl.MappingDSGraphDB;
import com.tinkerpop.blueprints.Vertex;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class MappingDSGraphDBTest {

    @BeforeClass
    public static void testSetup() {
        try {
            MappingDSGraphDB.init(null);
            MappingDSGraphDB.start();
        } catch (JsonParseException e) {
              // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void testCleanup() {
        MappingDSGraphDB.clear();
        //MappingDSGraphDB.stop();
    }

    @Test
    public void testInitiatedCountVertex() {
        assertTrue(MappingDSGraphDB.getDDgraph().getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, 0).iterator().hasNext());
        int i = 0;
        for (Vertex vertex : MappingDSGraphDB.getDDgraph().getVertices(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_ID, 0)) {
            i++;
            long maxVertex = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_VERTEX_MAXCUR_KEY);
            long maxEdge = vertex.getProperty(MappingDSGraphPropertyNames.DD_GRAPH_EDGE_MAXCUR_KEY);
            assertTrue(maxVertex == MappingDSGraphDB.getVertexMaxCursor());
            assertTrue(maxEdge == MappingDSGraphDB.getEdgeMaxCursor());
        }
        assertTrue(i == 1);
    }
}