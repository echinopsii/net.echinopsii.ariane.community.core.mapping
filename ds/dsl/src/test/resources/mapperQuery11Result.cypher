CYPHER 1.9
START startContainer = node(*)
MATCH startContainer-[owns]->startContainerContainerPrimaryAdminGate
WHERE
startContainer.MappingGraphVertexType! = "container" AND
startContainerContainerPrimaryAdminGate.MappingGraphVertexID = startContainer.containerPrimaryAdminGate AND
(startContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl03prd01.*")
WITH startContainer

START endContainer = node(*)
MATCH endContainer-[owns]->endContainerContainerPrimaryAdminGate
WHERE
endContainer.MappingGraphVertexType! = "container" AND
endContainerContainerPrimaryAdminGate.MappingGraphVertexID = endContainer.containerPrimaryAdminGate AND
(endContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdl05prd01.*")
WITH startContainer, endContainer

START moulticast = node(*)
WHERE
moulticast.MappingGraphVertexType! = "transport" AND
(moulticast.transportName = "multicast-udp-tibrv://;239.69.69.69")
WITH startContainer, endContainer, moulticast

START ptNodeEPs = node(*)
MATCH ptNode -[:owns*]-> ptNodeEPs
WHERE
ptNodeEPs.MappingGraphVertexType! = "endpoint" AND
ptNode.MappingGraphVertexType! = "node" AND
(ptNode.nodeName = "APP6969.tibrvrdmprd01")
WITH startContainer, endContainer, moulticast, ptNodeEPs

START ptUnion = node(*)
WHERE
ptUnion.MappingGraphVertexID! = moulticast.MappingGraphVertexID OR
ptUnion.MappingGraphVertexID! = ptNodeEPs.MappingGraphVertexID
WITH startContainer, endContainer, ptUnion

MATCH path = startContainer -[:owns|link*]- ptUnion -[:owns|link*]- endContainer
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) : m=n))) AND
ALL(n in nodes(path) where n.MappingGraphVertexType <> "cluster")
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path): n.MappingGraphVertexType! = "container"): co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path): n.MappingGraphVertexType! = "node"): no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path): n.MappingGraphVertexType! = "endpoint"): e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path): n.MappingGraphVertexType! = "transport"): t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) : type(r) = "link"): l.MappingGraphEdgeID) as LID;