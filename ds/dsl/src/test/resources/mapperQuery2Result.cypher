
START startNode = node(*)
WHERE
startNode.MappingGraphVertexType! = "node" AND
(startNode.nodeName = "APP6969.tibrvrdl03prd01")
WITH startNode

START endNode = node(*)
WHERE
endNode.MappingGraphVertexType! = "node" AND
(endNode.nodeName = "APP6969.tibrvrdl05prd01")
WITH startNode, endNode

MATCH path = startNode -[:owns|link*]- endNode
WHERE
ALL(n in nodes(path) where 1=length(filter(m in nodes(path) : m=n))) AND
ALL(n in nodes(path) where n.MappingGraphVertexType <> "cluster")
RETURN
EXTRACT(co in FILTER( n in nodes(path): n.MappingGraphVertexType! = "container"): co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path): n.MappingGraphVertexType! = "node"): no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path): n.MappingGraphVertexType! = "endpoint"): e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path): n.MappingGraphVertexType! = "transport"): t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) : type(r) = "link"): l.MappingGraphEdgeID) as LID;