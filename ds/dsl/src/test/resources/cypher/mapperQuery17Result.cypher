
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

MATCH path = standaloneBlockUnion -[:owns*]-> internalObjects
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node" or n.MappingGraphVertexType = "gate")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID

UNION

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

MATCH path = standaloneBlockUnion -[:owns*]-> (internalEndpoint:endpoint) -[:link]- (externalEndpoint:endpoint)
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node" or n.MappingGraphVertexType = "gate")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;