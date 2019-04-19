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

require(['bootstrap','css!../../css/bootstrap.css', 'css!../../css/writeOffAct.css']);

require(['angular', 'domReady!',
        '/scripts/factories/invoice-factory.js',
        '/scripts/modules/inputs/inputs.js',
        '/scripts/factories/pane-factory.js',
        '/scripts/factories/http-service.js',
        '/scripts/modules/modals/modals.js',
        '/scripts/modules/common/text-utils.js',
        '/scripts/filters/num-2-phrase.js'
    ],

    function (angular, document) {
        'use strict';

        let writeOffAct =  angular.module('writeOffAct', [
            'text-utils',
            'inputs',
            'modals',
            'num-2-phrase',
            'pane-factory',
            'invoice-factory',
            'common-http-service'
        ]);

        let openTextEditModal = ($s, index, modalFactory) => {
            let row;
            if(angular.isDefined(index))
                row = angular.extend($s.reportData.rows[index], {commentCause:'Причина списания'});
            else
                row = {commentCause:'Причина списания', comment: $s.reportData.rows[0].comment};
            $s.modalClose = () => {
                if(!angular.isDefined(index))
                    if (confirm("Подтвердите добавление причины списания для всех строк"))
                        $s.reportData.rows.map((r) => {r.comment = row.comment});

                //     httpService.addItem({text: row.comment, action: row.commentCause},
                //         'addSoldItemComment', {params: {id: row.id}}).then(
                //         () => {findItemsByFilter();},
                //         () => {console.log('comment has not been added!');}
                //     );
                // else findItemsByFilter();
            };
            modalFactory.openModal(undefined, [row], $s.modalData);
        };

        writeOffAct.controller("ctrlr", ['invoiceFactory', '$location', '$window', 'httpService', '$scope',
                    '$timeout', 'modalFactory',
            (invoiceFactory, $location, $window, httpService, $scope, $timeout, modalFactory) => {

                $scope.reportData = {};
                $scope.modalData = {hidden : true, row: {}};

                $scope.openCommentModal = (index) => {
                     openTextEditModal($scope, index, modalFactory);
                };

                httpService.getItemById($location.search().id, 'getWriteOffActById').then(
                    resp => {
                        $scope.reportData  = resp;
                        $scope.reportData.totals = invoiceFactory.getTotals(resp);
                        $scope.reportData.showStamp = JSON.parse($location.search().stamp);
                        //
                        $timeout(
                            () => {
                                $window.document.title = resp.buyer.name + " №" +
                                    resp.id + " от " + new Date(resp.date).toLocaleDateString();
                                // $window.print();
                            }, 200);
                    },
                    resp => { console.log(resp); }
                );
            }]);

        writeOffAct.config(['$locationProvider', function($locationProvider){
            $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            });
        }]);

        angular.bootstrap(document, ['writeOffAct']);
    }
);

