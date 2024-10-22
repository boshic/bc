
let cashboxPaneConfig = {
    // getReportsParams: ($s) => {
    //         return [{type: 'prices', data: $s.filter, method: 'addComingReportByFilter'}];
    //     },
    checkAddingReportCondition: () => {return true;},
    defaultFactoryName:'cashboxFactory',
    initMethod: 'setMethods',
    findItemUrl: 'findComingItemByFilter',
    paneName: 'Касса',
    paneId: 'cashbox-pane',
    watchingRowsFunc : null,
    watchingCollectionFunc: () => {},
    tabSelected : ($s) => {
      // return $s.cashboxUpdate();
    },
    doBeforeFindItemsByFilter: ($s) => {
            if(beforeSearchInInventoryMode($s))
                return (confirm("Записать результаты инвентаризации?")) ? $s.setInventoryValues() : true;
            return true;
        },

    resetFilter: (filterFactory, filter) => {
            filterFactory.resetComingFilter(filter);
        },

    openQuantityChangerModal : function (opts) {
        opts.$s.afterCloseQuantityChangerModal = () => {
            // return afterCloseQuantityChangerModalInInventoryMode($s);
        };
        let item = {name: this.factory.moneyOperations[opts.descr], unit: "BYN"};
        let row = angular.extend({}, {item: item});
        let currentQuantity = 0;
        let quantity = 10;
        row.quantity = currentQuantity;
        row.currentQuantity = quantity;
        opts.$s.quantityChangerModalData.params = {index: opts.descr, quantity: currentQuantity};
        opts.modalFactory.openModalWithConfig({rows: [row],
            limitQuantityField : 'currentQuantity',
            availQuantityField : 'currentQuantity',
            modalData: opts.$s.quantityChangerModalData});
    },
    // afterSearch: ($s) => {
    //     if(toOpenAQuantityModalAfterSearchInInventoryMode($s))
    //         $s.openQuantityChangerModalInInventoryMode(0);
    //     else
    //         $s.focusOnEanInput();
    //     quantityChangerModalEnabled = true;
    // },
    // getKeyupCombinations: ($s, keyCodes) => {
    //     return [
    //         {keyCode: keyCodes.escKeyCode, doAction: $s.calcTotalsAndRefresh},
    //         {keyCode: keyCodes.enterKeyCode, doAction: $s.setInventoryValues, ctrlReq: true}
    //     ];
    // },
    // setInventoryValues: ($s, config) => {
    //     config.httpService.addItem({data: $s.rows, url: 'setInventoryItems', requestParams: $s.requestParams})
    //         .then(
    //             () => { refreshAfterSettingInventoryValues($s); },
    //             (resp) => {}
    //         );
    // },
    // setInventoryValuesToZeroes: ($s) => {
    //     if(confirm("Установить нулевые остатки по факту в текущей выборке?")) {
    //         $s.rows.forEach(row => {
    //             row.currentQuantity = 0;
    //         });
    //         $s.totals=[];
    //     }
    // },
    // applyInventoryResults: ($s, config) => {
    //     if(confirm("Эта операция автоматически спишет излишки и недостачи на текущей странице! " +
    //       "Хорошенько подумайте, прежде чем продолжить!"))
    //         config.httpService.addItem({
    //             data: $s.filter,
    //             url: 'applyInventoryResults',
    //             params: {params: { buyerId: $s.inventoryBuyer.id }},
    //             requestParams: $s.requestParams})
    //         .then(
    //             (resp) => {
    //                 $s.calcTotalsAndRefresh();
    //                 $s.warning = resp.text;
    //             }
    //         );
    // }
};

module.exports = cashboxPaneConfig;