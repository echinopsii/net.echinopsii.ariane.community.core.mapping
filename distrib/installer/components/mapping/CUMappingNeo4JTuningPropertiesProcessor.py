# installer neo4j tuning properties configuration unit
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
from tools.AConfUnit import AConfUnit

__author__ = 'mffrench'


class CUMappingNeo4JTuningPropertiesProcessor(AConfUnit):

    def __init__(self, target_conf_dir, dist_dep_type):
        self.dist_dep_type = dist_dep_type
        self.confUnitName = "Mapping Neo4J tuning configuration file"
        if self.dist_dep_type != "mms":
            self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j-212/neo4j.properties.tpl")
        else:
            self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j-231/neo4j.properties.tpl")
        self.confFinalPath = target_conf_dir + "/neo4j.properties"
        self.paramsDictionary = {}

    def set_key_param_value(self, key, value):
        return super(CUMappingNeo4JTuningPropertiesProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUMappingNeo4JTuningPropertiesProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUMappingNeo4JTuningPropertiesProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUMappingNeo4JTuningPropertiesProcessor, self).get_param_from_key(key)
