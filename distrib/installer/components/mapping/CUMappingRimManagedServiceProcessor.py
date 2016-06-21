# installer mapping rim managed service configuration unit
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


class CPMappingDirectory(AConfParamNotNone):

    name = "##mappingDirectory"
    description = "Mapping DB path definition"
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


class CPMappingNeo4JConfigFile(AConfParamNotNone):

    name = "##mappingNeo4JConfigFile"
    description = "Mapping Neo4j configuration file path"
    hide = False

    def __init__(self):
        self.value = None

    def is_valid(self):
        if not super().is_valid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value):
                return True
            else:
                print(self.description + " (" + str(self.value) +
                      ") is not valid. Check if it exists and it has good rights.")
                return False


class CUMappingRimManagedServiceProcessor(AConfUnit):

    def __init__(self, target_conf_dir):
        self.confUnitName = "Mapping RIM managed service"
        self.confTemplatePath = os.path.abspath(
            "resources/templates/components/"
            "net.echinopsii.ariane.community.core.MappingRimManagedService.properties.tpl"
        )
        self.confFinalPath = target_conf_dir + \
            "net.echinopsii.ariane.community.core.MappingRimManagedService.properties"
        map_dir = CPMappingDirectory()
        map_neo4_j_conf_file = CPMappingNeo4JConfigFile()
        self.paramsDictionary = {
            map_dir.name: map_dir,
            map_neo4_j_conf_file.name: map_neo4_j_conf_file
        }

    def set_key_param_value(self, key, value):
        return super(CUMappingRimManagedServiceProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUMappingRimManagedServiceProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUMappingRimManagedServiceProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUMappingRimManagedServiceProcessor, self).get_param_from_key(key)
