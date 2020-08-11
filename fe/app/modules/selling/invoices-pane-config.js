/**
 * Created by xlinux on 24.12.19.
 */

let invoicesPaneConfig = {

    checkAddingReportCondition: () => {return false;},
    findItemUrl: 'getInvoicesByFilter',
    paneName: 'Документы расход',
    paneId: 'invoices-pane',
    doBeforeFindItemsByFilter: () => { return true;},
    resetFilter: (filterFactory, filter) => {
        filterFactory.resetSoldPaneFilter(filter);
    },
    editItem: ($s, config) => {
        $s.modalConfig.hidden = false;
        $s.invoice = config.row;
    },
    afterSearch: ($s) => {
        $s.focusOnEanInput();
    },
    changeItemDate : ($s, config) => {

        config.httpService.addItem({data: config.row, url: 'changeInvoiceDate', requestParams: $s.requestParams})
            .then(
                () => { $s.calcTotalsAndRefresh(); },
                resp => { console.log(resp);}
            );
    },
    getKeyupCombinations: ($s, keyCodes) => {
        return [
            {keyCode: keyCodes.escKeyCode, doAction: $s.calcTotalsAndRefresh}
        ];
    },
};

module.exports = invoicesPaneConfig;
