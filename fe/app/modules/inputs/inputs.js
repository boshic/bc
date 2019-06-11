import itemInputItemsTpl from './item-input-items.html';
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

    $s.$watch('item.name', (nv, ov) => {
        // if ((nv) || (ov))
        if(nv && !$s.addEditModalVisible)
            $s.getItems();
    }, true);

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

        $s.getEmptyItem = config.getEmptyItem;

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

let supplierInputCntrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'supplierConfig');
};

let supplierChangeCtrlr = ($s, itemFactory) => {

    return commonAddEditCtrlr($s, itemFactory, 'supplierConfig');
};

let itemInputCtrlr = ($s, itemFactory, paneFactory) => {

    $s.setEanPrefix = e => {
        $s.item.name = paneFactory.generateEanByKey(e, $s.item.name);
    };

    return commonItemCtrlr($s, itemFactory, 'itemConfig');
};

let itemChangeCtrlr = ($s, itemFactory, paneFactory) => {

    $s.getNextId = () => {
        if($s.item.id > 0)
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
            }
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
            }
        }
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
            controller: function ($scope, itemFactory ) {
                return supplierInputCntrlr($scope, itemFactory);
            }
        }
    })
    .directive( "addEditSupplier", () => {
        return {
            restrict: 'E',
            scope: { item: "=supplier", modalVisible: "=", getItems: '&?'},
            transclude: true,
            template:
            "<div ng-show='modalVisible' class='trans-layer'></div>" +
            "<div class='modal-container-addeditsupplier' ng-class='{modalactive: modalVisible}'>" +
            "<div class='wrapper' ng-keyup='handleKeyup($event)'>" +
            "<item-add-edit-id item-id='item.id'></item-add-edit-id>" +
            "<span title='Применить' class='glyphicon glyphicon-ok item-add' style='float: left;'" +
            "ng-click='appendData()'></span>" +
            "<span title='Закрыть' class='glyphicon glyphicon-remove item-blank' style='float: right;'" +
            "ng-click='closeModal()'></span>" +
            "<div>" +
            "<span>Наименование</span>" +
            "<span class='warning-item-input' ng-hide='item.name.length > 0'>Введите наименование</span>"+
                "<input type='text' class='form-control' " +
                    "id='{{inputId}}' ng-model='item.name' placeholder=''/>" +
                "</div>" +
            "</div>",
            controller : ($scope, itemFactory) => {
                return supplierChangeCtrlr($scope, itemFactory);
            }
        }
    })
    .component( "itemInputTotal", {
                bindings: {total: '<', },
                template:"<span ng-show='$ctrl.total > 0'>{{$ctrl.total}}</span>",
                controller: function() {}
        })
    .component( "itemAddEditId", {
        bindings: {id: '<itemId', },
        template:"<span style='margin-left: 20%;"+
                    "font-size: 20px;'>" +
                "<span ng-show='$ctrl.id > 0'>Id:</span><span>{{$ctrl.id}}</span>" +
                "<span class='add-edit-item-warning' ng-hide='$ctrl.id > 0'>Ввод нового</span>" +
                "<span class='add-edit-item-warning' ng-show='$ctrl.id > 0'>Редактирование</span>" +
            "</span>",
        controller: function() {}
    })
    .component( "itemInputName", {
        bindings: { item: '<', title: '@inputName', getItems: '&', inputId: '<', keypressHandler: '&?' },
        template:
        // "<div class='dropdown' style='position: absolute;'" +
        //     "ng-show='$ctrl.item.name.length>0'>" +
        //     "<span class='glyphicon glyphicon-filter item-input-filter'></span>" +
        //     "<ul class='dropdown-menu hoverable quick-filter-comment-delete background-white'>" +
        //         "<word-from-phrase-eraser phrase='$ctrl.item.name' title={{$ctrl.title}}>" +
        //         "</word-from-phrase-eraser>" +
        //     "</ul>" +
        // "</div>" +
        "<span class='warning-item-input' ng-hide='$ctrl.item.id>0'>" +
            "{{'Не выбран(а) ' + $ctrl.title + '!'}}</span>" +
        "<textarea rows='2' title='{{$ctrl.item.name}}' type='text' " +
            "class='form-control' placeholder={{$ctrl.title}} id={{$ctrl.inputId}} " +
                "ng-keydown='$ctrl.keypressHandler()($event)'" +
                "ng-model='$ctrl.item.name'" +
                "ng-readonly='$ctrl.item.id || $ctrl.item === null'><textarea/>",
        controller: function() {}
    })
    .component( "itemInputItems", {
        bindings: { items: '<', clearItem: '&',  selectItem: '&', changeItem: '&'},
        template: itemInputItemsTpl,
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
                        getItemsUrl: 'getSuppliers',
                        addItemUrl: 'addSupplier',
                        getItemByIdUrl: 'getSupplierById'
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
                    httpService.getItems({filter: $s.item.name || ''}, url).then(
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
                        $s.item = (angular.isDefined($s.item) && $s.item != null) ?
                            angular.extend({}, $s.item, {id: null})
                            : $s.getEmptyItem();
                    $s.addEditModalVisible = true;
                    $s.user = paneFactory.user;
                },
                addItem : ($s, url) => {
                    httpService.addItem($s.item, url).then(
                        resp => {
                            $s.item = resp.item;
                            if(resp.success) {
                                $s.getItems();
                                $s.closeModal();
                            }
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