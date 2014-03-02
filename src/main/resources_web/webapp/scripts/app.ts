/// <reference path="../tsds/angular.d.ts" />
/// <reference path="../tsds/angular-ui-router.d.ts" />
/// <reference path="../tsds/lodash.d.ts" />
/// <reference path="../tsds/moment.d.ts" />

'use strict';

angular
    .module('app', ['ui.router' , 'highcharts-ng', 'ngAnimate'])
    .config(function ($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider)
    {
        $locationProvider.hashPrefix('!');

        $urlRouterProvider
            .when('', '/home')
            .otherwise("/home");

        $stateProvider
            .state('404',
            {
                url: '/404',
                templateUrl: '404.html'
            })
            .state('home',
            {
                url: '/home',
                templateUrl: '/views/home.html',
                controller: 'HomeCtrl'
            })
            .state('settings',
            {
                url: '/settings',
                templateUrl: '/views/settings.html',
                controller: 'SettingsCtrl'
            });

        $httpProvider.defaults.transformResponse.push(function(data, responseInfo)
        {
            function parseDates(data)
            {
                _.forOwn(data, (field, fieldName, data) =>
                {
                    if (_.isString(field))
                    {
                        var momentDate = moment(field);
                        if (momentDate.isValid())
                            data[fieldName] = momentDate.toDate();
                    }
                    else if (_.isObject(field)) parseDates(field);
                });
            }

            if (_.contains(responseInfo()['content-type'], 'application/json')) parseDates(data);
            return data;
        });
    });