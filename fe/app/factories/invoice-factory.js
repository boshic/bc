/**
 * Created by xlinux on 23.11.18.
 */
// define(['angular'], angular => {
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
                                row.woVatPrice = +(row.price - row.price * row.vat/(row.vat + 100)).toFixed(2);
                                row.woVatSum = +(row.woVatPrice * row.quantity).toFixed(2);
                                row.vatSum = +(row.woVatSum * row.vat/100).toFixed(2);
                                row.sum = +(row.woVatSum + row.vatSum).toFixed(2);
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
