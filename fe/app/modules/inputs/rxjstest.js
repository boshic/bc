/**
 * Created by xlinux on 30.09.19.
 */
import 'angular1-async-filter';
import { Subject, of } from 'rxjs';
import { filter, tap, map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

angular.module('rxjstest', ['asyncFilter'])
    .directive('autocomplete', function () {
        return {
            scope: {},
            template: '<input id="rxjstest" ' +
                'ng-model= "term" ng-change="search()"/>' +
            '<ul class="suggestions">' +
            '<li ng-repeat="suggestion in suggestions | async:this track by $index">' +
            '<a href="https://github.com/{{ suggestion }}"' +
            ' target="_blank">{{ suggestion.name }}</a>' +
            '</li>' +
            '</ul>',
            controller: ($scope, httpService) => {
                let searchTerms = new Subject();
                $scope.term = '';
                // $scope.test = false;

                $scope.search = () => {
                    searchTerms.next($scope.term);
                };

                $scope.suggestions = searchTerms
                    .pipe(
                        debounceTime(400),
                        map(keyword => keyword.trim()),
                        // filter(keyword => keyword.length > 0),
                        // filter(() => $scope.test),
                        distinctUntilChanged(),
                        switchMap(keyword =>  keyword.length > 0 ?
                            httpService.getItemsRx({url: 'getItems', params: '?filter=' + keyword}) : of([])),
                        tap((keyword) => console.log(keyword))
                    );
            }
        };
    });

// /**
//  * Created by xlinux on 30.09.19.
//  */
// import 'angular1-async-filter';
// import { Observable, fromEvent } from 'rxjs';
// import { filter, tap, map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
//
// function searchGitHub(term) {
//     // let apiUrl = 'https://api.github.com/search/users?q=';
//     let apiUrl = '/getItems?filter=';
//     return new Observable((observer) => {
//         fetch(apiUrl + term || '')
//             .then(res => res.json())
//             .then(json => {
//                 console.log(json);
//                 observer.next(json);
//                 observer.complete();
//             })
//             .catch(observer.error);
//     });
// }
//
// angular.module('rxjstest', ['asyncFilter'])
//     .directive('autocomplete', function () {
//         return {
//             scope: {},
//             template: '<input id="rxjstest"/>' +
//             '<ul class="suggestions">' +
//                 '<li ng-repeat="suggestion in suggestions | async:this track by $index">' +
//                     '<a href="https://github.com/{{ suggestion }}"' +
//                         ' target="_blank">{{ suggestion.name }}</a>' +
//                 '</li>' +
//             '</ul>',
//             controller: ($scope) => {
//                 $scope.suggestions = ['boshic','5345ge'];
//             },
//             link: function (scope, element) {
//
//
//              scope.suggestions
//                  = fromEvent(document.getElementById('rxjstest'), 'keyup')
//                  .pipe(
//                      map(e => e.target.value),
//                      debounceTime(400),
//                      map(keyword => keyword.trim()),
//                      filter(keyword => keyword.length > 0),
//                      distinctUntilChanged(),
//                      switchMap(keyword => searchGitHub(keyword)),
//                      tap((keyword) => console.log(keyword))
//                 );
//             }
//         };
//     });