let quantityChangerModalEnabled = true;

let afterCloseQuantityChangerModalInInventoryMode = ($s) => {
    let params = $s.quantityChangerModalData.params;
    if(params.quantity != $s.quantityChangerModalData.row.quantity)
        $s.totals=[];
    $s.rows[params.index].currentQuantity = $s.quantityChangerModalData.row.quantity;

    if(toOpenAQuantityModalAfterSearchInInventoryMode($s))
        nextOpenOfQuantityModalInInventoryModeHandler($s);

};

let refreshAfterSettingInventoryValues = ($s) => {
    $s.totals = [{}];
    $s.calcTotalsAndRefresh();
};

let toOpenAQuantityModalAfterSearchInInventoryMode = ($s) => {
    return $s.filter.inventoryModeEnabled
        && $s.rows.length === 1
        && $s.filter.ean.length
        && quantityChangerModalEnabled
        && $s.autoOpenQuantityChangerModalInInventoryMode;
};

let beforeSearchInInventoryMode = ($s) => {
    return $s.filter.inventoryModeEnabled && !$s.totals.length && $s.rows.length
};

let nextOpenOfQuantityModalInInventoryModeHandler = ($s) => {
    if($s.totals.length === 0) {
        quantityChangerModalEnabled = false;
        $s.setInventoryValues();
    } else
        $s.focusOnEanInput();
};

let comingPaneConfig = {
    getReportsParams: ($s) => {
            return [{type: 'prices', data: $s.filter, method: 'addComingReportByFilter'}];
        },
    checkAddingReportCondition: () => {return true;},
    findItemUrl: 'findComingItemByFilter',
    paneName: 'Список',
    paneId: 'coming-pane',
    doBeforeFindItemsByFilter: ($s) => {
            if(beforeSearchInInventoryMode($s))
                return (confirm("Записать результаты инвентаризации?")) ? $s.setInventoryValues() : true;
            return true;
        },
    resetFilter: (filterFactory, filter) => {
            filterFactory.resetComingFilter(filter);
        },
    editItem: ($s, config) => {
            // let row = $s.rows[config.index];
            let row = config.row;
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
        modalFactory.openModalWithConfig({rows: [row],
            availQuantityField : 'currentQuantity', modalData: $s.quantityChangerModalData});
    },
    afterSearch: ($s) => {
        if(toOpenAQuantityModalAfterSearchInInventoryMode($s))
            $s.openQuantityChangerModalInInventoryMode(0);
        else
            $s.focusOnEanInput();
        quantityChangerModalEnabled = true;
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
                () => { refreshAfterSettingInventoryValues($s); },
                (resp) => {}
            );
    },
    setInventoryValuesToZeroes: ($s) => {
        if(confirm("Установить нулевые остатки по факту в текущей выборке?")) {
            $s.rows.forEach(row => {
                row.currentQuantity = 0;
            });
            $s.totals=[];
        }
    },
    applyInventoryResults: ($s, config) => {
        if(confirm("Эта операция автоматически спишет излишки и недостачи! Хорошенько подумайте, прежде чем продолжить!"))
            config.httpService.addItem({
                data: $s.filter,
                url: 'applyInventoryResults',
                params: {params: { buyerId: $s.inventoryBuyer.id }},
                requestParams: $s.requestParams})
            .then(
                (resp) => {
                    $s.calcTotalsAndRefresh();
                    $s.warning = resp.text;
                }
            );
    }
};

module.exports = comingPaneConfig;