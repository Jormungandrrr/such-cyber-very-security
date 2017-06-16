/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author melvin
 */
@Controller
@RequestMapping(value = "/index")
public class DashboardController {

    @RequestMapping()
    public String home(Model model, HttpServletRequest request) {
            return "index";
    }
}
