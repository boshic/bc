import angular from 'angular';
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

let bankInputCtrlr = ($s, httpService, paneFactory, elem, ctrlr) => {
    $s.items = [];
    $s.item = {name:""};
    $s.warning = true;
    $s.canChange = false;

    ctrlr.setItem = $s.setItem = item => {
        $s.item = (item == null || (!item)) ? {name:""} : item;
        $s.canChange = false;
        $s.checkItem();
    };

    $s.blankSearchAndGetItemsBy = () => {
        $s.setItem(null);
        $s.canChange = true;
        paneFactory.changeElementState(document.getElementById('bank-ipnut'), ['focus']);
    };

    $s.checkItem = () => {
        $s.getItems();
        if ("id" in $s.item) {
            $s.warning = false;
            $s.canChange = false;
        } else {
            $s.warning = true;
        }
    };

    $s.$watch('item.name', (nv, ov) => {
        if ((nv) || (ov))
            $s.checkItem();
    }, true);

    ctrlr.getAddEditData = (scope, elem) => {
        $s.addEditScope = scope;
        $s.addEditElement = elem;
    };

    $s.getItems = () => {
        $s.items=[];
        httpService.getItems({filter: $s.item.name}, 'getBanks').then(
            (value) => {$s.items = value;},
            (value) => {$s.item.name = value;}
        );
    };

    $s.addEditItem = function (newItem) {
        let item = this.x;
        $s.addEditScope.modalHidden = false;
        if (newItem) {
            (item) ? $s.addEditScope.item = {name: item.name}
                : $s.addEditScope.item = {};
        } else {
            $s.addEditScope.getItemById(item.id);
        }
        $s.addEditElement.find('.form-control').focus();
    };

    $s.selectItem = function() {
        $s.setItem(this.x);
    };

};

let addEditBankCntrlr = ($s, httpService) => {
    $s.modalHidden = true;
    $s.item = {};

    $s.closeModal = () => {
        $s.modalHidden = true;
    };

    $s.appendData = () => {
        httpService.addItem($s.item, 'addBank').then(
            resp => {
                $s.closeModal();
                (resp.item == null) ? $s.setItem({name: resp.text}) : $s.setItem(resp.item);
            },
            resp => { $s.item.name = resp; }
        );
    };

    $s.getItemById = (id) => {
        httpService.getItemById(id, 'getBankById').then(
            value => {$s.item = value;}
        );
    };
};

let buyerInputCntrlr = ($s, httpService, paneFactory, elem) => {

    $s.buyers = [];
    $s.buyer = {name:"", sellByComingPrices: false};
    $s.warning = true;
    $s.canChange = true;
    $s.addEditModalVisible = false;

    $s.$watch('buyer.name', (nv, ov) => {
        if ((ov) || (nv))
            $s.checkItem();
    }, true);

    $s.checkItem = () => {
        $s.getItems();
        ("id" in $s.buyer) ?
            $s.warning = $s.canChange = false :
            $s.warning = $s.canChange = true;
    };

    $s.getItems = () => {
        httpService.getItems({filter: $s.buyer.name}, 'getBuyers').then(
            resp => { $s.buyers = resp; },
            resp => { $s.buyer.name = resp; }
        );
    };

    $s.selectItem = function() {
        $s.buyer = this.x;
        $s.checkItem();
    };

    $s.addEditItem = function () {
        $s.editBuyer = this.x;
        $s.addEditModalVisible = true;
        $s.user = paneFactory.user;
    };

    $s.clearBuyer = () => {
        $s.buyer = {name:""};
        $s.checkItem();
        paneFactory.changeElementState(document.getElementById('buyer-input'), ['focus']);
    };
};

let docCtrlr = ($s, httpService) => {

    $s.dateFrom = new Date(0);
    $s.dateTo=new Date();
    $s.docs = [];
    $s.doc = {
        name:"",
        date: ""
    };

    $s.newDoc = {
        name:"",
        date: new Date().toLocaleDateString(),
        supplier : {
            name:""
        }
    };

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
            $s.newDoc = {
                date: new Date(),
                name: "",
                supplier: {name:""}
            };
        $s.checkDoc();
    };

    $s.blankNameAndGetItemsBy = () => {
        $s.doc = {
            name: "",
            date: ""
        };
        $s.warning = true;
        $s.getDocs();
    };

    $s.selectDoc = function () {
        $s.doc = this.x;
        $s.warning = false;
        $s.showDocsList();
    };

};

let itemCntrlr = ($s, paneFactory, httpService, elem, ctrlr) => {

    $s.items = [];
    $s.item = {name:""};
    $s.warning = true;
    $s.canChange = false;

    $s.$watch('item.name', (nv, ov) => {
        if ((ov) || (nv))
            $s.checkItem();
    }, true);

    ctrlr.getAddEditData = (scope, elem) => {
        $s.childScope = scope;
        $s.childElement = elem;
    };

    $s.getItems = () => {
        $s.items=[];
        if ( ($s.item) && ($s.item.name.length > 2)) {
            httpService.getItems({filter: $s.item.name}, 'getItems').then(
                (value) => {$s.items = value;},
                (value) => {$s.item.name = value;}
            );
        }
    };

    $s.deleteItem = function () {
        httpService.deleteItemById(this.x.id, 'deleteItem').then(
            resp => {
                (resp) ? console.log('не удалится - есть приходы! - ' + resp)
                    : console.log('удален товар из справочника!');
                $s.checkItem();
            },
            resp => { $s.item.name = resp;}
        );

    };

    $s.addEditItem = function (newItem) {
        let item = this.x;
        $s.childScope.itemsModalHidden = false;
        if (newItem) {
            (item) ? $s.childScope.item = {name: item.name, section: item.section, unit: item.unit}
                : $s.childScope.item = {name: $s.item.name, ean: $s.item.ean};
        } else {
            $s.childScope.getItemById(item.id);
        }
        $s.childElement.find('.form-control').focus();
    };

    ctrlr.setItem = $s.setItem = item => {
        $s.item = (item == null || (!item)) ? { name:"", ean:""} : item;
        $s.canChange = false;
        $s.checkItem();
    };

    $s.selectItem = function() {
        $s.setItem(this.x);
    };

    $s.blankSearchAndGetItemsBy = () => {
        $s.setItem(null);
        $s.canChange = true;
        paneFactory.changeElementState(document.getElementById('item-input'), ['focus']);
    };

    $s.checkItem = () => {
        $s.getItems();
        if (($s.item)  && ("id" in $s.item)){
            $s.warning = false;
            $s.canChange = false;
        } else {
            $s.warning = true;
        }
    };

    $s.setEanPrefix = e => {
        $s.item.name = paneFactory.generateEanByKey(e, $s.item.name);
    };

};

let addEditItemCtrlr = ($s, httpService, paneFactory) => {
    $s.itemsModalHidden = true;
    $s.item = {};

    $s.closeModal = () => {
        $s.itemsModalHidden = true;
    };

    $s.appendData = () => {
        httpService.addItem($s.item, 'addItem').then(
            resp => {
                $s.closeModal();
                (resp.item == null) ?
                    $s.setItem({name: resp.text, ean: ""}) : $s.setItem(resp.item);
            },
            resp => { $s.item.name = resp; }
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

    $s.setEanPrefix = e => {
        // if (e.ctrlKey && e.keyCode === panFactory.eanPrefix.keyCode)
        //     $s.item.ean = paneFactory.generateEan($s.item.name);
        $s.item.ean = paneFactory.generateEanByKey(e, $s.item.ean);
    };

    $s.getItemById = (id) => {
        httpService.getItemById(id, 'getItemById').then(
            value => {$s.item = value;}
        );
    };
};

let sectionCntrlr = ($s, httpService, paneFactory, elem, ctrlr) => {

    $s.sections = [];
    $s.section = {name:""};
    $s.warning = true;
    $s.canChange = false;

    ctrlr.setSection = $s.setSection = section => {
        $s.section = (section == null || (!section)) ? {name:""} : section;
        $s.canChange = false;
        $s.checkSection();
    };

    $s.blankSearchAndGetItemsBy = () => {
        $s.setSection(null);
        $s.canChange = true;
        paneFactory.changeElementState(document.getElementById('section-ipnut'), ['focus']);
    };

    $s.checkSection = () => {
        $s.getItems();
        if ("id" in $s.section) {
            $s.warning = false;
            $s.canChange = false;
        } else {
            $s.warning = true;
        }
    };

    $s.$watch('section.name', (nv, ov) => {
        // && 'id' in nv
        if ((ov) || (nv))
            $s.checkSection();
    }, true);

    ctrlr.getAddEditData = (scope, elem) => {
        $s.addEditScope = scope;
        $s.addEditElement = elem;
    };

    $s.getItems = () => {

        $s.sections=[];
        httpService.getItems({filter: $s['section'].name}, 'getSections').then(
            (value) => {$s.sections = value;},
            (value) => {$s.section.name = value;}
        );
    };

    $s.addEditSection = function (newItem) {
        let section = this.x;
        $s.addEditScope.sectionModalHidden = false;
        if (newItem) {
            (section) ? $s.addEditScope.section = {name: section.name}
                : $s.addEditScope.section = {};
        } else {
            $s.addEditScope.getItemById(section.id);
        }
        $s.addEditElement.find('.form-control').focus();
    };

    $s.selectSection = function() {
        $s.setSection(this.x);
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

let supplierInputCntrlr = ($s, $http, paneFactory, elem, ctrlr) => {

    $s.suppliers = [];
    $s.supplier = { name: ""};
    $s.warning = true;
    $s.canChange = false;

    $s.$watch('supplier', () => {
        $s.warning = (!(($s.supplier) && ("id" in $s.supplier)));
    });

    ctrlr.getChildScope = (scope, elem) => {
        $s.childScope = scope;
        $s.childElem = elem;
    };

    $s.checkItem = () => {
        $s.getItems();
        $s.warning = (!("id" in $s.supplier));
    };

    ctrlr.getItems = $s.getItems = () => {
        $http.get('/getSuppliers', {
            params: { filter: $s.supplier.name }
        }).then(
            resp => { $s.suppliers = resp.data; },
            () => { console.log('список поставщиков пуст!'); }
        );
    };

    $s.selectSupplier = function() {
        $s.supplier = this.x;
        $s.canChange = false;
        $s.checkItem();
    };

    $s.addEditSupplier = function (newSupplier) {
        let supplier = this.x;
        $s.childScope.modalHidden = false;
        if (newSupplier) {
            (supplier) ?  $s.childScope.supplier = {
                    name: supplier.name
                }
                : $s.childScope.supplier = {};
        } else {
            $s.childScope.getSupplierById(supplier.id);
        }
        $s.childElem.find('.form-control').focus();
    };

    $s.blankSearchAndGetItemsBy = function() {
        $s.supplier = { name:"" };
        $s.canChange = true;
        $s.checkItem();
        paneFactory.changeElementState(document.getElementById('supplier-input'), ['focus']);
    };
};


angular.module('inputs', [])
    .directive( "bankInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=bank' },
            template: bankInputTpl,
            // templateUrl: '/scripts/modules/inputs/bank-input.html',
            controller: function ($scope, httpService, paneFactory, $element) {
                return bankInputCtrlr($scope, httpService, paneFactory, $element, this);
            },
            link: (scope, elem) => {}
        }
    })
    .directive( "addEditBank", () => {
        return {
            restrict: 'E',
            require: '^^bankInput',
            scope: {},
            template: addEditBankTpl,
            // templateUrl: '/scripts/modules/inputs/add-edit-bank.html',
            controller:($scope, httpService) => { return addEditBankCntrlr($scope, httpService); },
            link: (scope, elem, attrs, itemInputCtrl) => {
                itemInputCtrl.getAddEditData(scope, elem);
                scope.setItem = itemInputCtrl.setItem;
            }
        }
    })
    .directive( "buyerInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { buyer: '=', buyers: '=?'},
            template: buyerInputTpl,
            // templateUrl: '/scripts/modules/inputs/buyer-input.html',
            controller: ($scope, httpService, paneFactory, $element) => {
                return buyerInputCntrlr($scope, httpService, paneFactory, $element)
            },
            link: () => {}
        }
    })
    .directive( "addEditBuyer", () => {
        return {
            restrict: 'E',
            scope: { buyer: "=", user: "=?", modalVisible: "="},
            template: addEditBuyerTpl,
            // templateUrl: '/scripts/modules/inputs/add-edit-buyer.html',
            controller:("ctrl", [ '$scope', 'httpService',
                function ($scope, httpService) {
                    $scope.warning ="";
                    $scope.buyer = {};

                    $scope.closeModal = () => {
                        $scope.modalVisible = false;
                        $scope.warning ="";
                    };

                    $scope.appendData = () => {
                        httpService.addItem($scope.buyer, 'addBuyer').then(
                            resp => {
                                (resp.success) ? $scope.closeModal() : $scope.warning = resp.text;
                            },
                            resp => { $scope.buyer.name = resp; }
                        );
                    };
                }
            ])
            ,
            link: (scope, elem) => {
                // $(elem).find('.trans-layer').on('click', event => {scope.modalVisible = false;});
            }
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
            controller: ($scope, httpService) => {

                return docCtrlr($scope, httpService);

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
            controller: function ($scope, paneFactory, httpService , $element) {
                return itemCntrlr($scope, paneFactory, httpService, $element, this);
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
            // templateUrl: '/scripts/modules/inputs/add-edit-item.html',
            controller: ($scope, httpService, paneFactory) => {
                return addEditItemCtrlr($scope, httpService, paneFactory);
            },
            link: (scope, elem, attrs, itemInputCtrl) => {
                itemInputCtrl.getAddEditData(scope, elem);
                scope.setItem = itemInputCtrl.setItem;
            }
        }
    })
    .directive( "itemSectionInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { section:'=', sections:'=?'},
            template: itemSectionInputTpl,
            // templateUrl: '/scripts/modules/inputs/item-section-input.html',
            controller: function ($scope, httpService, paneFactory, $element) {
                return sectionCntrlr($scope, httpService, paneFactory, $element, this);
            }
        }
    })
    .directive( "addEditItemSection", () => {
        return {
            restrict: 'E',
            require: '^^itemSectionInput',
            scope: {},
            template: addEditItemSectionTpl,
            // templateUrl: '/scripts/modules/inputs/add-edit-item-section.html',
            controller: ($scope, httpService) => {
                $scope.sectionModalHidden = true;
                $scope.section = {};

                $scope.closeModal = () => {
                    $scope.sectionModalHidden = true;
                };

                $scope.appendData = () => {
                    httpService.addItem($scope.section, 'addSection').then(
                        resp => {
                            $scope.closeModal();
                            (resp.item == null) ?
                                $scope.setSection({name: resp.text}) : $scope.setSection(resp.item);
                        },
                        resp => { $scope.item.name = resp; }
                    );
                };

                $scope.getItemById = (id) => {
                    httpService.getItemById(id, 'getSectionById').then(
                        value => {$scope.section = value;}
                    );
                };

            },
            link: (scope, elem, attrs, sectionInputCtrl) => {
                sectionInputCtrl.getAddEditData(scope, elem);
                scope.setSection = sectionInputCtrl.setSection;
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
                supplier: '=',
                suppliers: '=?'
            },
            template: supplierInputTpl,
            // templateUrl: '/scripts/modules/inputs/supplier-input.html',
            controller: function ($scope, $http, paneFactory, $element ) {
                return supplierInputCntrlr($scope, $http, paneFactory, $element, this);
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
            "<span>Id:</span><span>{{supplier.id}}</span>" +
            "<span title='Применить' class='glyphicon glyphicon-ok item-add' style='float: left;'" +
            "ng-click='appendData()'></span>" +
            "<span title='Закрыть' class='glyphicon glyphicon-remove item-blank' style='float: right;'" +
            "ng-click='closeModal()'></span>" +
            "<div>" +
            "<span>Наименование</span>" +
            "<input type='text' class='form-control' ng-model='supplier.name' placeholder=''/>" +
            "</div>" +
            "</div>",
            // templateUrl: 'html/supplier/add-edit-supplier.html',
            controller: ($scope, $http) => {

                $scope.modalHidden = true;
                $scope.supplier = {};

                $scope.closeModal =  () => { $scope.modalHidden = true; };

                $scope.handleKeyup = e => {
                    if (e.keyCode == 13)
                        $scope.appendData();
                };

                $scope.appendData = () => {
                    $http.post('/addSupplier', $scope.supplier)
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

                $scope.getSupplierById = id => {
                    $http.get('/getSupplierById', {
                        params: {
                            id: id
                        }
                    }).then(
                        resp => {
                            $scope.supplier = resp.data;
                        }
                    );
                };
            },
            link: (scope, elem, attrs, supplierInputCtrlr) => {
                supplierInputCtrlr.getChildScope(scope, elem);
                scope.parentScope = supplierInputCtrlr.getItems;
                // $( elem ).find( '.trans-layer' ).on( 'click', event => {
                //     scope.modalHidden = true;
                //     scope.$apply();
                // });
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
        });
