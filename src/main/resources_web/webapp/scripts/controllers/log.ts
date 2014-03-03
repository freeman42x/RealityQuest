/// <reference path="../../tsds/angular.d.ts" />
/// <reference path="../../tsds/atmosphere.d.ts" />
/// <reference path="../classes/log-item.ts" />

'use strict';

angular
    .module('app')
    .controller('LogCtrl', function ($scope) {
        $scope.log = { items: [] };
        var socket = $.atmosphere;
        var subSocket;

        var request:any = {
            url: "/api/log",
            contentType: "application/json",
            logLevel: 'debug',
            transport: 'websocket',
            fallbackTransport: 'long-polling'
        };

        request.onMessage = function (response) {
            var logItem = <LogItem>angular.fromJson(response.responseBody);
            $scope.log.items.push(logItem);
            $scope.$apply();
        };

        request.onClose = function(rs) {
            console.log('connection closed');
            console.log(rs);
            //debugger;
        };

        request.onError = function(rs) {
            console.log('connection error');
            console.log(rs);
            //debugger;
        };

        subSocket = socket.subscribe(request);
    });