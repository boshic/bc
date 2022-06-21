    angular.module('user-service', [])
        .factory('userService',[ '$http', '$q',
            function ($http, $q) {
              let getUser_ = () => {
                return  $http.get('/getUser');
              };
              let user = {};

              return {
                  user,
                  getUser_,
                    getUser : () => {
                        let defer = $q.defer();
                        $http.get('/getUser').then(
                            resp => {
                                resp.data.vendor = 'made by PAB p.vilmen@mail.ru';
                                defer.resolve(paneFactory.user = resp.data);
                            },
                            resp => { defer.reject(resp);}
                        );
                        return defer.promise;
                    }
                };
            }
        ]);
