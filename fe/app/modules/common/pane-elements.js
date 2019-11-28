    angular.module('pane-elements', [])
        .directive("totals", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: { totals: '='},
                template:
                "<div ng-repeat='x in totals' ng-click='switchTotalsView()'>" +
                "<div class='totals-root-div' ng-show='$index == selected'>" +
                "<div class='totals-quantity'>" +
                "<div class='description-on-totals'>{{x.quantDescr}}:</div>" +
                "<div class='value-on-totals'>{{x.quantValue + ' ед.'}}</div>" +
                "</div>" +
                "<div class='totals-sum'>" +
                "<div class='description-on-totals'>{{x.sumDescr}}:</div>" +
                "<div class='value-on-totals'>{{x.sumValue.toFixed(2) + ' руб.'}}</div>" +
                "</div>" +
                "</div>" +
                "</div>",
                controller: ($scope, paneFactory) => {

                    $scope.selected = 0;
                    $scope.totals = [];

                    $scope.switchTotalsView = () => {
                        if(paneFactory.user.role === 'ROLE_ADMIN') {
                            $scope.selected = ($scope.selected === $scope.totals.length - 1) ? 0: $scope.selected +=1;
                        }
                    };
                }
            }
        })
        .directive('myTabs', () => {
            return {
                restrict: 'E',
                transclude: true,
                controller: ['$scope', '$http', function MyTabsController($scope) {
                    let panes = $scope.panes = [];

                    $scope.select = (pane, e) => {
                        angular.forEach(panes, pane => {
                            pane.selected = false;
                        });
                        pane.selected = true;
                        $scope.$broadcast('tabSelected', {
                            pane: pane,
                            event: e
                        });
                    };

                    $scope.openPdf = () => {
                        window.open('/tools/pdfreport');
                    };

                    this.addPane = pane => {
                        if (panes.length === 0)
                            $scope.select(pane, null);
                        panes.push(pane);
                    };
                }],
                template: "<div class='tabbable'>" +
                    "<ul class='nav nav-tabs main-tab-selector'>" +
                        "<li ng-repeat='pane in panes' ng-class='{active:pane.selected}' class='main-pane'>" +
                            "<a href='' class='pane-toggler unprintable' ng-click='select(pane, $event)'>{{pane.name}}</a>" +
                        "</li>" +
                    "</ul>" +
                    "<div ng-transclude></div>" +
                "</div>"
            };
        })
        .directive('myPane', () => {
            return {
                require: '^^myTabs',
                restrict: 'E',
                transclude: true,
                scope: { name: '@' },
                link: (scope, element, attrs, tabsCtrl) => {
                    tabsCtrl.addPane(scope);
                },
                template:
                "<div class='tab-pane' ng-show='selected' ng-class='{selectedpane: selected}'>"+
                    "<div class='transcluded-pane' ng-transclude></div>" +
                "</div>"
            };
        })
        .component( "paneRequestIndicator", {
            bindings: {
                requestsQuantity: '<'
            },
            template:
            "<span class='glyphicon glyphicon-warning-sign pane-request-indicator'" +
                "ng-hide='$ctrl.requestsQuantity === 0'>" +
            "</span>",
            controller: function() {}
        })
        .component( "paneShader", {
            bindings: {
                active: '<'
            },
            template:
            "<div ng-show='$ctrl.active' class='trans-layer'></div>",
            controller: function() {}
        })
        .component( "onFlyCalcTotals", {
            bindings: {
                totals: '<',
                descr: '@'
            },
            template:
                "<span style='margin-right: 10%;'>{{($ctrl.totals.quantity.toFixed(3)) + ' ед.'}}</span>" +
                "<span style='color: yellow;'>{{($ctrl.totals.sum).toFixed(2) + ' руб.'}}</span>" +
                "<div class='totals-type'>{{$ctrl.descr}}</div>",
            controller: function() {}
        })
        .component( "paneQuantityInput", {
            bindings: {
                inputId: '@?',
                readOnly: '<?',
                inputValue: '=',
                valueField: '@',
                changeValue: '&?'
            },
            template:
            // selling-quantity
            "<td class='hoverable'>" +
                "<input type='number' class='form-control common-number' " +
                "ng-class='{\"warning-input\": $ctrl.inputValue[$ctrl.valueField] == 0 || \"\"}'" +
                    "id={{$ctrl.inputId}} ng-readonly='$ctrl.readOnly' string-to-number " +
                "ng-model='$ctrl.inputValue[$ctrl.valueField]' ng-change='$ctrl.changeValue()'/>" +
            "</td>",
            controller: function() {}
        })
        .component( "sortingRow", {
                bindings: {
                    listeningField: '<',
                    field: '<',
                    sortDirection: '='
                },
                template:
                "<button class='glyphicon glyphicon-arrow-up sorting-row' " +
                    "ng-show='$ctrl.sortDirection===\"ASC\" && $ctrl.field === $ctrl.listeningField'" +
                    "ng-click = '$ctrl.sortDirection=\"DESC\"'/>" +
                "<button class='glyphicon glyphicon-arrow-down sorting-row' " +
                    "ng-show='$ctrl.sortDirection===\"DESC\" && $ctrl.field === $ctrl.listeningField' " +
                    "ng-click = '$ctrl.sortDirection=\"ASC\"'/>",
                controller: function() {}
            });
