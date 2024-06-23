package org.selsup;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CrptAPI {

    private static final String CREATE_DOCUMENT_API = "https://ismp.crpt.ru/api/v3/lk/documents/create";
//    private static String JSON_STRING = "{\"description\": {\"participantInn\": \"string\"}, \"doc_id\": \"string\", \"doc_status\": \"string\", \"doc_type\": \"LP_INTRODUCE_GOODS\", \"importRequest\": true, \"owner_inn\": \"string\", \"participant_inn\": \"string\", \"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\", \"products\": [{\"certificate_document\": \"string\", \"certificate_document_date\": \"2020-01-23\", \"certificate_document_number\": \"string\", \"owner_inn\": \"string\", \"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"tnved_code\": \"string\", \"uit_code\": \"string\", \"uitu_code\": \"string\"}], \"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";

    // Request time limit value (1, 2, 3 etc.)
    private long requestTimeLimit;
    // Request time limit unit value (second, minute etc.)
    private TimeUnit timeUnit;
    private int requestLimit;
    private long timeLimitStart;
    private long timeLimitInMs;
    private HashMap<Integer, Long> requests;
    private int counter;

    public CrptAPI(long requestTimeLimit, TimeUnit timeUnit, int requestLimit) {
        this.requestTimeLimit = requestTimeLimit;
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        timeLimitStart = System.currentTimeMillis();
        timeLimitInMs = TimeUnit.MILLISECONDS.convert(requestTimeLimit, timeUnit);
        requests = new HashMap<>();
        counter = 1;
    }

    public long getRequestTimeLimit() {
        return requestTimeLimit;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public int getRequestLimit() {
        return requestLimit;
    }

    public HashMap<Integer, Long> getRequests() {
        return requests;
    }

    public synchronized void createDocument(JSONObject jsonObject, String signature) {

        if (counter <= requestLimit
                && System.currentTimeMillis() - timeLimitStart < timeLimitInMs) {

            requests.put(counter, System.currentTimeMillis());
            counter++;

            try {
                String requestBody = jsonObject.toString();

                HttpRequest request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .uri(URI.create(CREATE_DOCUMENT_API))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                // Response from API
//                System.out.println(response.statusCode());
//                System.out.println(response.body());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid URL: " + CREATE_DOCUMENT_API);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Invalid Request: Requests limit reached or request time exceeded");
        }
    }

    public JSONObject stringToJson(String jsonString) {
        return new JSONObject(jsonString);
    }
}
