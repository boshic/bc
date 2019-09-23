import comingItemTpl from './coming-item.html';

    let comingItemCntrlr = ($s, paneFactory, httpService, itemFactory) => {

        $s.coming = {};
        $s.canCome = false;
        $s.canChange = false;
        $s.hasSellings = false;
        $s.allowAllStocks = false;
        $s.requestParams = {requestsQuantity: 0};

        let setPermissions = (canCome, canChange) => {
            if(angular.isDefined(canCome))
                $s.canCome = canCome;
            if(angular.isDefined(canChange))
                $s.canChange = canChange;
        };

        $s.$watch('comingConfig', () => {
            if ($s.comingConfig.id)
                $s.getComing($s.comingConfig.id);
        });

        $s.$watch('coming', () => {
            $s.checkComing();
        }, true);

        $s.getComing = id => {
            $s.coming = {date: new Date(), quantity:1,
                buyer: itemFactory.buyerConfig.getEmptyItem(),
                item: itemFactory.itemConfig.getEmptyItem(),
                user: paneFactory.user
            };
            if (id) {
                httpService.getItemById({id: id, url: 'getComingById', requestParams: $s.requestParams})
                    .then(
                    resp => {
                        if (!(resp.item === null)) {
                            setPermissions(true);
                            $s.coming = resp.item;
                            $s.hasSellings = !($s.coming.quantity == $s.coming.currentQuantity);
                            paneFactory.changeElementState(document.getElementById('coming-item-start-pos'), ['focus']);
                        }
                    },
                    resp => {console.log(resp);}
                );
                setPermissions(false, false);
            } else {
                $s.hasSellings = false;
                $s.checkComing();
            }
        };

        $s.handleKeyup = e => {
            // if (e.keyCode == 27)
            //     $s.comingConfig.refresh();
            paneFactory.keyUpHandler(e, [{keyCode: paneFactory.keyCodes.escKeyCode, doAction: vm.comingConfig.refresh}]);
        };

        $s.updateComing = () => {
            if(confirm("Подтвердите изменение"))
                httpService.addItem({data: $s.coming, url:'updateComing',
                    requestParams: $s.requestParams})
                .then(
                    () => { $s.comingConfig.refresh(); },
                    (resp) => {console.log(resp);}
                );
        };

        $s.addComing = () => {
            $s.coming.user = paneFactory.user;
            httpService.addItem({data: $s.coming, url:'addComing',
                requestParams: $s.requestParams})
                .then(
                resp => {
                    $s.coming = resp.item;
                    $s.checkComing();
                    $s.hasSellings = false;
                },
                resp => { console.log(resp);}
            );
        };

        $s.deleteComing = () => {
            if(confirm("Подтвердите удаление"))
                httpService.getItemById({id: $s.coming.id, url: 'deleteComing',
                    requestParams: $s.requestParams})
                    .then(
                        () => {
                            $s.comingConfig.refresh();
                        },
                        resp => {
                            console.log(resp);
                        }
            );
        };

        $s.getPriceIn =  () => {
            $s.coming.price = ($s.coming.sum/$s.coming.quantity).toFixed(4);
        };

        $s.changeQuantity = () => {
            $s.coming.sum = ($s.coming.price*$s.coming.quantity).toFixed(2);
        };

        $s.checkComing = () => {

            let coming = $s.coming;
            if ((coming.quantity > 0) && angular.isDefined(coming.item)
                && (coming.price != null && coming.priceOut != null)
                && (coming.price >= 0 && coming.priceOut >= 0)
                && (angular.isDefined(coming.item.id) && coming.item.id !== null)
                && (angular.isDefined(coming.doc.id) && coming.doc.id !== null)
                && (angular.isDefined(coming.doc.date)))
                    setPermissions(true, coming.id > 0 && $s.hasSellings === false);
             else
                setPermissions(false, false);
        };

    };

    angular.module('coming-item', [])
        .directive( "comingItem", () => {
            return {
                restrict: "E",
                template: comingItemTpl,
                // templateUrl: 'html/coming/coming-item.html',
                scope: { comingConfig: '<'},
                transclude: true,
                controller: ($scope, paneFactory, httpService, itemFactory) => {
                        return comingItemCntrlr($scope, paneFactory, httpService, itemFactory);
                    },
                link: () => {}
            }
        })
        .directive( "comingModal", () => {
            return {
                restrict: "E",
                // templateUrl: 'html/coming/coming-modal.html',
                template:
                "<div ng-hide='modalConfig.hidden' class='trans-layer'></div>" +
                "<div class='modal-container' ng-class='{modalactive: !modalConfig.hidden}' ng-transclude>" +
                "<span class='glyphicon glyphicon-remove item-blank close-modal' ng-click='closeComingModal()'></span>" +
                "<my-tabs>" +
                "<my-pane name='Данные по приходу товара'>" +
                "<coming-item coming-config='comingConfig'></coming-item>" +
                "</my-pane>" +
                "</my-tabs>" +
                "</div>",
                scope: {modalConfig: '=modalConfig'},
                transclude: true,
                controller: $scope => {
                    $scope.modalConfig.hidden = true;
                    $scope.$watch('modalConfig.hidden', () => {
                        $scope.comingConfig = {
                            id: $scope.modalConfig.id,
                            refresh: $scope.closeComingModal
                        };
                    });

                    $scope.closeComingModal = () => {
                        $scope.modalConfig.id = null;
                        $scope.modalConfig.hidden = true;
                        $scope.modalConfig.refresh();
                    };
                },
                link: ( scope,elem) => {}
            }
        });