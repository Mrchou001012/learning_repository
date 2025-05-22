package com.mybackend.trade_backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class DataController {

    @GetMapping("/public")
    public String getPublicData() {
        return "This is public data";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String getUserData() {
        return "This is user data";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminData() {
        return "This is admin data";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String getManagerData() {
        return "This is manager data";
    }

    @GetMapping("/advanced")
    @PreAuthorize("hasAuthority('READ_ADVANCED_DATA')")
    public String getAdvancedData() {
        return "This is advanced data";
    }
}