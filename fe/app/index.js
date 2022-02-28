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
import './modules/coming/new-coming-doc';
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
     'coming-item', 'new-coming-doc'])
      .run(['itemFactory', Run]);

  function Run (itemFactory) {
    itemFactory.getStocks()
      .then(
        resp => {
          itemFactory.stocks = resp;
        },
        () => {
        console.log('Список складов не загружен!')
      });
  };

console.log('starting');


