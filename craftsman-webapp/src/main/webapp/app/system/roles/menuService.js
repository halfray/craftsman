'use strict';
angular.module('services').factory('menuService', function($resource) {
    return $resource(Main.rootPath + '/api/system/roles/:roleid/menus' , {
        roleid: '@roleid'
    }, {
        'query': {
            method: 'GET',
            isArray: true
        },
        update: {
            method: 'PUT'
        }
    });
});