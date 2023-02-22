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
// import './parts/common-reports-parts';

import '../../../../css/packingList.css';

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

let tnComment = "Товар не входит в Перечень 1 ПСМ No713 от 19.10.22";
let accompDocs = "товарная накладная № _______ серия ____";
let overheadRestricted = "цена импортера: _______, опт.надб.: ____ %";

let openTextEditModal = ($s, index, modalFactory) => {
    let row;
    let isTablesRow = (index >= 0);
    if(angular.isDefined(index))
        row = (isTablesRow) ? angular.extend($s.reportData.rows[index], {commentCause:'Строки примечания'})
              : {commentCause:'Комментарий к накладной', comment: $s.reportData[index]};

     $s.modalClose = () => {
        if(!isTablesRow)
          $s.reportData[index] = row.comment;

        window.document.title = $s.reportData.accompDocs + " "
          + $s.reportData.buyer.name + " от " + $s.reportData.dateLocal;
    };

    modalFactory.openModal(undefined, [row], $s.modalData);
};

        let packingList =  angular.module('packingList', [
            'text-utils',
            'inputs',
            'modals',
            'userInfo',
            'num-2-phrase',
            'pane-factory',
            'print-menu',
            'invoice-factory',
            // 'common-reports-parts',
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

                    httpService.getItemById({id:$location.search().id, url:'getInvoiceById'}).then(
                        resp => {
                            $scope.reportData  = resp;
                            $scope.reportData.totals = invoiceFactory.getTotals(resp);
                            $scope.reportData.showStamp = JSON.parse($location.search().stamp);
                            $scope.reportData.shipmentBasedOn = "счет-фактура " + " №" +
                                    resp.id + " от " + new Date(resp.date).toLocaleDateString();

                            $scope.reportData.rows.map((r) => {
                              if (!r.comment.length > 0)
                                r.comment = (r.item.section.percOverheadLimit > 0) ? overheadRestricted : tnComment;
                            });

                            $scope.reportData.accompDocs = accompDocs;
                            $scope.reportData.dateLocal = new Date(resp.date).toLocaleString(
                              'ru', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric'
                            });
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
        angular.bootstrap(document, ['packingList']);
      });
    });
})();

