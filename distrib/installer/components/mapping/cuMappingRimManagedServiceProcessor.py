# CC installer mapping rim managed service configuration unit
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


class cpMappingDirectory(AConfParamNotNone):

    name = "##mappingDirectory"
    description = "CC mapping DB path definition"
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


class cpMappingNeo4JConfigFile(AConfParamNotNone):

    name = "##mappingNeo4JConfigFile"
    description = "CC mapping Neo4j configuration file path"
    hide = False

    def __init__(self):
        self.value = None

    def isValid(self):
        if not super().isValid:
            return False
        else:
            if os.path.exists(self.value) and os.path.isFile(self.value):
                return True
            else:
                print(self.description + " (" + self.value + ") is not valid. Check if it exists and it has good rights.")
                return False


class cuMappingRimManagedServiceProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "CC mapping RIM managed service"
        self.confTemplatePath = os.path.abspath("resources/templates/components/com.spectral.cc.core.MappingRimManagedService.properties.tpl")
        self.confFinalPath = targetConfDir + "com.spectral.cc.core.MappingRimManagedService.properties"
        mapDir = cpMappingDirectory()
        mapNeo4JConfFile = cpMappingNeo4JConfigFile()
        self.paramsDictionary = {
            mapDir.name: mapDir,
            mapNeo4JConfFile.name: mapNeo4JConfFile
        }