/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.GeoIPv4;
import Models.GeoLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author melvin
 */
@Controller
@RequestMapping(value = "/dashboard")
public class DashboardController {

    @RequestMapping()
    public String home(Model model, HttpServletRequest request) {
        GeoLocation location = GeoIPv4.getLocation("101.52.36.93");

        HashMap<String, Integer> list = new HashMap<>();

        list.put("'testkey'", 1);
        request.setAttribute("list", list);
        return "dashboard";
    }

    private String alertFile() throws IOException {
        File file = new File("/var/log/snort/alert.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            //In case of the alert message containing ','.
            String combinedMessage = "";
            for (int i = 1; i < data.length - 1; i++) {
                combinedMessage += data[i];
            }

            String[] correctData = {
                data[0],
                combinedMessage,
                data[2]
            };
            
            for (int i = 0; i < correctData.length; i++) {
                correctData[i] = correctData[i].trim();
            }

            JSONObject json = new JSONObject();

            json.put("Hour", correctData[0].substring(6, correctData[0].indexOf(':')));
            json.put("Type", correctData[1]);

            //Only lookup a location if the source IP is not a local IP address.
            if (correctData[2].split(".")[0].equals("192")) {
                json.put("Location", "Internal");
            } else {
                GeoLocation location = GeoIPv4.getLocation(correctData[2]);

                json.put("Longitude", location.getLongitude());
                json.put("Latitude", location.getLatitude());
                json.put("CountryName", location.getCountryName());
            }
        }
        return "";
    }
}
