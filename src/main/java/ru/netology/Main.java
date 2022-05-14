package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args)  {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        ColumnPositionMappingStrategy<Employee> empStrategy = new ColumnPositionMappingStrategy<>();
        empStrategy.setType(Employee. class);
        empStrategy.setColumnMapping(columnMapping);

        List<Employee> list = parseCSV(empStrategy, fileName);
//        System.out.println(list);
        String json = listToJson(list);
        try (FileWriter file = new FileWriter("data.json")){
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

            List<Employee> list2 = parseXML("data.xml");
//        System.out.println(list2);
        String json2 = listToJson(list2);

        try (FileWriter file = new FileWriter("data2.json")){
            file.write(json2);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String file) {
        List<Employee> empList = null;
        try {
            List<String> xmlElement = new ArrayList<>();
            empList = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));
            Node root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeName().equals("employee")) {
                    NodeList nodeList1 = node.getChildNodes();
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        Node node_ = nodeList1.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            xmlElement.add(node_.getTextContent());
                        }
                    }
                    empList.add(new Employee(
                            Long.parseLong(xmlElement.get(0)),
                            xmlElement.get(1),
                            xmlElement.get(2),
                            xmlElement.get(3),
                            Integer.parseInt(xmlElement.get(4))));
                    xmlElement.clear();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return empList;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list,listType);

        return json;
    }

    private static List<Employee> parseCSV(ColumnPositionMappingStrategy colMap, String fileName) {
        List<Employee> empList = null;
        try(CSVReader reader = new CSVReader(new FileReader(fileName))) {

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(colMap)
                    .build();
            empList = csv.parse();

        } catch(IOException e) {
            e.printStackTrace();
        }
        return empList;
    }
}
