
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

START ptContainerEPs = node(*)
MATCH ptContainer -[:owns*]-> ptContainerEPs, ptContainer-[owns]->ptContainerContainerPrimaryAdminGate
WHERE
ptContainerEPs.MappingGraphVertexType! = "endpoint" AND
ptContainer.MappingGraphVertexType! = "container" AND
ptContainerContainerPrimaryAdminGate.MappingGraphVertexID = ptContainer.containerPrimaryAdminGate AND
(ptContainerContainerPrimaryAdminGate.nodeName =~ ".*tibrvrdmprd01.*")
WITH startContainer, endContainer, moulticast, ptContainerEPs

START ptUnion = node(*)
WHERE
ptUnion.MappingGraphVertexID! = moulticast.MappingGraphVertexID OR
ptUnion.MappingGraphVertexID! = ptContainerEPs.MappingGraphVertexID
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