// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ TreeController- Angular Tree controller                                              │ \\
// │ Use Angular.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Copyright (C) 2015  Echinopsii      												  │ \\
// │ Author : Sagar Ghuge                                                                 │ \\
// │																                      │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of						  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the						  │ \\
// │ GNU Affero General Public License for more details.								  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.				  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\
'use strict'

var app = angular.module('treeApp', ['jsTree.directive']);

app.controller('treeController', ['$scope', 'serviceMethods', function ($scope, serviceMethods) {
    $scope.treeData = [];
    $scope.subDirID = null;
    $scope.subDirName = null;
    $scope.lookupObj = {};
    $scope.lookupFileObj = {};
    $scope.directoryDescription = null;
    $scope.isDirectory;
    $scope.initVal = true;
    $scope.requestDetail = null;
    $scope.rootId = null;
    $scope.rootName = null;
    $scope.rootDescription = null;
    $scope.selectedNode = null;
    $scope.isCreate = false;
    $scope.reqUsed = false;
    $scope.sessionReqest = "Define your Mapping DSL request here...";

    serviceMethods.apiGETReq('/ariane/rest/mapping/registryDirectory/getRoot').then(function (dataObj) {
        $scope.rootName = dataObj.data.mappingDSLDirectoryName;
        $scope.rootId = dataObj.data.mappingDSLDirectoryID;
        $scope.rootDescription = dataObj.data.mappingDSLDirectoryDescription;

        dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
            var childNode = {
                id: child.subDirectoryID,
                parent: "#",
                text: child.subDirectoryName,
                data: {
                    "directoryDesc": child.subDirectoryDesc
                }
            };
            $scope.treeData.push(childNode);
            $scope.lookupObj[child.subDirectoryID] = childNode;
            initTree(child.subDirectoryID, child.subDirectoryName);
        });
    }, function (err) {
        console.log("Error occured. " + err);
    });

    var initTree = function (id, text) {
        // retrieve node for child upfront
        var postObj = {
            "data": {
                "subDirID": id
            }
        };

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/getChild', postObj).then(function (dataObj) {
            dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
                if (!(child.subDirectoryID in $scope.lookupObj)) {
                    var childNode = {
                        id: child.subDirectoryID,
                        parent: String(dataObj.data.mappingDSLDirectoryID),
                        text: child.subDirectoryName,
                        data: {
                            "directoryDesc": child.subDirectoryDesc
                        }
                    };
                    $scope.treeData.push(childNode);
                    $scope.lookupObj[child.subDirectoryID] = childNode;
                }
            });

            dataObj.data.mappingDSLDirectoryRequestsID.forEach(function (child) {
                if (!(child.dirRequestID in $scope.lookupFileObj)) {
                    var childNode = {
                        id: "child" + child.dirRequestID,
                        parent: String(dataObj.data.mappingDSLDirectoryID),
                        text: child.dirRequestName,
                        icon: "jstree-custom-file",
                        data: {
                            "requestReq": child.dirRequestReq,
                            "requestDesc": child.dirRequestDescription
                        }
                    };
                    $scope.treeData.push(childNode);
                    $scope.lookupFileObj[child.dirRequestID] = childNode;
                }
            });
        }, function (error) {
            console.error("failed to fetch childs");
        });
    };

    var setNodeMetaData = function (data) {
        $scope.folderName = null;
        $scope.folderDescription = null;
        $scope.parentDescription = null;
        $scope.directoryDescription = null;
        $scope.pathToNode = null;
        $scope.requestName = null;
        $scope.requestDesc = null;

        if ($scope.sessionReqest.startsWith("Define")) {
            $scope.requestReq = null;
        }
        $scope.directoryDescription = data.node.data.directoryDesc;

        if (data.node.parent !== "#") {
            $scope.pathToNode = "/" + $scope.rootName + "/" + $('#jstree_div').jstree(true).get_path(data.node.parents[0], "/") + "/" + data.node.text;
            // Parent description
            var dataIndex = null;
            for (var i = 0, len = $scope.treeData.length; i < len; ++i) {
                if ($scope.treeData[i].id.toString() === data.node.parents[0]) {
                    dataIndex = i;
                    break;
                }
            }
            var parentNode = $scope.treeData[i];

            $scope.parentDescription = parentNode.data.directoryDesc;
        } else {
            $scope.pathToNode = "/" + $scope.rootName + "/" + data.node.text;
            $scope.parentDescription = $scope.rootDescription;
        }
    };

    $scope.selectNodeReqCB = function (e, data) {
        $scope.selectedNode = data;
        $scope.dirRequest = false;
        $scope.reqUsed = true;

        if (data.node.icon !== "jstree-custom-file") {
            // Directory
            setNodeMetaData(data);
            $scope.dirRequest = true;
            initContextMenu(data, true);
        } else {
            initContextMenu(data, false);
            $scope.dirRequest = false;
            $scope.pathToNode = "/" + $scope.rootName + "/" + $('#jstree_div2').jstree(true).get_path(data.node.parents[0], "/");
            var parentNode = $scope.treeData.filter(function (obj) {
                return obj.id.toString() === data.node.parents[0];
            })[0];
            $scope.directoryDescription = parentNode.data.directoryDesc;
            $scope.requestName = data.node.text;
            if (!$scope.sessionReqest.startsWith("Define")) {
                $scope.requestReq = $scope.sessionReqest;
            } else {
                $scope.requestReq = data.node.data.requestReq;
            }
            $scope.requestDesc = data.node.data.requestDesc;
        }
        $scope.$apply();
    };

    var initContextMenu = function (data, isDirectory) {
        if (isDirectory) {
            $scope.contextMenu = {
                "dirCreateSubfolder": {
                    "label": "Create subfolder",
                    "icon": "icon-plus-sign",
                    "action": function (obj) {
                        $scope.isCreate = true;
                        setNodeMetaData(data);
                        $scope.$apply();
                        folderNewDialog.show();
                    }
                },
                "dirDelete": {
                    "label": "Delete",
                    "icon": "icon-remove-sign",
                    "action": function (obj) {
                        folderDeleteDialog2.show();
                    }
                },
                "dirEditPermissions": {
                    "label": "Edit Permissions",
                    "icon": "icon-pencil",
                    "action": function (obj) {
                        alert("You clicked " + obj.item.label);
                    }
                },
                "dirEditProperties": {
                    "label": "Edit Properties",
                    "icon": "icon-pencil",
                    "action": function (obj) {
                        setNodeMetaData(data);
                        $scope.folderName = data.node.text;
                        $scope.isCreate = false;
                        $scope.folderDescription = $scope.directoryDescription;
                        $scope.directoryDescription = $scope.parentDescription;
                        var path = $scope.pathToNode.split("/");
                        path.splice(path.length - 1);
                        $scope.pathToNode = path.join("/");
                        $scope.$apply();
                        folderUpdateDialog.show();
                    }
                }
            };
        } else {
            $scope.contextMenu = {
                "fileDelete": {
                    "label": "Delete",
                    "icon": "icon-remove-sign",
                    "action": function (obj) {
                        requestDeleteDialog2.show()
                    }
                },
                "fileEditPermissions": {
                    "label": "Edit Permissions",
                    "icon": "icon-pencil",
                    "action": function (obj) {
                    }
                },
                "fileEditProperties": {
                    "label": "Edit Properties",
                    "icon": "icon-pencil",
                    "action": function (obj) {
                        setNodeMetaData(data)
                        var path = $scope.pathToNode.split("/");
                        path.splice(path.length - 1);
                        $scope.pathToNode = path.join("/");
                        $scope.directoryDescription = $scope.parentDescription;
                        $scope.requestName = data.node.text;
                        $scope.requestReq = data.node.data.requestReq;
                        $scope.requestDesc = data.node.data.requestDesc;
                        $scope.$apply();
                        requestModificationDialog.show();
                    }
                }
            };
        }
    };

    $scope.selectNodeCB = function (e, data) {
        // if Selected node has children then it's a directory else child
        // according to that switch context Menu
        $scope.selectedNode = data;
        $scope.dirRequest = false;
        $scope.reqUsed = false;

        if (data.node.icon !== "jstree-custom-file") {
            // Directory
            $scope.isDirectory = true;
            $scope.initVal = true;
            $scope.leftPaneDirDesc = data.node.data.directoryDesc;
            initContextMenu(data, true);
        } else {
            // children
            $scope.isDirectory = false;
            $scope.initVal = false;
            $scope.requestDetail = data.node.data;
            initContextMenu(data, false);
        }
        $scope.$apply();
    };

    $scope.saveRequest = function () {
        var postObj = {
            "data": {
                "name": $scope.requestName,
                "description": $scope.requestDesc,
                "request": $scope.requestReq
            }
        }

        if ($scope.dirRequest) {
            postObj.data.rootId = $scope.selectedNode.node.id;
            postObj.data.requestId = '0';
        } else {
            if ($scope.selectedNode.node.parent === "#") {
                postObj.data.rootId = $scope.rootId.toString();
            } else {
                postObj.data.rootId = $scope.selectedNode.node.parent;
            }
            postObj.data.requestId = $scope.selectedNode.node.id.split("child").pop();
        }

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryRequest/saveRequest', postObj).then(function (result) {
            var childNode = {
                id: "child" + result.data,
                parent: postObj.data.rootId,
                text: postObj.data.name,
                icon: "jstree-custom-file",
                data: {
                    "requestReq": postObj.data.request,
                    "requestDesc": postObj.data.description
                }
            };

            if ($scope.dirRequest) {
                $scope.treeData.push(childNode);
                $scope.lookupObj[result.data] = childNode;
            } else {
                $scope.requestDetail = childNode.data;
                // Find object with same Id and update it
                var obj = $scope.treeData.filter(function (v) {
                    return v.id.toString() === "child" + result.data.toString();
                })[0];
                obj.data.requestDesc = postObj.data.description;
                obj.data.requestReq = postObj.data.request;
                obj.text = postObj.data.name;
            }

            growlMsg.show([{severity: 'info', summary:
                "Mapping DSL request " + ((postObj.data.requestId ==='0')? "created" : "updated") + " successfully !",
                detail: "Mapping DSL request " + ((postObj.data.name !== null) ? "name : " + postObj.data.name : "id :" + postObj.data.requestId)}])

        }, function (error) {
            growlMsg.show([{severity: 'error', summary:
                "Error while creating mapping dsl request " + postObj.data.name + " !",
                "detail" : "Error message : " + error}]);
        });

        $scope.reqUsed = false;
        $scope.pathToNode = null;
        $scope.directoryDescription = null;
        $scope.requestName = null;
        $scope.requestReq = null;
        $scope.requestDesc = null;
        requestSaveDialog2.hide();
        requestModificationDialog.hide();
    };

    $scope.saveDirectory = function () {
        var postObj = {
            "data": {
                "name": $scope.folderName,
                "description": $scope.folderDescription
            }
        };

        if ($scope.isCreate) {
            postObj.data.rootId = $scope.selectedNode.node.id;
            postObj.data.directoryId = '0';
        } else {
            if ($scope.selectedNode.node.parent === "#") {
                postObj.data.rootId = $scope.rootId.toString();
            } else {
                postObj.data.rootId = $scope.selectedNode.node.parent;
            }
            postObj.data.directoryId = $scope.selectedNode.node.id;
        }

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/saveDirectory', postObj).then(function (result) {
            var childNode = {
                id: result.data,
                parent: postObj.data.rootId,
                text: postObj.data.name,
                data: {
                    "directoryDesc": postObj.data.description
                }
            };

            $scope.leftPaneDirDesc = childNode.data.directoryDesc;

            if ($scope.isCreate) {
                $scope.treeData.push(childNode);
                $scope.lookupObj[result.data] = childNode;
            } else {
                // Find object with same Id and update it
                var obj = $scope.treeData.filter(function (v) {
                    return v.id.toString() === result.data;
                })[0];
                obj.data.directoryDesc = postObj.data.description;
                obj.text = postObj.data.name;
            }

        }, function (error) {
            console.error("failed to save/update directory");
        });

        $scope.reqUsed = false;
        if ($scope.isCreate) {
            folderNewDialog.hide();
        } else {
            folderUpdateDialog.hide();
        }
    };

    $scope.loadSelectedRequest = function (request) {
        $scope.sessionReqest = request;
        $scope.requestReq = request;
        requestLoaderDialog2.hide();
    };

    $scope.deleteDirectory = function (directoryID) {
        var postObj = {
            "data": {
                "directoryID": directoryID
            }
        };

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/deleteDirectory', postObj).then(function () {
            for (var i = 0; i < $scope.treeData.length; i++) {
                var obj = $scope.treeData[i];

                if (obj.id.toString() === directoryID.toString() || $scope.selectedNode.node.children_d.indexOf(obj.id.toString()) !== -1) {
                    $scope.treeData.splice(i, 1);
                    i--;
                }
            }
            folderDeleteDialog2.hide();
        }, function (error) {
            console.error("failed to delete directory");
        });
    };

    $scope.deleteRequest = function (requestID) {
        var postObj = {
            "data": {
                "requestID": requestID
            }
        };

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryRequest/deleteRequest', postObj).then(function () {
            for (var i = 0; i < $scope.treeData.length; i++) {
                var obj = $scope.treeData[i];

                if (obj.id === "child" + requestID) {
                    $scope.treeData.splice(i, 1);
                    i--;
                }
            }
            requestDeleteDialog2.hide();
        }, function (error) {
            console.error("failed to Request directory");
        });
    };

    $scope.closeNodeCB = function (e, data) {
        $scope.lookupObj[data.node.id].state = {
            "opened": false
        };
    };

    $scope.openNodeCB = function (e, data) {
        // User can open Node without selecting node
        // so need to keep track which node is opened.
        $scope.subDirID = data.node.id;
        $scope.subDirName = data.node.text;

        $scope.lookupObj[$scope.subDirID].state = {
            "opened": true
        };

        data.node.children.forEach(function (id) {
            // retrieve node for child upfront
            if (id.indexOf("child") === -1) {
                var postObj = {
                    "data": {
                        "subDirID": parseInt(id)
                    }
                };

                serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/getChild', postObj).then(function (dataObj) {
                    dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
                        if (!(child.subDirectoryID in $scope.lookupObj)) {
                            var childNode = {
                                id: child.subDirectoryID,
                                parent: String(dataObj.data.mappingDSLDirectoryID),
                                text: child.subDirectoryName,
                                data: {
                                    "directoryDesc": child.subDirectoryDesc
                                }
                            };
                            $scope.treeData.push(childNode);
                            $scope.lookupObj[child.subDirectoryID] = childNode;
                        }
                    });

                    dataObj.data.mappingDSLDirectoryRequestsID.forEach(function (child) {
                        if (!(child.dirRequestID in $scope.lookupFileObj)) {
                            var childNode = {
                                id: "child" + child.dirRequestID,
                                parent: String(dataObj.data.mappingDSLDirectoryID),
                                text: child.dirRequestName,
                                icon: "jstree-custom-file",
                                data: {
                                    "requestReq": child.dirRequestReq,
                                    "requestDesc": child.dirRequestDescription
                                }
                            };
                            $scope.treeData.push(childNode);
                            $scope.lookupFileObj[child.dirRequestID] = childNode;
                        }
                    });
                }, function (error) {
                    console.error("failed to fetch childs");
                });
            }
        });
    };
}]);