/**
 * Created by xlinux on 24.12.19.
 */

let invoicesPaneConfig = {

    checkAddingReportCondition: () => {return false;},
    findItemUrl: 'getInvoicesByFilter',
    paneName: 'Документы расход',
    paneId: 'invoices-pane',
    doBeforeFindItemsByFilter: () => { return true},
    resetFilter: (filterFactory, filter) => {
        filterFactory.resetSellingFilter(filter);
    },
    editItem: ($s, config) => {
        $s.modalConfig.hidden = false;
        $s.invoice = $s.rows[config.index];
    },
    afterSearch: ($s) => {
        $s.focusOnEanInput();
    },
    getKeyupCombinations: ($s, keyCodes) => {
        return [
            {keyCode: keyCodes.escKeyCode, doAction: $s.calcTotalsAndRefresh}
        ];
    },
};

module.exports = invoicesPaneConfig;
