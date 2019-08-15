    angular.module('common-http-service', [])
        .factory('httpService',['$http', '$q',
            function ($http, $q) {

                let checkRequestParams = (requestParams) => {
                    return angular.isObject(requestParams);
                };

                let checkRequestsQuantity = (requestParams) => {
                    return (checkRequestParams(requestParams)
                        && angular.isDefined(requestParams.requestsQuantity)
                        && requestParams.requestsQuantity != null
                        && typeof requestParams.requestsQuantity === 'number')
                };

                let addRequestsQuantity = (requestParams) => {
                    if(checkRequestsQuantity(requestParams))
                        requestParams.requestsQuantity += 1;
                };

                let decreaseRequestsQuantity = (requestParams) => {
                    if(checkRequestsQuantity(requestParams))
                        requestParams.requestsQuantity -= 1;
                };

                let successHandle = (defer, resp, opts) => {
                    defer.resolve(resp.data);
                    decreaseRequestsQuantity(opts.requestParams);
                };

                let errorHandle = (defer, resp, opts) => {
                    defer.reject(resp.data);
                    decreaseRequestsQuantity(opts.requestParams);
                };

                return {
                    addItem: (opts) => {
                        let defer = $q.defer();
                        addRequestsQuantity(opts.requestParams);
                        $http.post('/'+ opts.url, opts.data, opts.params)
                            .then(
                                resp => {
                                    successHandle(defer, resp, opts);
                                },
                                resp => {
                                    errorHandle(defer, resp, opts);
                                }
                            );
                        return defer.promise;
                    },
                    getItems: (opts) => {
                        let defer = $q.defer();
                        addRequestsQuantity(opts.requestParams);
                        $http.get('/'+ opts.url, {params: opts.params})
                            .then(
                                resp => {
                                    successHandle(defer, resp, opts);
                                },
                                resp => {
                                    errorHandle(defer, resp, opts);
                                }
                            );
                        return defer.promise;
                    },

                    getItemsByFilter: (opts) => {
                        let defer = $q.defer();
                        addRequestsQuantity(opts.requestParams);
                        $http.post('/'+opts.url, opts.filter)
                            .then(
                                resp => {
                                    successHandle(defer, resp, opts);
                                },
                                resp => {
                                    errorHandle(defer, resp, opts);
                                }
                            );
                        return defer.promise;
                    },
                    getItemById : (opts) => {
                        let defer = $q.defer();
                        addRequestsQuantity(opts.requestParams);
                        $http.get('/'+opts.url, {params: {id: opts.id}}).then(
                                resp => {
                                    successHandle(defer, resp, opts);
                                },
                                resp => {
                                    errorHandle(defer, resp, opts);
                                }
                            );
                        return defer.promise;
                    }
                };
            }
        ]);

