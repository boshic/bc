let commonPaneReleaseCtrlr = ($s, itemFactory, filterFactory, paneFactory, printFactory, modalFactory, paneConfig) => {

    let config = paneFactory[paneConfig];
    let httpService = paneFactory.getHttpService();

    let getEmptyItem = itemFactory.itemConfig.getEmptyItem;
    $s.item = getEmptyItem();

    $s.barcode = '';

    paneFactory.setPaneDefaults($s, {config, filterFactory});

    $s.filter = {visible: false, allowAllStocks: false, sortField: '$index', reverseOrder: false};

    $s.canRelease = false;
    $s.totals = { date: new Date, sum: 0, quantity: 0 };

    let getItems =(ean) => {
        paneFactory.getItemsForRelease(config.getFindItemParams(ean, $s), config.findItemUrl, $s);
    };

    $s.getItemsForReleaseByFilter = () => {
        paneFactory.getItemsForReleaseByFilter($s);
    };

    $s.$watchCollection(config.watchingCollectionValue, () => {
        if ($s.rows.length) {
            $s.checkRows();
            $s.blankSearch();
        }
    }, true);

    $s.$watch('barcode', (nv) => {
        if(nv && paneFactory.getItemByBarcode($s.barcode, getItems))
            $s.barcode ='';
    });

    $s.getItemsWithItemInput = (ean) => {
        if(paneFactory.getItemByBarcode(ean, getItems))
            $s.blankSearch();
        console.log('got items for parent');

    };

    $s.moveThis = () => {
        paneFactory.releaseItems($s, config.moveItemsUrl, config.getMoveItemsParams($s));
    };

    $s.deleteRows =  (itemId) => {
        paneFactory.deletePaneRows($s, {itemId});
    };

    $s.checkRows = () => {
        paneFactory.checkRows($s, paneFactory.user, config.checkRowsType);
    };

    $s.$on("tabSelected", (event, data) => {
        if (data.event != null && paneFactory.paneToggler(data.pane) === config.paneId) {
            $s.blankSearch();
            $s.user = paneFactory.user;
        }
    });

    $s.handleKeyup = e => {
        paneFactory.keyUpHandler(e, config.getKeyupCombinations($s, paneFactory.keyCodes));
    };

    $s.setEanPrefix = e => {
        $s.barcode = paneFactory.generateEanByKey(e, $s.barcode);
    };

    $s.editItem = (barcode) => {
        $s.item = angular.extend(getEmptyItem(), {name: barcode});
    };

    $s.blankSearch = () => {
        $s.warning = '';
        $s.barcode = '';
        $s.item = getEmptyItem();
        paneFactory.changeElementState(document.getElementById($s.searchInputId), ['focus']);
    };

    $s.openQuantityChangerModal = (itemId) => {
        if($s.rows.length)
            modalFactory.openModalWithConfig({itemId, rows: $s.rows,
                availQuantityField : 'currentQuantity',
                limitQuantityField : 'currentQuantity',
                modalData: $s.quantityChangerModalData});
    };

};

export default commonPaneReleaseCtrlr;
