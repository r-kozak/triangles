package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.repositories.BuildingDataRep;

public class SingletonData {
    private static ArrayList<CommBuildData> commBDArray;
    private static HashMap<String, CommBuildData> commBDMap;

    /**
     * возвращает данные о всех коммерческих строениях в виде массива
     * 
     * @param rep
     * @return - массив с данными о строениях
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<CommBuildData> getCommBuildDataArray(BuildingDataRep rep) {
        if (commBDArray == null || commBDArray.isEmpty()) {
            commBDArray = (ArrayList<CommBuildData>) rep.getCommBuildDataList();
        }
        return commBDArray;
    }

    /**
     * @param rep
     *            репозиторий где есть метод на получение списка с данными о всех коммерческих строениях
     * 
     * @return карту с ключами -
     */
    public static HashMap<String, CommBuildData> getCommBuildData(BuildingDataRep rep) {
        if (commBDMap == null || commBDMap.isEmpty()) {
            commBDMap = new HashMap<String, CommBuildData>();

            commBDArray = getCommBuildDataArray(rep);
            for (CommBuildData d : commBDArray) {
                commBDMap.put(d.getCommBuildType().name(), d);
            }
        }
        return commBDMap;
    }
}
