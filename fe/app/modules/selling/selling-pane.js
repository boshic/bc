import sellingPaneTpl from './selling-pane.html';

let sellingPaneCntrlr = ($s, $http, paneFactory, printFactory, modalFactory, itemFactory) => {

        $s.rows=[];

        let getEmptyItem = itemFactory.itemConfig.getEmptyItem;
        $s.item = getEmptyItem();

        $s.allowAllStocks = false;
        $s.quantityChangerModalData = {hidden : true, row: {}};
        $s.barcode = "";
        $s.warning = "";
        $s.comment = "";
        $s.requestParams = {requestsQuantity: 0};
        $s.canRelease = false;
        $s.totals = {
            date: new Date,
            sum: 0,
            quantity: 0
        };

        let eanInputElement = document.getElementById('selling-pane');

        let getItems =(ean) => {
            paneFactory.getItemsForRelease( {filter: ean, stockId: $s.stock.id}, 'getComingForSell', $s);
        };

        let setDefaultBuyer = () => {
            let buyer = paneFactory.user.buyer || null;
            if(!$s.rows.length && typeof buyer === 'object' && buyer !=null && buyer.id > 0)
                $s.buyer = buyer;

        };

    $s.editItem = (barcode) => {
        $s.item = angular.extend(getEmptyItem(), {name: barcode});
    };

    $s.getDiscountedPrice = paneFactory.getDiscountedPrice;

        $s.$watchCollection("[comment, rows, buyer, rows.length]", (nv, ov) => {
            if ((nv.indexOf(undefined) < 0)) {
                $s.checkRows();
                for(let i=0; i < nv.length; i++)
                    if(angular.isDefined(nv[i].id) && !angular.isDefined(ov[i].id)) {
                        $s.blankSearch();
                        break;
                    }
            }
        }, true);

    $s.getItems = (ean) => {
        if(paneFactory.getItemByBarcode(ean, getItems))
            $s.blankSearch();
        console.log('got items for parent');
    };

    $s.$watch('barcode', (nv) => {
        if(nv && paneFactory.getItemByBarcode($s.barcode, getItems))
            $s.barcode ='';
    });

        $s.sellThis = () => {
            if($s.canRelease) {
                for (let row of $s.rows)
                    row.price = paneFactory.getDiscountedPrice(row.price, $s.buyer.discount);
                paneFactory.releaseItems($s, 'addSellingsSet');
            }
        };

        $s.deleteRows = function () {
            if (this.$index >= 0)
                $s.rows.splice(this.$index,1);
            else {
                $s.comment = "";
                $s.rows=[];
                setDefaultBuyer();
            }
            $s.blankSearch();
        };

        let getRowsForReports =() => {
            return printFactory.getRowsForReports($s, 'price');

        };

        $s.setReportData = () => {
            let data = { stock: $s.stock, buyer: $s.buyer, comment: $s.comment, rows: getRowsForReports()};
            printFactory.setReportsByParams([{type: 'invoiceWithContract', data: data, method: 'addInvoice'},
                {type: 'invoice', data: data, method: 'addInvoice'},
                {type: 'salesReceipt', data: data, method: 'addInvoice'}], $s.reports);
        };

        $s.checkRows = () => {
            paneFactory.checkRowsForSelling($s, paneFactory.user);
        };

        $s.$on("tabSelected", (event, data) => {
            if (data.event != null && paneFactory.paneToggler(data.pane) === "Продавать!") {
                $s.user = paneFactory.user;
                setDefaultBuyer();
                $s.blankSearch();
            }
        });

        $s.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [
                {keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.openQuantityChangerModal},
                {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.sellThis, ctrlReq: true},
                {keyCode: paneFactory.keyCodes.backSpaceKeyCode, doAction: $s.deleteRows, ctrlReq: true}
            ]);
        };

        $s.setEanPrefix = e => {
            $s.barcode = paneFactory.generateEanByKey(e, $s.barcode);
        };

        $s.blankSearch = () => {
            $s.barcode = "";
            $s.item = getEmptyItem();
            paneFactory.changeElementState(eanInputElement, ['focus']);
        };

        $s.openQuantityChangerModal = (index) => {
            modalFactory.openModal(index, $s.rows, $s.quantityChangerModalData);
        };

    };

    angular.module('selling-pane', ['modals', 'pane-factory'])
        .directive( "sellingPane", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {},
                template: sellingPaneTpl,
                controller:  ($scope, $http, paneFactory, printFactory, modalFactory, itemFactory) => {
                    return sellingPaneCntrlr($scope, $http, paneFactory, printFactory, modalFactory, itemFactory);
                },
                link: () => {}
            }
        });
