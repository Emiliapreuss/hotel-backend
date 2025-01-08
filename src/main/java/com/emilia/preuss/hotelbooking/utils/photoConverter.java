package com.emilia.preuss.hotelbooking.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class photoConverter {

    public String convertToSting(byte[] photoBytes) {
        return Base64.getEncoder().encodeToString(photoBytes);
    }
}
