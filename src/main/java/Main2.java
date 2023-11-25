import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
/**
 * Класс Main2 предназначен для обработки данных о продажах и создания XML-файла с общим количеством продаж по датам.
 * Для каждой даты подсчитывается общее количество проданных товаров, и эти данные записываются в XML-файл "total_sales.xml".
 * В случае возникновения ошибок, информация об ошибке выводится в консоль.
 * @author Серявина Софья
 */
public class Main2 {
    public static void main(String[] args) {
        try {
            // Чтение данных о продажах из файла
            ClassLoader classLoader = Main.class.getClassLoader();
            File salesFile = new File(classLoader.getResource("sales.json").getFile());
            String salesData = new String(new FileInputStream(salesFile).readAllBytes());
            JSONArray salesArray = (JSONArray) new JSONParser().parse(salesData);

            // Создание карты для хранения общего количества продаж по датам
            Map<String, Integer> salesByDate = new HashMap<>();

            // Расчет общего количества продаж по датам
            for (Object saleObj : salesArray) {
                JSONObject sale = (JSONObject) saleObj;
                String date = sale.get("Дата_продажи").toString();
                int quantity = Integer.parseInt(sale.get("Количество_проданных_товаров").toString());

                if (salesByDate.containsKey(date)) {
                    salesByDate.put(date, salesByDate.get(date) + quantity);
                } else {
                    salesByDate.put(date, quantity);
                }
            }

            // Запись данных об общем количестве продаж по датам в XML-файл
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/total_sales.xml"));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<total_sales>\n");
            for (String date : salesByDate.keySet()) {
                writer.write("<date>\n");
                writer.write("\t<date_value>" + date + "</date_value>\n");
                writer.write("\t<total_sales>" + salesByDate.get(date) + "</total_sales>\n");
                writer.write("</date>\n");
            }
            writer.write("</total_sales>");
            writer.close();

            System.out.println("XML файл успешно создан!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

