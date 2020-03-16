let afterCloseQuantityChangerModalInInventoryMode = ($s) => {
    let params = $s.quantityChangerModalData.params;
    if(params.quantity != $s.quantityChangerModalData.row.quantity)
        $s.totals=[];
    $s.rows[params.index].currentQuantity = $s.quantityChangerModalData.row.quantity;
    if($s.rows.length === 1)
        $s.focusOnEanInput();
};

let comingPaneConfig = {
    getReportsParams: ($s) => {
            return [{type: 'prices', data: $s.filter, method: 'addComingReportByFilter'}];
        },
        checkAddingReportCondition: () => {return true;},
        // eanInputElementId: 'coming-pane-2',
        findItemUrl: 'findComingItemByFilter',
        paneName: 'Список',
        paneId: 'coming-pane',
    doBeforeFindItemsByFilter: ($s) => {
            if($s.filter.inventoryModeEnabled && !$s.totals.length && $s.rows.length)
                return (confirm("Записать результаты инвентаризации?")) ? $s.setInventoryValues() : true;
            return true;
        },
    resetFilter: (filterFactory, filter) => {
            filterFactory.resetComingFilter(filter);
        },
    editItem: ($s, config) => {
            let row = $s.rows[config.index];
            if(row.quantity != 0 && !$s.filter.inventoryModeEnabled) {
                $s.modalConfig.id = ((row) && ('id' in row)) ? row.id : null;
                $s.modalConfig.hidden = false;
            } else
                $s.filter.ean = row.item.ean;
    },
    openQuantityChangerModalInInventoryMode: ($s, index, modalFactory) => {
        $s.afterCloseQuantityChangerModal = () => {
            return afterCloseQuantityChangerModalInInventoryMode($s);
        };
        let row = angular.extend({}, $s.rows[index]);
        let currentQuantity = row.currentQuantity;
        let quantity = row.quantity;
        row.quantity = currentQuantity;
        row.currentQuantity = quantity;
        $s.quantityChangerModalData.params = {index, quantity: currentQuantity};
            modalFactory.openModalWithConfig({undefined, rows: [row],
            availQuantityField : 'currentQuantity', modalData: $s.quantityChangerModalData});
    },
    afterSearch: ($s) => {
        if($s.filter.inventoryModeEnabled && $s.rows.length === 1 && $s.autoOpenQuantityChangerModalInInventoryMode)
            $s.openQuantityChangerModalInInventoryMode(0);
        else
            $s.focusOnEanInput();
    },
    getKeyupCombinations: ($s, keyCodes) => {
        return [
            {keyCode: keyCodes.escKeyCode, doAction: $s.calcTotalsAndRefresh},
            {keyCode: keyCodes.enterKeyCode, doAction: $s.setInventoryValues, ctrlReq: true}
        ];
    },
    setInventoryValues: ($s, config) => {
        config.httpService.addItem({data: $s.rows, url: 'setInventoryItems', requestParams: $s.requestParams})
            .then(
                resp => {
                    $s.totals = [{}];
                    $s.calcTotalsAndRefresh();
                },
                (resp) => {}
            );
    },
    applyInventoryResults: ($s, config) => {
        config.httpService.addItem({data: $s.filter, url: 'applyInventoryResults', requestParams: $s.requestParams})
            .then(
                (resp) => {
                    $s.calcTotalsAndRefresh();
                    $s.warning = resp.text;
                }
            );
    }
};

module.exports = comingPaneConfig;