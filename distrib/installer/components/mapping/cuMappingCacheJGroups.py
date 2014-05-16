# CC installer IDM cache JGroup configuration unit processor
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
import json
import os
from tools.AConfParamNotNone import AConfParamNotNone
from tools.AConfUnit import AConfUnit
from tools.NetworkTools import getSystemNetworkInterfacesAndIPaddresses, isPortAvailable, printSystemNetworkInterfaces

__author__ = 'mffrench'


class cpJGroupsTCPBindAddress(AConfParamNotNone):

    name = "##JGroupsTCPBindAddress"
    description = "CC Mapping JGroups TCP bind address"
    hide = False

    def __init__(self):
        self.value = None


class cpJGroupsTCPBindPort(AConfParamNotNone):

    name = "##JGroupsTCPBindPort"
    description = "CC Mapping JGroups TCP bind port"
    hide = False

    def __init__(self):
        self.value = None


class cpJGroupsMPINGBindAddress(AConfParamNotNone):

    name = "##JGroupsMPINGBindAddress"
    description = "CC Mapping JGroups MPING bind address"
    hide = False

    def __init__(self):
        self.value = None


class cpJGroupsMPINGMulticastAddress(AConfParamNotNone):

    name = "##JGroupsMPINGMulticastAddress"
    description = "CC Mapping JGroups MPING multicast address"
    hide = False

    def __init__(self):
        self.value = None


class cpJGroupsMPINGMulticastPort(AConfParamNotNone):

    name = "##JGroupsMPINGMulticastPort"
    description = "CC Mapping JGroups MPING multicast port"
    hide = False

    def __init__(self):
        self.value = None


class cuMappingCacheJGroupsProcessor(AConfUnit):

    def __init__(self, targetConfDir):
        self.confUnitName = "CC Mapping cache JGroups"
        self.confTemplatePath = os.path.abspath("resources/templates/components/mapping-jgroups-tcp.xml.tpl")
        self.confFinalPath = targetConfDir + "jgroups-tcp.xml"
        JGroupsTCPBindAddress = cpJGroupsTCPBindAddress()
        JGroupsTCPBindPort = cpJGroupsTCPBindPort()
        JGroupsMPINGBindAddress = cpJGroupsMPINGBindAddress()
        JGroupsMPINGMulticastAddress = cpJGroupsMPINGMulticastAddress()
        JGroupsMPINGMulticastPort = cpJGroupsMPINGMulticastPort()
        self.paramsDictionary = {
            JGroupsTCPBindAddress.name: JGroupsTCPBindAddress,
            JGroupsTCPBindPort.name: JGroupsTCPBindPort,
            JGroupsMPINGBindAddress.name: JGroupsMPINGBindAddress,
            JGroupsMPINGMulticastAddress.name: JGroupsMPINGMulticastAddress,
            JGroupsMPINGMulticastPort.name: JGroupsMPINGMulticastPort
        }


class mappingCacheJGroupsSyringe:

    def __init__(self, targetCacheDir, silent):
        self.MappingCacheJGroupsProcessor = cuMappingCacheJGroupsProcessor(targetCacheDir)
        mappingCacheJGroupCUJSON = open("resources/configvalues/components/cuMappingCacheJGroups.json")
        self.mappingCacheJGroupsCUValues = json.load(mappingCacheJGroupCUJSON)
        mappingCacheJGroupCUJSON.close()
        self.silent = silent

    def shootBuilder(self):
        #DEFAULT VALUES
        mappingCacheTCPBindAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name]
        mappingCacheTCPBindAddressDefaultUI = "[default - "+mappingCacheTCPBindAddressDefault+"]"

        mappingCacheTCPBindPortDefault = self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name]
        mappingCacheTCPBindPortDefaultUI = "[default - "+mappingCacheTCPBindPortDefault+"]"

        mappingCacheMPINGBindAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name]
        mappingCacheMPINGBindAddressDefaultUI = "[default - "+mappingCacheMPINGBindAddressDefault+"]"

        mappingCacheMPINGMulticastAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name]
        mappingCacheMPINGMulticastAddressDefaultUI = "[default - " + mappingCacheMPINGMulticastAddressDefault + "]"

        mappingCacheMPINGMulticastPortDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastPort.name]
        mappingCacheMPINGMulticastPortDefaultUI = "[default - " + mappingCacheMPINGMulticastPortDefault + "]"

        availabeIPAddresses = getSystemNetworkInterfacesAndIPaddresses()

        #BEGIN CONFIGURATION SETTING
        if not self.silent:
            mappingCacheJGroupsTCPBindingAddressValid = False
            while not mappingCacheJGroupsTCPBindingAddressValid:
                if len(availabeIPAddresses) > 0:
                    printSystemNetworkInterfaces(availabeIPAddresses)
                    self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name] = input("%-- >> Define CC mapping cache TCP bind address " + mappingCacheTCPBindAddressDefaultUI + ": ")

                if self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name] != "":
                    mappingCacheJGroupsTCPBindingAddressValid = True
                    mappingCacheTCPBindAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name]
                    mappingCacheTCPBindAddressDefaultUI = "[default - "+mappingCacheTCPBindAddressDefault+"]"
                elif mappingCacheTCPBindAddressDefault != "":
                    mappingCacheJGroupsTCPBindingAddressValid = True
                    self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name] = mappingCacheTCPBindAddressDefault

            mappingCacheJGroupsTCPBindingPortValid = False
            while not mappingCacheJGroupsTCPBindingPortValid:
                tcpBindPort = 0
                tcpBindPortStr = input("%-- >> Define CC mapping cache TCP bind port " + mappingCacheTCPBindPortDefaultUI + ": ")

                if tcpBindPortStr is not None and tcpBindPortStr != "":
                    tcpBindPort = int(tcpBindPortStr)
                elif mappingCacheTCPBindPortDefault != "":
                    tcpBindPort = int(mappingCacheTCPBindPortDefault)
                    tcpBindPortStr = mappingCacheTCPBindPortDefault

                if (tcpBindPort <= 0) and (tcpBindPort > 65535):
                    print("%-- !! Invalid JGroups TCP bind port " + tcpBindPortStr + ": not in port range")
                elif isPortAvailable(self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name], tcpBindPort):
                    mappingCacheJGroupsTCPBindingPortValid = True
                    self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name] = tcpBindPortStr
                    mappingCacheTCPBindPortDefault = self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name]
                    mappingCacheTCPBindPortDefaultUI = "[default - "+mappingCacheTCPBindPortDefault+"]"
                else:
                    print("%-- !! Selected port " + tcpBindPortStr  +  " is already used on this OS ! Choose another one !")

            mappingCacheJGroupsMPINGBindingAddressValid = False
            while not mappingCacheJGroupsMPINGBindingAddressValid:
                if len(availabeIPAddresses) > 0:
                    printSystemNetworkInterfaces(availabeIPAddresses)
                    self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name] = input("%-- >> Define CC mapping cache MPING bind address " + mappingCacheMPINGBindAddressDefaultUI + ": ")

                if self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name] != "":
                    mappingCacheJGroupsMPINGBindingAddressValid = True
                    mappingCacheMPINGBindAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name]
                    mappingCacheMPINGBindAddressDefaultUI = "[default - "+mappingCacheMPINGBindAddressDefault+"]"
                elif mappingCacheMPINGBindAddressDefault != "":
                    mappingCacheJGroupsMPINGBindingAddressValid = True
                    self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name] = mappingCacheMPINGBindAddressDefault

            mappingCacheJGroupsMPINGMulticastAddressValid = False
            while not mappingCacheJGroupsMPINGMulticastAddressValid:
                self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name] = input("%-- >> Define CC mapping cache MPING multicast address " + mappingCacheMPINGMulticastAddressDefaultUI + ": ")

                if self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name] != "":
                    mappingCacheJGroupsMPINGMulticastAddressValid = True
                    mappingCacheMPINGMulticastAddressDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name]
                    mappingCacheMPINGMulticastAddressDefaultUI = "[default - "+mappingCacheMPINGMulticastAddressDefault+"]"
                elif mappingCacheMPINGMulticastAddressDefault != "":
                    mappingCacheJGroupsMPINGBindingAddressValid = True
                    self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name] = mappingCacheMPINGMulticastAddressDefault

            mappingCacheJGroupsMPINGMulticastPortValid = False
            while not mappingCacheJGroupsMPINGMulticastPortValid:
                multicastPort = 0
                multicastPortStr = input("%-- >> Define CC idm cache MPING multicast port " + mappingCacheMPINGMulticastPortDefaultUI + ": ")

                if multicastPortStr is not None and multicastPortStr != "":
                    multicastPort = int(multicastPortStr)
                elif mappingCacheMPINGMulticastPortDefault != "":
                    multicastPort = int(mappingCacheMPINGMulticastPortDefault)
                    multicastPortStr = mappingCacheMPINGMulticastPortDefault

                if (multicastPort <= 0) and (multicastPort > 65535):
                    print("%-- !! Invalid JGroups multicast port " + multicastPortStr + ": not in port range")
                elif isPortAvailable(self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name], multicastPort):
                    mappingCacheJGroupsMPINGMulticastPortValid = True
                    self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastPort.name] = multicastPortStr
                    mappingCacheMPINGMulticastPortDefault = self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastPort.name]
                    mappingCacheMPINGMulticastPortDefaultUI = "[default - "+mappingCacheMPINGMulticastPortDefault+"]"
                else:
                    print("%-- !! Selected port " + multicastPortStr + " is already used on this OS ! Choose another one !")

        else:
            self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name] = mappingCacheTCPBindAddressDefault
            self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name] = mappingCacheTCPBindPortDefault
            self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name] = mappingCacheMPINGBindAddressDefault
            self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name] = mappingCacheMPINGMulticastAddressDefault
            self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastPort.name] = mappingCacheMPINGMulticastPortDefault

        self.MappingCacheJGroupsProcessor.setKeyParamValue(cpJGroupsTCPBindAddress.name, self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindAddress.name])
        self.MappingCacheJGroupsProcessor.setKeyParamValue(cpJGroupsTCPBindPort.name, self.mappingCacheJGroupsCUValues[cpJGroupsTCPBindPort.name])
        self.MappingCacheJGroupsProcessor.setKeyParamValue(cpJGroupsMPINGBindAddress.name, self.mappingCacheJGroupsCUValues[cpJGroupsMPINGBindAddress.name])
        self.MappingCacheJGroupsProcessor.setKeyParamValue(cpJGroupsMPINGMulticastAddress.name, self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastAddress.name])
        self.MappingCacheJGroupsProcessor.setKeyParamValue(cpJGroupsMPINGMulticastPort.name, self.mappingCacheJGroupsCUValues[cpJGroupsMPINGMulticastPort.name])

    def inject(self):
        mappingCacheJGroupCUJSON = open("resources/configvalues/components/cuIDMCacheJGroups.json", "w")
        jsonStr = json.dumps(self.mappingCacheJGroupsCUValues, sort_keys=True, indent=4, separators=(',', ': '))
        mappingCacheJGroupCUJSON.write(jsonStr)
        mappingCacheJGroupCUJSON.close()
        self.MappingCacheJGroupsProcessor.process()