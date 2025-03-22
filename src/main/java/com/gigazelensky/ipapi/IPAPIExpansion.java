package com.gigazelensky.ipapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This expansion provides placeholders for IP data from ip-api.com
 */
public class IPAPIExpansion extends PlaceholderExpansion {

    private final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheExpiry = new ConcurrentHashMap<>();
    private static final long CACHE_TIME = 3600000; // 1 hour in milliseconds
    private final JSONParser parser = new JSONParser();

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "GigaZelensky";
    }

    @Override
    public String getIdentifier() {
        return "ipapi";
    }

    @Override
    public String getPlugin() {
        return null; // No dependencies
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        // Get the player's IP address
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        
        // Check if we need to fetch data for this IP
        if (!cache.containsKey(ipAddress) || System.currentTimeMillis() > cacheExpiry.getOrDefault(ipAddress, 0L)) {
            fetchIPData(ipAddress);
        }
        
        // Get the cached data
        Map<String, String> ipData = cache.getOrDefault(ipAddress, new HashMap<>());
        
        // Return the requested placeholder value
        return ipData.getOrDefault(identifier, "");
    }
    
    /**
     * Fetches IP data from ip-api.com and stores it in the cache
     * 
     * @param ip The IP address to fetch data for
     */
    private void fetchIPData(String ip) {
        try {
            // Request all standard fields
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,mobile,proxy,hosting,query");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "PlaceholderAPI-IPAPIExpansion/1.0.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse the JSON response
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
                Map<String, String> data = new HashMap<>();
                
                // Process all fields from the JSON response
                for (Object key : jsonResponse.keySet()) {
                    String keyStr = key.toString();
                    Object value = jsonResponse.get(keyStr);
                    data.put(keyStr, value != null ? value.toString() : "");
                }
                
                // Try to fetch DNS information separately (requires Pro subscription)
                fetchDNSData(ip, data);
                
                // Store in cache
                cache.put(ip, data);
                cacheExpiry.put(ip, System.currentTimeMillis() + CACHE_TIME);
                
                Bukkit.getLogger().log(Level.INFO, "Successfully fetched and cached IP data for " + ip);
            } else if (responseCode == 429) {
                // Rate limited
                Bukkit.getLogger().log(Level.WARNING, "IP-API rate limit exceeded. Try again later.");
                // Set a shorter cache time so we don't spam the API
                cacheExpiry.put(ip, System.currentTimeMillis() + 60000); // 1 minute
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Failed to fetch IP data: HTTP error code " + responseCode);
            }
        } catch (IOException | ParseException e) {
            Bukkit.getLogger().log(Level.WARNING, "Error fetching IP data", e);
        }
    }
    
    /**
     * Fetches DNS information for an IP and adds it to the data map
     * 
     * @param ip The IP address
     * @param data The map to store the DNS data in
     */
    private void fetchDNSData(String ip, Map<String, String> data) {
        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=dns");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "PlaceholderAPI-IPAPIExpansion/1.0.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
                Object dnsObj = jsonResponse.get("dns");
                
                if (dnsObj instanceof JSONObject) {
                    JSONObject dnsData = (JSONObject) dnsObj;
                    for (Object key : dnsData.keySet()) {
                        String keyStr = key.toString();
                        Object value = dnsData.get(keyStr);
                        data.put("dns_" + keyStr, value != null ? value.toString() : "");
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Error fetching DNS data", e);
        }
    }
}