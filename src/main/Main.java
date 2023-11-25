import java.io.FileReader;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();

            // Чтение файла с товарами
            JSONArray toys = (JSONArray) parser.parse(new FileReader("toys.json"));

            // Чтение файла с продавцами
            JSONArray sellers = (JSONArray) parser.parse(new FileReader("sellers.json"));

            // Для каждого товара определяем продавца с наибольшим количеством этого товара
            for (Object toyObj : toys) {
                JSONObject toy = (JSONObject) toyObj;
                int toyID = Integer.parseInt(toy.get("ID").toString());
                String toyName = toy.get("Наименование").toString();
                int maxQuantity = 0;
                String maxSellerName = "";

                for (Object sellerObj : sellers) {
                    JSONObject seller = (JSONObject) sellerObj;
                    int sellerID = Integer.parseInt(seller.get("ID").toString());
                    String sellerName = seller.get("Имя").toString() + " " + seller.get("Фамилия").toString();
                    int quantity = getToyQuantityForSeller(toyID, sellerID); // Метод для получения количества товара у продавца

                    if (quantity > maxQuantity) {
                        maxQuantity = quantity;
                        maxSellerName = sellerName;
                    }
                }

                // Запись результата в файл
                JSONObject result = new JSONObject();
                result.put("Товар", toyName);
                result.put("Продавец", maxSellerName);
                result.put("Количество", maxQuantity);

                FileWriter file = new FileWriter("result.json", true);
                file.write(result.toJSONString() + "\n");
                file.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для получения количества товара у продавца
    private static int getToyQuantityForSeller(int toyID, int sellerID) {
        // Реализация логики определения количества товара у продавца
        return 0; // Замените на вашу логику
    }
}