
START startContainers = node(*)
MATCH startContainers-[owns]->startContainersContainerPrimaryAdminGate
WHERE
startContainers.MappingGraphVertexType! = "container" AND
startContainersContainerPrimaryAdminGate.MappingGraphVertexID = startContainers.containerPrimaryAdminGate AND
(startContainersContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*" or startContainersContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl05prd01.*")
WITH startContainers

START endContainer = node(*)
MATCH endContainer-[owns]->endContainerContainerPrimaryAdminGate
WHERE
endContainer.MappingGraphVertexType! = "container" AND
endContainerContainerPrimaryAdminGate.MappingGraphVertexID = endContainer.containerPrimaryAdminGate AND
(endContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdwprd01.*")
WITH startContainers, endContainer

MATCH path = startContainers -[:owns|link*]- endContainer
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) : m=n))) AND
ALL(n in nodes(path) where n.MappingGraphVertexType <> "cluster")
RETURN
EXTRACT(n in nodes(path) : n.MappingGraphVertexID) as PVID,
EXTRACT(l in FILTER(r in relationships(path) : type(r) = "link") : l.MappingGraphEdgeID) as LEID;