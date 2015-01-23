
MATCH middleOfficeService-[:owns]->middleOfficeServiceContainerPrimaryAdminGate
WHERE
middleOfficeService.MappingGraphVertexType = "container" AND
middleOfficeServiceContainerPrimaryAdminGate.MappingGraphVertexID = middleOfficeService.containerPrimaryAdminGate AND
(middleOfficeServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.mo01")
WITH middleOfficeService

MATCH riskService-[:owns]->riskServiceContainerPrimaryAdminGate
WHERE
riskService.MappingGraphVertexType = "container" AND
riskServiceContainerPrimaryAdminGate.MappingGraphVertexID = riskService.containerPrimaryAdminGate AND
(riskServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.risk01")
WITH middleOfficeService, riskService

MATCH backOfficeService-[:owns]->backOfficeServiceContainerPrimaryAdminGate
WHERE
backOfficeService.MappingGraphVertexType = "container" AND
backOfficeServiceContainerPrimaryAdminGate.MappingGraphVertexID = backOfficeService.containerPrimaryAdminGate AND
(backOfficeServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.bo01")
WITH middleOfficeService, riskService, backOfficeService

MATCH endUnion
WHERE
endUnion.MappingGraphVertexID = riskService.MappingGraphVertexID OR
endUnion.MappingGraphVertexID = backOfficeService.MappingGraphVertexID
WITH middleOfficeService, endUnion

MATCH rbqNode1EP2
WHERE
rbqNode1EP2.MappingGraphVertexType = "endpoint" AND
(rbqNode1EP2.endpointURL =~ ".*MiddleOfficeService.*" or rbqNode1EP2.endpointURL =~ ".*RPC/BOQ.*" or rbqNode1EP2.endpointURL =~ ".*RPC/RIQ.*")
WITH middleOfficeService, endUnion, rbqNode1EP2

MATCH rbqNode2EP1
WHERE
rbqNode2EP1.MappingGraphVertexType = "endpoint" AND
(rbqNode2EP1.endpointURL =~ ".*MiddleOfficeService.*" or rbqNode2EP1.endpointURL =~ ".*BOQ/BOQ.*" or rbqNode2EP1.endpointURL =~ ".*RIQ/RIQ.*")
WITH middleOfficeService, endUnion, rbqNode1EP2, rbqNode2EP1

MATCH path = middleOfficeService -[:owns*]-> startContainerEP <-[:link]-> rbqNode1EP1 <-[:owns]- rbqNode1 -[:owns]-> rbqNode1EP2 -[:link]- rbqNode2EP1 <-[:owns]- rbqNode2 -[:owns]-> rbqNode2EP2 <-[:link]-> endContainerEP <-[:owns*]- endUnion
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;