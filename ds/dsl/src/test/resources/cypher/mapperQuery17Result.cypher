
MATCH (startContainer:container)-[:owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer

MATCH (startNode:node)
WHERE
(startNode.nodeName = "APP6969.tibrvrdl03prd01")
WITH startContainer, startNode

MATCH standaloneBlockUnion
WHERE
standaloneBlockUnion.MappingGraphVertexID = startContainer.MappingGraphVertexID OR
standaloneBlockUnion.MappingGraphVertexID = startNode.MappingGraphVertexID
WITH standaloneBlockUnion

RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(standaloneBlockUnion) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(standaloneBlockUnion) WHERE n.MappingGraphVertexType = "node")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(standaloneBlockUnion) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(standaloneBlockUnion) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(standaloneBlockUnion) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;