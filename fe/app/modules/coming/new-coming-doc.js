import newComingDocPaneTpl from './new-coming-doc.html';

    let newComingCtrlr = ($s, httpService, paneFactory, elem, printFactory, modalFactory) => {

        $s.rows = [];
        $s.allowAllStocks = false;

        $s.item = {name: ''};
        $s.buyer = {name: ''};
        $s.quantityChangerModalData = {hidden : true, row: {}};

        $s.barcode = "";
        $s.warning = "";
        $s.canRelease = false;
        $s.totals = {sum: 0, quantity: 0};

        let getDataFromLastRow = () => {
            if($s.rows.length > 0)
                return {sum: $s.rows[0].sum, price: $s.rows[0].price,
                            quantity: 0, priceOut: $s.rows[0].priceOut};
            return {quantity: 1, sum:0, price: 0, priceOut: 0};
        };

        let getRowsByPriceOut =() => {
                let rows = [];
                $s.rows.forEach((row) => {
                    rows.push({item: row.item, quantity: row.quantity, doc: $s.doc,
                        price: row.priceOut, vat: $s.stock.organization.vatValue});
                });
                return rows;
        };

        let setReportData = () => {
            let data = { stock: $s.stock, buyer: $s.buyer, id: undefined, rows: getRowsByPriceOut()};
            $s.reports = [];
            if(angular.isDefined($s.doc.id))
                printFactory.setReportsByParams([{type: 'prices', data, method: 'addComingReport'}], $s.reports);
            if(angular.isDefined($s.buyer.id))
                printFactory.setReportsByParams([
                    {type: 'salesReceipt', data, method: 'addInvoice'},
                    {type: 'invoiceWithContract', data, method: 'addInvoice'},
                        {type: 'invoice', data, method: 'addInvoice'}], $s.reports);
        };

        let getItems = ean => {
            $s.warning="";

            httpService.getItems({filter: ean}, 'getItemForNewComing').then(
                resp => {
                    $s.canRelease = false;
                    let item = resp.item;
                    if (item != null) {

                        let index = paneFactory.checkDuplicateRows({item}, $s.rows);
                        if (index < 0) {
                            (resp.priceOut !== 0) ?
                                $s.rows.splice(0,0, {
                                        item: item,
                                        doc: {name:""},
                                        quantity: 1,
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
                            $s.rows[index].quantity += 1;
                        }
                        $s.checkRows();
                        if(paneFactory.fractionalUnits.indexOf(item.unit) >= 0) {
                            $s.rows[index].quantity = 0;
                        }
                        $s.openQuantityChangerModal(index);

                    } else {
                        $s.warning = "Такого товара нет, нужно добавить!";
                        $s.item = {name: ean, ean: ean};
                    }
                },
                reps => {
                    $s.warning="Произошла ошибка во время обработки запроса.";
                    console.log(resp);
                }
            );
        };

        $s.editItem = (name) => {
            $s.item = {name: name};
        };

        $s.$watch("item", (nv) => {
            if ((nv) && ("id" in nv)) {
                getItems($s.item.ean);
                // $s.item = {name: ''};
                $s.blankSearch();
            }
        }, true);

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
            if (data.event != null && paneFactory.paneToggler(data.pane) === 'Новый')
                $s.blankSearch();
        });

        $s.checkRows = () => {
            $s.canRelease = false;

            if ($s.rows.length) {
                $s.totals = paneFactory.calcTotals($s.rows);
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

        $s.deleteRows = function () {
            if(this.$index >= 0) {
                $s.rows.splice(this.$index,1)
            }else {
                $s.rows=[];
                $s.doc={name:""};
            }
            $s.blankSearch();
        };

        $s.makeComing = () => {
            if($s.canRelease) {
                httpService.addItem($s.rows, 'addComings').then(
                    resp => {
                        $s.deleteRows();
                    },
                    resp => {
                        $s.warning = "ошибка при проведении операции! Позиций - "
                            + $s.rows.length + ', время: ' + new Date().toLocaleTimeString();
                    }
                );
            }
        };

        $s.handleKeyup = e => {
            paneFactory.keyupHandler(e, $s.openQuantityChangerModal, $s.makeComing);
        };

        $s.blankSearch = () => {
            $s.barcode = "";
            $s.item = {name: ''};
            $s.warning = "";
            paneFactory.changeElementState(document.getElementById('new-coming-doc'), ['focus']);
        };

        $s.openQuantityChangerModal = (index) => {
            modalFactory.openModal(index, $s.rows, $s.quantityChangerModalData);
        };
    };

    angular.module('new-coming-doc', [])
        .directive( "newComingDoc", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: newComingDocPaneTpl,
                // templateUrl: 'html/coming/new-coming-doc.html',
                controller: ($scope, httpService, paneFactory, $element, printFactory, modalFactory) => {
                    return newComingCtrlr($scope, httpService, paneFactory, $element, printFactory, modalFactory);
                },
                link: (scope,elem,attrs) => {
                }
            }
        });