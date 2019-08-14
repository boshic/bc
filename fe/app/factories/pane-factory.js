import snd from '../../media/audio/sell.mp3';

    angular.module('pane-factory', [])
        .factory('paneFactory',[ 'httpService', '$timeout', '$window',
            function ( httpService, $timeout, $window) {

                let barcodeLength = 13;
                let generateEanKeyCode = 192;
                let focusTimeout = 200;
                let successSound =  new Audio(snd);

                let fractionalUnits = ['кг', 'л', 'м'];

                let generateEan = (ean) => {
                    if (ean && (ean.valueOf() > 0)) {
                        let zeroes = barcodeLength - ean.length;
                        for(let i = 0; i < zeroes; i++)
                            ean = '0' + ean;
                    }
                    return ean;
                };

                let checkNumberLimit = (value, limit) => {
                    if((value < 0) || (value > limit))
                        return 0;
                    return value;
                };

                let getPages = numberOfPages => {
                    let pages = [];
                    for(let i = 0; i < numberOfPages; i++ )
                        pages[i] = i + 1;
                    return pages;
                };

                let getDiscountedPrice = (price, discount) => {
                        if(angular.isDefined(discount))
                            return (price - price * discount/100).toFixed(2);
                        else
                            return price;
                };

                let checkDuplicateRows = (value, rows) => {
                    if (rows.length > 0) {
                        for ( let i = 0; i < rows.length; i++)  {
                            if (rows[i].item.id === value.item.id) {
                                return i;
                            }
                        }
                    }
                    return -1;
                };

                let calcTotals = (rows, discount) => {
                    let totals = {quantity: 0, sum: 0};
                    rows.forEach(row =>  {
                        row.quantity = checkNumberLimit(row.quantity, row.currentQuantity);
                        (angular.isDefined(row.sum) && row.sum != null) ? totals.sum += +(row.sum) :
                            totals.sum += +(row.quantity * getDiscountedPrice(row.price, discount));
                        totals.quantity += +row.quantity;
                    });
                    return totals
                };

                return {
                    fractionalUnits,
                    successSound,
                    barcodeLength,
                    generateEan,
                    getDiscountedPrice,
                    getPages,
                    checkDuplicateRows,
                    calcTotals,
                    checkNumberLimit,
                    getItemByBarcode : (ean, getItems) => {
                        if ((ean.length === barcodeLength) && ean > 0) {
                            getItems(ean);
                            return true;
                        }
                    },
                    eanPrefix : {value: "000000000", keyCode: generateEanKeyCode},
                    generateEanByKey: (e, ean) => {
                        if (e.ctrlKey && e.keyCode === generateEanKeyCode)
                            return generateEan(ean);
                        return ean;
                    },
                    user : { name:"emptyUser" },
                    generateUuid: () => {
                        return Math.random().toString(36).substring(2, 15)
                            + Math.random().toString(36).substring(2, 15)
                    },
                    paneToggler: (pane) => {
                        for(let p of pane.$$childTail.panes) {
                            if(p.selected)
                                return p.name;
                        }
                    },
                    changeElementState: (element, states) => {
                        if(element != null)
                            states.forEach((state) => {
                                $timeout(function() {
                                    try {
                                        if(state === 'focus')
                                            this.focus();
                                        if(state === 'select')
                                            this.select();
                                    }
                                    catch (e) {
                                        console.log(e.message);
                                        // $window.location.reload();
                                    }
                                }.bind(element), focusTimeout);
                            });
                    },
                    keyupHandler: (e, callback1, callback2) => {
                        if (e.keyCode == 27)
                            callback1();
                        if (e.ctrlKey && e.keyCode == 13)
                            callback2();
                    },
                    findItemsByFilter: ($s, url)=> {
                        setTimeout(() => {
                            httpService.getItemsByFilter($s.filter, url).then(
                                resp => {
                                    $s.rows = resp.items;
                                    $s.pages = getPages(resp.numberOfPages);
                                    if (resp.success && $s.filter.calcTotal && resp.totals.length > 0)
                                        $s.totals = resp.totals;
                                    if('buyers' in resp)
                                        $s.filter.buyers = resp.buyers;
                                    if('suppliers' in resp)
                                        $s.filter.suppliers = resp.suppliers;
                                    if('sections' in resp)
                                        $s.filter.sections = resp.sections;
                                    if(typeof $s.setReports === 'function')
                                        $s.setReports();
                                    $s.filter.calcTotal = false;
                                },
                                resp => {
                                    console.log(resp);
                                }
                            );
                        }, 100);
                    },
                    releaseItems: ($s, url, params) => {
                        $s.requestsInProgress += 1;
                        if($s.canRelease) {
                            $s.checkRows();
                            let rows =   $s.rows;
                            $s.deleteRows();
                            httpService.addItem(rows, url, params)
                                .then(
                                    resp => {
                                        (resp.success) ? successSound.play() : $s.warning = resp.text;
                                        $s.warning = "Продано " + rows.length + " позиций.";
                                        $s.requestsInProgress -= 1;
                                    },
                                    (resp) => {
                                        $s.warning = "ошибка при проведении операции! Позиций - "
                                            + rows.length + ', время: ' + new Date().toLocaleTimeString();
                                        $s.rows = rows;
                                        $s.requestsInProgress -= 1;
                                    }
                                );
                        }
                    },
                    getItemsForRelease: (params, url, $s) => {
                        $s.requestsInProgress += 1;
                        httpService.getItems(params, url).then(
                            resp => {
                                if (resp.success) {
                                    $s.warning ="";
                                    let row = resp.item;
                                    row.coming = {
                                        item: row.item,
                                        stock: $s.stock
                                    };
                                    row.price = row.priceOut;
                                    row.vat = $s.stock.organization.vatValue;

                                    let index = checkDuplicateRows(row, $s.rows);
                                    let isFractional = fractionalUnits.indexOf(row.item.unit) >= 0;
                                    let quantity = row.quantity;

                                    if (index < 0) {
                                        $s.rows.splice(0, 0, angular.extend({}, row));
                                        index = 0;
                                        isFractional ? $s.rows[index].quantity = quantity :
                                            $s.rows[index].quantity = quantity || 1;
                                    }
                                    else
                                        isFractional ? $s.rows[index].quantity += quantity :
                                            $s.rows[index].quantity += quantity || 1;

                                    $s.checkRows();

                                    if(isFractional && !quantity) {
                                        // $s.rows[index].quantity = 0;
                                        $s.openQuantityChangerModal(index);
                                    }
                                } else {
                                    $s.warning =resp.text + "-" + params.filter;
                                }
                                $s.requestsInProgress -= 1;
                            },
                            resp => {
                                console.log("ошибка получения остатка товара!");
                                console.log(resp);
                                $s.requestsInProgress -= 1;
                            });
                    },
                    compareValues: function(first, second) {
                        let f = first.getTime ? first.getTime() : first;
                        let s = second.getTime ? second.getTime() : second;

                        if(angular.isObject(f, s))
                            return (('id' in f) && ('id' in s)) ? f.id === s.id :
                                !(('id' in f) || ('id' in s));

                        return f === s;
                    },
                    checkRowsForSelling: ($s, user) => {
                        $s.canRelease = false;
                        $s.totals = calcTotals($s.rows, $s.buyer.discount);
                        $s.reports = [];
                        if (angular.isDefined($s.buyer.id) && ($s.rows.length)) {
                            for(let row of $s.rows) {
                                      if (!row.quantity > 0 || !row.price) {
                                            return $s.canRelease = false;
                                        }
                                        // row.sum = (row.quantity * row.price).toFixed(2);
                                        row.user = user;
                                        row.buyer = $s.buyer;
                                        row.comment = $s.comment;
                            };
                            $s.canRelease = true;
                            $s.setReportData();
                        }
                    }
                };
            }
        ]);
// });
