package com.kozak.triangles.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.ui.Model;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.interfaces.Consts;

public class Util {
    public static Model addBalanceToModel(Model model, String userBalance) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String balance = formatter.format(Long.valueOf(userBalance));

        return model.addAttribute("balance", balance);
    }
    
    public static int getAreaPercent(CityAreasT cityArea) {
        switch (cityArea.ordinal()) {
        case 0:
            return Consts.GHETTO_P;
        case 1:
            return Consts.OUTSKIRTS_P;
        case 2:
            return Consts.CHINA_P;
        case 3:
            return Consts.CENTER_P;
        }
        return 0;
    }
}
