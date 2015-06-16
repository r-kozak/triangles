package com.kozak.triangles.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.ui.Model;

public class ModelCreator {
    public static Model addBalance(Model model, String userBalance) {
        // add balance to model
        String balance = formatBalance(userBalance);
        return model.addAttribute("balance", balance);
    }

    /**
     * Используется для добавления баланса в модель (ModelCreator.addBalance(...))
     * а также на страницах jsp для форматирования сумм
     */
    public static String formatBalance(String sum) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String balance = formatter.format(Long.valueOf(sum));

        return balance;
    }
}
