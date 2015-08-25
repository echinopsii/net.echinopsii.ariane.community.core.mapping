
MATCH (startContainer:container)-[:owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer

MATCH (startNode:node)
WHERE
(startNode.nodeName = "APP6969.tibrvrdl03prd01")
RETURN startContainer, startNode