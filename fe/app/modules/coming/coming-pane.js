import comingPaneTpl from './coming-pane.html';
import commonPaneCtrlr from '../../controllers/common-pane-ctrlr';

    angular.module('coming-pane', [])
        .directive( "comingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: comingPaneTpl,
                controller: ($scope, filterFactory, paneFactory, printFactory, modalFactory ) => {
                    return commonPaneCtrlr($scope , filterFactory, paneFactory, printFactory, modalFactory, 'comingPaneConfig');
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });
