import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Класс Main предназначен для обработки данных о продажах и создания XML-файла с информацией о продавцах,
 * имеющих наибольшее количество одного из товаров, включая наименование товара, фамилию продавца и количество товара.
 * Для этого класс читает данные о товарах, продавцах и наличии товаров из соответствующих JSON-файлов,
 * затем находит продавцов с максимальным количеством одного из товаров и записывает информацию в XML-файл "max_availability.xml".
 * В случае возникновения ошибок, информация об ошибке выводится в консоль.
 *  @author Серявина Софья
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Чтение файлов с данными
            ClassLoader classLoader = Main.class.getClassLoader();
            File toysFile = new File(classLoader.getResource("toys.json").getFile());
            File sellersFile = new File(classLoader.getResource("sellers.json").getFile());
            File availabilityFile = new File(classLoader.getResource("availability.json").getFile());

            // Преобразование содержимого файлов в строки
            String toysData = new String(new FileInputStream(toysFile).readAllBytes());
            String sellersData = new String(new FileInputStream(sellersFile).readAllBytes());
            String availabilityData = new String(new FileInputStream(availabilityFile).readAllBytes());

            // Преобразование строк в JSON объекты
            JSONArray toysArray = (JSONArray) new JSONParser().parse(toysData);
            JSONArray sellersArray = (JSONArray) new JSONParser().parse(sellersData);
            JSONArray availabilityArray = (JSONArray) new JSONParser().parse(availabilityData);

            // Создание карты для хранения информации о продавцах с максимальным количеством товара
            Map<Integer, JSONObject> maxAvailabilityMap = new HashMap<>();

            // Поиск продавца с максимальным количеством каждого товара
            for (Object availabilityObj : availabilityArray) {
                JSONObject availability = (JSONObject) availabilityObj;
                int sellerId = Integer.parseInt(availability.get("ID_продавца").toString());
                int quantity = Integer.parseInt(availability.get("Количество").toString());
                int toyId = Integer.parseInt(availability.get("ID_товара").toString());

                if (maxAvailabilityMap.containsKey(toyId)) {
                    JSONObject existingSeller = maxAvailabilityMap.get(toyId);
                    int existingQuantity = Integer.parseInt(existingSeller.get("Количество").toString());

                    if (quantity > existingQuantity) {
                        existingSeller.put("Количество", quantity);
                        existingSeller.put("ID_продавца", sellerId);
                    }
                } else {
                    maxAvailabilityMap.put(toyId, availability);
                }
            }

            // Создание XML файла и запись данных о продавцах с максимальным количеством товара
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/max_availability.xml"));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<max_availability>\n");
            for (Object toyObj : toysArray) {
                JSONObject toy = (JSONObject) toyObj;
                int toyId = Integer.parseInt(toy.get("ID").toString());

                if (maxAvailabilityMap.containsKey(toyId)) {
                    JSONObject seller = maxAvailabilityMap.get(toyId);
                    int sellerId = Integer.parseInt(seller.get("ID_продавца").toString());

                    // Поиск фамилии продавца по ID
                    String lastName = "";
                    for (Object sellerObj : sellersArray) {
                        JSONObject sellerData = (JSONObject) sellerObj;
                        int currentSellerId = Integer.parseInt(sellerData.get("ID").toString());

                        if (currentSellerId == sellerId) {
                            lastName = sellerData.get("Фамилия").toString();
                            break;
                        }
                    }

                    writer.write("<product>\n");
                    writer.write("\t<name>" + toy.get("Наименование") + "</name>\n");
                    writer.write("\t<seller>\n");
                    writer.write("\t\t<last_name>" + lastName + "</last_name>\n");
                    writer.write("\t\t<quantity>" + seller.get("Количество") + "</quantity>\n");
                    writer.write("\t</seller>\n");
                    writer.write("</product>\n");
                }
            }
            writer.write("</max_availability>");
            writer.close();

            System.out.println("XML файл успешно создан!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
