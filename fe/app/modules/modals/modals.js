
    import sellingModalTpl from './selling-modal.html';
    import movingModalTpl from './moving-modal.html';
    import invoiceModalTpl from './invoice-modal.html';

    let closeModal = (afterClose, modalData) => {
        if(typeof afterClose === "function")
            afterClose();
        modalData.hidden = true;
    };

    let quantityChangerModalCtrlr = function($scope, paneFactory, elem) {

        let vm = this;
        vm.cmpUuid = paneFactory.generateUuid();

        $scope.$watch(() => vm.modalData.row.quantity, (nv) => {
            if (nv >= 0) {
                vm.modalData.row.quantity =
                    paneFactory.checkNumberLimit(vm.modalData.row.quantity, vm.modalData.row.currentQuantity);
            }

            if(vm.checkInput)
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
            // paneFactory.keyupHandler(e, vm.closeModal);
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: vm.closeModal}]);
        };
    };
    quantityChangerModalCtrlr.$inject = ['$scope', 'paneFactory', '$element'];

    let textEditModalCtrlr = function($scope, paneFactory, elem) {

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

    let sellingModalCtrlr = ($s, $http, paneFactory, elem, printFactory) => {

    $s.totals = {
        date: new Date,
        sum: 0,
        quantity: 0
    };

    let checkInput = row => {
        row.quantity = paneFactory.checkNumberLimit(row.quantity, row.currentQuantity);
    };

    $s.$watchCollection("[modalConfig.hidden, buyer]", () => {

        if($s.modalConfig.data) {
            let coming = $s.modalConfig.data;
            $s.comment = "";
            $s.stock = coming.stock;

            $s.rows = [];
            $s.rows.push(
                {
                    coming: coming,
                    item: coming.item,
                    price: coming.priceOut,
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
            $s.changeQuantInLastRow();
    }, true);

    $s.modalConfig.hidden = true;

    $s.closeModal = () => {
        $s.modalConfig.data = undefined;
        $s.modalConfig.hidden = true;
    };

    $s.handleKeyup =  e => {
        // if (e.keyCode == 27)
        //     $s.closeModal();
        // if (e.keyCode == 13)
        //     $s.sellThis();
        paneFactory.keyUpHandler(e, [
            {keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.closeModal},
            {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.sellThis, ctrlReq: true}
        ]);
    };

    $s.setReportData = () => {
        let data = { stock: $s.stock, buyer: $s.buyer, comment: $s.comment, rows: $s.rows};
        printFactory.setReportsByParams([
            {type: 'invoiceWithContract', data: data, method: 'addInvoice'},
            {type: 'invoice', data: data, method: 'addInvoice'}], $s.reports);
    };

    $s.checkRows = () => {
        paneFactory.checkRowsForSelling($s, paneFactory.user);
    };

    $s.sellThis = () => {
        $s.checkRows();
        let row =   $s.rows[0];
        $s.closeModal();
        $http.post('/addOneSelling', row)
            .then(
                resp => {
                    (resp.data.success) ? paneFactory.successSound.play() : $s.warning = resp.data.text;
                    $s.modalConfig.refresh();
                },
                () => {$s.warning = "ошибка при проведении продажи!";}
            );
    };

    $s.changeQuantInLastRow = () => {
        paneFactory.changeElementState(document.getElementById('change-quantity-on-selling-modal'), ['focus']);
    };

};

    let movingModalCntrlr = ($s, $http, paneFactory, elem) => {

    $s.allowAllStocks = false;
    $s.totals = {
        date: new Date,
        sum: 0,
        quantity: 0
    };

    let checkInput = moving => {
        moving.quantity = paneFactory.checkNumberLimit(moving.quantity, moving.currentQuantity);
    };

    let calcTotals = () => {
        $s.totals.sum = $s.totals.quantity = 0;
        for (let moving of $s.movings) {
            checkInput(moving);
            $s.totals.sum += +(moving.quantity * moving.price);
            $s.totals.quantity += +moving.quantity;
        }
    };

    $s.$watchCollection("[modalConfig.hidden, stock]", () => {
        if($s.modalConfig.data) {
            $s.comment = "";
            $s.movings = [{
                quantity: 1,
                currentQuantity: $s.modalConfig.data.currentQuantity,
                coming: $s.modalConfig.data,
                price: $s.modalConfig.data.priceOut
            }];
            $s.modalConfig.data = undefined;
        }
        if($s.movings)
            $s.checkMoving();
        if(!$s.modalConfig.hidden)
            $s.changeQuantInLastRow();
    }, true);

    $s.modalConfig.hidden = true;

    $s.closeModal = () => {
        $s.modalConfig.data = undefined;
        $s.modalConfig.hidden = true;
        $s.modalConfig.refresh();
    };

    $s.handleKeyup =  e => {
        // if (e.keyCode == 27)
        //     $s.closeModal();
        // if (e.keyCode == 13)
        //     $s.moveThis();
        paneFactory.keyUpHandler(e, [
            {keyCode: paneFactory.keyCodes.escKeyCode, doAction: $s.closeModal},
            {keyCode: paneFactory.keyCodes.enterKeyCode, doAction: $s.moveThis, ctrlReq: true}
        ]);
    };

    $s.checkMoving = () => {
        $s.canMove = false;
        calcTotals();
        if (($s.movings.length) && ($s.movings[0].coming.stock.id !== $s.stock.id)) {
            for (let moving of $s.movings) {
                if (!moving.quantity)
                    return;
                moving.user = paneFactory.user;
                moving.comment = $s.comment;
            }
            $s.canMove = true;
        }
    };

    $s.moveThis = () => {
        $s.checkMoving();
        let moving =   $s.movings[0];
        $s.modalConfig.hidden = true;
        $http.post('/addOneMoving', moving, {
            params: {
                stockId: $s.stock.id
            }
        })
            .then(
                resp => {
                    $s.modalConfig.data = undefined;
                    $s.modalConfig.refresh();
                    console.log(resp.data.text);
                    // (resp.data.success) ? appConfig.sound.play() : $s.warning = resp.data.text;
                    // $s.comment = "";
                },
                () => {$s.warning = "ошибка при проведении продажи!";}
            );
    };

    $s.changeQuantInLastRow = () => {
        paneFactory.changeElementState(document.getElementById('change-quantity-on-moving-modal'), ['focus']);
    };
};

    let invoiceModalCtrlr = ($s, printFactory, $http ) => {

        $s.modalConfig = {hidden: true};
        let data = {};
        printFactory.setReportsByParams([
            {type: 'invoiceWithContract', data, method: 'addInvoice'},
            {type: 'workCompletionStatement', data, method: 'addInvoice'},
            {type: 'invoice', data, method: 'addInvoice'}],
            $s.reports = []);

        $s.deleteInvoice = () => {
            if(confirm("Подтвердите удаление"))
                $http.get('/deleteInvoice', { params: { id: $s.invoice.id }})
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
                        "<div class='item-name-on-qty-changer-modal' ng-show='$ctrl.modalData.row.item.name.length'>" +
                            "<span>{{$ctrl.modalData.row.item.name}}</span>" +
                        "</div>" +
                        "<div style='font-size: 18px;' ng-show='$ctrl.modalData.row.commentCause.length'>" +
                            "<comment-input ng-model='$ctrl.modalData.row.comment'></comment-input>" +
                        "</div>" +
                        "<div style='display: flex;'>" +
                            "<div style='width: 60%;'>" +
                                "<input type='number' class='form-control quantity-changer-input'" +
                                    "min='0' max='$ctrl.modalData.row.currentQuantity'" +
                                    "id = '{{$ctrl.cmpUuid}}'" +
                                    "ng-model='$ctrl.modalData.row.quantity'/>" +
                            "</div>"+
                            "<div class='quantity-rest-container'>" +
                                "<span ng-show='$ctrl.modalData.row.currentQuantity > 0'>" +
                                    "<span>из:</span>" +
                                    "<span>{{' ' + $ctrl.modalData.row.currentQuantity + $ctrl.modalData.row.coming.item.unit || 'ед.'}}</span>" +
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
                // templateUrl: 'html/selling/selling-modal.html',
                scope: {modalConfig: '='},
                transclude: true,
                controller: ( $scope, $http, paneFactory, $element, printFactory) => {
                    return sellingModalCtrlr($scope, $http, paneFactory, $element, printFactory);
                },
                link: ( scope,elem) => {}
            }
        })
        .directive( "movingModal", () => {
            return {
                restrict: "E",
                // templateUrl: 'html/moving/moving-modal.html',
                template: movingModalTpl,
                scope: {modalConfig: '='},
                transclude: true,
                controller: ( $scope, $http, paneFactory, $element) => {
                    return movingModalCntrlr($scope, $http, paneFactory, $element);
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
        .factory('modalFactory',[
            function () {
                return {
                    openModal: (index, rows, modalData) => {
                        if(rows.length) {
                            modalData.hidden = false;
                            (angular.isDefined(index)) ? modalData.row = rows[index] : modalData.row = rows[0];
                        }
                    },
                };
            }
        ]);
// });