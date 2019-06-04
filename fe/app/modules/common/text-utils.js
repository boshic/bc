/**
 * Created by xlinux on 05.02.19.
 */
// define(['angular'], angular => {

    let phraseByWordToFilterCtrlr = ($s) => {

        $s.setFilter = function () {
            $s.filter = ($s.filter === '') ? this.word : $s.filter + ' ' + this.word;
        };
    };

    let wordFromPhraseEraserCtrlr = ($s) => {

        $s.deleteWord = function () {
            let words = $s.phrase.split(' ');
            if(words.indexOf(this.word) != -1) {
                words.splice(words.indexOf(this.word), 1);
                $s.phrase = words.join(' ');
            }
        };

    };

    angular.module('text-utils', [])
        .directive( "eraser", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=data'},
                template:"<span class='glyphicon glyphicon-remove eraser-icon' ng-click='erase()'></span>",
                // templateUrl: '/html/common/eraser.html',
                controller: $scope => {
                    $scope.erase = () => {
                        $scope.data ="";
                    };
                },
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "phraseByWordToFilter", () => {
            return {
                restrict: 'E',
                scope: {
                    comment:'=comment',
                    filter: '=filter'
                },
                template:
                "<li style='border-bottom: 1px lightgray solid;'" +
                    // "ng-repeat=\"cmnt in comment.split('; ') | orderBy:'+':'true' track by $index\">" +
                "ng-repeat=\"cmnt in comment.split('; ') track by $index\">" +
                "<span class=\"price-out-on-coming-pane comment-row\""+
                "ng-repeat=\"word in cmnt.split(' ') track by $index\" " +
                "ng-click=\"setFilter()\"> " +
                "{{word}}" +
                "</span>" +
                "</li>",
                controller: ($scope) => {
                    return phraseByWordToFilterCtrlr($scope);
                },
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "wordFromPhraseEraser", () => {
            return {
                restrict: 'E',
                scope: {
                    phrase:'=phrase',
                    title: '@?title'
                },
                template:
                "<li style='border-bottom: 1px lightgray solid;'>" +
                "<div class='word-from-phrase-eraser-title'>{{title}}</div>"+
                "<div class=\"hoverable\""+
                "ng-repeat=\"word in phrase.split(' ') track by $index\" " +
                "ng-click=\"deleteWord()\"> " +
                "{{word}}" +
                "<span class='glyphicon glyphicon-remove'></span>" +
                "</div>" +
                "</li>",
                controller: ($scope) => {
                    return wordFromPhraseEraserCtrlr($scope);
                },
                link: (scope, elem, attrs) => {}
            }
        })
        .directive('stringToNumber', () => {
            return {
                require: 'ngModel',
                link: (scope, element, attrs, ngModel) => {
                    ngModel.$parsers.push(value => '' + value);
                    ngModel.$formatters.push(value => parseFloat(value));
                }
            };
        })
        .directive('dateInput', () => {
            return {
                restrict: 'A',
                require: 'ngModel',
                link: (scope, element, attrs, ngModel) => {
                    ngModel.$formatters.push(value => new Date(value));
                }
            };
        })
        .directive('dateInputDdmmyyy', () => {
            return {
                restrict: 'A',
                require: 'ngModel',
                link: (scope, element, attrs, ngModel) => {
                    ngModel.$parsers.push( value => {
                        if (value) {
                            return value.toLocaleDateString();
                        } else {
                            return new Date().toLocaleDateString();
                        }
                    });
                    ngModel.$formatters.push(value => {
                        if (value)  {
                            let parts = value.split(".");
                            return new Date(parts[2], parts[1]-1, parts[0]);
                        } else  {
                            return new Date();
                        }

                    });
                }
            };
        })
        .directive("unitSensitive", () => {
            return {
                restrict: 'A',
                scope : { unit: '=unit', quantity: '=quantity'},
                template: "{{ checkUnits(unit) ? quantity.toFixed(0) : quantity.toFixed(3)}}",
                controller: ($scope, paneFactory) => {
                    $scope.checkUnits = (value) => {
                        return (paneFactory.fractionalUnits.indexOf(value) < 0);
                    };
                }
            };
        });

// });