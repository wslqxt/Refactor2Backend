package com.sys.ecomerce.controller;

import com.sys.ecomerce.config.RsaKeyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rsa")
public class RsaController {

    private final RsaKeyProperties rsaKeyProperties;

    public RsaController(RsaKeyProperties rsaKeyProperties) {
        this.rsaKeyProperties = rsaKeyProperties;
    }

    @GetMapping("/public-key")
    public Map<String,String> getPublicKey(){
        Map<String,String> result = new HashMap<>();
        result.put("public-key", rsaKeyProperties.getPublicKey());
        return result;
    }
}