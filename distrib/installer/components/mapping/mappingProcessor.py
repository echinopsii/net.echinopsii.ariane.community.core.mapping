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
from components.mapping.cuMappingRimManagedServiceProcessor import cpMappingDirectory, cuMappingRimManagedServiceProcessor


__author__ = 'mffrench'


class mappingProcessor:
    def __init__(self, homeDirPath):
        print("\n%%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--%--\n")
        print("%-- CC mapping configuration : \n")
        self.homeDirPath = homeDirPath
        kernelRepositoryDirPath = self.homeDirPath + "/repository/cc-distrib/"
        if not os.path.exists(kernelRepositoryDirPath):
            os.makedirs(kernelRepositoryDirPath, 0o755)
        self.mappingRimManagedServiceCUProcessor = cuMappingRimManagedServiceProcessor(kernelRepositoryDirPath)

    def process(self):
        for key in self.mappingRimManagedServiceCUProcessor.getParamsKeysList():
            if key == cpMappingDirectory.name:
                mapDirPath = self.homeDirPath + "/CC/graph"
                if not os.path.exists(mapDirPath):
                    os.makedirs(mapDirPath, 0o755)
                self.mappingRimManagedServiceCUProcessor.setKeyParamValue(key, mapDirPath)
        self.mappingRimManagedServiceCUProcessor.process()
        return self