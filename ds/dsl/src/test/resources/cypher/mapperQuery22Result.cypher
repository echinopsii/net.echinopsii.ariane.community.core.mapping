
MATCH (firefox_dekatonmac:node) -[:owns*]-> (firefox_dekatonmacEPs:endpoint)
WHERE
(firefox_dekatonmac.nodeName = "[9626] firefox")
WITH firefox_dekatonmacEPs

MATCH (unkown_remote_endpoint:endpoint)
WHERE
(unkown_remote_endpoint.endpointURL = "tcp://178.236.6.191:443")
WITH firefox_dekatonmacEPs, unkown_remote_endpoint

MATCH standaloneLinkUnion
WHERE
standaloneLinkUnion.MappingGraphVertexID = firefox_dekatonmacEPs.MappingGraphVertexID OR
standaloneLinkUnion.MappingGraphVertexID = unkown_remote_endpoint.MappingGraphVertexID
WITH standaloneLinkUnion

MATCH path = standaloneLinkUnion -[:link]- remoteEPs
RETURN DISTINCT
EXTRACT(co in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "container")| co.MappingGraphVertexID) as CID,
EXTRACT(no in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "node" or n.MappingGraphVertexType = "gate")| no.MappingGraphVertexID) as NID,
EXTRACT(e in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "endpoint")| e.MappingGraphVertexID) as EID,
EXTRACT(t in FILTER( n in nodes(path) WHERE n.MappingGraphVertexType = "transport")| t.MappingGraphVertexID) as TID,
EXTRACT(l in FILTER( r in relationships(path) WHERE type(r) = "link")| l.MappingGraphEdgeID) as LID;