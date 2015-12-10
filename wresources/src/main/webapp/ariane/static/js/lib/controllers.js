'use strict';

/* Controllers */


var app = angular.module('application');

app.controller('MyCtrl1', ['$scope', 'apiMethods', function ($scope, apiMethods) {
    apiMethods.apiGETReq('/ariane/rest/mapping/registry/getRoot').then(function (res) {
        $scope.firstname = res.data.mappingDSLDirectoryName;
    },function(err){
        console.log("Error occured. "+err);
    })
}]);