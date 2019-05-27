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
                controller: $scope => {}
            }
        })
        .directive( "invoiceBody", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceBodyTpl,
                controller: $scope => {}
            }
        })
        .directive( "invoiceContractBody", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceContractBodyTpl,
                controller: $scope => {}
            }
        })
        .directive( "invoiceContractHeader", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceContractHeaderTpl,
                controller: $scope => {}
            }
        })
        .directive( "invoiceFooter", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceFooter,
                controller: $scope => {}
            }
        })
        .directive( "invoiceHeader", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {data: '=invoiceData'},
                template: invoiceHeader,
                controller: $scope => {}
            }
        });