
let sellingPaneConfig = {

  deletionTrackingUrl: 'addDeletedSelings',
  findItemUrl: 'getComingForSell',
  sellItemsUrl: 'addSellingsSet',
  checkRowsType: 'selling',
  paneName: 'Продавать!',
  paneId: 'selling-pane',
  watchingCollectionValue : '[comment, buyer, rows.length]',
  watchingCollectionFunc : ($s, nv, ov) => {
    if ((nv.indexOf(undefined) < 0)) {
      $s.checkRows();
      for(let i=0; i < nv.length; i++)
        if(angular.isDefined(nv[i].id) && !angular.isDefined(ov[i].id)) {
          $s.blankSearch();
          break;
        }
    }
  },

  getFindItemParams: (ean, $s) => {
    return {filter: ean, stockId: $s.filter.stock.id};
  },
  resetFilter: (filterFactory, filter) => {
    filterFactory.resetSellingPaneFilter(filter);
  },
  getKeyupCombinations: ($s, keyCodes) => {
    return [
      {keyCode: keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
      {keyCode: keyCodes.enterKeyCode, doAction: $s.sellThis, ctrlReq: true}
    ];
  },
  getDeleteRowsConfig : (params) => {
    return {
      getEmptyBuyer: params.getEmptyBuyer,
      itemId: params.itemId,
      deletionTrackingUrl: params.deletionTrackingUrl };
  },
  doAfterBlankSearch : ($s, params) => {
    if(!angular.isDefined($s.buyer.id) && !$s.rows.length)
      $s.buyer = params.itemFactory.buyerConfig.checkAndGetItem(params.paneFactory.user.buyer);
  },

  setReportData : ($s, params) => {
    let data = {
      stock: $s.filter.stock,
      buyer: $s.buyer,
      comment: $s.comment,
      rows: params.printFactory.getRowsForReports($s, 'price')
    };
    params.printFactory.setReportsByParams([
      {type: 'invoiceWithContract', data: data, method: 'addInvoice'},
      {type: 'invoice', data: data, method: 'addInvoice'},
      {type: 'salesReceipt', data: data, method: 'addInvoice'}], $s.reports);
  },

  sellThis : () => {}

};

module.exports = sellingPaneConfig;