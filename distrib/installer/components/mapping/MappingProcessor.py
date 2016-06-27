# installer mapping processor
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
from components.mapping.CUMappingNeo4JLoggingXMLProcessor import CUMappingNeo4JLoggingXMLProcessor
from components.mapping.CUMappingNeo4JServerPropertiesProcessor import CUMappingNeo4JServerPropertiesProcessor, \
    CPMappingNeo4JDirectory, CPMappingNeo4JRRDB, CPMappingNeo4JTuningPropsFile, CPMappingNeo4JLogConfigFile
from components.mapping.CUMappingNeo4JTuningPropertiesProcessor import CUMappingNeo4JTuningPropertiesProcessor
from components.mapping.CUMappingRimManagedServiceProcessor import CPMappingDirectory, \
    CUMappingRimManagedServiceProcessor, CPMappingNeo4JConfigFile
from components.mapping.DBIDMMySQLPopulator import DBIDMMySQLPopulator
from components.mapping.DBIDMMySQLInitiator import DBIDMMySQLInitiator


__author__ = 'mffrench'


class MappingProcessor:
    def __init__(self, home_dir_path, directory_db_conf, idm_db_conf, bus_processor, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--"
              "%--%--%--%--%--%--%--%--%--\n")
        print("%-- Mapping configuration : \n")
        self.homeDirPath = home_dir_path
        self.idmDBConfig = idm_db_conf
        self.directoryDBConfig = directory_db_conf
        self.busProcessor = bus_processor
        self.silent = silent

        neo4j_conf_dir_path = self.homeDirPath + "/ariane/neo4j/conf"
        if not os.path.exists(neo4j_conf_dir_path):
            os.makedirs(neo4j_conf_dir_path, 0o755)
        self.kernel_repository_dir_path = self.homeDirPath + "/repository/ariane-core/"
        if not os.path.exists(self.kernel_repository_dir_path):
            os.makedirs(self.kernel_repository_dir_path, 0o755)

        self.mappingRimManagedServiceCUProcessor = CUMappingRimManagedServiceProcessor(self.kernel_repository_dir_path)
        self.mappingIDMSQLInitiator = DBIDMMySQLInitiator(idm_db_conf)
        self.mappingIDMSQLPopulator = DBIDMMySQLPopulator(idm_db_conf)
        self.mappingNeo4JLogginXMLCUProcessor = CUMappingNeo4JLoggingXMLProcessor(neo4j_conf_dir_path)
        self.mappingNeo4JTunningPropertiesCUProcessor = CUMappingNeo4JTuningPropertiesProcessor(neo4j_conf_dir_path)
        self.mappingNeo4JServerPropertiesCUProcessor = CUMappingNeo4JServerPropertiesProcessor(neo4j_conf_dir_path)

    def process(self):
        self.busProcessor.process(
            "resources/templates/components/"
            "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.properties.tpl",
            self.kernel_repository_dir_path +
            "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.properties"
        )

        self.mappingNeo4JLogginXMLCUProcessor.process()
        self.mappingNeo4JTunningPropertiesCUProcessor.process()
        self.mappingIDMSQLInitiator.process()
        self.mappingIDMSQLPopulator.process()

        for key in self.mappingNeo4JServerPropertiesCUProcessor.get_params_keys_list():
            if key == CPMappingNeo4JDirectory.name:
                map_neo4j_dir_path = self.homeDirPath + "/ariane/neo4j/graph"
                if not os.path.exists(map_neo4j_dir_path):
                    os.makedirs(map_neo4j_dir_path, 0o755)
                self.mappingNeo4JServerPropertiesCUProcessor.set_key_param_value(key, map_neo4j_dir_path)
            elif key == CPMappingNeo4JRRDB.name:
                map_neo4j_rrdb_dir_path = self.homeDirPath + "/ariane/neo4j/data"
                if not os.path.exists(map_neo4j_rrdb_dir_path):
                    os.makedirs(map_neo4j_rrdb_dir_path, 0o755)
                self.mappingNeo4JServerPropertiesCUProcessor.set_key_param_value(key, map_neo4j_rrdb_dir_path+"/rrd")
            elif key == CPMappingNeo4JTuningPropsFile.name:
                self.mappingNeo4JServerPropertiesCUProcessor.set_key_param_value(key, self.homeDirPath +
                                                                                 "/ariane/neo4j/conf/neo4j.properties")
            elif key == CPMappingNeo4JLogConfigFile.name:
                self.mappingNeo4JServerPropertiesCUProcessor.\
                    set_key_param_value(key, self.homeDirPath + "/ariane/neo4j/conf/neo4j-http-logging.xml")
        self.mappingNeo4JServerPropertiesCUProcessor.process()

        for key in self.mappingRimManagedServiceCUProcessor.get_params_keys_list():
            if key == CPMappingDirectory.name:
                map_dir_path = self.homeDirPath + "/ariane/neo4j/graph"
                if not os.path.exists(map_dir_path):
                    os.makedirs(map_dir_path, 0o755)
                self.mappingRimManagedServiceCUProcessor.set_key_param_value(key, map_dir_path)
            elif key == CPMappingNeo4JConfigFile.name:
                self.mappingRimManagedServiceCUProcessor.\
                    set_key_param_value(key, self.homeDirPath + "/ariane/neo4j/conf/neo4j-server.properties")
        self.mappingRimManagedServiceCUProcessor.process()
        return self
