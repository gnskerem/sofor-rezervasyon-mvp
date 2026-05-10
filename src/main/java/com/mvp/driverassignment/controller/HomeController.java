package com.mvp.driverassignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("//")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("project", "Acente - Şoför Rezervasyon MVP");
        response.put("status", "Sistem Aktif");
        response.put("message", "Hoş geldiniz! Rezervasyon yapmak veya şoförleri listelemek için aşağıdaki uç noktaları kullanın.");

        // Kullanıcıya rehberlik edecek linkler
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("şoförleri_listele", "/drivers");
        endpoints.put("rezervasyon_yap", "/reservations/book (POST isteği atın)");
        endpoints.put("veritabanı_paneli", "/h2-console");

        response.put("available_endpoints", endpoints);
        return response;
    }
}