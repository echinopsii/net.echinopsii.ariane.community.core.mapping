
var app = angular.module("application");

app.factory('apiMethods', function($http) {
        return {
            apiGETReq : function(url) {
                return $http({
                    method : 'GET',
                    url : url
                });
            },
            apiPOSTReq : function(url, obj) {
                return $http({
                    method : 'POST',
                    url : url,
                    data : obj
                });
            }
        }
    });
