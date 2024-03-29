import angular from 'angular';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap';

import '../../../factories/http-service';
import '../../../factories/invoice-factory';
import '../../../factories/pane-factory';
import '../../../modules/modals/modals';
import '../../../modules/common/text-utils';
import '../../../modules/inputs/inputs';
import '../../../modules/reports/print-menu';
import '../../../filters/num-2-phrase';

import '../../../../css/writeOffAct.css';

(function() {

  // Get Angular's $http module.
  var initInjector = angular.injector(['ng']);
  var $http = initInjector.get('$http');

  // Get user info.
  $http.get('/getUser').then(
    function(success) {
      angular.module('userInfo', []).constant('userInfo', success.data);
    });
})();


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
    };
    modalFactory.openModal(undefined, [row], $s.modalData);
};


        let writeOffAct =  angular.module('writeOffAct', [
            'text-utils',
            'inputs',
            'modals',
            'userInfo',
            'num-2-phrase',
            'pane-factory',
            'print-menu',
            'invoice-factory',
            'common-http-service'
        ])
            .controller("ctrlr", ['invoiceFactory', '$location', '$window', 'httpService', '$scope',
                '$timeout', 'modalFactory', 'printFactory', 'userInfo',
                (invoiceFactory, $location, $window, httpService, $scope, $timeout, modalFactory, printFactory, userInfo) => {

                    $scope.reportData = {};
                    $scope.modalData = {hidden : true, row: {}};

                    $scope.openCommentModal = (index) => {
                        openTextEditModal($scope, index, modalFactory);
                    };

                    httpService.getItemById({id:$location.search().id, url:'getWriteOffActById'}).then(
                        resp => {
                            $scope.reportData  = resp;
                            $scope.reportData.totals = invoiceFactory.getTotals(resp);
                            $scope.reportData.showStamp = JSON.parse($location.search().stamp);
                            //
                            $timeout(
                                () => {
                                    $window.document.title = resp.buyer.name + " №" +
                                        resp.id + " от " + new Date(resp.date).toLocaleDateString();
                                    $window.print();
                                }, 200);
                        },
                        resp => { console.log(resp); }
                    );
                }])
            .config(['$locationProvider', function($locationProvider){
                $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
                });
            }]);


        // angular.bootstrap(document, ['writeOffAct']);
//     }
// );

(function() {

  // Get Angular's $http module.
  var initInjector = angular.injector(['ng']);
  var $http = initInjector.get('$http');

  // Get user info.
  $http.get('/getUser').then(
    function(success) {

      // Define a 'userInfo' module.
      angular.module('userInfo', []).constant('userInfo', success.data);

      angular.element(document).ready(function() {
        angular.bootstrap(document, ['writeOffAct']);
      });
    });
})();

