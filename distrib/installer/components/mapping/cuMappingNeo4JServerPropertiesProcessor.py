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


class cpMappingNeo4JDirectory(AConfParamNotNone):

    name = "##mappingNeo4JDirectory"
    description = "CC mapping Neo4j DB path definition"
    hide = False

    def __init__(self):
        self.value = None

    def isValid(self):
        if not super().isValid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isdir(self.value) and os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + self.value + ") is not valid. Check if it exists and it has good rights.")
                return False


class cpMappingNeo4JRRDB(AConfParamNotNone):

    name = "##mappingNeo4JRRDB"
    description = "CC mapping Neo4J RRDB path definition"
    hide = False

    def __init__(self):
        self.value = None

    def isValid(self):
        return super().isValid


class cpMappingNeo4JTuningPropsFile(AConfParamNotNone):

    name = "##mappingNeo4JTuningPropsFile"
    description = "CC mapping Neo4J Tunning properties path definition"
    hide = False

    def __init__(self):
        self.value = None

    def isValid(self):
        if not super().isValid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value) and os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + self.value + ") is not valid. Check if it exists and it has good rights.")
                return False


class cpMappingNeo4JLogConfigFile(AConfParamNotNone):

    name = "#mappingNeo4JLogConfigFile"
    description = "CC mapping Neo4J log config file path definition"
    hide = False

    def __init__(self):
        self.value = None

    def isValid(self):
        if not super().isValid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isfile(self.value) and os.access(self.value, os.W_OK) and os.access(self.value, os.W_OK):
                return True
            else:
                print(self.description + " (" + self.value + ") is not valid. Check if it exists and it has good rights.")
                return False


class cuMappingNeo4JServerPropertiesProcessor(AConfUnit):
    def __init__(self, targetConfDir):
        self.confUnitName = "CC mapping Neo4J server properties configuration file"
        self.confTemplatePath = os.path.abspath("resources/templates/components/neo4j-server.properties.tpl")
        self.confFinalPath = targetConfDir + "neo4j-server.properties"

        logConfigFilePath = cpMappingNeo4JLogConfigFile()
        tuningConfigFilePath = cpMappingNeo4JTuningPropsFile()
        rrdbFilePath = cpMappingNeo4JRRDB()
        neo4jDBDirPath = cpMappingNeo4JDirectory()

        self.paramsDictionary = {
            neo4jDBDirPath.name: neo4jDBDirPath,
            rrdbFilePath.name: rrdbFilePath,
            tuningConfigFilePath.name: tuningConfigFilePath,
            logConfigFilePath.name: logConfigFilePath
        }

