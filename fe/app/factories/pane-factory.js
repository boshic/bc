import snd from '../../media/audio/sell.mp3';
import failSnd from '../../media/audio/fail.mp3';
// import 'angular1-async-filter';
import { BehaviorSubject, of, Subject, from } from 'rxjs';
import { filter, tap, map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import newComingDocConfig from '../modules/coming/new-coming-doc-config';
import comingPaneConfig from '../modules/coming/coming-pane-config';
import movingPaneConfig from '../modules/moving/moving-pane-config';
import sellingPaneConfig from '../modules/selling/selling-pane-config';
import soldPaneConfig from '../modules/selling/sold-pane-config';
import invoicesPaneConfig from '../modules/selling/invoices-pane-config';

    angular.module('pane-factory', [])
        .factory('paneFactory',[ 'userInfo', 'httpService', 'printFactory', '$timeout', '$filter',
            function ( userInfo, httpService, printFactory, $timeout, $filter) {

                let barcodeLength = 13;

                let generateEanKeyCode = 192;
                let backSpaceKeyCode = 8;
                let escKeyCode = 27;
                let enterKeyCode = 13;
                let keyCodes = {escKeyCode, enterKeyCode, backSpaceKeyCode};

                let focusTimeout = 200;
                let successSound =  new Audio(snd);
                let failSound =  new Audio(failSnd);

                let fractionalUnits = ['кг', 'л', 'м', 'км', 'ч'];
                let user = angular.extend({}, userInfo);

                let generateEan = (ean) => {
                    if (ean && (ean.valueOf() > 0)) {
                        let zeroes = barcodeLength - ean.length;
                        for(let i = 0; i < zeroes; i++)
                            ean = '0' + ean;
                    }
                    return ean;
                };

                let isMobileClient = () => {
                  return ('ontouchstart' in window);
                };

                let isItFunction = (func) => {
                  return (angular.isDefined(func) && typeof func === 'function');
                };

                let searchInputAutoFocusEnabled = !isMobileClient();

                let checkRowsBeforeSelling = ($s, user) => {
                    if (angular.isDefined($s.buyer.id) && ($s.rows.length)) {
                        $s.canRelease = true;
                        for(let row of $s.rows) {
                            row.user = user;
                            row.buyer = $s.buyer;
                            row.comment = $s.comment;
                            if (!(row.quantity > 0) || !(row.price > 0)) {
                              $s.canRelease = false;
                            }
                        }
                        if($s.canRelease)
                          $s.setReportData();
                    }
                };
                let checkRowsBeforeMoving = ($s, user) => {
                    if ($s.rows.length > 0) {
                        for (let row of $s.rows) {
                            let stockDestId = angular.isDefined($s.stockDest) ? $s.stockDest.id : row.coming.stock.id;
                            let stockId = angular.isDefined($s.stock) ? $s.stock.id : $s.filter.stock.id;
                            row.user = user;
                            row.comment = $s.comment;
                            if (!row.quantity || stockId === stockDestId)
                              return $s.canRelease = false;

                        }
                        $s.canRelease = true;
                    }
                };

              let checkRowsBeforeComing = ($s, user) => {
                if ($s.rows.length) {
                    $s.totalsOut = calcTotals(printFactory.getRowsForReports($s, 'priceOut'));
                    for (let row of $s.rows) {
                      if (!(row.quantity > 0) || !(row.price > 0) || !(row.priceOut > 0))
                        return;

                      row.user = user;
                      row.stock = $s.stock;
                      row.doc = $s.doc;
                  }
                  if($s.totals.quantity > 0 && $s.totals.sum > 0 && $s.doc.id > 0)
                      $s.canRelease = true;
              }
                $s.setReportData();
              };

                let checkingsBeforeRelease = {
                  selling: checkRowsBeforeSelling,
                  newComing: checkRowsBeforeComing,
                  moving: checkRowsBeforeMoving
                };

                let checkNumberLimit = (value, limit) => {
                    if((!angular.isDefined(value)) || (value === null) ||(value === 'null') || (value < 0) || (value > limit))
                        return 0;
                    return value;
                };
                let fixIfFractional = (value, unit) => {
                    return fractionalUnits.indexOf(unit) < 0 ? +value.toFixed(0) : +value.toFixed(3);
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
                let checkDuplicateRowsByItem = (itemId, rows) => {
                    if (rows.length > 0) {
                        for ( let i = 0; i < rows.length; i++)  {
                            if (rows[i].item.id === itemId) {
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

                let sendDataAboutDeletionsFromSellingPane = ($s, index, url, params) => {
                  let rows = [];
                  (index >= 0) ?
                      rows = angular.extend([], [$s.rows[index]])
                  : rows = angular.extend([], $s.rows);
                    httpService.addItem({data: rows, url, params, requestParams:$s.requestParams});

                };

                let getNonNullFieldRows = (rows, field) => {
                  let result = [];
                  rows.forEach(row => {
                    if (row[field] > 0)
                      result.push(row);
                  });
                  return result;
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
                                if(resp.entityItems.length === 0 && $s.filter.page !== 1) {
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
                                    if(isItFunction($s.setReports))
                                        $s.setReports();
                                    $s.filter.calcTotal = false;
                                }
                                // console.log(resp);
                                if(isItFunction($s.afterSearch))
                                    $s.afterSearch();
                            }),
                        ), $s);

                    return searchTerms;
                };

                let getItemsBySearchTermsAndFilter = (term, filter) => {
                    term.next(JSON.stringify(filter));
                };

                let generateUuid = () => {
                    return Math.random().toString(36).substring(2, 15)
                        + Math.random().toString(36).substring(2, 15)
                };

                let keyUpHandler = (e, combinations) => {
                    combinations.forEach((comb) => {
                        if((e.keyCode === comb.keyCode)
                            && (!(comb.ctrlReq) || (comb.ctrlReq && e.ctrlKey)))
                            comb.doAction();
                    });
                };

                return {
                    user,
                    fractionalUnits,
                    successSound,
                    failSound,
                    barcodeLength,
                    searchInputAutoFocusEnabled,
                    keyCodes,
                    isEanValid,
                    isItFunction,
                    isMobileClient,
                    keyUpHandler,
                    generateUuid,
                    fixIfFractional,
                    generateEan,
                    getDiscountedPrice,
                    getPages,
                    checkDuplicateRowsByItem,
                    calcTotals,
                    checkNumberLimit,
                    getItemsBySearchTermsAndFilter,
                    getSearchTermsForGetItemsByFilter,
                    invoicesPaneConfig,
                    comingPaneConfig,
                    newComingDocConfig,
                    sellingPaneConfig,
                    movingPaneConfig,
                    soldPaneConfig,
                    getHttpService: () => {return httpService;},
                    getItemByBarcode : (ean, getItems) => {
                        if (isEanValid(ean)) {
                            getItems(ean);
                            return true;
                        }
                    },
                    setPaneDefaults: ($s, params) => {

                        $s.rows = [];
                        $s.comment = '';
                        $s.warning = '';
                        $s.user = user;
                        $s.searchInputId = generateUuid();
                        $s.quantityChangerModalData = { hidden : true, row: {} };
                        $s.requestParams = {requestsQuantity: 0};
                        $s.handleKeyup = e => {
                            keyUpHandler(e, params.config.getKeyupCombinations($s, keyCodes));
                        };
                        $s.resetFilter = () => {
                            params.config.resetFilter(params.filterFactory, $s.filter);
                        };
                        $s.filterByItem = (row) => {
                          $s.filter.item = row.item;
                        };

                    },
                    // keyCodes: {escKeyCode, enterKeyCode, backSpaceKeyCode},
                    eanPrefix : {value: "000000000", keyCode: generateEanKeyCode},
                    generateEanByKey: (e, ean) => {
                        if (e.ctrlKey && e.keyCode === generateEanKeyCode)
                            return generateEan(ean);
                        return ean;
                    },
                    paneToggler: (pane) => {
                        for(let p of pane.$$childTail.panes) {
                            if(p.selected)
                                return p.paneId || p.name;
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
                    calcTotalsAndRefresh : (filter, findItems) => {
                        filter.calcTotal = true;
                        findItems();
                    },
                    releaseItems: ($s, url, params) => {
                        if($s.canRelease) {
                            $s.checkRows();
                            let rows = $s.rows;
                            $s.rows = [];
                            $s.deleteRows();
                            httpService.addItem({data: rows, url, params, requestParams:$s.requestParams})
                                .then(
                                    resp => {
                                        if (resp.success)
                                            successSound.play();
                                        $s.warning = resp.text;
                                    },
                                    (resp) => {
                                        $s.warning = "ошибка при проведении операции! Позиций - "
                                            + rows.length + ', время: ' + new Date().toLocaleTimeString();
                                        $s.rows = rows;
                                      if(angular.isDefined($s.buyer) && isItFunction($s.getEmptyBuyer))
                                        $s.buyer = $s.getEmptyBuyer();
                                    }
                                );
                        }
                    },
                    deletePaneRows: ($s, config) => {
                        let i = config.itemId > 0 ? checkDuplicateRowsByItem(config.itemId, $s.rows) : -1;
                        if (angular.isDefined(config.deletionTrackingUrl)
                          && user.actsAllowed.indexOf('erasesTrackingEnabled')>-1)
                            sendDataAboutDeletionsFromSellingPane($s, i, config.deletionTrackingUrl);
                        if (i >= 0)
                            $s.rows.splice(i, 1);
                        else {
                            $s.rows =
                              (angular.isDefined($s.buyer) && $s.buyer.id > 0) ?
                              getNonNullFieldRows($s.rows, 'quantity') : [];
                            if(angular.isDefined($s.comment))
                                $s.comment = "";
                            if(isItFunction(config.getEmptyBuyer) && ($s.rows.length === 0))
                                $s.buyer = config.getEmptyBuyer();
                            if(isItFunction(config.getEmptyDoc))
                                $s.doc = config.getEmptyDoc();

                        }
                        $s.blankSearch();
                    },
                    getItemsForRelease: (params, url, $s) => {
                        httpService.getItems({params, url, requestParams:$s.requestParams}).then(
                            resp => {
                                if (resp.success) {
                                    $s.warning ="";
                                    let stock = angular.isDefined($s.stock) ? $s.stock : $s.filter.stock;
                                    let row = resp.entityItem;
                                    row.coming = {
                                        item: row.item, stock
                                    };
                                    row.priceIn = row.price;
                                    row.price = row.priceOut;
                                    row.vat = stock.organization.vatValue;

                                    let index = checkDuplicateRowsByItem(row.item.id, $s.rows);
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

                                    if(((angular.isDefined($s.filter) && $s.filter.alwaysOpenQuantityChangerModal)
                                        || isFractional)
                                        && !quantity) {
                                        $s.openQuantityChangerModal(row.item.id);
                                    }
                                } else {
                                    $s.warning =resp.text + "-" + params.filter;
                                    failSound.play();
                                }
                            },
                            resp => {
                                failSound.play();
                                console.log("ошибка получения остатка товара!");
                                console.log(resp);
                            });
                    },
                    getItemsForReleaseByFilter: ($s) => {
                        let index = 0;
                        httpService.getItemsByFilter(
                            {filter: $s.filter, url: 'getComingsForReleaseByFilter', requestParams:$s.requestParams}
                            ).then(
                            resp => {
                                if (resp.success) {
                                  resp.entityItems.forEach(row => {
                                      index = checkDuplicateRowsByItem(row.item.id, $s.rows);
                                      if (index < 0) {
                                        row.coming = {item : row.item, stock: row.stock};
                                        row.vat = row.stock.organization.vatValue;
                                        // row.buyer = $s.buyer;
                                        // row.comment =  (row.comment !== null && row.comment.length > 0) ? row.comment : '';
                                        $s.rows.push(row);
                                      }
                                    });
                                    if (angular.isDefined($s.buyer) && ($s.filter.invoiceNumber > 0 )) {
                                        httpService.getItemById({id:$s.filter.invoiceNumber, url:'getInvoiceById'}).then(
                                          resp => {
                                            $s.buyer = resp.buyer;
                                            if(angular.isDefined($s.comment))
                                              $s.comment = "счет-фактура №" + resp.id + " от " + new Date(resp.date).toLocaleDateString();
                                          },
                                          resp => {
                                            console.log(resp);
                                          }
                                        );
                                    }
                                } else {
                                    $s.warning = resp.text;
                                }
                            },
                            resp => {
                                console.log("ошибка получения остатков товаров по фильтру!");
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
                    checkRows: ($s, usr, checkingType) => {
                        $s.canRelease = false;
                        $s.totals = calcTotals($s.rows, typeof $s.buyer === 'object' && $s.buyer.id > 0 ? $s.buyer.discount : undefined);
                        $s.reports = [];
                        if (typeof checkingsBeforeRelease[checkingType] ==='function')
                          checkingsBeforeRelease[checkingType]($s, user);
                    }
                };
            }
        ]);
// });
