package com.nminh.quanlythuvien.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
    @GetMapping("/storekeeper")
    public String storekeepper(){
        return "storekeeper";
    }
    @GetMapping("/user")
    public String user(){
        return "user";
    }
    @GetMapping("/shipper")
    public String shipper(){
        return "shipper";
    }
    @GetMapping("/admin_orders")
    public String adminorders(){
        return "admin_orders";
    }
    @GetMapping("/cart")
    public String cart(){
        return "cart";
    }
    @GetMapping("/admin_shipper")
    public String adminshipper(){
        return "admin_shipper";
    }
    @GetMapping("/orders")
    public String orders(){
        return "user_orders";
    }
    @GetMapping("/admin_statistic")
    public String adminstatistic(){
        return "admin_statistic";
    }
    @GetMapping("/admin_account")
    public String adminaccount(){
        return "admin_account";
    }


}
