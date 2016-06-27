// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Service   - Wrapper for HTTP services                                                │ \\
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
var app = angular.module("treeApp");

app.factory('serviceMethods', function($http) {
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
