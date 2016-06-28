/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 11/03/14 echinopsii
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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.Pair;
import org.neo4j.server.CommunityBootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;

public class MappingDSGraphDBNeo4jBootstrapper {

    private final static Logger log = LoggerFactory.getLogger(MappingDSGraphDB.class);

    private CommunityBootstrapper communityBootstrapper;
    private Thread       shutdownHook ;

    public MappingDSGraphDBNeo4jBootstrapper start(String configFilePath) {
        log.debug("Create neo4j server");
        System.setProperty("org.neo4j.server.properties", configFilePath);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        communityBootstrapper = new CommunityBootstrapper();

        log.debug("Start neo4j server");
        communityBootstrapper.start(new File(configFilePath), (Pair<String, String>[]) new ArrayList().toArray(new Pair[0]));

        shutdownHook = new Thread() {
            @Override
            public void run() {
                log.info("Neo4j Server shutdown initiated by request");
                if (communityBootstrapper != null)
                    communityBootstrapper.stop();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return this;
    }

    public void stop() {
        if (communityBootstrapper!=null) communityBootstrapper.stop();
        if (shutdownHook!=null) Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    public GraphDatabaseService getDatabase() {
        if (communityBootstrapper!=null)
            return communityBootstrapper.getServer().getDatabase().getGraph();
        else
            return null;
    }
}