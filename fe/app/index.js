import angular from 'angular';

import 'bootstrap/dist/css/bootstrap.min.css';
// import 'webpack-icons-installer/bootstrap';
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
import './modules/moving/moving-pane';
import './modules/selling/selling-pane';
// import './modules/selling/sold-pane';
import './modules/selling/invoices-pane';


import './factories/http-service';
import './factories/user-service';
import './factories/buyer-factory';
import './factories/pane-factory';

// 'coming-pane',
// 'sold-pane',

    angular.module('barcode', [
    'common-http-service', 'user-service', 'pane-factory', 'buyer-factory',
    'inputs', 'modals', 'common-filter',
    'pane-elements', 'text-utils', 'print-menu',
    'selling-pane', 'invoices-pane',
    'coming-item', 'new-coming-doc', 'moving-pane'
]);

console.log('starting');


