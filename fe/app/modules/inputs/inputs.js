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

let commonItemCtrlr = ($s, itemFactory, itemConfig) => {

    $s.inputId = itemFactory.generateUuid();
    $s.items = [];
    let config = itemFactory[itemConfig];
    $s.getEmptyItem = config.getEmptyItem;
    $s.item = $s.getEmptyItem();

    $s.$watch('item.id', (nv) => {
        if(nv)
            $s.getItems();
    });

    $s.getItems = () => {
        itemFactory.getItems($s, config.getItemsUrl);
    };

    $s.getItemById = (id) => {
        return itemFactory.getItemById(id, config.getItemByIdUrl);
    };

    $s.selectItem = (id) => {
        itemFactory.selectItem(id, $s, config);
    };

    $s.changeItem = (id) => {
        itemFactory.changeItem(id, $s);
    };

    $s.clearItem =  () => {
        itemFactory.clearItem($s);
    };
};

let commonAddEditCtrlr = ($s, itemFactory, itemConfig) => {
        $s.warning ="";
        let config = itemFactory[itemConfig];

        $s.closeModal = () => {
            itemFactory.closeModal($s);
        };

        $s.appendData = () => {
            itemFactory.addItem($s, config.addItemUrl);
        };
    };

let bankInputCtrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'bankConfig');
};

let bankChangeCtrlr = ($s, itemFactory) => {

    return commonAddEditCtrlr($s, itemFactory, 'bankConfig');
};

let buyerInputCntrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'buyerConfig');
};

let buyerChangeCtrlr = ($s, itemFactory) => {

    return commonAddEditCtrlr($s, itemFactory, 'buyerConfig');
};

let sectionCtrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'sectionConfig');
};

let sectionChangeCtrlr = ($s, itemFactory) => {

    return commonAddEditCtrlr($s, itemFactory, 'sectionConfig');
};

let itemInputCtrlr = ($s, itemFactory, paneFactory) => {

    $s.$watch('item.name', (nv, ov) => {
        if ((nv) || (ov))
            $s.getItems();
    }, true);

    $s.setEanPrefix = e => {
        $s.item.name = paneFactory.generateEanByKey(e, $s.item.name);
    };

    return commonItemCtrlr($s, itemFactory, 'itemConfig');
};

let itemChangeCtrlr = ($s, itemFactory, paneFactory) => {

    $s.getNextId = () => {
        if('id' in $s.item)
            $s.item.ean = paneFactory.generateEan($s.item.id.toString());
        else
            itemFactory.setItemEanByTopId($s.item);
    };

    $s.setEanPrefix = (e, field) => {
        $s.item[field] = paneFactory.generateEanByKey(e, $s.item[field]);
    };

    return commonAddEditCtrlr($s, itemFactory, 'itemConfig');
};

let docCtrlr = ($s, httpService, itemFactory) => {

    $s.dateFrom = new Date(2015,0,1);
    $s.dateTo = new Date();
    $s.docs = [];
    $s.getEmptyItem = itemFactory.documentConfig.getEmptyItem;
    $s.doc = $s.getEmptyItem();

    $s.newDoc = angular.extend($s.getEmptyItem(), {date: new Date().toLocaleDateString()});

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
            $s.newDoc = angular.extend($s.getEmptyItem(), {date: new Date()});
        $s.checkDoc();
    };

    $s.blankNameAndGetItemsBy = () => {
        $s.doc = $s.getEmptyItem();
        $s.warning = true;
        $s.getDocs();
    };

    $s.selectDoc = function () {
        $s.doc = this.x;
        $s.warning = false;
        $s.showDocsList();
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

    $s.inputId = itemFactory.generateUuid();
    $s.items = [];
    let getEmptyItem = itemFactory.supplierConfig.getEmptyItem;
    $s.item = getEmptyItem();

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
        $s.item.name === "" ? $s.getItems() : $s.item = getEmptyItem();
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
            scope: { item: "=bank", modalVisible: "=", getItems: '&?'},
            template: addEditBankTpl,
            controller : ($scope, itemFactory) => {
                return bankChangeCtrlr($scope, itemFactory);
            }
        }
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
            controller : ($scope, itemFactory) => {
                return buyerChangeCtrlr($scope, itemFactory);
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
            controller: ($scope, httpService, itemFactory) => {
                return docCtrlr($scope, httpService, itemFactory);

            },
            link: (scope, ele, attrs) => {}
        }
    })
    .directive( "itemInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=', stock:'<?', getItemsForParent: '&?getItems' },
            template: itemInputTpl,
            controller: function ($scope, itemFactory, paneFactory) {
                return itemInputCtrlr($scope, itemFactory, paneFactory);
            }
        }
    })
    .directive( "addEditItem", () => {
        return {
            restrict: 'E',
            scope: { item: "=", user: "=?", modalVisible: "=", getItems: '&?'},
            template: addEditItemTpl,
            controller : ($scope, itemFactory, paneFactory) => {
                return itemChangeCtrlr($scope, itemFactory, paneFactory);
            }
        }
    })
    .directive( "itemSectionInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=section', items:'=?sections'},
            template: itemSectionInputTpl,
            controller: ($scope, itemFactory) => {
                return sectionCtrlr($scope, itemFactory);
            }
        }
    })
    .directive( "addEditItemSection", () => {
        return {
            restrict: 'E',
            scope: { item: "=section", user: "=?", modalVisible: "=", getItems: '&?'},
            template: addEditItemSectionTpl,
            controller : ($scope, itemFactory) => {
                return sectionChangeCtrlr($scope, itemFactory);
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
                                    $scope.getItems();
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
                scope.getItems = supplierInputCtrlr.getItems;
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
                bindings: {total: '<', },
                template:"<span ng-show='$ctrl.total > 0'>{{$ctrl.total}}</span>",
                controller: function() {}
        })
    .factory('itemFactory',['httpService', 'paneFactory',
        function (httpService, paneFactory) {

            let getNewBank = () => {return {name: ''};};
            let getNewSupplier = () => { return {name: ''};};
            let getNewBuyer = () => {return {name: '', bank: getNewBank()};};
            let getNewDocument = () => { return {name: '', date: '', supplier : getNewSupplier()};};
            let getNewSection = () => {return {name: ''};};
            let getNewItem = () => {return {name: '', ean: '', predefinedQuantity: 0, eanSynonym: '', section: getNewSection()};};

            let getItemById = (id, url) => {
                if(id)
                    return httpService.getItemById(id, url);
            };

            return {
                getItemById,
                generateUuid : paneFactory.generateUuid,
                bankConfig :
                    {
                        getEmptyItem: getNewBank,
                        getItemsUrl: 'getBanks',
                        addItemUrl: 'addBank',
                        getItemByIdUrl: 'getBankById'

                },
                buyerConfig :
                    {
                        getEmptyItem: getNewBuyer,
                        getItemsUrl: 'getBuyers',
                        addItemUrl: 'addBuyer',
                        getItemByIdUrl: 'getBuyerById'
                },
                documentConfig :
                    {
                        getEmptyItem: getNewDocument,
                        getItemsUrl: 'getDocs'
                },
                itemConfig :
                    {
                        getEmptyItem: getNewItem,
                        addItemUrl: 'addItem',
                        getItemsUrl: 'getItems',
                        getItemByIdUrl: 'getItemById',
                        selectItemForParent: ($s) => {
                            if(typeof $s.getItemsForParent === 'function'
                                && (angular.isDefined($s.item) && $s.item != null)
                                && (angular.isDefined($s.item.ean) && $s.item.ean != null))
                                $s.getItemsForParent()($s.item.ean);
                        }
                },
                supplierConfig :
                    {
                        getEmptyItem: getNewSupplier,
                        getItemsUrl: 'getSuppliers'
                },
                sectionConfig :
                    {
                        getEmptyItem: getNewSection,
                        addItemUrl: 'addSection',
                        getItemsUrl: 'getSections',
                        getItemByIdUrl: 'getSectionById'
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
                selectItem: (id, $s, config) => {
                    $s.getItemById(id).then(
                        item => {
                            $s.item = item;
                            if(typeof config.selectItemForParent === 'function')
                                config.selectItemForParent($s);
                        }
                    );
                },
                clearItem: ($s) => {
                    $s.item = $s.getEmptyItem();
                    $s.getItems();
                    paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
                },
                changeItem: (id, $s) => {
                    id > 0 ? $s.getItemById(id).then( item => { $s.item = item; }) :
                        $s.item = (angular.isDefined($s.item) && $s.item != null) ? $s.item : $s.getEmptyItem();
                    $s.addEditModalVisible = true;
                    $s.user = paneFactory.user;
                },
                addItem : ($s, url) => {
                    httpService.addItem($s.item, url).then(
                        resp => {
                            if(resp.success) {
                                $s.closeModal();
                                $s.getItems();
                            }
                            else
                                $s.warning = resp.text;
                        },
                        resp => { $s.item.name = resp; }
                    );
                },
                closeModal : ($s) => {
                    $s.modalVisible = false;
                    $s.warning ="";
                },
                setItemEanByTopId : (item) => {
                    httpService.getItemById(null, 'getTopId').then(
                        resp => {
                            item.ean = paneFactory.generateEan((resp + 1).toString());
                        },
                        resp => {console.log(resp);}
                    );
                }
            };
        }
    ]);