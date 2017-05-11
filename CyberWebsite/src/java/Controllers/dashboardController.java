/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Melvin
 */
@Controller
@RequestMapping(value = "/dashboard")
public class dashboardController {
   
    
    @RequestMapping("/dashboard")
    public String dashboard(Model model , HttpServletRequest request) {
        
              
        return "dashboard";
    }
}
