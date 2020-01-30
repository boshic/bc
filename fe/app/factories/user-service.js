    angular.module('user-service', [])
        .factory('userService',[ '$http', '$q', 'paneFactory',
            function ($http, $q, paneFactory) {
                return {
                    getUser : () => {
                        let defer = $q.defer();
                        $http.get('/getUser').then(
                            resp => {
                                paneFactory.user = resp.data;
                                paneFactory.user.vendor = 'made by PAB p.vilmen@mail.ru';
                                defer.resolve(paneFactory.user);
                            },
                            resp => { defer.reject(resp);}
                        );
                        return defer.promise;
                    }
                };
            }
        ]);
