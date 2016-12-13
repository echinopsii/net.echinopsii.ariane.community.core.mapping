
MATCH (startContainer:container)-[:owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer

MATCH (endContainer:container)-[:owns]->endContainerContainerPrimaryAdminGate
WHERE
endContainerContainerPrimaryAdminGate.MappingGraphVertexID = endContainer.containerPrimaryAdminGate AND
(endContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl05prd01.*")
WITH startContainer, endContainer

MATCH (moulticast:transport)
WHERE
(moulticast.transportName =~ ".*239.69.69.69.*")
WITH startContainer, endContainer, moulticast

MATCH (ptEP:endpoint)
WHERE
(ptEP.endpointURL =~ ".*tibrvrdmprd01.*")
WITH startContainer, endContainer, moulticast, ptEP

MATCH ptUnion
WHERE
ptUnion.MappingGraphVertexID = moulticast.MappingGraphVertexID OR
ptUnion.MappingGraphVertexID = ptEP.MappingGraphVertexID
WITH startContainer, endContainer, ptUnion

MATCH path = startContainer -[:owns|link*]- ptUnion -[:owns|link*]- endContainer
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) WHERE m=n)))
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node" or n.MappingGraphVertexType = "gate")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;