
MATCH (startContainer:container)-[:owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer

RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(startContainer) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(startContainer) WHERE n.MappingGraphVertexType = "node")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(startContainer) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(startContainer) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(startContainer) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;