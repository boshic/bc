import angular from 'angular';
import JsBarcode from 'jsbarcode';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap';

import '../../../factories/http-service';
import '../../../../css/prices.css';


let pricesCtrlr = ($location, $window, httpService, $s, $timeout) => {
            $s.reportData = {};
            $s.prices = [];

            let drawBarcodesTimeout = 200;

            let drawBarcodes = (prices) => {
                prices.forEach((price, index) => {
                    JsBarcode(document.getElementsByClassName('barcode')[index],
                        price.item.ean, {
                            fontSize: 12,
                            textMargin:0,
                            font:'arial',
                            width:0.8,
                            height:25
                        });
                });
            };

            $s.deletePrice = function () {
                $s.prices.splice(this.$index, 1);
                $timeout(() => {drawBarcodes($s.prices);}, drawBarcodesTimeout);
            };

            $s.copyPrice = function () {
                $s.prices.splice(this.$index, 0, this.price);
                $timeout(() => {drawBarcodes($s.prices);}, drawBarcodesTimeout);
            };

            httpService.getItemById({id: $location.search().id, url:'getReportById'}).then(
                resp => {
                    resp.rows.forEach((row) => {
                        let price = row.price;
                        row.rub = Math.trunc(price);
                        let kop = ((price - row.rub) * 100).toFixed(0);
                        row.kop = kop < 10? '0'+ kop : kop;
                        row.priceForOneUnit = row.item.perUnitQuantity > 0
                          ? (price/row.item.perUnitQuantity).toFixed(2) : 0;
                    });
                    $s.prices  = resp.rows;
                    $s.stock = resp.stock;
                    $s.reportData.showStamp = JSON.parse($location.search().stamp);

                    $timeout(
                        () => {
                            $window.document.title = " Цены товара №" +
                                resp.id + " от " + new Date(resp.date).toLocaleDateString();
                            drawBarcodes($s.prices);
                            $window.print();
                        }, drawBarcodesTimeout);
                },
                resp => { console.log(resp); }
            );
        };

    angular.module('prices', ['common-http-service'])

            .controller("pricesCtrl", ['$location', '$window', 'httpService', '$scope', '$timeout',
                ($location, $window, httpService, $scope, $timeout) => {
                    return pricesCtrlr($location, $window, httpService, $scope, $timeout);
                }
            ])

        .config(['$locationProvider', function($locationProvider) {
            $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            });
        }]);

    angular.bootstrap(document, ['prices']);