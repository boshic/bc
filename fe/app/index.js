import angular from 'angular';

import 'bootstrap/dist/css/bootstrap.min.css';

import 'bootstrap';
import '../css/styles.css';
import './modules/modals/modals';
import './modules/inputs/inputs';
import './modules/common/pane-elements';
import './modules/common/text-utils';
import './modules/filter/common-filter';
import './modules/reports/print-menu';
import './modules/coming/coming-item';
// import './modules/coming/new-coming-doc';
// import './modules/selling/selling-pane';

import './factories/http-service';
import './factories/user-service';
import './factories/buyer-factory';
import './factories/pane-factory';

// 'selling-pane',
    angular.module('barcode', [
    'common-http-service', 'user-service', 'pane-factory', 'buyer-factory',
    'inputs', 'modals', 'common-filter',
    'pane-elements', 'text-utils', 'print-menu',
     'coming-item',
      'userInfo'])
      .run(['itemFactory', '$rootScope', Run]);

  function Run (itemFactory, $rootScope) {
    itemFactory.getStocks()
      .then(
        resp => {
          // itemFactory.stocks = resp;
          $rootScope.$broadcast('StocksPrepared', resp);
        },
        () => {
        console.log('Список складов не загружен!')
      });
  };

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
        angular.bootstrap(document, ['barcode']);
      });
    });
})();

console.log('starting');