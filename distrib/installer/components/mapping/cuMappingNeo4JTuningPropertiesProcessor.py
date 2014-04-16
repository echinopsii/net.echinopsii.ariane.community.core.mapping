# CC installer neo4j tuning properties configuration unit
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


class cuMappingNeo4JTuningPropertiesProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "CC mapping Neo4J tuning configuration file"
        self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j.properties.tpl")
        self.confFinalPath = targetConfDir + "neo4j.properties"
        self.paramsDictionary = {}