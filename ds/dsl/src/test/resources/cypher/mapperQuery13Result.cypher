
MATCH (feederService:container)-[:owns]->(feederServiceContainerPrimaryAdminGate:gate)
WHERE
feederServiceContainerPrimaryAdminGate.MappingGraphVertexID = feederService.containerPrimaryAdminGate AND
(feederServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.feeder01")
WITH feederService

MATCH (frontService:container)-[:owns]->(frontServiceContainerPrimaryAdminGate:gate)
WHERE
frontServiceContainerPrimaryAdminGate.MappingGraphVertexID = frontService.containerPrimaryAdminGate AND
(frontServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.fof.*")
WITH feederService, frontService

MATCH (rbqNode2TwinsConsumerEP:endpoint)
WHERE
rbqNode2TwinsConsumerEP.endpointURL =~ ".*5672.*"
WITH feederService, frontService, rbqNode2TwinsConsumerEP

MATCH
	path = feederService -[:owns*]-> feederEP <-[:link]-> rbqNode1EP1 <-[:owns]- rbqNode1 -[:owns]-> rbqNode1EP2 <-[:link]-> rbqNode2EP1 <-[:owns]- rbqNode2 <-[:twin]-> rbqNode2Twins -[:owns]-> rbqNode2TwinsConsumerEP <-[:link]-> frontEP <-[:owns*]- frontService
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;