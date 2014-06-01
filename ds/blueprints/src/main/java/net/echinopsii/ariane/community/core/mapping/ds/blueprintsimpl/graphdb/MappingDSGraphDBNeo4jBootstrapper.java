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
import org.neo4j.kernel.logging.BufferingConsoleLogger;
import org.neo4j.kernel.logging.DefaultLogging;
import org.neo4j.kernel.logging.Logging;
import org.neo4j.server.Bootstrapper;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.NeoServer;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.PropertyFileConfigurator;
import org.neo4j.server.configuration.validation.DatabaseLocationMustBeSpecifiedRule;
import org.neo4j.server.configuration.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MappingDSGraphDBNeo4jBootstrapper {

    private final static Logger log = LoggerFactory.getLogger(MappingDSGraphDB.class);

    private Bootstrapper bootstrapper = Bootstrapper.loadMostDerivedBootstrapper();
    private Configurator configurator ;
    private NeoServer    server ;
    private Thread       shutdownHook ;

    public MappingDSGraphDBNeo4jBootstrapper start(String configFilePath) {
        File configFile = new File(configFilePath);
        log.debug("Create configuration from {}", configFilePath);
        BufferingConsoleLogger console = new BufferingConsoleLogger();
        configurator = new PropertyFileConfigurator(new Validator(new DatabaseLocationMustBeSpecifiedRule()),configFile,console);
        Logging logging = DefaultLogging.createDefaultLogging(configurator.getDatabaseTuningProperties());
        log.debug("Create neo4j server");
        server = new CommunityNeoServer(configurator, logging);
        log.debug("Start neo4j server");
        server.start();

        shutdownHook = new Thread() {
            @Override
            public void run() {
                log.info( "Neo4j Server shutdown initiated by request" );
                if ( server != null )
                    server.stop();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return this;
    }

    public void stop() {
        if (server!=null) server.stop();
        if (shutdownHook!=null) Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    public GraphDatabaseService getDatabase() {
        if (server!=null)
            return server.getDatabase().getGraph();
        else
            return null;
    }
}