package org.selsup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    static String JSON_STRING = "{\"description\": {\"participantInn\": \"string\"}, \"doc_id\": \"string\", \"doc_status\": \"string\", \"doc_type\": \"LP_INTRODUCE_GOODS\", \"importRequest\": true, \"owner_inn\": \"string\", \"participant_inn\": \"string\", \"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\", \"products\": [{\"certificate_document\": \"string\", \"certificate_document_date\": \"2020-01-23\", \"certificate_document_number\": \"string\", \"owner_inn\": \"string\", \"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"tnved_code\": \"string\", \"uit_code\": \"string\", \"uitu_code\": \"string\"}], \"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";

    public static void main(String[] args) throws InterruptedException {
        // When calling API it returns -401 unauthorised
        CrptAPI crptAPI = new CrptAPI(5, TimeUnit.SECONDS, 10);
        JSONObject json = crptAPI.stringToJson(JSON_STRING);

        // Imitating thread requests
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() ->
                    crptAPI.createDocument(json, "sign"));
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
            t.join();
        }

        System.out.println(crptAPI.getRequests().size());
        System.out.println(crptAPI.getRequests().get(1));
        System.out.println(crptAPI.getRequests().get(10));

    }
}
