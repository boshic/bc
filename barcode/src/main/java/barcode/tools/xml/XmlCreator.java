package barcode.tools.xml;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlCreator {

    public static void create() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Создаем элементы XML
        Element root = document.createElement("root");
        Element child = document.createElement("child");
        Text text = document.createTextNode("Это текстовый узел");

        // Связываем элементы в дерево
        document.appendChild(root);
        root.appendChild(child);
        child.appendChild(text);

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        OutputFormat outputFormat = new OutputFormat(document);
//        XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
//        serializer.serialize(document);
//
//        return new ByteArrayInputStream(outputStream.toByteArray());

        // Сохраняем XML-документ в файл
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(new File("/home/pab/_temp_/example.xml")));

//        https://stackoverflow.com/questions/865039/how-to-create-an-inputstream-from-a-document-or-node

        System.out.println("XML-файл успешно создан");
    }
}
