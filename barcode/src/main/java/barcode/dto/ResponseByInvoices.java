package barcode.dto;

import barcode.dao.entities.Invoice;
import barcode.dao.entities.QInvoice;
import barcode.dao.predicates.InvoicesPredicatesBuilder;
import barcode.dao.services.AbstractEntityManager;
import barcode.dao.services.BuyerHandler;
import barcode.utils.CommonUtils;
import barcode.utils.SoldItemFilter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by xlinux on 20.11.18.
 */
public class ResponseByInvoices
    extends ResponseItemExt<Invoice>
    implements ResponseWithTotals<SoldItemFilter> {

//    public ResponseByInvoices() {}
    public ResponseByInvoices(String text, List<Invoice> items, Boolean success, Integer number) {
        super(text, items, success, number);
    }

    @Override
    public void calcTotals(AbstractEntityManager abstractEntityManager, SoldItemFilter filter) {

        final EntityManager em = abstractEntityManager.getEntityManager();
        QInvoice qInvoice = QInvoice.invoice;
        JPAQuery<BigDecimal> queryBdcml = new JPAQuery<BigDecimal>(em);
        InvoicesPredicatesBuilder pb = new InvoicesPredicatesBuilder();
        BooleanBuilder predicate = pb.buildByFilter(filter, em);
        queryBdcml = queryBdcml.from(qInvoice).where(predicate);

        BigDecimal
            invoicesNumber = new BigDecimal(queryBdcml.select(qInvoice).fetchCount()),
            sum = CommonUtils.validateBigDecimal(queryBdcml.select(qInvoice.sum.sum()).fetchOne());

        super.getTotals().add(new ResultRowByItemsCollection<BigDecimal, BigDecimal>
            (QUANTITY, invoicesNumber, SUMM , sum));

         super.setBuyers(
             BuyerHandler.getDtoBuyers(
                 new JPAQuery<Tuple>(em)
                .from(qInvoice).where(predicate)
                .select(qInvoice.buyer.id, qInvoice.buyer.name, qInvoice.buyer.debt)
                .distinct().orderBy(qInvoice.buyer.name.asc()).fetch()
             )
        );
    }

}
