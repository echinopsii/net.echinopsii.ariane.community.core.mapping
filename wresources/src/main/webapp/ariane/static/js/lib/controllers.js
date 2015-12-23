'use strict'
/* Controllers */


var app = angular.module('application', ['jsTree.directive','ngDialog']);

app.controller('MyCtrl1', ['$scope', 'apiMethods', 'ngDialog', function ($scope, apiMethods, ngDialog) {
    $scope.treeData = [];
    $scope.subDirID = null;
    $scope.subDirName = null;
    $scope.lookupObj = {};
    $scope.lookupFileObj = {};

    $scope.ShowNgDialog = function () {
        ngDialog.open({
            template: '../../ariane/ahtml/templates/load.html',
            scope:$scope
        });
    }

    apiMethods.apiGETReq('/ariane/rest/mapping/registryDirectory/getRoot').then(function (dataObj) {
        var parentNode = {
            id: dataObj.data.mappingDSLDirectoryID,
            parent: "#",
            text: dataObj.data.mappingDSLDirectoryName
        }
        $scope.treeData.push(parentNode);

        $scope.lookupObj[dataObj.data.mappingDSLDirectoryID] = parentNode;

        dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
            var childNode = {
                id: child.subDirectoryID,
                parent: String(dataObj.data.mappingDSLDirectoryID),
                text: child.subDirectoryName
            };
            $scope.treeData.push(childNode);
            $scope.lookupObj[child.subDirectoryID] = childNode;
        })
    }, function (err) {
        console.log("Error occured. " + err);
    });

    $scope.selectNodeCB = function (e, data) {
        // if Selected node has children then it's a directory else child
        // according to that switch context Menu
        if (data.node.icon !== "jstree-custom-file") {
            // parent
            $scope.contextMenu = {
                "dirCreateSubfolder": {
                    "label": "Create subfolder",
                    "action": function (obj) {

                    }
                },
                "dirDelete": {
                    "label": "Delete",
                    "action": function(obj){
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
    };

    var deleteDirectory = function (directoryID) {
        var postObj = {
            "data": {
                "directoryID": directoryID
            }
        };

        apiMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/deleteDirectory', postObj).then(function () {
            for(var i = 0; i < $scope.treeData.length; i++) {
                var obj = $scope.treeData[i];

                if(obj.id === parseInt(directoryID)) {
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

        apiMethods.apiPOSTReq('/ariane/rest/mapping/registryRequest/deleteRequest', postObj).then(function () {
            for(var i = 0; i < $scope.treeData.length; i++) {
                var obj = $scope.treeData[i];

                if(obj.id === "child"+requestID) {
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
                        "subDirID": id
                    }
                };

                apiMethods.apiPOSTReq('/ariane/rest/mapping/registryDirectory/getChild', postObj).then(function (dataObj) {
                    dataObj.data.mappingDSLDirectorySubDirsID.forEach(function (child) {
                        if (!(child.subDirectoryID in $scope.lookupObj)) {
                            var childNode = {
                                id: child.subDirectoryID,
                                parent: String(dataObj.data.mappingDSLDirectoryID),
                                text: child.subDirectoryName
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
                                icon: "jstree-custom-file"
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