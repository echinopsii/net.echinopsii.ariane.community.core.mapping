#!/usr/bin/python3

import getpass

import requests
import json
#from pprint import pprint

username = input("%-- >> Username : ")
password = getpass.getpass("%-- >> Password : ")
srvurl = input("%-- >> CC server url (like http://serverFQDN:6969/) : ")

# CREATE REQUESTS SESSION
s = requests.Session()
s.auth = (username, password)


## CREATE LAN RVD APP6969 RVD 11
containerParams = {'primaryAdminURL':'http://app6969rvd11.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6969rvd11'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')
rvd11 = containerID

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.36.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan2'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","71ab90"], "name":["String","DEV APP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","app6969rvd11"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)


## ADD A GATE TO LAN APP6969 RVD 11
gateParams = {"URL":"http://app6969rvd11.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.app6969rvd11", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

nodeParams = {"name":"APP6969.RVD11", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')


#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'APP FX prices diffusion'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)


## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://app6969rvd11.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)


## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')

linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams);






## CREATE LAN RVD APP6969 RVD 12
containerParams = {'primaryAdminURL':'http://app6969rvd12.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6969rvd12'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')
rvd12 = containerID

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.36.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan2'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","71ab90"], "name":["String","DEV APP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","app6969rvd12"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)


## ADD A GATE TO LAN APP6969 RVD 12
gateParams = {"URL":"http://app6969rvd12.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.app6969rvd12", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

nodeParams = {"name":"APP6969.RVD12", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')


#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'APP FX prices diffusion'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)


## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://app6969rvd12.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)


## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams);






## CREATE LAN RVD APP6969 RVD 13
containerParams = {'primaryAdminURL':'http://app6969rvd13.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6969rvd13'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')
rvd13 = containerID

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.37.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan3'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","71ab90"], "name":["String","DEV APP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","app6969rvd13"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)


## ADD A GATE TO LAN APP6969 RVD 13
gateParams = {"URL":"http://app6969rvd13.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.app6969rvd13", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

nodeParams = {"name":"APP6969.RVD13", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')


#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'APP FX prices diffusion'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)


## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://app6969rvd13.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)


## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams);






## CREATE LAN RVD APP6969 RVD 14
containerParams = {'primaryAdminURL':'http://app6969rvd14.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6969rvd14'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')
rvd14 = containerID

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.37.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan3'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","71ab90"], "name":["String","DEV APP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","app6969rvd14"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)


## ADD A GATE TO LAN APP6969 RVD 14
gateParams = {"URL":"http://app6969rvd14.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.app6969rvd14", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

## ADD A NODE TO LAN APP6969 RVD 14
nodeParams = {"name":"APP6969.RVD14", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')


#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'APP FX prices diffusion'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)


## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://app6969rvd14.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","e3a164"], "name":["String","APP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)


## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)





## CREATE LAN RVD BPP6669 RVD 11
containerParams = {'primaryAdminURL':'http://bpp6669rvd11.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6669rvd11'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.38.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan4'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","ad853b"], "name":["String","DEV BPP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","bpp6669rvd11"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

## ADD A GATE TO LAN BPP6669 RVD 11
gateParams = {"URL":"http://bpp6669rvd11.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.bpp6669rvd11", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

## ADD A NODE TO LAN BPP6669 RVD 11
nodeParams = {"name":"BPP6669.RVD11", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd11.lab01.dev.dekatonshivr.echinopsii.net/;239.69.66.69:6669", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.66.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')

transportProperty = {'ID':transportID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/transport/update/properties/add', params=transportProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
transportProperty = {'ID':transportID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/transport/update/properties/add', params=transportProperty)

linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)





## CREATE LAN RVD BPP6669 RVD 12
containerParams = {'primaryAdminURL':'http://bpp6669rvd12.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6669rvd12'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.38.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan4'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","ad853b"], "name":["String","DEV BPP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","bpp6669rvd12"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

## ADD A GATE TO LAN BPP6669 RVD 12
gateParams = {"URL":"http://bpp6669rvd12.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.bpp6669rvd12", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

## ADD A NODE TO LAN BPP6669 RVD 12
nodeParams = {"name":"BPP6669.RVD12", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd12.lab01.dev.dekatonshivr.echinopsii.net/;239.69.66.69:6669", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.66.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)

## ADD A NODE TO LAN BPP6669 RVD 12
nodeParams = {"name":"BRDG-6969-6669.RVD12", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINTS TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd12.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)


## CREATE LAN RVD BPP6669 RVD 13
containerParams = {'primaryAdminURL':'http://bpp6669rvd13.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6669rvd13'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.39.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan5'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","ad853b"], "name":["String","DEV BPP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","bpp6669rvd13"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

## ADD A GATE TO LAN BPP6669 RVD 13
gateParams = {"URL":"http://bpp6669rvd13.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.bpp6669rvd13", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

## ADD A NODE TO LAN BPP6669 RVD 13
nodeParams = {"name":"BPP6669.RVD13", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd13.lab01.dev.dekatonshivr.echinopsii.net/;239.69.66.69:6669", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.66.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)



## CREATE LAN RVD BPP6669 RVD 14
containerParams = {'primaryAdminURL':'http://bpp6669rvd14.lab01.dev.dekatonshivr.echinopsii.net:7580', 'primaryAdminGateName':'webadmingate.app6669rvd14'}
r = s.get(srvurl + 'CC/rest/domain/container/create', params=containerParams)
containerID = r.json().get('containerID')

# MANDATORY FOR GRAPH RENDER
containerCompany = {'ID':containerID,'company':'Tibco'}
r = s.get(srvurl + 'CC/rest/domain/container/update/company', params=containerCompany)

containerProduct = {'ID':containerID,'product':'Tibco Rendez Vous'}
r = s.get(srvurl + 'CC/rest/domain/container/update/product', params=containerProduct)

containerType = {'ID':containerID,'type':'RV Daemon'}
r = s.get(srvurl + 'CC/rest/domain/container/update/type', params=containerType)

datacenter = {"dc":["String","My little paradise"], "gpsLng":["double",2.246621], "address":["String","26 rue de Belfort"], "gpsLat":["double",48.895308], "town":["String","Courbevoie"], "country":["String","France"]}
containerProperty = {'ID':containerID,'propertyName':'Datacenter','propertyValue':json.dumps(datacenter),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

network = {'subnetip':['String','192.168.39.0'], 'subnetmask':['String','255.255.255.0'], 'type':['String','LAN'], 'lan':['String','lab01.lan5'], 'marea':['String',"angelsMind"]}
containerProperty = {'ID':containerID,'propertyName':'Network','propertyValue':json.dumps(network),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

supportTeam = {"color":["String","ad853b"], "name":["String","DEV BPP"]}
containerProperty = {'ID':containerID,'propertyName':'supportTeam','propertyValue':json.dumps(supportTeam),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

server = { "os":["String","Fedora 18 - x86_64"], "hostname":["String","bpp6669rvd14"] }
containerProperty = {'ID':containerID,'propertyName':'Server','propertyValue':json.dumps(server),'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/container/update/properties/add', params=containerProperty)

## ADD A GATE TO LAN BPP6669 RVD 14
gateParams = {"URL":"http://bpp6669rvd14.lab01.dev.dekatonshivr.echinopsii.net:7500", "name":"rvdgate.bpp6669rvd14", "containerID":containerID, "isPrimaryAdmin":False}
r = s.get(srvurl + 'CC/rest/domain/gate/create', params=gateParams)

## ADD A NODE TO LAN BPP6669 RVD 14
nodeParams = {"name":"BPP6669.RVD14", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd14.lab01.dev.dekatonshivr.echinopsii.net/;239.69.66.69:6669", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.66.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)

## ADD A NODE TO LAN BPP6669 RVD 14
nodeParams = {"name":"BRDG-6969-6669.RVD14", "containerID":containerID, "parentNodeID":0}
r = s.get(srvurl + 'CC/rest/domain/node/create', params=nodeParams)
nodeID = r.json().get('nodeID')

#OPTIONAL NODE PROPERTIES (BUT USEFULL)
nodeProperty = {'ID':nodeID,'propertyName':'busDescription','propertyValue':'BPP FX prices historization'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
nodeProperty = {'ID':nodeID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/node/update/properties/add', params=nodeProperty)

## ADD ENDPOINT TO PREVIOUS NODE
endpointParams = {"endpointURL":"multicast-udp-tibrv://bpp6669rvd14.lab01.dev.dekatonshivr.echinopsii.net/;239.69.69.69:6969", "parentNodeID":nodeID}
r = s.get(srvurl + 'CC/rest/domain/endpoint/create', params=endpointParams)
endpointID = r.json().get('endpointID')

primaryApp = {"color":["String","852e48"], "name":["String","BPP"]}
endpointProperty = {'ID':endpointID,'propertyName':'primaryApplication','propertyValue':json.dumps(primaryApp), 'propertyType':'map'}
r = s.get(srvurl + 'CC/rest/domain/endpoint/update/properties/add', params=endpointProperty)

## LINK ENDPOINT TO MULTICAST TRANSPORT
transportParams = {"name": "multicast-udp-tibrv://angelsMind;239.69.69.69"}
## if the transport already exist according the name the rest service return the existing transport
r = s.get(srvurl + 'CC/rest/domain/transport/create', params=transportParams)
transportID = r.json().get('transportID')
linkParams = {"SEPID":endpointID,"TEPID":0,"transportID":transportID}
r = s.get(srvurl + 'CC/rest/domain/link/create', params=linkParams)


