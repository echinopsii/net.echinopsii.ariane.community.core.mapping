'use strict'
/* Controllers */


var app = angular.module('application');

app.controller('MyCtrl1', ['$scope', 'apiMethods', function ($scope, apiMethods) {
    $scope.treeData = [];
    $scope.subDirID = null;
    $scope.subDirName = null;
    $scope.lookupObj = {};
    $scope.lookupFileObj = {};

    apiMethods.apiGETReq('/ariane/rest/mapping/registry/getRoot').then(function (dataObj) {
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
                "Menu 1": {
                    "label": "Create subfolder",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "Menu 2": {
                    "label": "Delete",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "Menu 3": {
                    "label": "Edit Permissions",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "Menu 4": {
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
                "Menu 1": {
                    "label": "Delete",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "Menu 2": {
                    "label": "Edit Permissions",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                },
                "Menu 3": {
                    "label": "Edit Properties",
                    "action": function (obj) {
                        console.log(obj);
                        alert("You clicked " + obj.item.label);
                    }
                }
            };
        }
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
                        "subDirID": id
                    }
                };

                apiMethods.apiPOSTReq('/ariane/rest/mapping/registry/getChild', postObj).then(function (dataObj) {
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