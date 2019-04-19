import contractFooterTpl from './contract-footer.html';
import invoiceBodyTpl from './invoice-body.html';
import invoiceFooter from './invoice-footer.html';
import invoiceHeader from './invoice-header.html';
import invoiceContractBodyTpl from './invoice-contract-body.html';
import invoiceContractHeaderTpl from './invoice-contract-header.html';

    angular.module('common-reports-parts', [])
        .directive( "commonContractFooter", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: contractFooterTpl,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "invoiceBody", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceBodyTpl,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "invoiceContractBody", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceContractBodyTpl,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "invoiceContractHeader", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceContractHeaderTpl,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "invoiceFooter", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceFooter,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        })
        .directive( "invoiceHeader", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceHeader,
                controller: $scope => {},
                link: (scope, elem, attrs) => {}
            }
        });