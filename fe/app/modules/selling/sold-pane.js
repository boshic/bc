import soldPaneTpl from './sold-pane.html';
import commonPaneCtrlr from '../../controllers/common-pane-ctrlr';

    angular.module('sold-pane', ['pane-elements', 'text-utils'])
        .directive( "soldPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: soldPaneTpl,
                controller: ($scope, filterFactory, paneFactory, printFactory, modalFactory) => {
                    return commonPaneCtrlr($scope, filterFactory, paneFactory, printFactory, modalFactory, 'soldPaneConfig');
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });