--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT IGNORE INTO `resource` (description, resourceName, version) VALUES
    ('Mapping DB','mappingDB',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE,`resource` WRITE;
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can write Mapping DB', 'mappingDB:write', 1, id FROM resource WHERE resourceName='mappingDB';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can read Mapping DB', 'mappingDB:read', 1, id FROM resource WHERE resourceName='mappingDB';
UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE,`permission` AS p WRITE,`resource` AS r WRITE ;
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='mappingDB' AND p.permissionName='mappingDB:write';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='mappingDB' AND p.permissionName='mappingDB:read';
UNLOCK TABLES;



--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT IGNORE INTO `role` (description, roleName, version) VALUES
    ('mapping injector role','mappinginjector',1),
    ('mapping reader role','mappingreader',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:write' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:write' AND r.roleName='mappinginjector';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='mappinginjector';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='mappingreader';
UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:write' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:write' AND r.roleName='mappinginjector';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='mappinginjector';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='mappingDB:read' AND r.roleName='mappingreader';
UNLOCK TABLES;



--
-- Dumping data for table `uxpermission`
--

LOCK TABLES `uxpermission` WRITE;
INSERT IGNORE INTO `uxpermission` VALUES (1,'READ.USER',0),(2,'READ.GROUP',0),(3,'READ.OTHER',0),(4,'WRITE.USER',0),(5,'WRITE.GROUP',0),(6,'WRITE.OTHER',0),(7,'CHPERM.USER',0),(8,'CHPERM.GROUP',0),(9,'CHPERM.OTHER',0);
UNLOCK TABLES;



--
-- Dumping data for table `uxResourceDirectory`
--

LOCK TABLES `uxResourceDirectory` WRITE;
INSERT IGNORE INTO `uxResourceDirectory` VALUES
    (1,'The Mapping DSL Root Registry Directory','MappingDSLRegistry',1,7,NULL,1),
    (2,'The Mapping DSL Samples Directory','Samples',1,8,1,1),
    (3,'The Mapping DSL Users Directory','Users',1,9,1,1),
    (4,'General samples folder','General',1,8,2,1);
UNLOCK TABLES;



--
-- Dumping data for table `uxResourceDirectory_uxpermission`
--

LOCK TABLES `uxResourceDirectory_uxpermission` WRITE;
INSERT IGNORE INTO `uxResourceDirectory_uxpermission` VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,7),
    (2,1),(2,2),(2,3),(2,4),(2,5),(2,7),
    (3,1),(3,2),(3,3),(3,4),(3,5),(3,7),
    (4,1),(4,2),(4,3),(4,4),(4,5),(4,7);
UNLOCK TABLES;



--
-- Dumping data for table `uxResourceRequest`
--

LOCK TABLES `uxResourceRequest` WRITE;
INSERT IGNORE INTO `uxResourceRequest` VALUES
    (1,'This template requests routes between two containers.','\0','container--container.tpl','{\r\n    \'startContainer\': \'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}\r\n--\r\n{\r\n    \'endContainer\': \'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}',5,8,4,1),
    (2,'This template requests routes between two nodes.','\0','node--node.tpl','{\r\n    \'startNode\': \'FROM node WHERE startNode.nodeName=\"<node name>\" \'\r\n}\r\n--\r\n{\r\n    \'endNode\': \'FROM node WHERE endNode.nodeName=\"<node name>\" \'\r\n}',3,8,4,1),
    (3,'This template requests routes between two endpoints.','\0','endpoint--endpoint.tpl','{\r\n    \'startEP\': \'FROM endpoint WHERE startEP.endpointURL=\"<endpoint url>\" \'\r\n}\r\n--\r\n{\r\n    \'endEP\': \'FROM endpoint WHERE endEP.endpointURL=\"<endpoint url>\" \'\r\n}',2,8,4,1),
    (4,'This template requests routes between one container and one endpoint.','\0','container--endpoint.tpl','{\r\n    \'startContainer\': \'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}\r\n--\r\n{\r\n    \'endEP\': \'FROM endpoint WHERE endEP.endpointURL=\"<endpoint url>\" \'\r\n}',3,8,4,1),
    (5,'This template requests routes between two start container and one end container.','\0','containers--container.tpl','{\r\n    \'startContainers\': \'FROM container WHERE  startContainers.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\"  OR startContainers.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}\r\n--\r\n{\r\n    \'endContainer\': \'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}',2,8,4,1),
    (6,'This template requests routes from one container and one node to one container.','\0','container_node--container.tpl','{\r\n    \'startContainer\': \'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \',\r\n    \'startNode\': \'FROM node WHERE startNode.nodeName=\"<node name>\" \'\r\n}\r\n--\r\n{\r\n    \'endContainer\': \'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}',5,8,4,1),
    (7,'This template requests routes from one container to one container and one node.','\0','container--container_node.tpl','{\r\n    \'startContainer\': \'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}\r\n--\r\n{\r\n    \'endContainer\': \'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \',\r\n    \'endNode\': \'FROM node WHERE endNode.nodeName=\"<node name>\" \'\r\n}',3,8,4,1),
    (8,'This template requests routes from one container to another though the specified transport.','\0','container-transport-container.tpl','{\r\n    \'startContainer\': \'FROM container WHERE startContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}\r\n- {\r\n    \'moulticast\': \'FROM transport WHERE moulticast.transportName=\"<transport name>\" \' \r\n} -\r\n{\r\n    \'endContainer\': \'FROM container WHERE endContainer.containerPrimaryAdminGate.nodeName =~ \"<container primary admin gate identifier>\" \'\r\n}',1,8,4,1);
UNLOCK TABLES;