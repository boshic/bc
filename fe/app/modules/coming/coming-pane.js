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

        let beforeFindItemsByFilter = () => {
            if($s.filter.inventoryModeEnabled && !$s.totals.length && $s.rows.length)
                return (confirm("Записать результаты инвентаризации?")) ? $s.setInventoryValues() : true;
            return true;
        };

        let findItemsByFilter = () => {
            if(!(typeof beforeFindItemsByFilter === 'function') || (beforeFindItemsByFilter()))
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

        let focusOnEanInput = () => {
            if(!$s.filter.visible)
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
            if($s.filter.inventoryModeEnabled && $s.rows.length === 1
                && $s.filter.autoOpenQuantityChangerModalInInventoryMode)
                $s.openQuantityChangerModal(0);
            else
                focusOnEanInput();
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
            $s.quantityChangerModalData.params = {index, quantity: $s.rows[index].currentQuantity};
            modalFactory.openModalWithConfig({undefined, rows: [angular.extend({}, $s.rows[index])],
                availQuantityField : 'quantity', modalData: $s.quantityChangerModalData});
        };

        $s.afterCloseQuantityChangerModal = () => {
            let params = $s.quantityChangerModalData.params;
            if(params.quantity != $s.quantityChangerModalData.row.quantity)
                $s.totals=[];
            $s.rows[params.index].currentQuantity = $s.quantityChangerModalData.row.quantity;
            // $s.totals=[];
            if($s.rows.length === 1)
                focusOnEanInput();
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Список") {
                $s.user = paneFactory.user;
                focusOnEanInput();
            }
        });

        $s.blankSearch = () => {
            $s.filter.ean = "";
            focusOnEanInput();
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
