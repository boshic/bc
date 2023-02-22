let getDataFromLastRow = (rows) => {
  if(rows.length)
    return {
      sum: rows[0].sum,
      price: rows[0].price,
      impOverheadPerc: 0,
      firstImpPrice: 0,
      quantity: 0,
      priceOut: rows[0].priceOut
    };
  return {quantity: 0, sum:0, price: 0, priceOut: 0, firstImpPrice: 0, impOverheadPerc:0};
};

let newComingDocConfig = {

  findItemUrl: 'getItemForNewComing',
  checkRowsType: 'newComing',
  paneName: 'Новый!',
  paneId: 'new-coming-doc-pane',
  watchingCollectionValue : '[doc, stock, buyer, rows.length]',
  watchingCollectionFunc : ($s, nv, ov) => {
    if ((nv.indexOf(undefined) < 0)) {
      $s.checkRows();
      // $s.blankSearch();!!!!!)))(((поскуда!
    }
  },

  watchingRowsFunc : ($s, nv, ov, params) => {
    if ((nv) && (nv.length) && (ov) && (ov.length)) {
      if (ov.length === nv.length)
        for( let i = 0; i < nv.length; i++)
          for (let key in nv[i]) {
            if ((angular.isDefined(ov[i][key], nv[i][key])
              && ((ov[i][key] != null) && (nv[i][key] != null))
              && (!params.paneFactory.compareValues(nv[i][key], ov[i][key])))) {
              if(key === 'quantity') {
                nv[i].priceOut = (nv[i].priceOut < 0) ? 0 : nv[i].priceOut;
                nv[i].sum = (nv[i].price*nv[i].quantity).toFixed(2);
              }
              if(key === 'sum') {
                nv[i].sum = (nv[i].sum < 0)? 0 : nv[i].sum;
                if(nv[i].quantity > 0)
                  nv[i].price = (nv[i].sum/nv[i].quantity).toFixed(4);
              }
            }
          }
    }
    $s.checkRows();
  },

  getFindItemParams: (ean, $s) => {
    return {filter: ean, stockId: $s.stock.id};
  },
  getKeyupCombinations: ($s, keyCodes) => {
    return [
      {keyCode: keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
      {keyCode: keyCodes.enterKeyCode, doAction: $s.makeComing, ctrlReq: true}
    ];
  },

  getDeleteRowsConfig : (params) => {
    return {
      itemId: params.itemId,
      getEmptyBuyer: params.getEmptyBuyer,
      getEmptyDoc: params.getEmptyDoc };
  },
  doAfterBlankSearch : ($s, params) => {
    if(!angular.isDefined($s.buyer.id) && !$s.rows.length)
      $s.buyer = params.itemFactory.buyerConfig.checkAndGetItem(params.paneFactory.user.buyer);
  },

  setReportData : ($s, params) => {
            if($s.rows.length) {
                let data = {
                  stock: $s.stock,
                  buyer: $s.buyer,
                  id: undefined,
                  rows: params.printFactory.getRowsForReports($s, 'priceOut')};
                if(angular.isDefined($s.doc.id))
                  params.printFactory.setReportsByParams(
                      [{type: 'prices', data, method: 'addComingReport'}],
                      $s.reports);
                if(angular.isDefined($s.buyer.id))
                  params.printFactory.setReportsByParams([
                        {type: 'salesReceipt', data, method: 'addInvoice'},
                        {type: 'invoiceWithContract', data, method: 'addInvoice'},
                        {type: 'workCompletionStatement', data, method: 'addInvoice'},
                        {type: 'invoice', data, method: 'addInvoice'}], $s.reports);
            }
        },

  getItems : (params, url, $s) => {
            $s.warning = "";
            params.paneFactory.getHttpService().getItems(
              {params, url, requestParams:$s.requestParams})
                .then(
                resp => {
                    $s.canRelease = false;
                    let item = resp.item;
                    if (item != null) {

                        let index = params.paneFactory.checkDuplicateRowsByItem(item.id, $s.rows);
                        if (index < 0) {
                            (resp.priceOut !== 0) ?
                                $s.rows.splice(0,0, {
                                        item: item,
                                        doc: {name:""},
                                        quantity: 0, //1
                                        sum: resp.price,
                                        price: resp.price,
                                        priceOut: resp.priceOut,
                                        impOverheadPerc: 0,
                                        firstImpPrice: item.section.percOverheadLimit > 0
                                          ? (resp.price - (resp.price*20/120).toFixed(2)): 0
                                    })
                                    : $s.rows.splice(0,0, angular.extend({
                                            item: item,
                                            doc: {name:""}
                                        }, getDataFromLastRow($s.rows)));
                            index = 0;
                        } else {
                            $s.rows[index].item = item;
                            if (resp.priceOut > 0)
                              $s.rows[index].priceOut = resp.priceOut;
                        }
                        $s.checkRows();
                        $s.openQuantityChangerModal(item.id);

                    } else {
                        $s.warning = "Такого товара нет, нужно добавить!";
                        $s.itemInputVisible = true;
                        $s.item = angular.extend($s.getEmptyItem(), {
                          name: params.filter, ean: params.filter
                        });
                    }
                },
                resp => {
                    $s.warning="Произошла ошибка во время обработки запроса.";
                    console.log(resp);
                }
            );
  },

  makeComing : ($s, params) => {
    if($s.canRelease) {
      $s.canRelease = false;
      params.paneFactory.getHttpService().addItem(
          {data: $s.rows, url: 'addComings', requestParams:$s.requestParams})
        .then(
          resp => {
            resp.entityItem.success ? $s.deleteRows() : $s.warning = resp.entityItem.text;
          },
          resp => {
            $s.warning = "ошибка при проведении операции! Позиций - "
              + $s.rows.length + ', время: ' + new Date().toLocaleTimeString();
          }
        );
    }
  }
};

module.exports = newComingDocConfig;