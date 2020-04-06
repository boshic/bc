import quickFilterTpl from './quick-filter.html';
import commonFilterTpl from './common-filter.html';

    let commonFilterCtrlr = ($s, paneFactory, elem) => {

        $s.allowAllStocks = true;

        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.toggleFilter}]);
        };

        $s.toggleFilter = () => {
            if (!$s.filter.visible)
                paneFactory.changeElementState(document.getElementById('filter-input'), ['focus']);
            $s.filter.visible = !$s.filter.visible;
        };
    };

    angular.module('common-filter', ['text-utils'])
        .directive( "commonFilter", function(){
            return {
                restrict: 'E',
                transclude: true,
                scope: { filter: '=', resetFilter : '&'},
                template: commonFilterTpl,
                // templateUrl: 'html/filter/common-filter.html',
                controller: ($scope, paneFactory, $element) => {
                    return commonFilterCtrlr($scope, paneFactory, $element);
                },
                link: () => {}
            }
        })
        .directive( "quickFilter", () => {
            return {
                restrict: 'E',
                scope: {filter: '='},
                template: quickFilterTpl,
                // templateUrl: 'html/filter/quick-filter.html',
                controller: ($scope, itemFactory) => {
                    $scope.getEmptyBuyer = itemFactory.buyerConfig.getEmptyItem;
                    $scope.getEmptyItem = itemFactory.itemConfig.getEmptyItem;
                    $scope.getEmptySection = itemFactory.sectionConfig.getEmptyItem;
                    $scope.getEmptySupplier = itemFactory.supplierConfig.getEmptyItem;
                    $scope.getEmptyDocument = itemFactory.documentConfig.getEmptyItem;

                },
                link: (scope, elem, attrs) => {}
            }
        })
        .factory('filterFactory',['paneFactory', function (paneFactory) {

                let toDate;
                let defaultRowsOnPage = 15;

                let resetCommonFilterFields = (filter) => {

                    filter.comments = [
                        'Оприходован',
                        'Перемещен',
                        'Изменен',
                        'Удален',
                        'Продажа',
                        'Возврат'
                    ];

                    toDate = new Date();
                    filter.ean = "";
                    filter.sortDirection = 'DESC';
                    filter.hideNullQuantity = true;
                    filter.sectionPart = "";
                    filter.section = {name:""};
                    filter.supplier = {name:""};
                    filter.calcTotal = true;
                    filter.page = 1;
                    filter.searchString = "";
                    filter.rowsOnPage = defaultRowsOnPage;
                    filter.comment = "";
                    filter.document = {name:""};
                    filter.item = {name:""};
                    filter.toDate =  toDate.setHours(23,59,59,999);
                };

                let validate = (filter) => {
                    if(('toDate' in filter) && !(filter.toDate > 0)) {
                        console.log('validatating toDate!');
                        filter.toDate =  toDate.setHours(23,59,59,999);
                    }
                    if(('fromDate' in filter) && !(filter.fromDate > 0)) {
                        console.log('validatating fromDate!');
                        filter.fromDate = angular.isDefined(filter.groupByItems)
                            ? toDate.setHours(0,0,0,0)
                            : filter.fromDate = new Date(toDate.getFullYear(), toDate.getMonth(), 1);
                    }
                    if(('rowsOnPage' in filter) && !(filter.rowsOnPage > 0)) {
                        console.log('validatating rowsOnPage!');
                        filter.rowsOnPage = defaultRowsOnPage;
                    }
                };

                let resetPage = (filter, resetFuction) => {
                    (filter['page'] = 1) ? resetFuction() : filter['page'] = 1;
                };

                let execRowsOnPageFilter = (filter, findItemsByFilter) => {
                    let value = filter['rowsOnPage'];
                    // console.log('execRowsOnPageFilter func: ', value, (value > 0));
                    if(!((value^0) === value))
                        return () => {filter['rowsOnPage'] = (value^0);};
                    return () => {
                        resetPage(filter, findItemsByFilter);
                    };
                };

                let changeEan = (filter, calcTotalsAndRefresh) => {
                    if (filter.ean.length > paneFactory.barcodeLength && filter.ean > 0)
                        filter.ean = filter.ean.substring(paneFactory.barcodeLength);
                    if (filter.ean.length === paneFactory.barcodeLength && filter.ean > 0) {
                        calcTotalsAndRefresh();
                    }
                    filter.calcTotal = true;
                };

                return {
                    resetComingFilter: (filter) => {

                        resetCommonFilterFields(filter);

                        filter.inventoryModeEnabled = false;
                        filter.sortField = 'doc.date';
                        filter.fromDate = new Date(toDate.getFullYear(), toDate.getMonth(), 1);

                    },
                    resetSellingFilter: (filter) => {

                        resetCommonFilterFields(filter);

                        filter.sortField = 'date';
                        filter.user = {};
                        filter.groupByItems = false;
                        filter.buyer = {name:""};
                        filter.fromDate = toDate.setHours(0,0,0,0);
                    },
                    doFilter : (calcTotalsAndRefresh, findItemsByFilter, nv, ov) => {
                        for (let key in nv) {
                            if ((angular.isDefined(ov[key], nv[key])
                                && ((ov[key] != null) && (nv[key] != null))
                                && (!paneFactory.compareValues(nv[key], ov[key])))) {
                                if ((key === 'toDate') || (key === 'fromDate') || (key === 'user')
                                    || (key === 'comment') || (key === 'document') || (key === 'supplier')
                                    || (key === 'sectionPart') || (key === 'hideNullQuantity')
                                    || (key === 'inventoryModeEnabled')
                                    || (key === 'section') || (key === 'item') || (key === 'buyer')
                                    || (key === 'stock') || (key === 'searchString')) {
                                    calcTotalsAndRefresh();
                                }
                                // if((key === 'fromDate') && (!paneFactory.isEanValid(nv['ean'])))
                                //     calcTotalsAndRefresh();
                                if(key === 'page')
                                    (nv['inventoryModeEnabled']) ? calcTotalsAndRefresh() : findItemsByFilter();
                                if((key === 'sortDirection') || (key === 'sortField'))
                                    findItemsByFilter();
                                if((key === 'rowsOnPage'))
                                    execRowsOnPageFilter(nv, findItemsByFilter)();
                                if(key === 'groupByItems')
                                    resetPage(nv, findItemsByFilter);
                                if(key === 'ean')
                                    changeEan(nv, calcTotalsAndRefresh);
                            }
                            else {
                                // console.log('validate value: ', key, nv[key]);
                                validate(nv);
                            }
                        }
                    }
                };
            }
        ]);