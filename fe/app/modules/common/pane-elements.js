import comingPaneTpl from '../coming/coming-pane.html';
import soldPaneTpl from '../selling/sold-pane.html';
import invoicesPaneTpl from '../selling/invoices-pane.html';
import itemRowOnPanesTpl from './item-row-on-panes.html';
import commonPaneCtrlr from '../../controllers/common-pane-ctrlr';


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
                controller: ['$scope', '$rootScope', '$http', function MyTabsController($scope) {
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
                scope: { name: '@', paneId : '@?' },
                link: (scope, element, attrs, tabsCtrl) => {
                    tabsCtrl.addPane(scope);
                },
                template:
                "<div class='tab-pane' ng-show='selected' ng-class='{selectedpane: selected}'>"+
                    "<div class='transcluded-pane' ng-transclude></div>" +
                "</div>"
            };
        })
        .directive( "comingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: comingPaneTpl,
                controller: ($scope, itemFactory, filterFactory, paneFactory, printFactory, modalFactory ) => {

                    return commonPaneCtrlr($scope,  itemFactory, filterFactory, paneFactory, printFactory, modalFactory, 'comingPaneConfig');
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        })
        .directive( "soldPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: soldPaneTpl,
                controller: ($scope,  itemFactory, filterFactory, paneFactory, printFactory, modalFactory) => {
                    return commonPaneCtrlr($scope,  itemFactory, filterFactory, paneFactory, printFactory, modalFactory, 'soldPaneConfig');
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        })
        .directive( "invoicesPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: invoicesPaneTpl,
                controller: ($scope,  itemFactory, filterFactory, paneFactory, printFactory, modalFactory) => {
                    return commonPaneCtrlr($scope,  itemFactory, filterFactory, paneFactory, printFactory, modalFactory, 'invoicesPaneConfig');
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
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
        .component( "inventoryPanel", {
            bindings: {
                buyer: '=',
                isEnabledApplyInventoryResults: '<',
                isEnabledSetInventoryValues: '<',
                setInventoryValues: '&',
                applyInventoryResults: '&',
                setInventoryValuesToZeroes: '&?',
                autoOpenQuantityChangerModal: '=',
            },
            template:
                "<div style='display: flex;'>" +
                    "<div style='width: 40%; padding-top: 9px; margin-left: 4px;'>" +
                        "<buyer-input buyer='$ctrl.buyer'></buyer-input>" +
                    "</div>"+
                    "<div style='width: 60%'>" +
                        "<button class='inventory-button apply-inventory-results'" +
                            "ng-disabled='!$ctrl.isEnabledApplyInventoryResults'" +
                            "ng-class='{\"disabled-inventory-btn\": !$ctrl.isEnabledApplyInventoryResults}'" +
                            "ng-click = '$ctrl.applyInventoryResults()'>" +
                                "Применить результаты инвентаризации =(?" +
                        "</button>" +
                        "<button class='inventory-button set-invetnory-values-to-zeroes'" +
                            "ng-click = '$ctrl.setInventoryValuesToZeroes()'>" +
                                "Занулить факт" +
                        "</button>" +
                        "<button class='inventory-button set-invetnory-values'" +
                            "ng-disabled='!$ctrl.isEnabledSetInventoryValues'" +
                            "ng-class='{\"disabled-inventory-btn\": !$ctrl.isEnabledSetInventoryValues}'" +
                            "ng-click = '$ctrl.setInventoryValues()'>" +
                                "Записать остатки" +
                        "</button>" +
                        "<div>" +
                            "<input class='inventory-checkbox'" +
                                "type='checkbox' ng-model='$ctrl.autoOpenQuantityChangerModal'/>" +
                            "<label>" +
                                "Автом. открытие окна ввода количества: {{($ctrl.autoOpenQuantityChangerModal()) ? 'Да' : 'Нет'}}" +
                            "</label>" +
                        "</div>" +
                    "</div>" +
                "</div>",
            controller: function() {
                this.autoOpenQuantityChangerModal = false;
            }
        })
        .component( "paneComment", {
            bindings: {
                comment: '=',
                filter: '=',
                row: '=?',
                openTextModal: '&?'
            },
            template:
            "<div class='dropdown'>" +
                "<span class='glyphicon glyphicon-th-list'></span>" +
                    "<ul class='dropdown-menu hoverable comments-container left-side'" +
                        "ng-show='$ctrl.comment.length > 0'>" +
                        "<li style='text-align: center' ng-show='$ctrl.openTextModal != null'>" +
                            "<button class='glyphicon glyphicon-plus add-comment-btn'" +
                                "ng-click='$ctrl.openTextModal()'>" +
                            "</button>" +
                        "</li>" +
                        "<phrase-by-word-to-filter filter='$ctrl.filter.comment' comment='$ctrl.comment'></phrase-by-word-to-filter>" +
                    "</ul>" +
                    "<ul class='dropdown-menu hoverable comments-container left-side'" +
                        "ng-show='$ctrl.comment.length == 0'>" +
                        "<li>" +
                            "<button>Загрузить комментарии</button>" +
                        "</li>" +
                    "</ul>" +
            "</div>",
            controller: function() {}
        })
        .component( "currentStockIndicator", {
        bindings: { stock: '='},
        template: "<div style='display: flex;'>" +
            "<div style='width: 45%;'></div>" +
            "<div class='current-stock-indicator'>{{'Склад: ' + $ctrl.stock.name}}</div>" +
            "</div>",
        controller: function() {}
    })
        .component( "paneSearchInput", {
            bindings: {
                inputId: '<',
                inputValue: '=',
                keypressHandler:'&?'
            },
            template:
            "<input class='searchinput' type='text' " +
                "id={{$ctrl.inputId}} " +
                "placeholder='Введите что-нибудь ...' " +
                "ng-model='$ctrl.inputValue' " +
                "ng-keydown='$ctrl.keypressHandler()($event)'/>",
            controller: function() {}
        })
        .component( "itemRowOnPanes", {
        bindings: {
            row: '=',
            filter: '=',
            editItem:'&?'
        },
        template: itemRowOnPanesTpl,
        controller: function() {}
    })
        .component( "paneDateChangeControl", {
        bindings: {
            user: '<',
            row: '=',
            changeItemDate : '&',
            requestsQuantity:'<'
        },
        template:
        "<span style='position: absolute; cursor: pointer;'" +
            "ng-if='(($ctrl.user.role == \"ROLE_ADMIN\" || $ctrl.user.actsAllowed.indexOf(allowedActRequired) > -1) " +
                "&& (($ctrl.row.quantity > 0) || (!$ctrl.row.deleted)))'" +
            "ng-show = '$ctrl.requestsQuantity === 0'>" +
            "<div class='dropdown'>" +
                "<span class='glyphicon glyphicon-pencil'></span>" +
                    "<ul class='dropdown-menu hoverable comments-container sip-date-changer'>" +
                        "<li>" +
                            "<input date-input class='form-control' type='datetime-local' ng-model='$ctrl.row.date'/>" +
                            "<div>" +
                                "<button class='glyphicon glyphicon-ok sip-date-changer-ok-btn'" +
                                    "ng-click='$ctrl.changeItemDate()($ctrl.row)'" +
                                    "title='Изменить дату!'/>" +
                            "</div>" +
                        "</li>" +
                    "</ul>" +
            "</div>" +
        "</span>",
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
