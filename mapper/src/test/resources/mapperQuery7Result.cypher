
START startContainer = node(*)
MATCH startContainer-[owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainer.MappingGraphVertexType! = "container" AND
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdwprd01.*")
WITH startContainer

START endContainer = node(*)
MATCH endContainer-[owns]->endContainerContainerPrimaryAdminGate
WHERE
endContainer.MappingGraphVertexType! = "container" AND
endContainerContainerPrimaryAdminGate.MappingGraphVertexID = endContainer.containerPrimaryAdminGate AND
(endContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer, endContainer

START endNode = node(*)
WHERE
endNode.MappingGraphVertexType! = "node" AND
(endNode.nodeName = "APP6969.tibrvrdl03prd01")
WITH startContainer, endContainer, endNode

START endUnion = node(*)
WHERE
endUnion.MappingGraphVertexID! = endContainer.MappingGraphVertexID OR
endUnion.MappingGraphVertexID! = endNode.MappingGraphVertexID
WITH startContainer, endUnion

MATCH path = startContainer -[:owns|link*]- endUnion
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) : m=n))) AND
ALL(n in nodes(path) where n.MappingGraphVertexType <> "cluster")
RETURN
EXTRACT(n in nodes(path) : n.MappingGraphVertexID) as PVID,
EXTRACT(l in FILTER(r in relationships(path) : type(r) = "link") : l.MappingGraphEdgeID) as LEID;