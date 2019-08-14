
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
                    addItemWithRequestCount: (data, url, requestParams, params) => {
                        let defer = $q.defer();
                        requestParams.requestsQuantity += 1;
                        $http.post('/'+url, data, params)
                            .then(
                                resp => {
                                    defer.resolve(resp.data);
                                    requestParams.requestsQuantity -= 1;
                                },
                                resp => {
                                    defer.reject(resp);
                                    requestParams.requestsQuantity -= 1;
                                }
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
                    },
                    getItemByIdWithRequestCount : (id, url, requestParams) => {
                        let defer = $q.defer();
                        requestParams.requestsQuantity += 1;
                        $http.get('/'+url, {params: {id: id}})
                            .then(
                                resp => {
                                    defer.resolve(resp.data);
                                    requestParams.requestsQuantity -= 1;
                                },
                                resp => {
                                    defer.reject(resp);
                                    requestParams.requestsQuantity -= 1;
                                }
                            );
                        return defer.promise;
                    }
                };
            }
        ]);

