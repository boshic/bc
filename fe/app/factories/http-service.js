define(['angular'], angular => {
    angular.module('common-http-service', [])
        .factory('httpService',['$http', '$q',
            function ($http, $q) {

                return {
                    addItem: (data, url, params) => {
                        let defer = $q.defer();
                        $http.post('/'+url, data, params)
                            .then(
                                resp => {defer.resolve(resp.data);},
                                resp => {defer.reject(resp);}
                            );
                        return defer.promise;
                    },
                    deleteItemById : (id, url) => {
                        let defer = $q.defer();
                        $http.get('/'+url, { params: { id: id }})
                            .then(
                                resp => {defer.resolve(resp.data);},
                                resp => {defer.reject(resp);}
                            );
                        return defer.promise;
                    },
                    getItems: (params, url) => {
                        let defer = $q.defer();
                        $http.get('/'+url, {params: params})
                            .then(
                                resp => {defer.resolve(resp.data);},
                                resp => {defer.reject(resp);}
                            );
                        return defer.promise;
                    },

                    getItemsByFilter : (filter, url) => {
                        let defer = $q.defer();
                        $http.post('/'+url, filter)
                            .then(
                                resp => {defer.resolve(resp.data);},
                                resp => {defer.reject(resp);}
                            );
                        return defer.promise;
                    },
                    getItemById : (id, url) => {
                        let defer = $q.defer();
                        $http.get('/'+url, {params: {id: id}})
                            .then(
                                resp => {defer.resolve(resp.data);},
                                resp => {defer.reject(resp);}
                            );
                        return defer.promise;
                    }
                };
            }
        ]);

        // .service('pendingRequests', function() {
        //     var pending = [];
        //     this.get = function() {
        //         return pending;
        //     };
        //     this.add = function(request) {
        //         pending.push(request);
        //     };
        //     this.remove = function(request) {
        //         pending = _.filter(pending, function(p) {
        //             return p.url !== request;
        //         });
        //     };
        //     this.cancelAll = function() {
        //         angular.forEach(pending, function(p) {
        //             p.canceller.resolve();
        //         });
        //         pending.length = 0;
        //     };
        // })
        // // This service wraps $http to make sure pending requests are tracked
        // .service('httpService1', ['$http', '$q', 'pendingRequests', function($http, $q, pendingRequests) {
        //     this.get = function(url,data) {
        //         var canceller = $q.defer();
        //         pendingRequests.add({
        //             url: url,
        //             canceller: canceller
        //         });
        //         //Request gets cancelled if the timeout-promise is resolved
        //         var requestPromise = $http.get(url, { timeout: canceller.promise });
        //         //Once a request has failed or succeeded, remove it from the pending list
        //         requestPromise.finally(function() {
        //             pendingRequests.remove(url);
        //         });
        //         return requestPromise;
        //     }
        // }]);

    // .config(function ($provide, $httpProvider) {
    //
    //         $provide.factory('myHttpInterceptor', function($q, $location) {
    //             let canceller = $q.defer();
    //             return {
    //                 // optional method
    //                 'request': function(config) {
    //                     // do something on success
    //                     // config.timeout = canceller.promise;
    //                     return config;
    //                 },
    //
    //                 // optional method
    //                 'requestError': function(rejection) {
    //                     // do something on error
    //                     if (canRecover(rejection)) {
    //                         return responseOrNewPromise
    //                     }
    //                     console.log('working!');
    //                     return $q.reject(rejection);
    //                 },
    //
    //
    //                 // optional method
    //                 'response': function(response) {
    //                     // do something on success
    //                     return response;
    //                 },
    //
    //                 // optional method
    //                 'responseError': function(rejection) {
    //                     // do something on error
    //                     // if (canRecover(rejection)) {
    //                     //     return responseOrNewPromise
    //                     // }
    //                     if (rejection.status === 401) {
    //                         canceller.resolve('Unauthorized');
    //                         $location.url('/mainPage');
    //                     }
    //                     if (rejection.status === 403) {
    //                         canceller.resolve('Forbidden');
    //                         $location.url('/');
    //                     }
    //                     if (rejection.status === -1) {
    //                         canceller.resolve('network error');
    //                         $location.url('/');
    //                     }
    //                     return $q.reject(rejection);
    //
    //                 }
    //             };
    //         });
    //
    //         $httpProvider.interceptors.push('myHttpInterceptor');
    //
    //     }
    // );
});
