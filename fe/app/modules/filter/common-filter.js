import quickFilterTpl from './quick-filter.html';
import commonFilterTpl from './common-filter.html';

    let commonFilterCtrlr = ($s, paneFactory, elem) => {

      $s.invoiceNumberInputId = paneFactory.generateUuid();
      $s.commentInputId = paneFactory.generateUuid();
      $s.sectionPartInputId = paneFactory.generateUuid();
      $s.searchStringInputId = paneFactory.generateUuid();

      $s.selectByInputId = (inputId) => {
        paneFactory.changeElementState(document.getElementById(inputId), ['select']);
      };


        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.toggleFilter}]);
        };

        $s.toggleFilter = () => {
            if (!$s.filter.visible)
                paneFactory.changeElementState(document.getElementById('filter-input'), ['focus']);
            $s.filter.visible = !$s.filter.visible;
        };

        $s.checkGroupSettings = (key) => {
          for(let prop in $s.filter){
            if((prop === 'groupByItems' || prop === 'groupBySections') && (prop !== key)) {
              $s.filter[prop] = false;
            }
          }
        }
    };

    angular.module('common-filter', ['text-utils'])
        .directive( "commonFilter", function(){
            return {
                restrict: 'E',
                transclude: true,
                scope: {
                  filter: '=',
                  stockPickerDisabled: '<?',
                  resetFilter : '&'
                },
                template: commonFilterTpl,
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

                let today;
                let defaultRowsOnPage = 15;

                let resetBasicReleaseFilterFields = (filter) => {

                    filter.sectionPart = "";
                    filter.section = {name:""};
                    filter.supplier = {name:""};
                    filter.searchString = "";
                    filter.document = {name:""};
                    filter.item = {name:""};


                };

                let resetBasicPaneFilterFields = (filter) => {

                    resetBasicReleaseFilterFields(filter);

                    filter.comments = [
                        'Оприходован',
                        'Перемещен',
                        'Изменен',
                        'Удален',
                        'Продажа',
                        'Возврат',
                        'Серия'
                    ];

                    today = new Date();
                    // filter.allowAllStocks = true;
                    if(!angular.isDefined(filter.showInScrollMode))
                      filter.showInScrollMode = paneFactory.isMobileClient();
                    filter.ean = "";
                    filter.sortDirection = 'DESC';
                    filter.hideNullQuantity = true;
                    filter.calcTotal = true;
                    filter.page = 1;
                    filter.rowsOnPage = defaultRowsOnPage;
                    filter.comment = "";
                    filter.strictCommentSearch = false;
                    filter.toDate =  new Date(today).setHours(23,59, 0 ,0 );
                };

                let validate = (filter) => {
                    if(('toDate' in filter) && !(filter.toDate > 0)) {
                        console.log('validatating toDate!');
                        filter.toDate =  new Date(today).setHours(23,59, 0 ,0 );
                    }
                    if(('fromDate' in filter) && !(filter.fromDate > 0)) {
                        console.log('validating fromDate!');
                        filter.fromDate = angular.isDefined(filter.groupByItems)
                            ? today.setHours(0,0, 0 ,0 )
                            : filter.fromDate = new Date(today.getFullYear(), today.getMonth(), 1);
                    }
                    if(('toDate' in filter && 'fromDate' in filter) && (filter.fromDate > filter.toDate > 0)) {
                      console.log('validating fromDate > toDate!');
                      filter.toDate =  new Date(filter.fromDate);
                      filter.toDate.setHours(23,59);
                      // // toDate.setHours(23,59);
                      // filter.toDate = toDate;
                    }
                    if(('rowsOnPage' in filter) && !(filter.rowsOnPage > 0)) {
                        console.log('validating rowsOnPage!');
                        filter.rowsOnPage = defaultRowsOnPage;
                    }
                };

                let resetPage = (filter, resetFunction) => {
                    (filter['page'] = 1) ? resetFunction() : filter['page'] = 1;
                };


                let execRowsOnPageFilter = (filter, findItemsByFilter) => {
                    let value = filter['rowsOnPage'];
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
                  resetBasicPaneFilterFields,
                  resetMovingFilter: (filter) => {

                        filter.alwaysOpenQuantityChangerModal = true;
                        filter.allowUploadingWithBlankFilter = true;
                        resetBasicReleaseFilterFields(filter);
                    },

                  resetSellingPaneFilter: (filter) => {

                    filter.allowUploadingWithBlankFilter = false;
                    filter.alwaysOpenQuantityChangerModal = false;
                    filter.invoiceNumber = 0;
                    resetBasicReleaseFilterFields(filter);
                  },
                    resetComingFilter: (filter) => {

                        resetBasicPaneFilterFields(filter);

                        filter.inventoryModeEnabled = false;
                        filter.sortField = 'doc.date';
                        filter.fromDate = new Date(today.getFullYear(), today.getMonth(), 1);

                    },
                    resetSoldPaneFilter: (filter) => {

                        resetBasicPaneFilterFields(filter);

                        filter.sortField = 'date';
                        filter.user = {};
                        filter.mayBeError = false;
                        filter.groupByItems = false;
                        filter.groupBySections = false;
                        filter.showNotForDeductions = true;
                        filter.buyer = {name:""};
                        filter.compositeItem = {name:""};
                        filter.fromDate = new Date(today).setHours(0,0,0,0);
                    },
                    doFilter : (calcTotalsAndRefresh, findItemsByFilter, nv, ov) => {
                        for (let key in nv) {
                            if ((angular.isDefined(ov[key], nv[key])
                                && ((ov[key] != null) && (nv[key] != null))
                                && (!paneFactory.compareValues(nv[key], ov[key])))) {
                                if ((key === 'toDate')
                                    || (key === 'fromDate')
                                    || (key === 'user')
                                    || (key === 'comment')
                                    || (key === 'document')
                                    || (key === 'supplier')
                                    || (key === 'sectionPart')
                                    || (key === 'strictCommentSearch')
                                    || (key === 'hideNullQuantity')
                                    || (key === 'mayBeError')
                                    || (key === 'showNotForDeductions')
                                    || (key === 'inventoryModeEnabled')
                                    || (key === 'compositeItem')
                                    || (key === 'section')
                                    || (key === 'item')
                                    || (key === 'buyer')
                                    || (key === 'stock')
                                    || (key === 'searchString')) {
                                    calcTotalsAndRefresh();
                                }
                                if(key === 'page')
                                    (nv['inventoryModeEnabled']) ? calcTotalsAndRefresh() : findItemsByFilter();
                                if((key === 'sortDirection') || (key === 'sortField'))
                                    findItemsByFilter();
                                if((key === 'rowsOnPage'))
                                    execRowsOnPageFilter(nv, findItemsByFilter)();
                                if(key === 'groupByItems' || key === 'groupBySections') {
                                  resetPage(nv, findItemsByFilter);
                                }
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