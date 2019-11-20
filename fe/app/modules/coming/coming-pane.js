import comingPaneTpl from './coming-pane.html';

    let comingPaneCtrlr = ($s, filterFactory, paneFactory, printFactory, modalFactory) => {

        $s.rows=[];
        $s.requestParams = {requestsQuantity: 0};
        $s.quantityChangerModalData = { hidden : true, row: {} };
        $s.filter = {visible: false};
        $s.modalConfig = {};
        $s.sellingModalConfig = {};
        $s.movingModalConfig = {};
        $s.totals = {};
        $s.user = {};

        printFactory.setReportsByParams(
            [{type: 'prices', data: $s.filter, method: 'addComingReportByFilter'}], $s.reports=[]);

        let eanInputElement =  document.getElementById('coming-pane');

        let searchTerms = paneFactory.getSearchTermsForGetItemsByFilter($s, 'findComingItemByFilter');

        let findItemsByFilter = () => {
            paneFactory.getItemsBySearchTermsAndFilter(searchTerms, $s.filter);
        };

        let calcTotalsAndRefresh = () => {
            return paneFactory.calcTotalsAndRefresh($s.filter, findItemsByFilter);
        };

        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [
                {keyCode: paneFactory.keyCodes.escKeyCode, doAction: calcTotalsAndRefresh},
                {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.setInventoryValues, ctrlReq: true}
            ]);
        };

        $s.resetFilter = () => {
            filterFactory.resetComingFilter($s.filter);
        };

        $s.setEanPrefix = e => {
            $s.filter.ean = paneFactory.generateEanByKey(e, $s.filter.ean);
        };

        $s.setInventoryValues = () => {
            paneFactory.setInventoryValues($s, 'setInventoryItems');
        };

        $s.refresh = () => {
            calcTotalsAndRefresh();
        };

        $s.focusOnEanInput = () => {
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

        $s.afterSearch = () => {
            if($s.filter.inventoryModeEnabled && $s.rows.length === 1)
                $s.openQuantityChangerModal(0);
            else
                $s.focusOnEanInput();
        };

        $s.openComingModal = function() {
            if(this.x.quantity != 0 && !$s.filter.inventoryModeEnabled) {
                $s.modalConfig.id = ((this.x) && ('id' in this.x)) ? this.x.id : null;
                $s.modalConfig.hidden = false;
            } else
                $s.filter.ean = this.x.item.ean;
        };

        $s.sellThis = function () {
            angular.extend($s.sellingModalConfig, {hidden: false, data: this.x, requestParams: $s.requestParams});
        };

        $s.moveThis = function () {
            angular.extend($s.movingModalConfig, {hidden: false, data: this.x, requestParams: $s.requestParams});
        };

        $s.openQuantityChangerModal = (index) => {
            let row = $s.rows[index];
            $s.quantityChangerModalData.params = {index};
            modalFactory.openModal(undefined,
                [{ quantity : row.currentQuantity, currentQuantity: undefined, item: row.item }],
                $s.quantityChangerModalData);
        };

        $s.afterCloseQuantityChangerModal = () => {
            let index = $s.quantityChangerModalData.params.index;
            $s.rows[index].currentQuantity = $s.quantityChangerModalData.row.quantity;
            $s.totals=[{quantDescr: 'нажмите записать остатки', quantValue: 0, sumValue: 0}];
            if($s.rows.length === 1)
                $s.focusOnEanInput();
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Список") {
                $s.user = paneFactory.user;
                // $s.blankSearch();
                $s.focusOnEanInput();
            }
        });

        $s.blankSearch = () => {
            $s.filter.ean = "";
            $s.focusOnEanInput();
        };
     };

    angular.module('coming-pane', [])
        .directive( "comingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: comingPaneTpl,
                controller: ($scope, filterFactory, paneFactory, printFactory, modalFactory ) => {
                    return comingPaneCtrlr($scope , filterFactory, paneFactory, printFactory, modalFactory);
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });
