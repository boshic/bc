import userPickerTpl from './user-picker.html';
import pagePickerTpl from './page-picker.html';
import bankInputTpl from './bank-input.html';
import addEditBankTpl from './add-edit-bank.html';
import buyerInputTpl from './buyer-input.html';
import addEditBuyerTpl from './add-edit-buyer.html';
import docInputTpl from './doc-input.html';
import itemInputTpl from './item-input.html';
import addEditItemTpl from './add-edit-item.html';
import itemSectionInputTpl from './item-section-input.html';
import addEditItemSectionTpl from './add-edit-item-section.html';
import supplierInputTpl from './supplier-input.html';

let commonLinkFunction = (scope, elem, attrs, parentCtrl) => {
    parentCtrl.getAddEditData(scope, elem);
    scope.setItem = parentCtrl.setItem;
};

let commonInputCtrlr = ($s, itemFactory, itemConfig) => {

    $s.inputId = itemFactory.generateUuid();
    $s.items = [];
    let config = itemFactory[itemConfig]();
    $s.emptyItem = config.getEmptyItem;
    $s.item = $s.emptyItem();

    $s.$watch('item.id', (nv) => {
        if(nv)
            $s.getItems();
    });

    $s.getItems = () => {
        itemFactory.getItems($s, config.getItemsUrl);
    };

    $s.selectItem = (id) => {
        itemFactory.selectItem(id, config.getItemByIdUrl, $s);
    };

    $s.changeItem = (id) => {
        itemFactory.changeItem(id, $s);
    };

    $s.clearItem =  () => {
        itemFactory.clearItem($s);
    };
};

let bankInputCtrlr = ($s, itemFactory) => {

    return commonInputCtrlr($s, itemFactory, 'bankConfig');
};

let buyerInputCntrlr = ($s, itemFactory) => {

    return commonInputCtrlr($s, itemFactory, 'buyerConfig');
};

let itemSectionCtrlr = ($s, httpService, paneFactory, itemFactory, ctrlr) => {

    $s.inputId = paneFactory.generateUuid();
    $s.items = [];
    let emptyItem = paneFactory.emptySection;
    $s.item = emptyItem();


    $s.$watch('item.name', (nv, ov) => {
        if ((nv) || (ov))
            $s.getItems();
    }, true);


    ctrlr.getAddEditData = (scope) => {
        $s.addEditScope = scope;
    };

    ctrlr.setItem = $s.setItem = item => {
        $s.item = (!item || item === null ) ? emptyItem() : angular.extend({}, item);
    };

    $s.blankSearchAndGetItemsBy = () => {
        if(!angular.isDefined($s.item) || $s.item === null)
            $s.setItem(null);
        $s.item.name ==="" ? $s.getItems() : $s.setItem(null);
        paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
    };


    $s.getItems = () => {
        itemFactory.getItems($s, 'getSections');
    };

    $s.addEditItem = (item) => {
        return itemFactory.addEditItem(item, $s.addEditScope);
    };

    $s.selectItem = function() {
        $s.setItem(this.x);
    };

};

let itemInputCtrlr = ($s, httpService, paneFactory, itemFactory, ctrlr) => {

    $s.inputId = paneFactory.generateUuid();
    $s.items = [];
    let emptyItem = paneFactory.emptyItem;
    $s.item = emptyItem();


    $s.$watch('item.name', (nv, ov) => {
        if ((nv) || (ov))
            $s.getItems();
    }, true);


    ctrlr.getAddEditData = (scope) => {
        $s.addEditScope = scope;
    };

    ctrlr.setItem = $s.setItem = item => {
        $s.item = (!item || item === null ) ? emptyItem() : angular.extend({}, item);
    };

    $s.blankSearchAndGetItemsBy = () => {
        if(!angular.isDefined($s.item) || $s.item === null)
            $s.setItem(null);
        $s.item.name ==="" ? $s.getItems() : $s.setItem(null);
        paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
    };

    $s.getItems = () => {
        if($s.item.name.length >= 2)
            itemFactory.getItems($s, 'getItems');
    };

    $s.setEanPrefix = e => {
        $s.item.name = paneFactory.generateEanByKey(e, $s.item.name);
    };

    $s.addEditItem = (item) => {
        return itemFactory.addEditItem(item, $s.addEditScope);
    };

    $s.selectItem = function() {
        $s.setItem(this.x);
    };

};

let docCtrlr = ($s, httpService, paneFactory) => {

    $s.dateFrom = new Date(2015,0,1);
    $s.dateTo = new Date();
    $s.docs = [];
    $s.emptyItem = paneFactory.emptyDocument;
    $s.doc = $s.emptyItem();

    $s.newDoc = angular.extend($s.emptyItem(), {date: new Date().toLocaleDateString(), supplier : paneFactory.emptySupplier()});

    $s.docsListVisible = false;
    $s.datePickerVisible = false;
    $s.warning = true;
    $s.addingVisible = false;
    $s.canChange = false;

    $s.$watch('newDoc.supplier', () => {
        $s.checkDoc();
    });

    $s.$watch('doc',  () => {
        $s.warning = (!(($s.doc) && ("id" in $s.doc)));
    });

    $s.checkDoc = () => {
        (($s.newDoc.date) && ($s.newDoc.name) && ($s.newDoc.supplier.name)) ?
            $s.canChange = true : $s.canChange = false;
    };

    $s.showDocsList = () => {
        $s.datePickerVisible = false;
        $s.docsListVisible = !$s.docsListVisible;
        if($s.docsListVisible)
            $s.getDocs();
        $s.addingVisible = false;
    };

    $s.showDatePicker = function () {
        $s.datePickerVisible = (!$s.datePickerVisible);
        $s.docsListVisible = false;
    };

    $s.appendData = () => {
        $s.addingVisible = false;
        httpService.addItem( $s.newDoc, 'addDocument')
            .then(
                resp => {
                    (resp.item == null) ? $s.doc.name = resp.text : $s.doc = resp.item;
                    $s.showDocsList();
                },
                resp => {
                    console.log("ошибка при добавлении документа!");
                }
            );
    };

    $s.getDocs = () => {
        httpService.getItemsByFilter({
                searchString: $s.doc.name,
                fromDate: $s.dateFrom,
                toDate: $s.dateTo
            }, 'getDocs'
        ).then(
            resp => {
                $s.docs = resp;
            },
            resp => {
                console.log('список документов пуст!');
            }
        );
    };

    $s.addEditDoc = function() {
        $s.addingVisible = true;
        if (this.$index >= 0)
            $s.newDoc = this.x;
        else
            $s.newDoc = angular.extend($s.emptyItem(), {date: new Date(), supplier : paneFactory.emptySupplier()});
        $s.checkDoc();
    };

    $s.blankNameAndGetItemsBy = () => {
        $s.doc = $s.emptyItem();
        $s.warning = true;
        $s.getDocs();
    };

    $s.selectDoc = function () {
        $s.doc = this.x;
        $s.warning = false;
        $s.showDocsList();
    };

};

let addEditItemCtrlr = ($s, httpService, paneFactory) => {

    let emptyItem = (text) => { return {name: text, ean: ''}};
    $s.modalHidden = true;
    $s.item = {};
    $s.inputId = paneFactory.generateUuid();

    $s.closeModal = () => {
        $s.modalHidden = true;
    };

    $s.appendData = () => {
        httpService.addItem($s.item, 'addItem').then(
            resp => {
                if(resp.success) {
                    $s.closeModal();
                    $s.setItem(resp.item || emptyItem(resp.text));
                } else
                    $s.item = resp.item || emptyItem(resp.text);
            },
            resp => { $s.item.name = resp.text;}
        );
    };

    $s.getNextId = () => {
        if('id' in $s.item)
            $s.item.ean = paneFactory.generateEan($s.item.id.toString());
        else
            httpService.getItemById(null, 'getTopId').then(
                resp => {
                    $s.item.ean = paneFactory.generateEan((resp + 1).toString());
                },
                resp => {console.log(resp);}
            );
    };

    $s.setEanPrefix = (e, field) => {
        $s.item[field] = paneFactory.generateEanByKey(e, $s.item[field]);
    };

    $s.getItemById = (id) => {
        httpService.getItemById(id, 'getItemById').then(
            value => {$s.item = value;}
        );
    };
};

let stockCntrlr = ($s, httpService) => {

    $s.stocks = [];
    $s.stockListVisible = false;

    let reSelectStock = () => {
        for (let stock of $s.stocks)
            (stock.id === $s.stock.id) ? stock.selected = true : stock.selected = false;
    };

    $s.toggleStockList = () => {
        $s.stockListVisible = !$s.stockListVisible;
    };

    $s.$watch('stock', (nv, ov) => {
        if ((nv) && (ov)) {
            reSelectStock();
        }
    });

    $s.getStocks = () => {

        httpService.getItems({allowAll: $s.allowAll}, 'getStocks').then(
            resp => {
                $s.stocks = resp;
                for (let stock of $s.stocks)
                    if (stock.selected)
                        $s.stock = stock;
            },
            () => { console.log('Список складов пуст');}
        );
    };

    $s.clickOnStock = function (stock) {
        $s.stock = stock;
        $s.toggleStockList();
    };

};

let supplierInputCntrlr = ($s, $http, paneFactory, itemFactory, ctrlr) => {

    $s.inputId = paneFactory.generateUuid();
    $s.items = [];
    let emptyItem = paneFactory.emptyItem;
    $s.item = emptyItem();

    $s.$watch('item.name', (nv, ov) => {
        if ((ov) || (nv))
            $s.getItems();
    }, true);

    ctrlr.getChildScope = (scope) => {
        $s.addEditScope = scope;
    };

    ctrlr.getItems = $s.getItems = () => {
        $http.get('/getSuppliers', {
            params: { filter: $s.item.name }
        }).then(
            resp => { $s.items = resp.data; },
            () => { console.log('список поставщиков пуст!'); }
        );
    };

    $s.selectItem = function() {
        $s.item = this.x;
    };

    $s.addEditItem = (item) => {
        return itemFactory.addEditItem(item, $s.addEditScope);
    };

    $s.blankSearchAndGetItemsBy = function() {
        $s.item.name === "" ? $s.getItems() : $s.item = emptyItem();
        paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
    };
};

angular.module('inputs', [])
    .directive( "bankInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=bank' },
            template: bankInputTpl,
            controller: function ($scope, itemFactory) {
                return bankInputCtrlr($scope, itemFactory);
            }
        }
    })
    .directive( "addEditBank", () => {
        return {
            restrict: 'E',
            scope: { item: "=bank", modalVisible: "="},
            template: addEditBankTpl,
            controller:([ '$scope', 'httpService',
                function ($scope, httpService) {
                    $scope.warning ="";

                    $scope.closeModal = () => {
                        $scope.modalVisible = false;
                        $scope.warning ="";
                    };

                    $scope.appendData = () => {
                        httpService.addItem($scope.item, 'addBank').then(
                            resp => {
                                (resp.success) ? $scope.closeModal() : $scope.warning = resp.text;
                            },
                            resp => { $scope.item.name = resp; }
                        );
                    };
                }
            ])        }
    })
    .directive( "buyerInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item: '=buyer', items: '=?buyers'},
            template: buyerInputTpl,
            controller: ($scope, itemFactory) => {
                return buyerInputCntrlr($scope, itemFactory);
            }
        }
    })
    .directive( "addEditBuyer", () => {
        return {
            restrict: 'E',
            scope: { item: "=buyer", user: "=?", modalVisible: "=", getItems: '&?'},
            template: addEditBuyerTpl,
            controller:([ '$scope', 'httpService',
                function ($scope, httpService) {
                    $scope.warning ="";
                    $scope.item = {};

                    $scope.closeModal = () => {
                        $scope.modalVisible = false;
                        $scope.warning ="";
                    };

                    $scope.appendData = () => {
                        httpService.addItem($scope.item, 'addBuyer').then(
                            resp => {
                                if(resp.success) {
                                    $scope.closeModal();
                                    $scope.getItems();
                                }
                                else
                                    $scope.warning = resp.text;
                            },
                            resp => { $scope.item.name = resp; }
                        );
                    };
                }
            ])
        }
    })
    .directive( "commentInput", () => {
        return {
            restrict: 'E',
            scope: {
                comment:'=ngModel'
            },
            template:
            "<div class='comment-container'>" +
            "<textarea class='form-control comment-on-selling-pane' type='text' " +
                "placeholder='Комментарий' id='comment-input'" +
                "ng-model='comment'>" +
            "</textarea>" +
            "<eraser style='margin-top: 5px;' data='comment'></eraser>" +
            "</div>",
            // templateUrl:'html/comment/comment-input.html',
            controller: ($scope) => {},
            link: (scope, elem, attrs) => {}
        }
    })
    .directive( "docInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: {doc:'='},
            template : docInputTpl,
            // templateUrl: '/scripts/modules/inputs/doc-input.html',
            controller: ($scope, httpService, paneFactory) => {

                return docCtrlr($scope, httpService, paneFactory);

            },
            link: (scope, ele, attrs) => {}
        }
    })
    .directive( "itemInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=', stock:'<?' },
            template: itemInputTpl,
            // templateUrl: '/scripts/modules/inputs/item-input.html',
            controller: function ($scope, httpService, paneFactory, itemFactory) {
                return itemInputCtrlr($scope, httpService, paneFactory, itemFactory, this);
            },
            link: () => {}
        }
    })
    .directive( "addEditItem", () => {
        return {
            restrict: 'E',
            require: '^^itemInput',
            scope: {},
            template: addEditItemTpl,
            controller: ($scope, httpService, paneFactory) => {
                return addEditItemCtrlr($scope, httpService, paneFactory);
            },
            link: (scope, elem, attrs, itemInputCtrl) => {
                return commonLinkFunction(scope, elem, attrs, itemInputCtrl);
            }
        }
    })
    .directive( "itemSectionInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=section', items:'=?sections'},
            template: itemSectionInputTpl,
            controller: function ($scope, httpService, paneFactory, itemFactory) {
                return itemSectionCtrlr($scope, httpService, paneFactory, itemFactory, this);
            }
        }
    })
    .directive( "addEditItemSection", () => {
        return {
            restrict: 'E',
            require: '^^itemSectionInput',
            scope: {},
            template: addEditItemSectionTpl,
            controller: ($scope, httpService, paneFactory) => {
                $scope.modalHidden = true;
                $scope.item = {};
                $scope.inputId = paneFactory.generateUuid();

                $scope.closeModal = () => {
                    $scope.modalHidden = true;
                };

                $scope.appendData = () => {
                    httpService.addItem($scope.item, 'addSection').then(
                        resp => {
                            $scope.closeModal();
                            (resp.item == null) ?
                                $scope.setItem({name: resp.text}) : $scope.setItem(resp.item);
                        },
                        resp => { $scope.item.name = resp; }
                    );
                };

                $scope.getItemById = (id) => {
                    httpService.getItemById(id, 'getSectionById').then(
                        value => {$scope.item = value;}
                    );
                };

            },
            link: (scope, elem, attrs, sectionInputCtrl) => {
                return commonLinkFunction(scope, elem, attrs, sectionInputCtrl);
            }
        }
    })
    .directive( "pagePicker", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { page: '=', pages: '='},
            template: pagePickerTpl,
            // templateUrl: '/scripts/modules/inputs/page-picker.html',
            controller: $scope => {
                $scope.pagePickerVisible = false;

                $scope.pageForward = () => {
                    if($scope.page < $scope.pages.length)
                        $scope.page += 1;
                };

                $scope.pageBack = () => {
                    if($scope.page > 1)
                        $scope.page -= 1;
                };

                $scope.setFirstPage = () => {
                    $scope.page = 1;
                };
                $scope.setLastPage = () => {
                    $scope.page = $scope.pages.length;
                };

                $scope.togglePagePicker =  () => {
                    $scope.pagePickerVisible = !$scope.pagePickerVisible;
                };

                $scope.selectPage = function() {
                    $scope.page = this.x;
                    $scope.togglePagePicker();
                }
            },
            link: (scope, elem, attrs) => {}
        }
    })
    .directive( "stockPicker", () => {

        return {
            restrict: 'E',
            transclude: true,
            scope: { allowAll: '=allowAll', stock: '=stock'},
            template:
            "<div class='dropdown' style='display: inherit;'>" +
            "<div class='glyphicon glyphicon-home stock-icon' ng-click='toggleStockList()'></div>" +
            "<ul class='stocks-list' ng-show='stockListVisible'>" +
            "<li style='list-style: none;'>" +
            "<table class='table'>" +
            "<tbody>" +
            "<tr class='hoverable unselected-stock' " +
            "ng-repeat='x in stocks' " +
            "ng-class='{\"selected-stock\": (x.selected)}'>" +
            "<td class='hoverable' ng-click='clickOnStock(x)'>{{ x.id }}</td>" +
            "<td class='hoverable' ng-click='clickOnStock(x)'>{{ x.name }}</td>" +
            "</tr>" +
            "</tbody>" +
            "</table>" +
            "</li>" +
            "</ul>" +
            "</div>",
            // templateUrl: 'html/stock/stock-picker.html',
            controller: ($scope, httpService) => {

                return stockCntrlr($scope, httpService)

            },
            link: scope => {
                scope.getStocks();
            }
        };

    })
    .directive( "supplierInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: {
                item: '=supplier',
                items: '=?suppliers'
            },
            template: supplierInputTpl,
            controller: function ($scope, $http, paneFactory, itemFactory ) {
                return supplierInputCntrlr($scope, $http, paneFactory, itemFactory, this);
            }
        }
    })
    .directive( "addEditSupplier", () => {
        return {
            restrict: 'E',
            scope: true,
            require: '^^supplierInput',
            transclude: true,
            template:
            "<div ng-hide='modalHidden' class='trans-layer'></div>" +
            "<div class='modal-container-addeditsupplier' ng-class='{modalactive: !modalHidden}'>" +
            "<div class='wrapper' ng-keyup='handleKeyup($event)'>" +
            "<span>Id:</span><span>{{item.id}}</span>" +
            "<span title='Применить' class='glyphicon glyphicon-ok item-add' style='float: left;'" +
            "ng-click='appendData()'></span>" +
            "<span title='Закрыть' class='glyphicon glyphicon-remove item-blank' style='float: right;'" +
            "ng-click='closeModal()'></span>" +
            "<div>" +
            "<span>Наименование</span>" +
                "<input type='text' class='form-control' " +
                    "id='{{inputId}}' ng-model='item.name' placeholder=''/>" +
            "</div>" +
            "</div>",
            controller: ($scope, $http, paneFactory) => {

                $scope.inputId = paneFactory.generateUuid();
                $scope.modalHidden = true;
                $scope.item = {};

                $scope.closeModal =  () => { $scope.modalHidden = true; };

                $scope.handleKeyup = e => {
                    if (e.keyCode == 13)
                        $scope.appendData();
                };

                $scope.appendData = () => {
                    $http.post('/addSupplier', $scope.item)
                        .then(
                            resp => {
                                if (resp.data.success) {
                                    $scope.closeModal();
                                    $scope.parentScope.getItems();
                                }
                            },
                            () => {console.log("Ошибка при добавлении поставщика!");}
                        );
                };

                $scope.getItemById = id => {
                    $http.get('/getSupplierById', { params: { id: id }})
                        .then(resp => {
                            $scope.item = resp.data;
                        }
                    );
                };
            },
            link: (scope, elem, attrs, supplierInputCtrlr) => {
                supplierInputCtrlr.getChildScope(scope);
                scope.parentScope = supplierInputCtrlr.getItems;
            }
        }
    })
    .directive( "userPicker", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: userPickerTpl,
                controller: ($scope, userService) => {
                    $scope.user = { name: "Tes" };

                    userService.getUser().then(
                        resp => {$scope.user = resp;},
                        resp => {$scope.user.name = resp;}
                    );
                },
                link: (scope, elem, attrs) => {}
            }
        })
    .component( "itemInputTotal", {
                bindings: {total: '<'},
                template:"<span ng-show='$ctrl.total > 1'>{{$ctrl.total}}</span>",
                controller: function() {}
        })
    .factory('itemFactory',['httpService', 'paneFactory',
        function (httpService, paneFactory) {
            return {
                generateUuid : paneFactory.generateUuid,
                bankConfig : () => {
                    return {
                        getEmptyItem: () => { return {name: ''}; },
                        getItemsUrl: 'getBanks',
                        getItemByIdUrl: 'getBankById'
                    }
                },
                buyerConfig : () => {
                    return {
                        getEmptyItem: () => {return {name: '', bank: {name: ''}};},
                        getItemsUrl: 'getBuyers',
                        getItemByIdUrl: 'getBuyerById'
                    }
                },
                documentConfig : () => {
                    return {
                        getEmptyItem: () => {return {name: '', date: ''};},
                        getItemsUrl: 'getBuyers',
                        getItemByIdUrl: 'getBuyerById'
                    }
                },
                addEditItem: (item, childScope) => {
                    childScope.modalHidden = false;
                    if (typeof item === 'object')
                        childScope.item = item;
                    if(typeof item === 'number' && item > 0)
                        childScope.getItemById(item);
                    document.getElementById(childScope.inputId).focus();
                },
                getItems: ($s, url) => {
                    $s.items=[];
                    httpService.getItems({filter: $s.item.name}, url).then(
                        (value) => {
                            $s.items = value;
                        },
                        (value) => {
                            $s.item.name = value;
                        }
                    );
                },
                selectItem: (id, url, $s) => {
                    if(id)
                        httpService.getItemById(id, url).then(
                            resp => { $s.item = resp;}
                        );
                },
                clearItem: ($s) => {
                    $s.item = $s.emptyItem();
                    $s.getItems();
                    paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
                },
                changeItem: (id, $s) => {
                    id > 0 ? $s.selectItem(id) : $s.item = $s.emptyItem();
                    $s.addEditModalVisible = true;
                    $s.user = paneFactory.user;
                }
            };
        }
    ]);