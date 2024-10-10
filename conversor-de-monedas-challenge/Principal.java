import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Principal {
    private static final String API_KEY = "3b176cc908652903a3290258";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Map<String, String> monedas = obtenerMonedasDisponibles();
            System.out.println("Monedas disponibles: " + monedas.keySet());

            System.out.println("Ingresa la moneda base (por ejemplo, USD): ");
            String baseCurrency = sc.nextLine().toUpperCase();

            System.out.println("Ingresa la moneda a convertir (por ejemplo, MXN): ");
            String targetCurrency = sc.nextLine().toUpperCase();

            double rate = obtenerTasaDeCambio(baseCurrency, targetCurrency);
            System.out.println("Ingresa la cantidad en " + baseCurrency + ": ");
            double cantidad = sc.nextDouble();

            double resultado = cantidad * rate;
            System.out.printf("%.2f %s equivalen a %.2f %s\n", cantidad, baseCurrency, resultado, targetCurrency);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }

    private static Map<String, String> obtenerMonedasDisponibles() throws Exception {
        String urlStr = BASE_URL + API_KEY + "/codes";
        URL url = new URL(urlStr);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        if (!jsonobj.get("result").getAsString().equals("success")) {
            throw new Exception("Error en la respuesta de la API");
        }

        JsonArray currencies = jsonobj.getAsJsonArray("supported_codes");
        Map<String, String> monedas = new HashMap<>();
        for (JsonElement element : currencies) {
            JsonArray currencyArray = element.getAsJsonArray();
            String code = currencyArray.get(0).getAsString();
            String name = currencyArray.get(1).getAsString();
            monedas.put(code, name);
        }
        return monedas;
    }

    private static double obtenerTasaDeCambio(String baseCurrency, String targetCurrency) throws Exception {
        String urlStr = BASE_URL + API_KEY + "/latest/" + baseCurrency;
        URL url = new URL(urlStr);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        if (!jsonobj.get("result").getAsString().equals("success")) {
            throw new Exception("Error en la respuesta de la API");
        }

        JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");
        return rates.get(targetCurrency).getAsDouble();
    }
}
