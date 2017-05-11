/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        
            HashMap<String,Integer> list = new HashMap<>();
            list.put("'testkey'",1);
            request.setAttribute("list", list);
            return "dashboard";
    }
}
