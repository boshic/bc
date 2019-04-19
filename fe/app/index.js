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
import './modules/coming/coming-pane';
import './modules/coming/new-coming-doc';
import './modules/moving/moving-pane';
import './modules/selling/selling-pane';
import './modules/selling/sold-pane';
import './modules/selling/invoices-pane';


import './factories/http-service';
import './factories/user-service';
import './factories/pane-factory';


angular.module('barcode', [
    'common-http-service', 'user-service', 'pane-factory',
    'inputs', 'modals', 'common-filter',
    'pane-elements', 'text-utils', 'print-menu',
    'sold-pane', 'selling-pane', 'invoices-pane',
    'coming-item', 'coming-pane', 'new-coming-doc', 'moving-pane'
]);

console.log('starting');


