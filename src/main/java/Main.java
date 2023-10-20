import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // создаем файл data.csv
        File data = new File("/Users/imshcheglov/Desktop/JD-51 HW/5. Java Core/5. Работа с файлами CSV, XML, JSON/HW_CSV_XML_JSON/data.csv");
        try {
            boolean created = data.createNewFile();
            if (created)
                System.out.println("Файл data.csv создан");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // создаем массив строчек columnMapping, содержащий информацию о предназначении колонок в CVS файле:
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // определяем имя для считываемого CSV файла:
        String fileName = "data.csv";
        // получаем список сотрудников, вызвав метод parseCSV():
        List<Employee> list = parseCSV(columnMapping, fileName);
        // список преобразуем в строчку в формате JSON
        String json = listToJson(list);
        // записываем полученный JSON в файл
        writeString(json, "/Users/imshcheglov/Desktop/JD-51 HW/5. Java Core/5. Работа с файлами CSV, XML, JSON/HW_CSV_XML_JSON/data.json");

        // создаем файл data.xml
        File dataXml = new File("/Users/imshcheglov/Desktop/JD-51 HW/5. Java Core/5. Работа с файлами CSV, XML, JSON/HW_CSV_XML_JSON/data.xml");
        try {
            boolean created = dataXml.createNewFile();
            if (created)
                System.out.println("Файл data.xml создан");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // определяем имя для считываемого XML файла:
        String fileNameXml = "data.xml";
        // получаем список сотрудников, вызвав метод parseXML():
        List<Employee> employee = parseXML(fileNameXml);
        // список преобразуем в строчку в формате JSON
        String jsonSecond = listToJson(employee);
        // записываем полученный JSON в файл
        writeString(jsonSecond, "/Users/imshcheglov/Desktop/JD-51 HW/5. Java Core/5. Работа с файлами CSV, XML, JSON/HW_CSV_XML_JSON/data2.json");

        // cоздаем файл new_data.json
        File dataJson = new File("/Users/imshcheglov/Desktop/JD-51 HW/5. Java Core/5. Работа с файлами CSV, XML, JSON/HW_CSV_XML_JSON/new_data.json");
        try {
            boolean created = dataJson.createNewFile();
            if (created)
                System.out.println("Файл new_data.json создан");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // получаем JSON из файла
        String jsonThird = readString("new_data.json");
        // преобразование в список сотрудников
        List<Employee> listEmloyee = jsonToList(jsonThird);
        // вывод в консоль
        listEmloyee.forEach(System.out::println);
    }

    private static List<Employee> parseCSV(String[] columnMaping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            var strategy = new ColumnPositionMappingStrategy<Employee>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMaping);
            var csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            var employees = csv.parse();

            return employees;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<Employee> parseXML(String s) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(s);

            Node staff = document.getFirstChild();

            NodeList staffChilds = staff.getChildNodes();

            List<Employee> employeeList = new ArrayList<>();
            long id = 0;
            String firstName = "";
            String lastName = "";
            String country = "";
            int age = 0;
            for (int i = 0; i < staffChilds.getLength(); i++) {
                if (staffChilds.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Node node = staffChilds.item(i);

                Element employeer = (Element) node;

                id = Long.parseLong(employeer.getElementsByTagName("id").item(0).getTextContent());
                firstName = employeer.getElementsByTagName("firstName").item(0).getTextContent();
                lastName = employeer.getElementsByTagName("lastName").item(0).getTextContent();
                country = employeer.getElementsByTagName("country").item(0).getTextContent();
                age = Integer.parseInt(employeer.getElementsByTagName("age").item(0).getTextContent());
                Employee employee = new Employee(id, firstName, lastName, country, age);
                employeeList.add(employee);
            }
            return employeeList;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readString(String jsonFile) {
        StringBuffer data = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            String s;
            while ((s = br.readLine()) != null) {
                data.append(s).append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return data.toString();
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> listEmployees = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray employee = (JSONArray) obj;
            for (Object i : employee) {
                listEmployees.add(gson.fromJson(i.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listEmployees;
    }
}
