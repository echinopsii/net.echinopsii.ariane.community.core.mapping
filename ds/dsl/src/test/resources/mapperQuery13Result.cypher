
MATCH feederService-[:owns]->feederServiceContainerPrimaryAdminGate
WHERE
feederService.MappingGraphVertexType = "container" AND
feederServiceContainerPrimaryAdminGate.MappingGraphVertexID = feederService.containerPrimaryAdminGate AND
(feederServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.feeder01")
WITH feederService

MATCH frontService-[:owns]->frontServiceContainerPrimaryAdminGate
WHERE
frontService.MappingGraphVertexType = "container" AND
frontServiceContainerPrimaryAdminGate.MappingGraphVertexID = frontService.containerPrimaryAdminGate AND
(frontServiceContainerPrimaryAdminGate.nodeName =~ "rbqcliadmingate.fof.*")
WITH feederService, frontService

MATCH 
	path = feederService -[:owns*]-> feederEP <-[:link]-> rbqNode1EP1 <-[:owns]- rbqNode1 -[:owns]-> rbqNode1EP2 <-[:link]-> rbqNode2EP1 <-[:owns]- rbqNode2 <-[:twin]-> rbqNode2Twins -[:owns]-> rbqNode2TwinsConsumerEP <-[:link]-> frontEP <-[:owns*]- frontService
WHERE
	rbqNode2TwinsConsumerEP.endpointURL =~ ".*5672.*"
return path