# CC installer neo4j server properties configuration unit
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
from tools.AConfParamNotNone import AConfParamNotNone
from tools.AConfUnit import AConfUnit

__author__ = 'mffrench'


class CPMappingNeo4JDirectory(AConfParamNotNone):

    name = "##mappingNeo4JDirectory"
    description = "Mapping Neo4j DB path definition"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isdir(self.value) and \
                    os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CPMappingNeo4JRRDB(AConfParamNotNone):

    name = "##mappingNeo4JRRDB"
    description = "Mapping Neo4J RRDB path definition"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        return super().is_valid


class CPMappingNeo4JTuningPropsFile(AConfParamNotNone):

    name = "##mappingNeo4JTuningPropsFile"
    description = "Mapping Neo4J Tunning properties path definition"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value) and \
                    os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CPMappingNeo4JLogConfigFile(AConfParamNotNone):

    name = "##mappingNeo4JLogConfigFile"
    description = "Mapping Neo4J log config file path definition"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value) and \
                    os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CUMappingNeo4JServerPropertiesProcessor(AConfUnit):

    def __init__(self, target_conf_dir):
        self.confUnitName = "Mapping Neo4J server properties configuration file"
        self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j-server.properties.tpl")
        self.confFinalPath = target_conf_dir + "/neo4j-server.properties"

        log_config_file_path = CPMappingNeo4JLogConfigFile()
        tuning_config_file_path = CPMappingNeo4JTuningPropsFile()
        rrdb_file_path = CPMappingNeo4JRRDB()
        neo4j_db_dir_path = CPMappingNeo4JDirectory()

        self.paramsDictionary = {
            neo4j_db_dir_path.name: neo4j_db_dir_path,
            rrdb_file_path.name: rrdb_file_path,
            tuning_config_file_path.name: tuning_config_file_path,
            log_config_file_path.name: log_config_file_path
        }

    def set_key_param_value(self, key, value):
        return super(CUMappingNeo4JServerPropertiesProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUMappingNeo4JServerPropertiesProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUMappingNeo4JServerPropertiesProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUMappingNeo4JServerPropertiesProcessor, self).get_param_from_key(key)
