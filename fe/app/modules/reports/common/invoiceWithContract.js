requirejs.config({
    baseUrl: '../../../scripts/libs',
    paths: {
        angular:'angular.min',
        domReady:'domReady',
        bootstrap: 'bootstrap.min',
        jquery : 'jquery-3.2.1.min'
    },
    shim: {
        bootstrap : ['jquery'],
        angular: { exports: 'angular'},
        barcode: { exports: 'barcode'},
        jquery: { exports: '$'}
    },
    waitSeconds: 60
});

require(['bootstrap','css!../../css/bootstrap.css', 'css!../../css/styles.css']);

require(['angular', 'domReady!',
        '/scripts/modules/printing-parts/common/invoice-contract-header.js',
        '/scripts/modules/printing-parts/common/invoice-contract-body.js',
        '/scripts/modules/printing-parts/common/common-reports-parts.js',
        '/scripts/modules/printing-parts/common/invoice-body.js',
        '/scripts/factories/invoice-factory.js',
        '/scripts/factories/http-service.js',
        '/scripts/filters/num-2-phrase.js'
    ],

    function (angular, document) {
        'use strict';

        let invoiceWithContract =  angular.module('invoiceWithContract', [
            'invoice-contract-header',
            'invoice-body',
            'contract-footer',
            'invoice-contract-body',
            'invoice-factory',
            'common-http-service'
        ]);

        invoiceWithContract.controller("ctrlr", ['invoiceFactory', '$location', '$window', 'httpService', '$scope',
                (invoiceFactory, $location, $window, httpService, $scope) => {

                    $scope.reportData = {};

                    httpService.getItemById($location.search().id, 'getInvoiceById').then(
                        resp => {
                            $scope.reportData  = resp;
                            $scope.reportData.totals = invoiceFactory.getTotals(resp);
                            $scope.reportData.showStamp = JSON.parse($location.search().stamp);
                            //
                            setTimeout(
                                () => {
                                    $window.document.title = resp.buyer.name + " №" +
                                        resp.id + " от " + new Date(resp.date).toLocaleDateString();
                                    // $window.print();
                                }, 200);
                        },
                        resp => { console.log(resp); }
                    );
                }]);

        invoiceWithContract.config(['$locationProvider', function($locationProvider){
                $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
                });
            }]);

        angular.bootstrap(document, ['invoiceWithContract']);
    }
);

