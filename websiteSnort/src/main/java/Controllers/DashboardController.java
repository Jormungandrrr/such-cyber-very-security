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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author melvin
 */
@Controller
@RequestMapping(value = "/index")
public class DashboardController {

    @RequestMapping()
    public ModelAndView home(Model model, HttpServletRequest request) {
        try {
            return alertFile();
        } catch (Exception e) {
            return new ModelAndView("index", "ERROR", true);
        }
    }

    private ModelAndView alertFile() {
        BufferedReader reader = null;
        List<String> JObjects = new ArrayList<>();

        try {
            File file = new File("/var/log/snort/alert.csv");
            reader = new BufferedReader(new FileReader(file));

            final String IPADDRESS_PATTERN =
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                //In case of the alert message containing ','.
                String combinedMessage = "";
                for (int i = 1; i < data.length - 1; i++)
                    combinedMessage += data[i];

                String[] correctData = {
                        data[0],
                        combinedMessage,
                        data[2]
                };

                for (int i = 0; i < correctData.length; i++)
                    correctData[i] = correctData[i].trim();

                JSONObject json = new JSONObject();

                json.put("Hour", correctData[0].substring(6, correctData[0].indexOf(':')));
                json.put("Type", correctData[1]);

                //Check if data contains an IP.
                if (pattern.matcher(correctData[2]).find()) {
                    //Only lookup a location if the source IP is not a local IP address.
                    if (correctData[2].substring(0, correctData[2].indexOf(".")).equals("192"))
                        json.put("Location", "Internal");
                    else {
                        GeoLocation location = GeoIPv4.getLocation(correctData[2]);

                        json.put("Longitude", location.getLongitude());
                        json.put("Latitude", location.getLatitude());
                        json.put("CountryCode", location.getCountryCode());
                        json.put("CountryName", location.getCountryName());
                        json.put("ContainsIP", "true");
                    }
                } else {
                    json.put("ContainsIP", "false");
                }

                JObjects.add(json.toString());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new ModelAndView("index", "JObjects", JObjects);
        }
    }
}
