import invoicesPaneTpl from './invoices-pane.html';

    let invoicesCtrlr = ($s, httpService, filterFactory, paneFactory, elem) => {

        $s.rows=[];
        $s.filter = {visible: false};
        $s.totals = {};
        $s.user = {};

        let findItemsByFilter = () => {
            return paneFactory.findItemsByFilter($s, 'getInvoicesByFilter');
        };

        let calcTotalsAndRefresh = () => {
            return paneFactory.calcTotalsAndRefresh($s.filter, findItemsByFilter);
        };

        $s.handleKeyup = e => {
            // if (e.keyCode == 27)
            //     calcTotalsAndRefresh();
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
        };

        $s.$watch('filter', (nv, ov) => {
            if ((nv) && (ov)) {
                filterFactory.doFilter(calcTotalsAndRefresh, findItemsByFilter, nv, ov);
            }
        }, true);

        $s.openInvoiceModal = function () {
            $s.modalConfig.hidden = false;
            // $s.modalConfig.reportTypes = ['invoiceWithContract', 'invoice'];
            $s.invoice = this.x;
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Документы") {
                $s.user = paneFactory.user;
                $s.blankSearch();
            }
        });

        $s.blankSearch = () => {
            $s.filter.ean = "";
            paneFactory.changeElementState(document.getElementById('invoices-pane'), ['focus']);
        };

    };

    angular.module('invoices-pane', [])
        .directive( "invoicesPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: true,
                template: invoicesPaneTpl,
                controller: ($scope, httpService, filterFactory, paneFactory, $element) => {

                    return invoicesCtrlr($scope, httpService, filterFactory, paneFactory, $element);

                },
                link: (scope) => {
                    scope.resetFilter();
                }
            }
        });
