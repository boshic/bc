let commonPaneCtrlr = ($s, filterFactory, paneFactory, printFactory, modalFactory, paneConfig) => {

    let config = paneFactory[paneConfig];
    let httpService = paneFactory.getHttpService();

    $s.rows=[];
    $s.reports = [];
    $s.filter = {visible: false};
    $s.requestParams = {requestsQuantity: 0};

    $s.quantityChangerModalData = { hidden : true, row: {} };
    $s.sellingModalConfig = {hidden : true, row: {}};
    $s.textEditModalData = {hidden : true, row: {}};
    $s.movingModalConfig = {hidden : true, row: {}};
    $s.modalConfig = {};

    $s.searchInputId = paneFactory.generateUuid();
    $s.warning = "";
    $s.totals = {};
    $s.user = {};

    // $s.inventoryBuyer = itemFactory.buyerConfig.getEmptyItem();

    $s.setReports = () => {
        $s.reports = [];
        if(config.checkAddingReportCondition($s))
            printFactory.setReportsByParams(config.getReportsParams($s), $s.reports);
    };

    let searchTerms = paneFactory.getSearchTermsForGetItemsByFilter($s, config.findItemUrl);

    let beforeFindItemsByFilter = () => {
        return config.doBeforeFindItemsByFilter($s);
    };

    let findItemsByFilter = () => {
        if(!(typeof beforeFindItemsByFilter === 'function') || (beforeFindItemsByFilter()))
            paneFactory.getItemsBySearchTermsAndFilter(searchTerms, $s.filter);
    };

    $s.calcTotalsAndRefresh = () => {
        return paneFactory.calcTotalsAndRefresh($s.filter, findItemsByFilter);
    };

    $s.resetFilter = () => {
        config.resetFilter(filterFactory, $s.filter);
    };

    $s.setEanPrefix = e => {
        $s.filter.ean = paneFactory.generateEanByKey(e, $s.filter.ean);
    };

    $s.setInventoryValuesToZeroes = () => {
        config.setInventoryValuesToZeroes($s);
    };

    $s.setInventoryValues = () => {
        config.setInventoryValues($s, {httpService});
    };

    $s.focusOnEanInput = () => {
        if(!$s.filter.visible)
            paneFactory.changeElementState( document.getElementById($s.searchInputId), ['focus']);
    };

    $s.$watch('filter', (nv, ov) => {
        if ((nv) && (ov)) {
            filterFactory.doFilter($s.calcTotalsAndRefresh, findItemsByFilter, nv, ov);
        }
    }, true);

    $s.sellingModalConfig.refresh = $s.movingModalConfig.refresh = $s.modalConfig.refresh = () => {
        $s.calcTotalsAndRefresh()
    };

    $s.afterSearch = () => {
        return config.afterSearch($s);
    };

    $s.handleKeyup = e => {
        paneFactory.keyUpHandler(e, config.getKeyupCombinations($s, paneFactory.keyCodes));
    };

    $s.blankSearch = () => {
        $s.warning = "";
        $s.filter.ean = "";
        $s.focusOnEanInput();
    };

    $s.$on("tabSelected", (event, data) => {
        if (data.event != null && paneFactory.paneToggler(data.pane) === config.paneId) {
            $s.user = paneFactory.user;
            $s.focusOnEanInput();
        }
    });

    $s.editItem = (row) => {
        config.editItem($s, {row, httpService });
    };

    $s.openTextModal = (x) => {
        config.openTextModal($s, {data: x, httpService, modalFactory});
    };

    $s.makeReturn = (index) => {
        config.makeReturn($s, {index, httpService, modalFactory});
    };

    $s.changeItemDate =  (row) => {
        config.changeItemDate($s, {row, httpService});
    };

    $s.sellThis = (data) => {
        angular.extend($s.sellingModalConfig, {hidden: false, data, parentScope: $s});
    };

    $s.moveThis = (data) => {
        angular.extend($s.movingModalConfig, {hidden: false, data, parentScope: $s});
    };

    $s.openQuantityChangerModalInInventoryMode = (index) => {
        config.openQuantityChangerModalInInventoryMode($s, index, modalFactory);
    };

    $s.applyInventoryResults = () => {
        config.applyInventoryResults($s, {httpService});
    };

};

module.exports = commonPaneCtrlr;