import soldPaneTpl from './sold-pane.html';

    let soldPaneCtrlr = ($s, httpService, filterFactory, paneFactory, elem, printFactory, modalFactory) => {

        $s.rows = [];
        $s.reports = [];
        $s.filter = {visible: false};
        $s.quantityChangerModalData = {hidden : true, row: {}};
        $s.textEditModalData = {hidden : true, row: {}};
        $s.quantityChangerModalClose = () => {};
        $s.requestParams = {requestsQuantity: 0};
        $s.totals = {};
        $s.user = {};

        let findItemsByFilter = () => {
            return paneFactory.findItemsByFilter($s, 'findSoldItemByFilter');
        };

        let calcTotalsAndRefresh = () => {
            return paneFactory.calcTotalsAndRefresh($s.filter, findItemsByFilter);
        };

        $s.setReports = () => {
            $s.reports = [];
            if($s.filter.buyer.id > 0)
                printFactory.setReportsByParams(
                    [{type: 'writeOffAct', data: $s.filter, method: 'addWriteOffAct'},
                        {type: 'salesReceipt', data: $s.filter, method: 'addInvoiceByFilter'},
                        {type: 'invoiceWithContract', data: $s.filter, method: 'addInvoiceByFilter'},
                        {type: 'invoice', data: $s.filter, method: 'addInvoiceByFilter'}], $s.reports);
        };

        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: calcTotalsAndRefresh}]);
        };

        $s.resetFilter = () => {
            filterFactory.resetSellingFilter($s.filter);
        };

        $s.setEanPrefix = e => {
            $s.filter.ean = paneFactory.generateEanByKey(e, $s.filter.ean);
        };

        $s.refresh = () => {
            calcTotalsAndRefresh();
            paneFactory.changeElementState(document.getElementById('sold-pane'), ['focus']);
        };

        $s.$watch('filter', (nv, ov) => {
            if ((nv) && (ov)) {
                filterFactory.doFilter(calcTotalsAndRefresh, findItemsByFilter, nv, ov);
            }
        }, true);

        $s.openTextEditModal = (index) => {
            let row = angular.extend($s.rows[index], {comment: '', commentCause:''});
            $s.textEditModalClose = () => {
                if(confirm("Подтвердите добавление комментария"))
                    httpService.addItem({
                        data: {text: row.comment, action: row.commentCause},
                        url: 'addSoldItemComment',
                        requestParams: $s.requestParams,
                        params: {params: {id: row.id}}})
                        .then(
                            () => {findItemsByFilter();},
                            () => {console.log('comment has not been added!');}
                    );
                else findItemsByFilter();
            };
            modalFactory.openModal(undefined, [row], $s.textEditModalData);
        };

        $s.makeReturn = (index) => {
            let row = angular.extend($s.rows[index], {comment: '', commentCause:'Возврат',
                currentQuantity: $s.rows[index].quantity});
            $s.quantityChangerModalClose = () => {
                if(confirm("Подтвердите возврат"))
                    httpService.addItem({data: row, url: 'returnSoldItem', requestParams: $s.requestParams})
                        .then(
                        () => {calcTotalsAndRefresh();},
                        () => {console.log('return failed');}
                    );
                else findItemsByFilter();
            };
            modalFactory.openModal(undefined, [row], $s.quantityChangerModalData);
        };

        $s.changeSoldItemDate = function () {
            httpService.addItem({data: this.x, url: 'changeSoldItemDate', requestParams: $s.requestParams})
                .then(
                () => {
                    calcTotalsAndRefresh();
                },
                (resp) => { console.log(resp);}
            );
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Продано") {
                $s.user = paneFactory.user;
                $s.blankSearch();
            }
        });

        $s.blankSearch = () => {
            $s.filter.ean = "";
            paneFactory.changeElementState(document.getElementById('sold-pane'), ['focus']);
        };

    };

    angular.module('sold-pane', ['pane-elements', 'text-utils'])
        .directive( "soldPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: soldPaneTpl,
                controller: ($scope, httpService, filterFactory, paneFactory, $element, printFactory, modalFactory) => {
                    return soldPaneCtrlr($scope, httpService, filterFactory, paneFactory, $element, printFactory, modalFactory);
                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });