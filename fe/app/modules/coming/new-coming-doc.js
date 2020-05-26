import newComingDocPaneTpl from './new-coming-doc.html';

    let newComingCtrlr = ($s, httpService, paneFactory, elem, printFactory, modalFactory, itemFactory) => {

        $s.rows = [];
        $s.allowAllStocks = false;
        $s.itemInputVisible = false;
        $s.searchInputId = paneFactory.generateUuid();

        let getEpmtyItem = itemFactory.itemConfig.getEmptyItem;
        $s.item = getEpmtyItem();
        $s.buyer = itemFactory.buyerConfig.getEmptyItem();
        $s.quantityChangerModalData = {hidden : true, row: {}};
        $s.requestParams = {requestsQuantity: 0};
        $s.filter = {visible: false, allowAllStocks: false, sortField: '$index', reverseOrder: false};

        $s.barcode = "";
        $s.warning = "";
        $s.canRelease = false;
        $s.totalsByComingVisible = true;
        $s.totals = { sum: 0, quantity: 0 };
        $s.totalsOut = { sum: 0, quantity: 0 };

        let getDataFromLastRow = () => {
            if($s.rows.length)
                return {sum: $s.rows[0].sum, price: $s.rows[0].price,
                            quantity: 0, priceOut: $s.rows[0].priceOut};
            return {quantity: 0, sum:0, price: 0, priceOut: 0};
        };

        let getRowsForReports =() => {
            return printFactory.getRowsForReports($s, 'priceOut');
        };

        let setReportData = () => {
            $s.reports = [];
            if($s.rows.length) {
                let data = { stock: $s.stock, buyer: $s.buyer, id: undefined, rows: getRowsForReports()};
                if(angular.isDefined($s.doc.id))
                    printFactory.setReportsByParams([{type: 'prices', data, method: 'addComingReport'}], $s.reports);
                if(angular.isDefined($s.buyer.id))
                    printFactory.setReportsByParams([
                        {type: 'salesReceipt', data, method: 'addInvoice'},
                        {type: 'invoiceWithContract', data, method: 'addInvoice'},
                        {type: 'workCompletionStatement', data, method: 'addInvoice'},
                        {type: 'invoice', data, method: 'addInvoice'}], $s.reports);
            }
        };

        let getItems = ean => {
            $s.warning="";

            httpService.getItems({params: {filter: ean},
                    url: 'getItemForNewComing', requestParams:$s.requestParams})
                .then(
                resp => {
                    $s.canRelease = false;
                    let item = resp.item;
                    if (item != null) {

                        let index = paneFactory.checkDuplicateRowsByItem(item.id, $s.rows);
                        if (index < 0) {
                            (resp.priceOut !== 0) ?
                                $s.rows.splice(0,0, {
                                        item: item,
                                        doc: {name:""},
                                        quantity: 0, //1
                                        sum: resp.price,
                                        price: resp.price,
                                        priceOut: resp.priceOut
                                    })
                                    : $s.rows.splice(0,0, angular.extend({
                                            item: item,
                                            doc: {name:""}
                                        }, getDataFromLastRow()));
                            index = 0;
                        } else {
                            $s.rows[index].item = item;
                            // $s.rows[index].quantity += 1;
                        }
                        $s.checkRows();
                        // if(paneFactory.fractionalUnits.indexOf(item.unit) >= 0) {
                        //     $s.rows[index].quantity = 0;
                        // }
                        $s.openQuantityChangerModal(item.id);

                    } else {
                        $s.warning = "Такого товара нет, нужно добавить!";
                        $s.itemInputVisible = true;
                        $s.item = angular.extend(getEpmtyItem(), {name: ean, ean: ean});
                    }
                },
                reps => {
                    $s.warning="Произошла ошибка во время обработки запроса.";
                    console.log(resp);
                }
            );
        };

        $s.editItem = (name) => {
            $s.item = angular.extend(getEpmtyItem(), {name: name, ean: name});
            $s.itemInputVisible = !$s.itemInputVisible;
        };

        $s.getItems = (ean) => {
            if(paneFactory.getItemByBarcode(ean, getItems))
                $s.blankSearch();
            console.log('got items for parent');
        };

        $s.$watch('barcode', (nv) => {
            if(nv && paneFactory.getItemByBarcode($s.barcode, getItems))
                $s.barcode ='';
        });

        $s.$watchCollection("[doc, stock, buyer]", (nv) => {
            if ((nv.indexOf(undefined) < 0))
                $s.checkRows();
        }, true);

        $s.$watch("rows", (nv, ov) => {
            if ((nv) && (nv.length) && (ov) && (ov.length)) {
                $s.checkRows();
                if (ov.length === nv.length)
                    for( let i = 0; i < nv.length; i++)
                        for (let key in nv[i]) {
                            if ((angular.isDefined(ov[i][key], nv[i][key])
                                && ((ov[i][key] != null) && (nv[i][key] != null))
                                && (!paneFactory.compareValues(nv[i][key], ov[i][key])))) {
                                if(key === 'quantity') {
                                    nv[i].priceOut = (nv[i].priceOut < 0) ? 0 : nv[i].priceOut;
                                    nv[i].sum = (nv[i].price*nv[i].quantity).toFixed(2);
                                }
                                if(key === 'sum') {
                                    nv[i].sum = (nv[i].sum < 0)? 0 : nv[i].sum;
                                    if(nv[i].quantity > 0)
                                        nv[i].price = (nv[i].sum/nv[i].quantity).toFixed(4);
                                }
                                $s.checkRows();
                            }
                        }
            }
        }, true);

        $s.setEanPrefix = e => {
            $s.barcode = paneFactory.generateEanByKey(e, $s.barcode);
        };

        $s.$on("tabSelected",  (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === 'Новый') {
                $s.blankSearch();
            }
        });

        $s.checkRows = () => {
            $s.canRelease = false;

            if ($s.rows.length) {
                $s.totals = paneFactory.calcTotals($s.rows);
                $s.totalsOut = paneFactory.calcTotals(printFactory.getRowsForReports($s, 'priceOut'));
                for (let row of $s.rows) {

                    if ((!row.quantity) || (!row.price) || (!row.priceOut))
                        return $s.canRelease = false;

                    row.user = paneFactory.user;
                    row.stock = $s.stock;
                    row.doc = $s.doc;
                }
                if($s.totals.quantity > 0 && $s.totals.sum > 0 && $s.doc.id > 0)
                    $s.canRelease = true;
                setReportData();
            }
        };

        $s.deleteRows =  (itemId) => {
            paneFactory.deletePaneRows($s, {itemId, getEmptyDoc: itemFactory.documentConfig.getEmptyItem});
        };

        $s.makeComing = () => {
            if($s.canRelease) {
                $s.canRelease = false;
                httpService.addItem({data: $s.rows, url: 'addComings', requestParams:$s.requestParams})
                    .then(
                    resp => {
                        resp.entityItem.success ? $s.deleteRows() : $s.warning = resp.entityItem.text;

                    },
                    resp => {
                        $s.warning = "ошибка при проведении операции! Позиций - "
                            + $s.rows.length + ', время: ' + new Date().toLocaleTimeString();
                    }
                );
            }
        };

        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [
                {keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
                {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.makeComing, ctrlReq: true}
            ]);
        };

        $s.blankSearch = () => {
            $s.barcode = "";
            $s.item = getEpmtyItem();
            $s.warning = "";
            $s.itemInputVisible = false;
            paneFactory.changeElementState(document.getElementById($s.searchInputId), ['focus']);
            if(!angular.isDefined($s.buyer.id) && !$s.rows.length)
                $s.buyer = itemFactory.buyerConfig.checkAndGetItem(paneFactory.user.buyer);
        };

        $s.openQuantityChangerModal = (itemId) => {
            if($s.rows.length)
                modalFactory.openModalWithConfig({itemId, rows: $s.rows,
                    availQuantityField : 'currentQuantity',
                    limitQuantityField : 'currentQuantity',
                    modalData: $s.quantityChangerModalData});
        };
    };

    angular.module('new-coming-doc', [])
        .directive( "newComingDoc", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: newComingDocPaneTpl,
                controller: ($scope, httpService, paneFactory, $element, printFactory, modalFactory, itemFactory) => {
                    return newComingCtrlr($scope, httpService, paneFactory, $element, printFactory, modalFactory, itemFactory);
                },
                link: (scope,elem,attrs) => {
                }
            }
        });