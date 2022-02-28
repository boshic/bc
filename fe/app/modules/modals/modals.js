
    import sellingModalTpl from './selling-modal.html';
    import movingModalTpl from './moving-modal.html';
    import invoiceModalTpl from './invoice-modal.html';

    let closeModal = (afterClose, modalData) => {
        if(typeof afterClose === "function")
            afterClose();
        modalData.hidden = true;
    };

    let quantityChangerModalCtrlr = function($scope, paneFactory) {

        let vm = this;
        vm.cmpUuid = paneFactory.generateUuid();

        $scope.$watch(() => vm.modalData.row.quantity, (nv) => {
            if (nv >= 0) {
                vm.modalData.row.quantity =
                    paneFactory.checkNumberLimit(vm.modalData.row.quantity, vm.modalData.row.limitQuantity);
                vm.modalData.row.quantity =
                    paneFactory.fixIfFractional(vm.modalData.row.quantity, vm.modalData.row.item.unit);
            }
            if(typeof vm.checkInput === 'function')
                vm.checkInput();

        });

        vm.closeModal = () => {
            return closeModal(vm.afterClose, vm.modalData);
        };

        $scope.$watch(() => vm.modalData.hidden, (nv, ov) => {
            if(!nv || ov) {
                paneFactory.changeElementState(document.getElementById(vm.cmpUuid), ['focus', 'select']);

            }
        });

        vm.handleKeyup = e => {
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: vm.closeModal}]);
        };
    };
    quantityChangerModalCtrlr.$inject = ['$scope', 'paneFactory', '$element'];

    let textEditModalCtrlr = function($scope, paneFactory) {

        let vm = this;
        vm.cmpUuid = paneFactory.generateUuid();

        let causes = [{id: '1', name: 'Другое'},{id: '2', name: 'Причина списания'}];

        let selectCommentCause = (cause) => {
            let result = causes[0];
            if(cause)
                causes.forEach((c) => {
                    if(c.name.toLowerCase() === cause.toLowerCase())
                        return result = c;
                });
            return  result;
        };

        vm.commentCause = { causes, selectedOption: causes[0] };

        vm.closeModal = () => {
            vm.modalData.row.commentCause = vm.commentCause.selectedOption.name;
            if(vm.modalData.row.comment.length)
                return closeModal(vm.afterClose, vm.modalData);
            else vm.modalData.hidden = true;
        };

        $scope.$watch(() => vm.modalData.hidden, (nv, ov) => {
            if(!nv || ov) {
                let textInput = document.getElementById(vm.cmpUuid) || undefined;
                if(angular.isDefined(textInput))
                    paneFactory.changeElementState(textInput.querySelector('#comment-input'), ['focus', 'select']);
                vm.commentCause.selectedOption = selectCommentCause(vm.modalData.row.commentCause);
            }
        });

        vm.handleKeyup = e => {
            // paneFactory.keyupHandler(e, vm.closeModal);
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: vm.closeModal}]);
        };
    };
    textEditModalCtrlr.$inject = ['$scope', 'paneFactory', '$element'];

    let commonReleaseModalController = ($s, paneFactory, modalFactory, modalParams) => {

        $s.quantityInputId = paneFactory.generateUuid();
        $s.modalConfig.hidden = true;
        let config = modalFactory[modalParams];
        let getReleaseItemParams = typeof config.getReleaseItemParams === 'function' ?
            config.getReleaseItemParams : () => { return undefined; };

        let getReleaseItemUrl = typeof config.addItemUrl === 'function' ?
            config.addItemUrl : () => { return config.addItemUrl; };
        // $s.requestParams = {requestsQuantity: 0};

        $s.totals = {
            date: new Date,
            sum: 0,
            quantity: 0
        };

        let checkInput = row => {
            row.quantity = paneFactory.checkNumberLimit(row.quantity, row.currentQuantity);
        };

        $s.$watchCollection(config.watchingValue, () => {

            if($s.modalConfig.data) {
                let coming = $s.modalConfig.data;
                $s.comment = "";
                $s.stock = coming.stock || null;

                $s.rows = [];
                $s.rows.push(
                    {
                        id: $s.modalConfig.id,
                        coming: coming,
                        item: coming.item,
                        price: coming.item.price > 0 ? coming.item.price : coming.priceOut,
                        vat: paneFactory.user.stock.organization.vatValue,
                        quantity: 1,
                        currentQuantity: coming.currentQuantity
                    }
                );

                $s.modalConfig.data = undefined;
            }
            if($s.rows)
                $s.checkRows();
            if(!$s.modalConfig.hidden)
                setTimeout(() => { paneFactory
                    .changeElementState(document.getElementById($s.quantityInputId), ['focus', 'select']);
                }, 0);
        }, true);

        $s.closeModal = () => {
            $s.modalConfig.data = undefined;
            $s.modalConfig.hidden = true;
        };

        $s.handleKeyup =  e => {
            paneFactory.keyUpHandler(e, [
                {keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.closeModal},
                {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.release, ctrlReq: true}
            ]);
        };

        $s.setReportData = () => {
            modalFactory.setReportData($s, config);
        };

        $s.checkRows = () => {
            paneFactory.checkRows($s, paneFactory.user, config.checkingType);
        };

        $s.release = () => {
            modalFactory.releaseItem($s, getReleaseItemUrl($s.modalConfig.url),
                getReleaseItemParams($s));
        };
    };

    let sellingModalCtrlr = ($s, paneFactory, modalFactory) => {

        return commonReleaseModalController($s, paneFactory, modalFactory, 'sellingModalConfig');
    };

    let movingModalCntrlr = ($s, paneFactory, modalFactory) => {

        return commonReleaseModalController($s, paneFactory, modalFactory, 'movingModalConfig');
    };

    let invoiceModalCtrlr = ($s, printFactory ) => {

        $s.modalConfig = {hidden: true};
        let data = {};
        printFactory.setReportsByParams([
            {type: 'invoiceWithContract', data, method: 'addInvoice'},
            {type: 'workCompletionStatement', data, method: 'addInvoice'},
            {type: 'invoice', data, method: 'addInvoice'}],
            $s.reports = []);

      $s.deleteInvoice = () => {
        if(confirm("Подтвердите удаление и запрет на выгрузку документа! "))
            printFactory.deleteReport({ id: $s.invoice.id })
              .then(closeModal($s.refresh, $s.modalConfig));
      };
      $s.allowInvoiceUpload = () => {
        if(confirm("Подтвердите разрешение на загрузку документа! "))
          printFactory.allowUploadReport({ id: $s.invoice.id })
            .then(closeModal($s.refresh, $s.modalConfig));
      };
    };

    angular.module('modals', [])
        .component( "quantityChangerModal", {
            transclude: true,
            template:
            "<div ng-hide='$ctrl.modalData.hidden' class='trans-layer'></div>" +
            "<div class='modal-container' ng-class='{modalactive: !$ctrl.modalData.hidden}'" +
                    "ng-keyup='$ctrl.handleKeyup($event)'" +
                    "ng-transclude>" +
                "<div>" +
                    "<div>" +
                        "<div class='item-name-on-qty-changer-modal' " +
                            "ng-show='$ctrl.modalData.row.item.name.length || $ctrl.modalData.row.coming.item.name'>" +
                            "<span>{{$ctrl.modalData.row.item.name || $ctrl.modalData.row.coming.item.name}}</span>" +
                        "</div>" +
                        "<div style='font-size: 18px;' ng-show='$ctrl.modalData.row.commentCause.length'>" +
                            "<comment-input ng-model='$ctrl.modalData.row.comment'></comment-input>" +
                        "</div>" +
                        "<div style='display: flex;'>" +
                            "<div style='width: 60%;'>" +
                                "<input type='number' class='form-control quantity-changer-input'" +
                                    "min='0' max='$ctrl.modalData.row.limitQuantity'" +
                                    "id = '{{$ctrl.cmpUuid}}'" +
                                    "ng-model='$ctrl.modalData.row.quantity'/>" +
                            "</div>"+
                            "<div class='quantity-rest-container'>" +
                                "<span ng-show='$ctrl.modalData.row.availQuantity > 0'>" +
                                    "<span>из: </span>" +
                                    "<span>" +
                                        "{{$ctrl.modalData.row.availQuantity}}" +
                                    "</span>" +
                                "</span>" +
                                "<span ng-show='$ctrl.modalData.row.item.unit.length > 0 || $ctrl.modalData.row.coming.item.unit.length > 0'>" +
                                    "{{($ctrl.modalData.row.item.unit || $ctrl.modalData.row.coming.item.unit)}}" +
                                "</span>" +
                            "</div>" +
                        "</div>"+
            "</div>" +
                "</div>" +
                "<div>" +
                    "<button class='quantity-changer-btn' ng-click='$ctrl.closeModal()'>ОК</button>" +
                "</div>" +
            "</div>",
            controller: quantityChangerModalCtrlr,
            bindings: {
                checkInput: '&?', afterClose: '&?', modalData: '='
            }
        })
        .component( "textEditModal", {
        transclude: true,
        template:
        "<div ng-hide='$ctrl.modalData.hidden' class='trans-layer'></div>" +
        "<div class='modal-container' ng-class='{modalactive: !$ctrl.modalData.hidden}'" +
                "ng-keyup='$ctrl.handleKeyup($event)'>" +
            "<div class='comment-cause'>" +
                "<div><label for='cause-select'>Назначение комментария:</label></div>" +
                "<div><select style='font-size: 30px;' name='cause-select'" +
                    "ng-options='option.name for option in $ctrl.commentCause.causes track by option.id'" +
                    "ng-model='$ctrl.commentCause.selectedOption'></select></div>" +
            "</div>" +
            "<div class='comment-input-on-text-modal' id = '{{$ctrl.cmpUuid}}'>" +
                "<comment-input ng-model='$ctrl.modalData.row.comment'></comment-input>" +
            "</div>" +
            "<div>" +
                "<button class='quantity-changer-btn' ng-click='$ctrl.closeModal()'>ОК</button>" +
            "</div>" +
        "</div>",
        controller: textEditModalCtrlr,
        bindings: {
            checkInput: '&?', afterClose: '&?', modalData: '='
        }
    })
        .directive( "sellingModal", () => {
            return {
                restrict: "E",
                template: sellingModalTpl,
                scope: {modalConfig: '='},
                transclude: true,
                controller: ( $scope, paneFactory, modalFactory) => {
                    return sellingModalCtrlr($scope, paneFactory, modalFactory);
                }
            }
        })
        .directive( "movingModal", () => {
            return {
                restrict: "E",
                // templateUrl: 'html/moving/moving-modal.html',
                template: movingModalTpl,
                scope: {modalConfig: '='},
                transclude: true,
                controller: ( $scope, paneFactory, modalFactory) => {
                    return movingModalCntrlr($scope, paneFactory, modalFactory);
                },
                link: () => {}
            }
        })
        .directive( "invoiceModal", () => {
            return {
                restrict: 'E',
                transclude: true,
                scope: {
                    modalConfig: '=', invoice: '=', refresh : '&'
                },
                template: invoiceModalTpl,
                controller: ($scope, printFactory, $http) => {
                    return invoiceModalCtrlr($scope, printFactory, $http);
                }
            }
        })
        .factory('modalFactory',['httpService', 'paneFactory', 'printFactory',
            function (httpService, paneFactory, printFactory) {
                return {
                    sellingModalConfig: {
                        watchingValue: '[modalConfig.hidden, buyer]',
                        addItemUrl: (url) => {
                            if(angular.isDefined(url))
                                return url;
                            return   'addOneSelling';
                        },
                        checkingType: 'selling',
                        priceProp: 'price',
                        allowedReports: (data) => {
                            return [
                                {type: 'invoiceWithContract', data: data, method: 'addInvoice'},
                                {type: 'invoice', data: data, method: 'addInvoice'}
                            ];
                        }
                    },
                    movingModalConfig: {
                        watchingValue: '[modalConfig.hidden, stock]',
                        addItemUrl: 'addOneMoving',
                        getReleaseItemParams: ($s) => {
                            return $s.stock ? {params:{stockId: $s.stock.id}}: undefined;
                        },
                        checkingType: 'moving',
                        priceProp: 'price',
                        allowedReports: (data) => {
                            return [];
                        }
                    },
                    openModal: (index, rows, modalData) => {
                        if(rows.length) {
                            modalData.hidden = false;
                            (angular.isDefined(index)) ? modalData.row = rows[index] : modalData.row = rows[0];
                        }
                    },
                    openModalWithConfig: (config) => {
                        if(config.rows.length)
                            config.modalData.hidden = false;
                        let i = config.itemId > 0 ? paneFactory.checkDuplicateRowsByItem(config.itemId, config.rows) : 0;
                        if(angular.isDefined(config.availQuantityField))
                            config.rows[i].availQuantity = config.rows[i][config.availQuantityField];
                        if(angular.isDefined(config.limitQuantityField))
                            config.rows[i].limitQuantity = config.rows[i][config.limitQuantityField];
                        config.modalData.row = config.rows[i];
                    },
                    setReportData: ($s, config) => {
                        let data = { stock: $s.stock, buyer: $s.buyer, comment: $s.comment,
                            rows: printFactory.getRowsForReports($s, config.priceProp)};
                        printFactory.setReportsByParams(
                            config.allowedReports({
                                stock: $s.stock, buyer: $s.buyer, comment: $s.comment,
                                rows: printFactory.getRowsForReports($s, config.priceProp)
                            }), $s.reports);
                    },
                    releaseItem: ($s, url, params) => {
                        $s.checkRows();
                        let row =   $s.rows[0];
                        $s.rows = [];
                        $s.closeModal();
                        if($s.canRelease)
                            httpService.addItem({data: row, url,
                                requestParams: $s.modalConfig.parentScope.requestParams, params})
                                .then(
                                    resp => {
                                        if(resp.success)
                                            paneFactory.successSound.play();
                                        $s.modalConfig.parentScope.warning = resp.text;
                                        $s.modalConfig.refresh();
                                    },
                                    () => {
                                        $s.modalConfig.parentScope.warning = "ошибка при проведении продажи!";
                                    }
                                );
                    }
                };
            }
        ]);