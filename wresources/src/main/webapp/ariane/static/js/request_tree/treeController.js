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
    $scope.requestDetail;

    serviceMethods.apiGETReq('/ariane/rest/mapping/registryDirectory/getRoot').then(function (dataObj) {
        dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
            var childNode = {
                id: child.subDirectoryID,
                parent: "#",
                text: child.subDirectoryName,
                data: {
                    "directoryDesc": child.subDirectoryDesc
                }
            };
            console.log(child)
            $scope.treeData.push(childNode);
            $scope.lookupObj[child.subDirectoryID] = childNode;
            initTree(child.subDirectoryID, child.subDirectoryName)
        })
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
                    }
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
                    }
                    $scope.treeData.push(childNode);
                    $scope.lookupFileObj[child.dirRequestID] = childNode;
                }
            })
        }, function (error) {
            console.error("failed to fetch childs")
        })
    }


    $scope.selectNodeCB = function (e, data) {
        // if Selected node has children then it's a directory else child
        // according to that switch context Menu
        console.log(data)
        if (data.node.icon !== "jstree-custom-file") {
            // parent
            $scope.directoryDescription = data.node.data.directoryDesc
            $scope.isDirectory = true;
            $scope.initVal = true;
            $scope.contextMenu = {
                "dirCreateSubfolder": {
                    "label": "Create subfolder",
                    "action": function (obj) {

                    }
                },
                "dirDelete": {
                    "label": "Delete",
                    "action": function (obj) {
                        deleteDirectory(data.node.id)
                    }
                },
                "dirEditPermissions": {
                    "label": "Edit Permissions",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "dirEditProperties": {
                    "label": "Edit Properties",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                }
            };
        } else {
            // children
            $scope.isDirectory = false
            $scope.initVal = false;
            $scope.requestDetail = data.node.data;
            $scope.contextMenu = {
                "fileDelete": {
                    "label": "Delete",
                    "action": function (obj) {
                        deleteRequest(data.node.id.split("child")[1])
                    }
                },
                "fileEditPermissions": {
                    "label": "Edit Permissions",
                    "action": function (obj) {
                        console.log(data.node);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "fileEditProperties": {
                    "label": "Edit Properties",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                }
            };
        }
        $scope.$apply()
    };

    var deleteDirectory = function (directoryID) {
        var postObj = {
            "data": {
                "directoryID": directoryID
            }
        };

        serviceMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/deleteDirectory', postObj).then(function () {
            for (var i = 0; i < $scope.treeData.length; i++) {
                var obj = $scope.treeData[i];

                if (obj.id === parseInt(directoryID)) {
                    $scope.treeData.splice(i, 1);
                    i--;
                }
            }
        }, function (error) {
            console.error("failed to delete directory");
        })
    }

    var deleteRequest = function (requestID) {
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
        }, function (error) {
            console.error("failed to Request directory");
        })
    }

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
                            }
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
                            }
                            $scope.treeData.push(childNode);
                            $scope.lookupFileObj[child.dirRequestID] = childNode;
                        }
                    })
                }, function (error) {
                    console.error("failed to fetch childs")
                })
            }
        })
    };
}]);