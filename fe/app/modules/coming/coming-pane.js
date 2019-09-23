import comingPaneTpl from './coming-pane.html';

    let comingPaneCtrlr = ($s, filterFactory, paneFactory, elem, printFactory) => {

        $s.rows=[];
        $s.requestParams = {requestsQuantity: 0};
        $s.filter = {visible: false};
        // $s.hideModal = true;
        $s.modalConfig = {};
        $s.sellingModalConfig = {};
        $s.movingModalConfig = {};
        $s.totals = {};
        $s.user = {};

        printFactory.setReportsByParams(
            [{type: 'prices', data: $s.filter, method: 'addComingReportByFilter'}], $s.reports=[]);

        let eanInputElement =  document.getElementById('coming-pane');

        let findItemsByFilter = ()=> {
            return paneFactory.findItemsByFilter($s, 'findComingItemByFilter');
        };

        let calcTotalsAndRefresh = () => {
            return paneFactory.calcTotalsAndRefresh($s.filter, findItemsByFilter);
        };

        $s.handleKeyup = e => {
            // if (e.keyCode == 27)
            //     calcTotalsAndRefresh();
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: calcTotalsAndRefresh}]);
        };

        $s.resetFilter = () => {
            filterFactory.resetComingFilter($s.filter);
        };

        $s.setEanPrefix = e => {
            $s.filter.ean = paneFactory.generateEanByKey(e, $s.filter.ean);
        };

        $s.refresh = () => {
            calcTotalsAndRefresh();
            paneFactory.changeElementState(eanInputElement, ['focus']);
        };

        $s.$watch('filter', (nv, ov) => {
            if ((nv) && (ov)) {
                filterFactory.doFilter(calcTotalsAndRefresh, findItemsByFilter, nv, ov);
            }
        }, true);

        $s.sellingModalConfig.refresh = $s.movingModalConfig.refresh = $s.modalConfig.refresh = () => {
                calcTotalsAndRefresh()
        };

        $s.openComingModal = function() {
            if(this.x.quantity !=0) {
                $s.modalConfig.id = ((this.x) && ('id' in this.x)) ? this.x.id : null;
                $s.modalConfig.hidden = false;
            }
        };

        $s.sellThis = function () {
            $s.sellingModalConfig.hidden = false;
            $s.sellingModalConfig.data = this.x;
        };

        $s.moveThis = function () {
            $s.movingModalConfig.hidden = false;
            $s.movingModalConfig.data = this.x;
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Список") {
                $s.user = paneFactory.user;
                $s.blankSearch();
            }
        });

        $s.blankSearch = () => {
            $s.filter.ean = "";
            paneFactory.changeElementState(eanInputElement, ['focus']);
        };

     };

    angular.module('coming-pane', [])
        .directive( "comingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: comingPaneTpl,
                controller: ($scope, filterFactory, paneFactory, $element, printFactory ) => {
                    return comingPaneCtrlr($scope , filterFactory, paneFactory, $element, printFactory);
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });
