
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
    watchingCollectionFunc : ($s, nv, ov) => {
      if ($s.rows.length) {
          $s.checkRows();
          $s.blankSearch();
      }
    },
    getFindItemParams: (ean, $s) => {
        return {filter: ean, stockId: $s.filter.stock.id};
    },
    resetFilter: (filterFactory, filter) => {
        filterFactory.resetMovingFilter(filter);
    },
    getKeyupCombinations: ($s, keyCodes) => {
        return [
            {keyCode: keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
            {keyCode: keyCodes.enterKeyCode, doAction: $s.moveThis, ctrlReq: true}
        ];
    },
    getDeleteRowsConfig : (params) => {
      return {itemId: params.itemId };
    }

};

module.exports = movingPaneConfig;


