// define(['angular'], angular => {

import movingPaneTpl from './moving-pane.html';

    let movingPaneCntrlr = ($s, $http, paneFactory, elem, modalFactory) => {

        $s.rows=[];
        $s.allowAllStocks = false;
        $s.barcode = "";
        $s.warning = "";
        $s.canRelease = false;
        $s.quantityChangerModalData = {hidden : true, row: {}};
        $s.totals = { date: new Date, sum: 0, quantity: 0 };

        let getItems =(ean) => {
            paneFactory.getItemsForRelease( {filter: ean, stockId: $s.stock.id}, 'getComingForSell', $s);
        };

        $s.$watchCollection("[rows, stockDest, rows.length]", () => {
            if ($s.rows.length) {
                $s.checkRows();
                $s.blankSearch();
            }
        }, true);

        $s.$watch('barcode', (nv) => {
            if(nv && paneFactory.getItemByBarcode($s.barcode, getItems))
                $s.barcode ='';
        });

        $s.moveThis = () => {
            paneFactory.releaseItems($s, 'makeMovings', { params: { stockId: $s.stockDest.id } });
        };

        $s.deleteRows = function () {
            if (this.$index >= 0)
                $s.rows.splice(this.$index,1);
            else {
                $s.comment = "";
                $s.rows=[];
            }
            $s.blankSearch();
        };

        $s.checkRows = () => {
            let rows = $s.rows;
            $s.canRelease =  false;
            if (rows.length > 0) {
                $s.totals = paneFactory.calcTotals($s.rows);
                for (let row of rows) {
                    if (!row.quantity || $s.stock.id === $s.stockDest.id)
                        return $s.canRelease = false;
                    row.user = paneFactory.user;
                    // row.coming.stock = $s.stock;
                    row.comment = $s.comment;
                }
                $s.canRelease = true;
            }

        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === 'Перемещение') {
                $s.blankSearch();
                $s.user = paneFactory.user;
            }
        });

        $s.handleKeyup = e => {
            paneFactory.keyupHandler(e, $s.openQuantityChangerModal, $s.moveThis);
        };

        $s.setEanPrefix = e => {
            $s.barcode = paneFactory.generateEanByKey(e, $s.barcode);
        };

        $s.blankSearch = () => {
            $s.barcode = '';
            paneFactory.changeElementState(document.getElementById('moving-pane'), ['focus']);
        };

        $s.openQuantityChangerModal = (index) => {
            modalFactory.openModal(index, $s.rows, $s.quantityChangerModalData);
        };

    };

    angular.module('moving-pane', [])
        .directive( "movingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: movingPaneTpl,
                controller: ($scope, $http, paneFactory, $element, modalFactory) => {

                    return movingPaneCntrlr($scope, $http, paneFactory, $element, modalFactory);
                },
                link: (scope,elem) => {}
            }
        });
// });