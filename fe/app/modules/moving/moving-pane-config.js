/**
 * Created by xlinux on 19.05.20.
 */

let movingPaneConfig = {

    findItemUrl: 'getComingForSellNonComposite',
    moveItemsUrl: 'makeMovings',
    checkRowsType: 'moving',
    getMoveItemsParams: ($s) => {
        return { params: { stockId: $s.stockDest.id }};
    },
    paneName: 'Перемещение',
    paneId: 'moving-pane',
    watchingCollectionValue : '[rows, stockDest, rows.length]',
    getFindItemParams: (ean, $s) => {
        return {filter: ean, stockId: $s.filter.stock.id};
    },
    resetFilter: (filterFactory, filter) => {
        filterFactory.resetReleaseFilter(filter);
    },
    getKeyupCombinations: ($s, keyCodes) => {
        return [
            {keyCode: keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
            {keyCode: keyCodes.enterKeyCode, doAction: $s.moveThis, ctrlReq: true}
        ];
    },

};

module.exports = movingPaneConfig;


