import sellingPaneTpl from './selling-pane.html';

let sellingPaneCntrlr = ($s, $http, paneFactory, elem, printFactory, modalFactory) => {

        $s.rows=[];

        $s.item = {name: ''};

        $s.allowAllStocks = false;
        $s.quantityChangerModalData = {hidden : true, row: {}};
        $s.barcode = "";
        $s.warning = "";
        $s.comment = "";
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

        $s.$watch('barcode', (nv) => {
            if(nv && paneFactory.getItemByBarcode($s.barcode, getItems))
                $s.barcode ='';
        });

        $s.$watch("item", (nv) => {
            if ((nv) && ("id" in nv)) {
                getItems($s.item.ean);
                $s.blankSearch();
            }
        }, true);


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

        $s.setReportData = () => {
            let data = { stock: $s.stock, buyer: $s.buyer, comment: $s.comment, rows: $s.rows};
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
            paneFactory.keyupHandler(e, $s.openQuantityChangerModal, $s.sellThis);
        };

        $s.setEanPrefix = e => {
            $s.barcode = paneFactory.generateEanByKey(e, $s.barcode);
        };

        $s.blankSearch = () => {
            $s.barcode = "";
            $s.item = {name:''};
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
                controller:  ($scope, $http, paneFactory, $element, printFactory, modalFactory) => {
                    return sellingPaneCntrlr($scope, $http, paneFactory, $element, printFactory, modalFactory);
                },
                link: () => {}
            }
        });
