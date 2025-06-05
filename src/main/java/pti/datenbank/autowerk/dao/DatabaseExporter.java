package pti.datenbank.autowerk.dao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.*;

public class DatabaseExporter {

    public static void exportToXml(String filePath) throws Exception {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (Connection connection = DBConnection.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("database");
            doc.appendChild(root);

            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                if (tableName.toLowerCase().startsWith("sys") ||
                        tableName.toLowerCase().startsWith("trace_") ||
                        tableName.contains("$")) {
                    continue;
                }

                Element tableElement = doc.createElement("table");
                tableElement.setAttribute("name", tableName);
                root.appendChild(tableElement);

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

                    ResultSetMetaData rsMeta = rs.getMetaData();
                    int columnCount = rsMeta.getColumnCount();

                    while (rs.next()) {
                        Element row = doc.createElement("row");
                        tableElement.appendChild(row);

                        for (int i = 1; i <= columnCount; i++) {
                            String colName = rsMeta.getColumnName(i);
                            Object value = rs.getObject(i);

                            Element col = doc.createElement(colName);
                            col.appendChild(doc.createTextNode(value == null ? "" : value.toString()));
                            row.appendChild(col);
                        }
                    }

                } catch (SQLException ex) {
                    System.err.println("⚠️ Skipping table: " + tableName + " — " + ex.getMessage());
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        }
    }
}