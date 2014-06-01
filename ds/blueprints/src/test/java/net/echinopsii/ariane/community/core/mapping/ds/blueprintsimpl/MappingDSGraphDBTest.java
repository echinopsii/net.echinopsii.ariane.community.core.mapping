package net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.echinopsii.ariane.community.core.mapping.ds.MappingDSGraphPropertyNames;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.graphdb.MappingDSGraphDB;
import net.echinopsii.ariane.community.core.mapping.ds.blueprintsimpl.cfg.MappingDSCfgLoader;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class MappingDSGraphDBTest {

    private final static Logger log = LoggerFactory.getLogger(MappingDSGraphDB.class);

    @BeforeClass
    public static void testSetup() {
        try {
            Properties prop = new Properties();
            prop.load(MappingDSGraphDBTest.class.getResourceAsStream("/net.echinopsii.ariane.community.core.MappingRimManagedService.properties"));
            //randomize target graphdb directory to avoid test collapse
            prop.setProperty("mapping.ds.blueprints.graphpath", ((String)prop.get("mapping.ds.blueprints.graphpath")) + 1 + (int)(Math.random() * ((100 - 1) + 1)));
            MappingDSGraphDB.init(prop);
            MappingDSGraphDB.start();
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void testCleanup() throws IOException {
        if (MappingDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath()!=null) {
            File dir = new File(MappingDSCfgLoader.getDefaultCfgEntity().getBlueprintsGraphPath());
            if (dir.isDirectory()) FileUtils.deleteDirectory(dir);
        }
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