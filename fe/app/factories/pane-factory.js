import snd from '../../media/audio/sell.mp3';
// import 'angular1-async-filter';
import { BehaviorSubject, of, Subject, from } from 'rxjs';
import { filter, tap, map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import comingPaneConfig from '../modules/coming/coming-pane-config';
import soldPaneConfig from '../modules/selling/sold-pane-config';

    angular.module('pane-factory', [])
        .factory('paneFactory',[ 'httpService', '$timeout', '$filter',
            function ( httpService, $timeout, $filter) {

                let barcodeLength = 13;

                let generateEanKeyCode = 192;
                let backSpaceKeyCode = 8;
                let escKeyCode = 27;
                let enterKeyCode = 13;

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

                let checkRowsBeforeSelling = ($s, user) => {
                    if (angular.isDefined($s.buyer.id) && ($s.rows.length)) {
                        for(let row of $s.rows) {
                            if (!row.quantity > 0 || !row.price) {
                                return $s.canRelease = false;
                            }
                            row.user = user;
                            row.buyer = $s.buyer;
                            row.comment = $s.comment;
                        }
                        $s.canRelease = true;
                        $s.setReportData();
                    }
                };

                let checkRowsBeforeMoving = ($s, user) => {
                    if ($s.rows.length > 0) {
                        for (let row of $s.rows) {
                            let stockId = angular.isDefined($s.stockDest) ? $s.stockDest.id : row.coming.stock.id;
                            if (!row.quantity || $s.stock.id === stockId)
                                return $s.canRelease = false;
                            row.user = user;
                            row.comment = $s.comment;
                        }
                        $s.canRelease = true;
                    }
                };

                let checkNumberLimit = (value, limit) => {
                    if((!angular.isDefined(value)) || (value === null) ||(value === 'null') || (value < 0) || (value > limit))
                        return 0;
                    return value;
                };

                let getPages = numberOfPages => {
                    let pages = [];
                    for(let i = 0; i < numberOfPages; i++ )
                        pages[i] = i + 1;
                    return pages;
                };

                let isEanValid = (ean) => {
                    return (ean > 0 && ean.length === barcodeLength);
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
                        if (angular.isDefined(row.price))
                            row.price = checkNumberLimit(row.price, undefined);
                        if (angular.isDefined(row.vat))
                            row.vat = checkNumberLimit(row.vat, undefined);
                        row.quantity = checkNumberLimit(row.quantity, row.currentQuantity);
                        (angular.isDefined(row.sum) && row.sum != null) ? totals.sum += +(row.sum) :
                            totals.sum += +(row.quantity * getDiscountedPrice(row.price, discount));
                        totals.quantity += +row.quantity;
                    });
                    return totals
                };

                let getSearchTermsForGetItemsByFilter = ($s, url) => {

                    let searchTerms = new Subject();

                    $filter('async')(searchTerms
                        .pipe(
                            debounceTime(500),
                            switchMap(filter => {
                                return httpService.getItemsByFilterRx({requestParams: $s.requestParams,url,
                                    filter: JSON.parse(filter)});
                            }),
                            tap((resp) => {
                                if(resp.entityItems.length === 0 && $s.filter.page != 1) {
                                    $s.filter.calcTotal = true;
                                    $s.filter.page =  1;
                                }
                                else {
                                    $s.rows = resp.entityItems;
                                    $s.pages = getPages(resp.numberOfPages);
                                    if (resp.success && $s.filter.calcTotal && resp.totals.length > 0)
                                        $s.totals = resp.totals;
                                    if('buyers' in resp && resp.buyers != null )
                                        $s.filter.buyers = resp.buyers;
                                    if('suppliers' in resp && resp.suppliers != null)
                                        $s.filter.suppliers = resp.suppliers;
                                    if('sections' in resp && resp.sections != null)
                                        $s.filter.sections = resp.sections;
                                    // if('goods' in resp && resp.goods != null)
                                    //     $s.filter.items = resp.goods;
                                    if(typeof $s.setReports === 'function')
                                        $s.setReports();
                                    $s.filter.calcTotal = false;
                                }
                                // console.log(resp);
                                if(typeof $s.afterSearch === 'function')
                                    $s.afterSearch();
                            }),
                        ), $s);

                    return searchTerms;
                };

                let getItemsBySearchTermsAndFilter = (term, filter) => {
                    term.next(JSON.stringify(filter));
                };

                return {
                    fractionalUnits,
                    successSound,
                    barcodeLength,
                    isEanValid,
                    generateEan,
                    getDiscountedPrice,
                    getPages,
                    checkDuplicateRows,
                    calcTotals,
                    checkNumberLimit,
                    getItemsBySearchTermsAndFilter,
                    getSearchTermsForGetItemsByFilter,
                    comingPaneConfig,
                    soldPaneConfig,
                    getHttpService: () => {return httpService;},
                    getItemByBarcode : (ean, getItems) => {
                        if (isEanValid(ean)) {
                            getItems(ean);
                            return true;
                        }
                    },
                    keyCodes: {escKeyCode, enterKeyCode, backSpaceKeyCode},
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
                    keyUpHandler: (e, combinations) => {
                        combinations.forEach((comb) => {
                            if((e.keyCode === comb.keyCode)
                                && (!(comb.ctrlReq) || (comb.ctrlReq && e.ctrlKey)))
                                comb.doAction();
                        });
                    },
                    calcTotalsAndRefresh : (filter, findItems) => {
                        filter.calcTotal = true;
                        findItems();
                    },
                    releaseItems: ($s, url, params) => {
                        if($s.canRelease) {
                            $s.checkRows();
                            let rows =   $s.rows;
                            $s.deleteRows();
                            httpService.addItem({data: rows, url, params, requestParams:$s.requestParams})
                                .then(
                                    resp => {
                                        (resp.success) ? successSound.play() : $s.warning = resp.text;
                                        $s.warning = "Продано " + rows.length + " позиций.";
                                    },
                                    (resp) => {
                                        $s.warning = "ошибка при проведении операции! Позиций - "
                                            + rows.length + ', время: ' + new Date().toLocaleTimeString();
                                        $s.rows = rows;
                                    }
                                );
                        }
                    },
                    getItemsForRelease: (params, url, $s) => {
                        httpService.getItems({params, url, requestParams:$s.requestParams}).then(
                            resp => {
                                if (resp.success) {
                                    $s.warning ="";
                                    let row = resp.entityItem;
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
                            },
                            resp => {
                                console.log("ошибка получения остатка товара!");
                                console.log(resp);
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
                    checkRows: ($s, user, checkingType) => {
                        $s.canRelease = false;
                        $s.totals = calcTotals($s.rows, typeof $s.buyer === 'object' && $s.buyer.id > 0 ? $s.buyer.discount : undefined);
                        $s.reports = [];
                        if(checkingType === 'selling')
                            checkRowsBeforeSelling($s, user);
                        else
                            checkRowsBeforeMoving($s, user);
                    }
                };
            }
        ]);
// });
