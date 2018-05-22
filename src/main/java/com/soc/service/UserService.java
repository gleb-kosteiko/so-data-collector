package com.soc.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * UserService class.
 * <p>
 * Date: May 21, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class UserService {
    private static final String KEY_PARAM = "&key=";
    private static final String SO_USERS_URL = "https://api.stackexchange.com/2.2/users?page=%d&pagesize=100&order=desc&sort=reputation&site=stackoverflow";
    private static final String FILE_NAME = "so-users.txt";
    private static final String ITEMS_FIELD = "items";
    private static final String ITEMS_SEPARATOR = ",";
    private static final String HAS_MORE_FIELD = "has_more";
    private static CloseableHttpClient client = HttpClients.createDefault();
    private static BufferedWriter fileWriter;
    private String apiKey;
    private static Gson gson = new GsonBuilder()
            .create();

    public UserService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void collectUsers() {
        configureOutput();
        String url = StringUtils.isNotBlank(apiKey) ? SO_USERS_URL + KEY_PARAM + apiKey : SO_USERS_URL;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            HttpGet httpGet = new HttpGet(String.format(url, i));
            try {
                HttpResponse resp = client.execute(httpGet);
                JsonObject responseJson = gson.fromJson(EntityUtils.toString(resp.getEntity()), JsonObject.class);
                if (resp.getStatusLine().getStatusCode() == 200) {
                    if (responseJson.has(ITEMS_FIELD)) {
                        JsonArray items = responseJson.get(ITEMS_FIELD).getAsJsonArray();
                        for (int j = 0; j < items.size(); j++) {
                            String user = items.get(j).toString();
                            fileWriter.write(user + ITEMS_SEPARATOR);
                            fileWriter.newLine();
                            System.out.println(i * (j + 1) + " | " + user);
                        }
                        if (!responseJson.get(HAS_MORE_FIELD).getAsBoolean()) {
                            break;
                        }
                    } else {
                        System.out.println("Status code 200 but no items");
                        break;
                    }
                } else {
                    System.out.println("Status code is not 200");
                    //TODO add processing of timeouts between calls
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureOutput() {
        File fout = new File(FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileWriter = new BufferedWriter(new OutputStreamWriter(fos));
    }
}
