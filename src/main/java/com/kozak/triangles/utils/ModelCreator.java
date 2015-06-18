package com.kozak.triangles.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.ui.Model;

public class ModelCreator {
    public static Model addBalance(Model model, String userBalance) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String balance = formatter.format(Long.valueOf(userBalance));

        return model.addAttribute("balance", balance);
    }
}
