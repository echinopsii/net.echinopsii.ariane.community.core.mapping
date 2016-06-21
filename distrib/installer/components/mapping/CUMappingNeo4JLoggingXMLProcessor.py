# installer neo4j logging configuration unit
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


class CUMappingNeo4JLoggingXMLProcessor(AConfUnit):

    def __init__(self, target_conf_dir):
        self.confUnitName = "Mapping Neo4J logging configuration file"
        self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j-http-logging.xml.tpl")
        self.confFinalPath = target_conf_dir + "/neo4j-http-logging.xml"
        self.paramsDictionary = {}

    def set_key_param_value(self, key, value):
        return super(CUMappingNeo4JLoggingXMLProcessor, self).set_key_param_value(key, value)

    def get_params_keys_list(self):
        return super(CUMappingNeo4JLoggingXMLProcessor, self).get_params_keys_list()

    def process(self):
        return super(CUMappingNeo4JLoggingXMLProcessor, self).process()

    def get_param_from_key(self, key):
        return super(CUMappingNeo4JLoggingXMLProcessor, self).get_param_from_key(key)
