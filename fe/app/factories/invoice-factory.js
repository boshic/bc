// /**
//  * Created by xlinux on 23.11.18.
//  */
angular.module('invoice-factory', [])
    .factory('invoiceFactory',[
        function () {
            return {
                getTotals: (data) => {
                    let totals = {
                        quantity: 0,
                        woVatSum: 0,
                        vatSum: 0,
                        sum: 0,
                        lines: data.rows.length
                    };

                    for(let row of data.rows) {
                        if(row.vat === null)
                            row.vat = data.stock.organization.vatValue;
                        if(data.stock.retail) {
                            row.woVatPrice = row.price;
                            row.sum = +(row.quantity * row.price).toFixed(2);
                            row.woVatSum = row.sum;
                            row.vatSum = +(row.sum * row.vat/100).toFixed(2);
                        } else {
                            row.sum = +(row.price * row.quantity).toFixed(2);
                            row.woVatSum = +(row.sum - row.sum * row.vat/(row.vat + 100)).toFixed(2);
                            row.woVatPrice = +(row.woVatSum/row.quantity).toFixed(2);
                            row.vatSum = +(row.sum - row.woVatSum).toFixed(2);
                        }
                        totals.quantity += row.quantity;
                        totals.woVatSum += +row.woVatSum;
                        totals.vatSum += +row.vatSum;
                        totals.sum += +row.sum;
                    }
                    return totals;
                }
            };
        }
    ]);
// });
