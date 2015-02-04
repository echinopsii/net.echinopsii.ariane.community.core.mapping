
MATCH (startContainer:container)-[:owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdwprd01.*")
WITH startContainer

MATCH (endContainer:container)-[:owns]->endContainerContainerPrimaryAdminGate
WHERE
endContainerContainerPrimaryAdminGate.MappingGraphVertexID = endContainer.containerPrimaryAdminGate AND
(endContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer, endContainer

MATCH (endNode:node)
WHERE
(endNode.nodeName = "APP6969.tibrvrdl03prd01")
WITH startContainer, endContainer, endNode

MATCH endUnion
WHERE
endUnion.MappingGraphVertexID = endContainer.MappingGraphVertexID OR
endUnion.MappingGraphVertexID = endNode.MappingGraphVertexID
WITH startContainer, endUnion

MATCH path = startContainer -[:owns|link*]- endUnion
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;