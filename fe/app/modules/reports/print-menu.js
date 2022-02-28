// define(['angular'], angular => {

    let printMenuCntrlr = function ($scope, httpService, paneFactory, printFactory)  {

        let vm = this;
        vm.btnId = paneFactory.generateUuid();

        let openReport = (report, id) => {
            window.open('reports/' + report.group + '/' + report.type +'.html?id='+id+'&stamp='+vm.hasStamp, "Атчот");
        };

        vm.reportListVisible = false;
        vm.hasStamp = false;

        vm.toggleReportList = () => {
            vm.reportListVisible = !vm.reportListVisible;
        };

        vm.print = function(report, e) {

            paneFactory.changeElementState(document.getElementById(vm.btnId), ['focus']);
            e.stopPropagation();
            vm.toggleReportList();

            if(vm.reportId) {
                openReport(report, vm.reportId);
            } else {
                httpService.addItem({data: report.data, url: report.method}).then(
                    resp => {
                        if (report.type === 'salesReceipt')
                            printFactory.deleteReport({id: resp});
                        openReport(report, resp);
                    },
                    resp=> {console.log(resp);}
                );
            }
        };
    };
    printMenuCntrlr.$inject = ['$scope', 'httpService', 'paneFactory', 'printFactory'];

    angular.module('print-menu', [])
        .component( "printMenu", {
            transclude: true,
            bindings: {
                reports: "=reports",
                reportId: "=reportId",
                reportListVisible: '=?reportListVisible'
            },
            template:
            "<div class='dropdown' style='display: inherit;'>" +
            "<button class='glyphicon glyphicon-print print-cmp-btn' " +
            "id={{$ctrl.btnId}} ng-click='$ctrl.toggleReportList()'></button>" +
            "<ul class='stocks-list' ng-show='$ctrl.reportListVisible'>" +
            "<li style='list-style: none;'>" +
            "<table class='table report-list-table'>" +
            "<thead>" +
            "<tr>" +
            "<td style='text-align: center;'>" +
            "<label style='color: coral;'>" +
            "Штамп: {{($ctrl.hasStamp) ? 'Да' : 'Нет'}}" +
            "<input style='right: 5px; zoom: 1.5; position: absolute;" +
            "top: 20px;' type='checkbox'" +
            "ng-model='$ctrl.hasStamp'/>" +
            "</label>" +
            "</td>" +
            "</tr>" +
            "</thead>" +
            "<tbody>" +
            "<tr class='hoverable unselected-stock'" +
            "ng-hide='x === undefined'" +
            "ng-repeat='x in $ctrl.reports'>" +
            "<td class='hoverable' ng-click='$ctrl.print(x, $event)'>" +
            "{{ x.name }}" +
            "</td>" +
            "</tr>" +
            "</tbody>" +
            "</table>" +
            "</li>" +
            "</ul>" +
            "</div>",
            controller: printMenuCntrlr
        })
        .factory('printFactory',['httpService',
            function (httpService) {
                // salesReceipt.html
                let reports =   [
                    {id:1, allowNoId: true, name:'Счет', group: 'common',type: 'invoice'},
                    {id:2, allowNoId: true, name:'Счет-договор', group: 'common', type: 'invoiceWithContract'},
                    {id:3, allowNoId: true, name:'Акт на списание', group: 'common', type: 'writeOffAct'},
                    {id:4, allowNoId: true, name:'Товарный чек', group: 'common', type: 'salesReceipt'},
                    {id:5, allowNoId: true, name:'Ценники', group: 'common', type: 'prices'},
                    {id:6, allowNoId: true, name:'Акт вып.работ', group: 'common', type: 'workCompletionStatement'}
                ];

                return {
                    getReports: () => {
                        return reports;
                    },
                    getRowsForReports: ($s, priceField) => {
                        let rows = [];
                        let stock = angular.isDefined($s.stock) ? $s.stock : $s.filter.stock;
                        $s.rows.forEach((row) => {
                            rows.push({
                                item: row.item,
                                comment: '',
                                quantity: row.quantity,
                                sum: (row[priceField] * row.quantity).toFixed(2),
                                doc: $s.doc,
                                price: row[priceField],
                                vat: stock.organization.vatValue
                            });
                        });
                        return rows;
                    },
                    deleteReport: (params) => {
                      return httpService.getItemById({id: params.id, url: 'deleteInvoice', requestParams: params.requestParams});
                    },
                    allowUploadReport: (params) => {
                      return httpService.getItemById({id: params.id, url: 'allowUploadReport', requestParams: params.requestParams});
                    },
                    setReportsByParams: (params, reps) => {
                        reports.forEach((report)=> {
                           params.forEach((param) => {
                               if(param.type === report.type)
                                reps.push(angular.extend({}, report, {method: param.method, data: param.data}));
                           });
                        });

                    }
                };
            }
        ]);
