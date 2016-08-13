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
from components.mapping.CUMappingNeo4JWrapperConfProcessor import CUMappingNeo4JWrapperConfProcessor
from components.mapping.CUMappingRimManagedServiceProcessor import CPMappingDirectory, \
    CUMappingRimManagedServiceProcessor, CPMappingNeo4JConfigFile, CPMappingBundleName
from components.mapping.DBIDMMySQLPopulator import DBIDMMySQLPopulator
from components.mapping.DBIDMMySQLInitiator import DBIDMMySQLInitiator


__author__ = 'mffrench'


class MappingProcessor:
    def __init__(self, home_dir_path, dist_dep_type, directory_db_conf, idm_db_conf, bus_processor, silent):
        print("\n%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--"
              "%--%--%--%--%--%--%--%--%--\n")
        print("%-- Mapping configuration : \n")
        self.homeDirPath = home_dir_path
        self.dist_dep_type = dist_dep_type
        self.idmDBConfig = idm_db_conf
        self.directoryDBConfig = directory_db_conf
        self.busProcessor = bus_processor
        self.silent = silent

        neo4j_conf_dir_path = self.homeDirPath + "/ariane/neo4j/conf"
        if not os.path.exists(neo4j_conf_dir_path):
            os.makedirs(neo4j_conf_dir_path, 0o755)
        if self.dist_dep_type == "mno" or self.dist_dep_type == "frt":
            self.conf_dir_path = self.homeDirPath + "/repository/ariane-core/"
            if not os.path.exists(self.conf_dir_path):
                os.makedirs(self.conf_dir_path, 0o755)
        else:
            self.conf_dir_path = self.homeDirPath + "/ariane/config/"
            if not os.path.exists(self.conf_dir_path):
                os.makedirs(self.conf_dir_path, 0o755)

        self.mappingRimManagedServiceCUProcessor = CUMappingRimManagedServiceProcessor(self.conf_dir_path,
                                                                                       self.dist_dep_type)
        if self.dist_dep_type == "mno" or self.dist_dep_type == "frt":
            self.mappingIDMSQLInitiator = DBIDMMySQLInitiator(idm_db_conf)
            self.mappingIDMSQLPopulator = DBIDMMySQLPopulator(idm_db_conf)
        if self.dist_dep_type != "frt":
            self.mappingNeo4JLogginXMLCUProcessor = CUMappingNeo4JLoggingXMLProcessor(neo4j_conf_dir_path,
                                                                                      self.dist_dep_type)
            self.mappingNeo4JTunningPropertiesCUProcessor = CUMappingNeo4JTuningPropertiesProcessor(neo4j_conf_dir_path,
                                                                                                    self.dist_dep_type)
            self.mappingNeo4JServerPropertiesCUProcessor = CUMappingNeo4JServerPropertiesProcessor(neo4j_conf_dir_path,
                                                                                                   self.dist_dep_type)
            if self.dist_dep_type == "mms":
                self.mappintNeo4jWrapperConf = CUMappingNeo4JWrapperConfProcessor(neo4j_conf_dir_path,
                                                                                  self.dist_dep_type)

    def process(self):
        if self.dist_dep_type != "frt":
            if self.dist_dep_type == "mno":
                self.busProcessor.process(
                    "resources/templates/components/"
                    "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.properties.tpl",
                    self.conf_dir_path +
                    "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.properties"
                )
            elif self.dist_dep_type == "mms":
                self.busProcessor.process(
                    "resources/templates/components/"
                    "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.properties.tpl",
                    self.conf_dir_path +
                    "net.echinopsii.ariane.community.core.MappingMsgsrvManagedService.cfg"
                )

            self.mappingNeo4JLogginXMLCUProcessor.process()
            self.mappingNeo4JTunningPropertiesCUProcessor.process()
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
                    self.mappingNeo4JServerPropertiesCUProcessor.set_key_param_value(key,
                                                                                     map_neo4j_rrdb_dir_path+"/rrd")
                elif key == CPMappingNeo4JTuningPropsFile.name:
                    self.mappingNeo4JServerPropertiesCUProcessor.set_key_param_value(key, self.homeDirPath +
                                                                                     "/ariane/neo4j/conf/"
                                                                                     "neo4j.properties")
                elif key == CPMappingNeo4JLogConfigFile.name:
                    self.mappingNeo4JServerPropertiesCUProcessor.\
                        set_key_param_value(key, self.homeDirPath + "/ariane/neo4j/conf/neo4j-http-logging.xml")
            self.mappingNeo4JServerPropertiesCUProcessor.process()
            if self.dist_dep_type == "mms":
                self.mappintNeo4jWrapperConf.process()

        if self.dist_dep_type == "mno" or self.dist_dep_type == "frt":
            self.mappingIDMSQLInitiator.process()
            self.mappingIDMSQLPopulator.process()

        for key in self.mappingRimManagedServiceCUProcessor.get_params_keys_list():
            if key == CPMappingDirectory.name:
                if self.dist_dep_type != "frt":
                    map_dir_path = self.homeDirPath + "/ariane/neo4j/graph"
                    if not os.path.exists(map_dir_path):
                        os.makedirs(map_dir_path, 0o755)
                    self.mappingRimManagedServiceCUProcessor.set_key_param_value(key, map_dir_path)
            elif key == CPMappingNeo4JConfigFile.name:
                if self.dist_dep_type != "frt":
                    self.mappingRimManagedServiceCUProcessor.\
                        set_key_param_value(key, self.homeDirPath + "/ariane/neo4j/conf/neo4j-server.properties")
            elif key == CPMappingBundleName.name:
                if self.dist_dep_type == "frt":
                    map_bundle_name = "net.echinopsii.ariane.community.core.mapping.ds.msgcli"
                else:
                    map_bundle_name = "net.echinopsii.ariane.community.core.mapping.ds.blueprints"
                self.mappingRimManagedServiceCUProcessor.\
                    set_key_param_value(key, map_bundle_name)
        self.mappingRimManagedServiceCUProcessor.process()
        if self.dist_dep_type == "frt":
            self.busProcessor.process(
                self.conf_dir_path +
                "net.echinopsii.ariane.community.core.MappingRimManagedService.properties",
                self.conf_dir_path +
                "net.echinopsii.ariane.community.core.MappingRimManagedService.properties"
            )

        return self
