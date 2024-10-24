import { Observable } from 'rxjs';

angular.module('common-http-service', [])
        .factory('httpService',['$http', '$q',
            function ($http, $q) {

                // let tpUrl = "http://localhost:3335/";
              // let tpUrl = "http://192.168.100.50:3335/";
              let tpUrl = "http://192.168.43.71:3335/";

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

              let incompatableReqSymbols = ['%'];
              let checkReqForIncompatableSymbols = (request) => {
                if(typeof request === 'string') {
                  incompatableReqSymbols.forEach(symbol => {
                    let pos = request.indexOf(symbol);
                    while (pos > -1) {
                      request = request.replace(symbol, "");
                      pos = request.indexOf(symbol);
                    }
                  });
                }
                return request;
              };


                return {
                    getItemsRx: (opts) => {
                        addRequestsQuantity(opts.requestParams);
                        return new Observable((observer) => {
                            $http.get('/'+ opts.url + checkReqForIncompatableSymbols(opts.params))
                              .then(resp => {
                                observer.next(resp.data);
                                decreaseRequestsQuantity(opts.requestParams);
                                observer.complete();
                              })
                              .catch(eroror => {
                                decreaseRequestsQuantity(opts.requestParams);
                                observer.error();
                              });
                        });
                    },
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
                    posttp: (opts) => {
                      let defer = $q.defer();
                      addRequestsQuantity(opts.requestParams);
                      $http({
                        url: tpUrl + opts.url,
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        method: "POST",
                        data: opts.data
                      })
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

                  getItemsTp: (opts) => {
                    let defer = $q.defer();
                    addRequestsQuantity(opts.requestParams);
                    $http.get(tpUrl+ opts.url, {params: opts.params})
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
                    getItemsByFilterRx: (opts) => {
                        addRequestsQuantity(opts.requestParams);
                        return new Observable((observer) => {
                            $http.post('/'+ opts.url, opts.filter)
                                .then(resp => {
                                    observer.next(resp.data);
                                    decreaseRequestsQuantity(opts.requestParams);
                                    observer.complete();
                                })
                                .catch(eroror => {
                                    decreaseRequestsQuantity(opts.requestParams);
                                    observer.error();
                                });
                        });
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

