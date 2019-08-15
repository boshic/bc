import angular from 'angular';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap';

import '../../../factories/http-service';
import '../../../factories/invoice-factory';
import '../../../filters/num-2-phrase';
import './parts/common-reports-parts';

import '../../../../css/invoice.css';

        let invoiceCtrlr = (invoiceFactory, $location, $window, httpService, $s) => {

            $s.reportData = {};

            httpService.getItemById({id:$location.search().id, url:'getInvoiceById'}).then(
                resp => {
                    $s.reportData = resp;
                    $s.reportData.totals = invoiceFactory.getTotals(resp);
                    $s.reportData.showStamp = JSON.parse($location.search().stamp);

                    setTimeout(
                        () => {
                            $window.document.title = resp.buyer.name + " №" +
                                resp.id + " от " + new Date(resp.date).toLocaleDateString() +
                                ", " +resp.stock.name;
                            // $window.print();
                        }, 200);
                },
                resp => { console.log(resp); }
            );
        };

        angular.module('invoice', [
                'num-2-phrase',
                'common-reports-parts',
                'invoice-factory',
                'common-http-service'
            ])
            .controller("invoiceCtrl", ['invoiceFactory', '$location', '$window', 'httpService', '$scope',
            (invoiceFactory, $location, $window, httpService, $scope) => {
                return invoiceCtrlr(invoiceFactory, $location, $window, httpService, $scope);
            }])
            .config(['$locationProvider', function($locationProvider){
            $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            });
        }]);

        angular.bootstrap(document, ['invoice']);
//     }
// );
