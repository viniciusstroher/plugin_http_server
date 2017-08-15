/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *

	EX IN APP JS

	navigator.httpd.startHttpd($scope.server.porta,$scope.server.senha);
    $timeout(function(){
      WatchJS.watch(window.httpd, "contador", function(prop, action, newvalue, oldvalue) {
        console.log("Novo request:",window.httpd.requests[window.httpd.ultimaUri][window.httpd.requests[window.httpd.ultimaUri].length-1]);
      });
    },1000);


*/

var exec = require('cordova/exec');

var Httpd = {
    startHttpd:function(porta,senha) {
    	var params 			  = {};
    	params.porta 		  = porta;
    	params.senha    	  = senha;
        exec(null, null, "Httpd", "startHttpd", [params]);
    },

    stopHttpd:function() {
        var params            = {};
        exec(null, null, "Httpd", "stopHttpd", [params]);
    }
};

module.exports = Httpd;
