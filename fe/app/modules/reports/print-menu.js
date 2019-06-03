// define(['angular'], angular => {

    let printMenuCntrlr = function ($scope, httpService)  {

        let vm = this;

        let openReport = (group, type, id) => {
            window.open('reports/' + group + '/' + type +'.html?id='+id+'&stamp='+vm.hasStamp, "Атчот");
        };

        vm.reportListVisible = false;
        vm.hasStamp = false;

        vm.toggleReportList = () => {
            vm.reportListVisible = !vm.reportListVisible;
        };

        vm.print = function(report, e) {

            e.stopPropagation();
            vm.toggleReportList();

            if(vm.reportId) {
                openReport(report.group, report.type, vm.reportId);
            } else {
                httpService.addItem(report.data, report.method).then(
                    resp => {
                        openReport(report.group, report.type, resp);
                    },
                    resp=> {console.log(resp);}
                );
            }
        };
    };
    printMenuCntrlr.$inject = ['$scope', 'httpService'];

    angular.module('print-menu', [])
        .component( "printMenu",
            {
            transclude: true,
            bindings: {
                reports: "=reports",
                reportId: "=reportId",
                reportListVisible: '=?reportListVisible'
            },
            template:
            "<div class='dropdown' style='display: inherit;'>" +
            "<button class='glyphicon glyphicon-print print-cmp-btn' " +
            "ng-click='$ctrl.toggleReportList()'></button>" +
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
        .factory('printFactory',[
            function () {
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
                        $s.rows.forEach((row) => {
                            rows.push({
                                item: row.item,
                                comment: '',
                                quantity: row.quantity,
                                sum: (row[priceField] * row.quantity).toFixed(2),
                                doc: $s.doc,
                                price: row[priceField],
                                vat: $s.stock.organization.vatValue
                            });
                        });
                        return rows;
                    },
                    setReportsByParams: (params, reps) => {
                        // let arr = reports;
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
