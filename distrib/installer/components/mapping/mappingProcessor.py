# CC installer mapping processor
#
# Copyright (C) 2014 Mathilde Ffrench
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
import os
from components.mapping.cuMappingNeo4JLoggingXMLProcessor import cuMappingNeo4JLoggingXMLProcessor
from components.mapping.cuMappingNeo4JServerPropertiesProcessor import cuMappingNeo4JServerPropertiesProcessor, cpMappingNeo4JDirectory, cpMappingNeo4JRRDB, cpMappingNeo4JTuningPropsFile, cpMappingNeo4JLogConfigFile
from components.mapping.cuMappingNeo4JTuningPropertiesProcessor import cuMappingNeo4JTuningPropertiesProcessor
from components.mapping.cuMappingRimManagedServiceProcessor import cpMappingDirectory, cuMappingRimManagedServiceProcessor, cpMappingNeo4JConfigFile
from components.mapping.dbIDMMySQLPopulator import dbIDMMySQLPopulator


__author__ = 'mffrench'


class mappingProcessor:
    def __init__(self, idmDBConfig, homeDirPath, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--\n")
        print("%-- CC mapping configuration : \n")
        self.homeDirPath = homeDirPath

        neo4jConfDirPath = self.homeDirPath + "/CC/neo4j/conf"
        if not os.path.exists(neo4jConfDirPath):
            os.makedirs(neo4jConfDirPath, 0o755)
        kernelRepositoryDirPath = self.homeDirPath + "/repository/cc-distrib/"
        if not os.path.exists(kernelRepositoryDirPath):
            os.makedirs(kernelRepositoryDirPath, 0o755)

        self.mappingRimManagedServiceCUProcessor = cuMappingRimManagedServiceProcessor(kernelRepositoryDirPath)
        self.mappingIDMSQLPopulator = dbIDMMySQLPopulator(idmDBConfig)
        self.mappingNeo4JLogginXMLCUProcessor = cuMappingNeo4JLoggingXMLProcessor(neo4jConfDirPath)
        self.mappingNeo4JTunningPropertiesCUProcessor = cuMappingNeo4JTuningPropertiesProcessor(neo4jConfDirPath)
        self.mappingNeo4JServerPropertiesCUProcessor = cuMappingNeo4JServerPropertiesProcessor(neo4jConfDirPath)

    def process(self):
        self.mappingNeo4JLogginXMLCUProcessor.process()
        self.mappingNeo4JTunningPropertiesCUProcessor.process()
        self.mappingIDMSQLPopulator.process()

        for key in self.mappingNeo4JServerPropertiesCUProcessor.getParamsKeysList():
            if key == cpMappingNeo4JDirectory.name:
                mapNeo4JDirPath = self.homeDirPath + "/CC/neo4j/graph"
                if not os.path.exists(mapNeo4JDirPath):
                    os.makedirs(mapNeo4JDirPath, 0o755)
                self.mappingNeo4JServerPropertiesCUProcessor.setKeyParamValue(key, mapNeo4JDirPath)
            elif key == cpMappingNeo4JRRDB.name:
                mapNeo4JRRDBDirPath = self.homeDirPath + "/CC/neo4j/data"
                if not os.path.exists(mapNeo4JRRDBDirPath):
                    os.makedirs(mapNeo4JRRDBDirPath, 0o755)
                self.mappingNeo4JServerPropertiesCUProcessor.setKeyParamValue(key, mapNeo4JRRDBDirPath+"/rrd")
            elif key == cpMappingNeo4JTuningPropsFile.name:
                self.mappingNeo4JServerPropertiesCUProcessor.setKeyParamValue(key, self.homeDirPath + "/CC/neo4j/conf/neo4j.properties")
            elif key == cpMappingNeo4JLogConfigFile.name:
                self.mappingNeo4JServerPropertiesCUProcessor.setKeyParamValue(key, self.homeDirPath + "/CC/neo4j/conf/neo4j-http-logging.xml")
        self.mappingNeo4JServerPropertiesCUProcessor.process()

        for key in self.mappingRimManagedServiceCUProcessor.getParamsKeysList():
            if key == cpMappingDirectory.name:
                mapDirPath = self.homeDirPath + "/CC/neo4j/graph"
                if not os.path.exists(mapDirPath):
                    os.makedirs(mapDirPath, 0o755)
                self.mappingRimManagedServiceCUProcessor.setKeyParamValue(key, mapDirPath)
            elif key == cpMappingNeo4JConfigFile.name:
                self.mappingRimManagedServiceCUProcessor.setKeyParamValue(key, self.homeDirPath + "/CC/neo4j/conf/neo4j-server.properties")
        self.mappingRimManagedServiceCUProcessor.process()
        return self