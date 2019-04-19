package barcode.dao.services;

import com.querydsl.core.types.Predicate;
import barcode.dao.entities.Bank;
import barcode.dao.entities.Document;
import barcode.dao.entities.QDocument;
import barcode.dao.repositories.DocumentRepository;
import barcode.dao.utils.BasicFilter;
import barcode.dao.utils.DocumentFilter;
import barcode.dto.ResponseItem;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DocumentHandler extends EntityHandlerImpl {

    private DocumentRepository documentRepository;

    public static QDocument qDocument = QDocument.document;

    public DocumentHandler(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    private ResponseItem<Document> update(Document newDocument, Document document) {

        ResponseItem<Document> responseItem = new ResponseItem<Document>();

        newDocument.setName(document.getName());

        newDocument.setDate(document.getDate());

        newDocument.setSupplier(document.getSupplier());

        documentRepository.save(newDocument);

        responseItem.setItem(newDocument);

        return responseItem;
    }

    private List<Document> getDocsByNameAndDateBetween(String name, Date dateFrom, Date dateTo) {

        return documentRepository
                .findByNameContainingIgnoreCaseAndDateBetweenOrderByDateDesc(
                        name,
                        getDateByTime(dateFrom, 0, 0),
                        getDateByTime(dateTo, 23, 59));
    }

    public ResponseItem<Document> addItem(Document document) {

        List<Document> docs = getDocsByNameAndDateBetween(document.getName(), document.getDate(), document.getDate());

        if (docs.size() == 0)
            return update(new Document(), document);

        else
            return new ResponseItem<Document>("Для документа '" + document.getName() +
                    "' имеется совпадение, измените дату или наименование!");
    }

    public ResponseItem updateItem(Document document) {

        List<Document> docs = getDocsByNameAndDateBetween(document.getName(), document.getDate(), document.getDate());

        if((docs.size() == 0) || ((docs.size() == 1) && (docs.get(0).getId().equals(document.getId()))))
            return update(documentRepository.findOne(document.getId()), document);

        return new ResponseItem<Bank>("Для документа '" + document.getName() +
                "' имеется совпадение, измените наименование или дату!");
    }

    public Iterable<Document> getItems(BasicFilter filter) {

        return getDocsByNameAndDateBetween(filter.getSearchString(), filter.getFromDate(), filter.getToDate()) ;
    }


    public Document findOneDocumentByFilter(DocumentFilter filter) {

        Predicate predicate = qDocument.name.eq(filter.getSearchString())
                .and(qDocument.date.between(getDateByTime(filter.getFromDate(),0, 0),
                        getDateByTime(filter.getToDate(), 23, 59)))
                .and(qDocument.supplier.name.equalsIgnoreCase(filter.getSupplier().getName()));

        return documentRepository.findOne(predicate);
    }

    public void saveDocument(Document document) {
        documentRepository.save(document);
    }

}
