package me.nerdoron.himyb.modules.bot;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccessAmariAPI {
    private static final Logger logger = LoggingHandler.logger(AccessAmariAPI.class);
    private final String token;

    public AccessAmariAPI() {
        Dotenv dotenv = Dotenv.load();
        this.token = dotenv.get("AMARI");
    }

    private JSONObject fetchUserData(String id) {
        String urlString = String.format("https://amaribot.com/api/v1/guild/850396197646106624/member/%s", id);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    return new JSONObject(response.toString());
                }
            } else {
                logger.error("Failed to fetch user data for {}. HTTP code: {}.", id, responseCode);
            }
        } catch (IOException e) {
            logger.error("Exception occurred while fetching user data for {}: {}", id, e.getMessage());
        }

        return null;
    }

    public String getLeaderboardPosition(String userId) {
        String urlString = "https://amaribot.com/api/v1/guild/raw/leaderboard/850396197646106624";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray leaderboard = jsonResponse.getJSONArray("data");

                for (int i = 0; i < leaderboard.length(); i++) {
                    JSONObject user = leaderboard.getJSONObject(i);
                    if (user.getString("id").equals(userId)) {
                        return String.valueOf(i + 1);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Error";
    }

    public String getAmariLevel(String id) {
        JSONObject userData = fetchUserData(id);
        if (userData != null) {
            return String.valueOf(userData.optInt("level", -1));
        }
        return "Error";
    }

    public String getAmariExp(String id) {
        JSONObject userData = fetchUserData(id);
        if (userData != null) {
            return userData.optString("exp", "Error");
        }
        return "Error";
    }
}
