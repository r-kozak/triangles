package com.kozak.triangles.win_service.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kozak.triangles.model.WinDataModel;

@Service("BonusWinHandler")
public class BonusWinHandler implements WinHandler {

    @Override
    public void handle(List<WinDataModel> winModels, long userId) {
        // TODO Auto-generated method stub

    }

}
