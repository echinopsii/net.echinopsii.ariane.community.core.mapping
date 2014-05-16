# CC installer IDM cache configuration unit processor
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


class cpJGroupConfPath(AConfParamNotNone):

    name = "##JGroupConfPath"
    description = "CC Mapping JGroup configuration path"
    hide = False

    def __init__(self):
        self.value = None


class cuMappingCacheProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "CC Mapping cache"
        self.confTemplatePath = os.path.abspath("resources/templates/components/infinispan.mapping.cache.xml.tpl")
        self.confFinalPath = targetConfDir + "infinispan.mapping.cache.xml"
        JGroupConfPath = cpJGroupConfPath()
        self.paramsDictionary = {
            JGroupConfPath.name: JGroupConfPath
        }


class mappingCacheSyringe:

    def __init__(self, targetCacheDir):
        self.JGroupConfPath = targetCacheDir + "jgroups-tcp.xml"
        self.MappingCache = cuMappingCacheProcessor(targetCacheDir)

    def shootBuilder(self):
        self.MappingCache.setKeyParamValue(cpJGroupConfPath.name, self.JGroupConfPath)

    def inject(self):
        self.MappingCache.process()