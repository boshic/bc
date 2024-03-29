/**
 * Created by xlinux on 17.12.19.
 */
let soldPaneConfig =
    {
        findItemUrl: 'findSoldItemByFilter',
        paneName: 'Отпущено',
        paneId: 'sold-pane',
        afterSearch: ($s) => {
            $s.focusOnEanInput();
        },
        getReportsParams: ($s) => {
            return [
                {type: 'writeOffAct', data: $s.filter, method: 'addWriteOffAct'},
                {type: 'salesReceipt', data: $s.filter, method: 'addInvoiceByFilter'},
                {type: 'invoiceWithContract', data: $s.filter, method: 'addInvoiceByFilter'},
                {type: 'invoice', data: $s.filter, method: 'addInvoiceByFilter'}
            ];
        },
        getKeyupCombinations: ($s, keyCodes) => {
            return [
                {keyCode: keyCodes.escKeyCode, doAction: $s.calcTotalsAndRefresh}
            ];
        },
        resetFilter: (filterFactory, filter) => {
            filterFactory.resetSoldPaneFilter(filter);
        },
        checkAddingReportCondition: ($s) => {return $s.filter.buyer.id > 0;},
        doBeforeFindItemsByFilter: () => { return true},
        openTextModal : ($s, config) => {
            let row = angular.extend(config.data, {comment: '', commentCause:''});
            $s.textEditModalClose = () => {
                if(confirm("Подтвердите добавление комментария"))
                    config.httpService.addItem({
                        data: {text: row.comment, action: row.commentCause},
                        url: 'addSoldItemComment',
                        requestParams: $s.requestParams,
                        params: {params: {id: row.id}}})
                        .then(
                            () => { $s.calcTotalsAndRefresh(); },
                            () => { console.log('comment has not been added!'); }
                        );
                else $s.calcTotalsAndRefresh();
            };
            config.modalFactory.openModal(undefined, [row], $s.textEditModalData);
        },
        makeReturn: ($s, config) => {
            let row = angular.extend($s.rows[config.index],
                {comment: '', commentCause:'Возврат', item: $s.rows[config.index].coming.item});
            $s.quantityChangerModalCloseWhenSellingReturns = () => {
                if(confirm("Подтвердите возврат") && row.quantity > 0)
                    config.httpService.addItem({data: row, url: 'returnSoldItem', requestParams: $s.requestParams})
                        .then(
                            (resp) => {
                                if(!resp.success)
                                    $s.warning = resp.text;
                                $s.calcTotalsAndRefresh();
                            },
                            () => {
                              $s.warning = 'ошибка проведения операции!';
                              console.log('return failed');
                            }
                        );
                else $s.calcTotalsAndRefresh();
            };
            config.modalFactory.openModalWithConfig({rows: [row],
                availQuantityField : 'quantity',
                limitQuantityField : 'quantity',
                modalData: $s.quantityChangerModalData});
        },

        changeItemDate : ($s, config) => {
            // let date = config.row.date;
            // config.row.date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());
            config.httpService.addItem({data: config.row, url: 'changeSoldItemDate', requestParams: $s.requestParams})
                .then(
                () => {
                    $s.calcTotalsAndRefresh();
                },
                (resp) => { console.log(resp);}
            );
        },
        editItem: ($s, config) => {
                config.httpService.getItemById(
                    {url: 'getSoldItemById', requestParams: $s.requestParams, id: config.row.id}
                    )
                    .then(
                        resp => {
                            let data = angular.extend(resp.coming, {
                                currentQuantity: resp.quantity, priceOut: resp.price, buyer: resp.buyer});
                            angular.extend($s.sellingModalConfig,
                              {url: 'changeSoldItem', id: resp.id,
                                refresh: $s.calcTotalsAndRefresh, hidden: false, data, parentScope: $s});
                        },
                        resp => {
                            console.log(resp);
                        }
                    );

        },

    };

module.exports = soldPaneConfig;