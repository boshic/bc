// import './rxjstest';

import 'angular1-async-filter';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { filter, tap, map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import itemInputItemsTpl from './item-input-items.html';
import userPickerTpl from './user-picker.html';
import pagePickerTpl from './page-picker.html';
import bankInputTpl from './bank-input.html';
import addEditBankTpl from './add-edit-bank.html';
import buyerInputTpl from './buyer-input.html';
import addEditBuyerTpl from './add-edit-buyer.html';
import docInputTpl from './doc-input.html';
import itemInputTpl from './item-input.html';
import itemComponentInputTpl from './item-component-input.html';
import compositeItemInputTpl from './composite-item-input.html';
import itemComponentsPickerTpl from './item-components-picker.html';
import addEditItemTpl from './add-edit-item.html';
import itemSectionInputTpl from './item-section-input.html';
import addEditItemSectionTpl from './add-edit-item-section.html';
import supplierInputTpl from './supplier-input.html';
import addEditSupplierTpl from './add-edit-supplier.html';

let commonItemCtrlr = ($s, itemFactory, itemConfig) => {

    $s.inputId = itemFactory.generateUuid();
    $s.items = [];
    $s.requestParams = { requestsQuantity: 0 };
    let config = itemFactory[itemConfig];
    $s.getEmptyItem = config.getEmptyItem;
    $s.item = $s.getEmptyItem();

    $s.$watch('item.name', (nv) => {
        if(nv && !$s.addEditModalVisible)
            $s.getItems();
    }, true);

    let searchTerms = itemFactory.getSearchTerms($s, config.getItemsUrl);

    $s.getItems = () => {
        searchTerms.next($s.item.name);
    };

    $s.getItemById = (id) => {
        return itemFactory.getItemById(id, config.getItemByIdUrl, $s.requestParams);
    };
    //
    $s.selectItem = (id) => {
        itemFactory.selectItem(id, $s, config);
    };
    //
    $s.changeItem = (id) => {
        itemFactory.changeItem(id, $s);
    };
    //
    $s.clearItem = () => {
        itemFactory.clearItem($s);
    };
    //
    $s.setEanPrefix = (e, field) => {
        itemFactory.setEanPrefix($s, e, field);
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

        $s.setEanPrefix = (e, field) => {
            itemFactory.setEanPrefix($s, e, field);
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

let itemInputCtrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'itemConfig');
};

let itemComponentInputCtrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'itemComponentConfig');
};

let compositeItemInputCtrlr = ($s, itemFactory) => {

    return commonItemCtrlr($s, itemFactory, 'compositeItemConfig');
};

let itemChangeCtrlr = ($s, itemFactory, paneFactory) => {

    $s.eanInputId = itemFactory.generateUuid();
    $s.priceInputId = itemFactory.generateUuid();

    $s.openItemComponentsModal = () => {
        itemFactory.openItemComponentsModal($s);
        // $s.itemComponentsModalVisible = true;
    };

    $s.getNextId = () => {
        if($s.item.id > 0)
            $s.item.ean = paneFactory.generateEan($s.item.id.toString());
        else
            itemFactory.setItemEanByTopId($s.item);
    };

    $s.focusOnEan = () => {
        paneFactory.changeElementState(document.getElementById($s.eanInputId), ['select']);
    };

    $s.focusOnPriceInputId = () => {
        paneFactory.changeElementState(document.getElementById($s.priceInputId), ['select']);
    };

    $s.copyEanToSynonym = () => {
        $s.item.eanSynonym = $s.item.ean;
        $s.focusOnEan();
    };

    return commonAddEditCtrlr($s, itemFactory, 'itemConfig');
};

let docCtrlr = ($s, httpService, itemFactory) => {

    $s.dateFrom = new Date(2015,0,1);
    $s.dateTo = new Date();
    $s.docs = [];

    $s.requestParams = {requestsQuantity: 0};
    $s.getEmptyItem = itemFactory.documentConfig.getEmptyItem;
    $s.doc = $s.getEmptyItem();

    $s.newDoc = angular.extend($s.getEmptyItem(), {date: new Date()});

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
        (($s.newDoc.date > 0) && ($s.newDoc.name.length > 0) && ($s.newDoc.supplier.id > 0)) ?
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
        httpService.addItem({data: $s.newDoc, url: 'addDocument'})
            .then(
                resp => {
                    (resp.entityItem == null) ? $s.doc.name = resp.text : $s.doc = resp.entityItem;
                    $s.showDocsList();
                },
                resp => {
                    console.log("ошибка при добавлении документа!");
                }
            );
    };

    $s.getDocs = () => {
        httpService.getItemsByFilter({
            filter: { searchString: $s.doc.name, fromDate: $s.dateFrom, toDate: $s.dateTo },
            url: 'getDocs'
        }).then(
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
        else {
            let date = new Date();
            $s.newDoc = angular.extend($s.getEmptyItem(), {date: new Date()});

        }
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

        httpService.getItems({params: {allowAll: $s.allowAll}, url: 'getStocks'}).then(
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

angular.module('inputs', ['asyncFilter'])
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
            scope: { item: "=bank", modalVisible: "=", getItems: '&?', requestParams: '='},
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
            scope: {
                item: "=buyer",
                user: "=?",
                modalVisible: "=",
                getItems: '&?',
                requestParams: '='
            },
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
            controller: function ($scope, itemFactory) {
                return itemInputCtrlr($scope, itemFactory);
            }
        }
    })
    .directive( "itemComponentInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=', stock:'<?', getItemsForParent: '&?getItems' },
            template: itemComponentInputTpl,
            controller: function ($scope, itemFactory) {
                return itemComponentInputCtrlr($scope, itemFactory);
            }
        }
    })
    .directive( "compositeItemInput", () => {
        return {
            restrict: 'E',
            transclude: true,
            scope: { item:'=', stock:'<?', getItemsForParent: '&?getItems' },
            template: compositeItemInputTpl,
            controller: function ($scope, itemFactory) {
                return compositeItemInputCtrlr($scope, itemFactory);
            }
        }
    })
    .directive( "addEditItem", () => {
        return {
            restrict: 'E',
            scope: { item: "=", user: "=?", modalVisible: "=", getItems: '&?', requestParams: '='},
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
            scope: { item: "=section", user: "=?", modalVisible: "=", getItems: '&?', requestParams: '='},
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
            // scope: true,
            template: userPickerTpl,
            controller: ($scope, userService, paneFactory) => {

                $scope.user = { name: "Tes" };
                $scope.searchInputAutoFocusEnabled = paneFactory.searchInputAutoFocusEnabled;
                $scope.showMenu = false;


                userService.getUser().then(
                    resp => {$scope.user = resp;},
                    resp => {$scope.user.name = resp;}
                );

                $scope.searchInputAutoFocusToggle = () => {
                    $scope.searchInputAutoFocusEnabled = !$scope.searchInputAutoFocusEnabled;
                    paneFactory.searchInputAutoFocusEnabled = $scope.searchInputAutoFocusEnabled;
                };
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
            scope: { item: "=supplier", modalVisible: "=", getItems: '&?',
                requestParams: '=', user: '=?'},
            transclude: true,
            template: addEditSupplierTpl,
            controller : ($scope, itemFactory) => {
                return supplierChangeCtrlr($scope, itemFactory);
            }
        }
    })
    .component( "itemInputTotal", {
                bindings: {total: '<', requestsQuantity: '='},
                template:"<span ng-show='$ctrl.total > 0 && $ctrl.requestsQuantity === 0'>" +
                    "{{$ctrl.total}}</span>" +
                "<span class='glyphicon glyphicon-warning-sign' " +
                        "ng-show='$ctrl.requestsQuantity > 0'></span>",
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
    .component( "commonItemTextInput", {
        bindings: {
            text: '=',
            description: '@',
            requestsQuantity: '<?'
        },
        template:
        "<span>{{$ctrl.description}}:</span>" +
        "<span class='warning-item-input' ng-hide='$ctrl.text.length > 0'>" +
            "{{'Введите ' + $ctrl.description}}</span>" +
        "<input type='text' class='form-control' ng-model='$ctrl.text'" +
        "ng-readonly='$ctrl.requestsQuantity'" +
        "placeholder=''/>",
        controller: function() {}
    })
    .component( "datePicker", {
        bindings: {
            dateValue: '=',
            changeValue: '&?',
            readOnly: '<?'
        },
        template:
            "<input date-input class='form-control' type='datetime-local'" +
                "ng-readonly = '$ctrl.readOnly'" +
                "ng-change = '$ctrl.changeValue()'" +
                "ng-model='$ctrl.dateValue'/>",
        controller: function() {}
    })

    .component( "dateIntervalPicker", {
        bindings: {
            fromDate: '=',
            toDate: '=',
            pickerEnabled: '<?'
        },
        template:
        "<div style='display: flex;'>" +
            "<span class='date-picker-interval-notice'>c</span>" +
            "<div style='width: 50%;'>" +
               "<date-picker date-value='$ctrl.fromDate' read-only='$ctrl.pickerEnabled'></date-picker>" +
            "</div>" +
            "<span class='date-picker-interval-notice'>по</span>" +
            "<div style='width: 50%;'>" +
               "<date-picker date-value='$ctrl.toDate' read-only='$ctrl.pickerEnabled'></date-picker>" +
            "</div>" +
        "</div>",
        controller: function() {}
    })
    .component( "itemAddEditControls", {
        bindings: {
            appendData: '&',
            closeModal: '&',
            isOkActive: '<',
            isCloseHidden: '<?',
            itemId: '<'
        },
        template:
        "<div style='display: flex;'>" +
        "<div style='width: 10%'>"+
            "<button title='Применить' class='glyphicon glyphicon-ok common-ok-btn add'" +
            "ng-class='{disabled: !$ctrl.isOkActive}' style='font-size: 22px;' " +
            "ng-disabled='!$ctrl.isOkActive' ng-click='$ctrl.appendData()'></button></div>" +
            "<div style='width: 100%;'><item-add-edit-id item-id='$ctrl.itemId'></item-add-edit-id></div>" +
            "<span ng-hide='$ctrl.isCloseHidden' title='Закрыть' class='glyphicon glyphicon-remove item-blank'" +
                "style='margin-top: 4px;' ng-click='$ctrl.closeModal()'></span></div>",
        controller: function() {}
    })
    .component( "itemInputName", {
        bindings: {
            item: '<',
            items: '<',
            requestsQuantity: '<',
            title: '@inputName',
            getItems: '&',
            inputId: '<',
            keypressHandler: '&?'
        },
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
            "{{'Не выбран(а)(о) ' + $ctrl.title + '!'}}</span>" +
        "<textarea rows='2' title='{{$ctrl.item.ean + \"  \" + $ctrl.item.name}}' type='text' " +
            "class='form-control' placeholder={{$ctrl.title}} id={{$ctrl.inputId}} " +
                "ng-keydown='$ctrl.keypressHandler()($event, \"name\")'" +
                "ng-model='$ctrl.item.name'" +
                "ng-readonly='$ctrl.item.id || $ctrl.item === null'></textarea>" +
        "<span class='item-input-toolbox' ng-hide='$ctrl.item.id > 0 && $ctrl.requestsQuantity === 0'>" +
            "<item-input-total requests-quantity='$ctrl.requestsQuantity'" +
                "total='$ctrl.items.length'></item-input-total>" +
        "</span>",
        controller: function() {}
    })
    .component( "itemInputItems", {
        bindings: { items: '<', clearItem: '&',  selectItem: '&', changeItem: '&'},
        template: itemInputItemsTpl,
        controller: function() {}
    })
  .component( "itemInputItemsBlock", {
    bindings: {
      item: '<',
      items: '<',
      inputId: '<',
      requestsQuantity: '<',
      inputName: '@',
      getItems: '&',
      keypressHandler: '&?',
      clearItem: '&',
      selectItem: '&',
      changeItem: '&'
    },
    template:
      "<div class='dropdown'>" +
      "    <item-input-name input-id='$ctrl.inputId' item='$ctrl.item' input-name='{{$ctrl.inputName}}'" +
      "                     requests-quantity='$ctrl.requestsQuantity' items='$ctrl.items'" +
      "                     get-items='$ctrl.getItems()' keypress-handler='$ctrl.keypressHandler()'>" +
      "    </item-input-name>" +
      "    <ul class='dropdown-menu hoverable item-input-dropdown'>" +
      "        <item-input-items ng-hide='$ctrl.requestParams.requestsQuantity > 0'" +
      "                items='$ctrl.items' clear-item='$ctrl.clearItem()'" +
      "                select-item='$ctrl.selectItem()' change-item='$ctrl.changeItem()'>" +
      "        </item-input-items>" +
      "    </ul>" +
      "</div>",
    controller: function() {}
  })
    .component( "itemComponentsPicker", {
        bindings: { item: '=', modalVisible: '='},
        template: itemComponentsPickerTpl,
        controller: ['paneFactory', function(paneFactory) {
            this.addItemComponent = () => {
                let component = {
                    item: this.component,
                    quantity: paneFactory.fixIfFractional(this.componentQuantity, this.component.unit)
                };
                let index = paneFactory.checkDuplicateRowsByItem(component.item.id, this.item.components);
                index < 0 ? this.item.components.push(component)
                    : this.item.components[index].quantity += component.quantity;
            }
        }]
    })
    .factory('itemFactory',['httpService', 'paneFactory', '$filter',
        function (httpService, paneFactory, $filter) {

          let incompatableReqSymbols = ['%'];
          let checkReqForIncompatableSymbols = (request) => {
            if(typeof request === 'string') {
              incompatableReqSymbols.forEach(symbol => {
                let pos = request.indexOf(symbol);
                while (pos > -1) {
                  request = request.replace(symbol, "");
                  pos = request.indexOf(symbol);
                }
              });
            }
            return request;
          };

            let getNewBank = () => {return {name: ''};};
            let getNewSupplier = () => { return {name: ''};};
            let getNewBuyer = () => {return {name: '', bank: getNewBank()};};
            let getNewDocument = () => { return {name: '', date: '', supplier : getNewSupplier()};};
            let getNewSection = () => {return {name: ''};};
            let getNewItem = () => {
                return {
                    name: '', ean: '', alterName: '',
                    predefinedQuantity: 0, price: 0, eanSynonym: '',
                    section: getNewSection(),
                    component: {},
                    componentQuantity: 0,
                    components:[] };
            };

            let getItemById = (id, url, requestParams) => {
                if(id)
                    return httpService.getItemById({id, url, requestParams});
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
                        getItemByIdUrl: 'getBuyerById',
                        checkAndGetItem: (buyer) => {
                            if(angular.isDefined(buyer)
                                && typeof buyer === 'object'
                                && buyer !=null
                                && buyer.id > 0)
                                return buyer;
                            return getNewBuyer();
                        }
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
                itemComponentConfig : {
                        getEmptyItem: getNewItem,
                        getItemsUrl: 'getItemsForComponents',
                        getItemByIdUrl: 'getItemById'
                },
                compositeItemConfig : {
                    getEmptyItem: getNewItem,
                    getItemsUrl: 'getCompositeItems',
                    getItemByIdUrl: 'getItemById'
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
                getItems: ($s, url) => {
                    $s.items=[];
                    httpService.getItems({params: {filter: $s.item.name || ''},
                        requestParams: $s.requestParams, url})
                        .then(
                        (value) => {
                            $s.items = value;
                        },
                        (value) => {
                            $s.item.name = value;
                        }
                    );
                },
                selectItem: (id, $s, config) => {
                    // $s.items=[];
                    $s.getItemById(id).then(
                        item => {
                            $s.items=[item];
                            $s.item = item;
                            if(typeof config.selectItemForParent === 'function')
                                config.selectItemForParent($s);
                        },
                        resp => {$s.item.name = resp;}
                    );
                },
                clearItem: ($s) => {
                    $s.item = $s.getEmptyItem();
                    $s.getItems();
                    paneFactory.changeElementState(document.getElementById($s.inputId), ['focus']);
                },
                changeItem: (id, $s) => {
                    $s.items=[];
                    id > 0 ? $s.getItemById(id).then( item => { $s.item = item; }) :
                        $s.item = (angular.isDefined($s.item) && $s.item != null) ?
                            angular.extend({}, $s.item, {id: null})
                            : $s.getEmptyItem();
                    $s.addEditModalVisible = true;
                    $s.user = paneFactory.user;
                },
                addItem : ($s, url) => {
                    httpService.addItem({data: $s.item, url, requestParams: $s.requestParams})
                        .then(
                        resp => {
                            $s.item = resp.entityItem;
                            if(resp.success) {
                                $s.closeModal();
                            }
                        },
                        resp => { $s.item.name = resp; }
                    );
                },
                closeModal : ($s) => {
                    if($s.item.id > 0)
                        $s.getItems()($s.item.id);
                    $s.modalVisible = false;
                    $s.cannotBeComposite = false;
                    $s.warning ="";
                },
                openItemComponentsModal : ($s) => {
                    httpService.getItemById({id: $s.item.id, url: 'checkIfItemCannotBeComposite'})
                        .then(resp => {
                                $s.cannotBeComposite = resp;
                                $s.itemComponentsModalVisible = !resp;
                            }
                        );
                },
                setItemEanByTopId : (item) => {
                    httpService.getItemById({id: null, url: 'getTopId'})
                        .then(
                        resp => {
                            item.ean = paneFactory.generateEan((resp + 1).toString());
                        },
                        resp => {console.log(resp);}
                    );
                },
                setEanPrefix : ($s, e, field) => {
                    $s.item[field] = paneFactory.generateEanByKey(e, $s.item[field]);
                },
                getSearchTerms: ($s, url) => {
                    let searchTerms = new Subject();

                    $filter('async')(searchTerms
                        .pipe(
                            debounceTime(400),
                            map(keyword => keyword.trim()),
                            // filter(keyword => keyword.length > 0),
                            // filter(() => $scope.test),
                            // distinctUntilChanged(),
                            switchMap(keyword =>
                                httpService.getItemsRx({
                                    requestParams: $s.requestParams,
                                    url,
                                    params: '?filter=' + keyword
                                })),
                            tap((value) => {
                                $s.items = value;
                                console.log(value);
                            }),
                        ), $s);

                    return searchTerms;
                }
            };
        }
    ]);