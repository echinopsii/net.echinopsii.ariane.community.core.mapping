
var app = angular.module("application");

app.factory('apiMethods', function($http) {
        return {
            apiGETReq : function(url, obj) {
                return $http({
                    method : 'GET',
                    url : url,
                    params: obj
                });
            },
            apiPOSTReq : function(url, obj) {
                return $http({
                    method : 'POST',
                    url : url,
                    params : obj
                });
            }
        }
    });
