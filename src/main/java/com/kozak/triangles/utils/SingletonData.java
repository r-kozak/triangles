package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.repositories.BuildingDataRep;

public class SingletonData {
    private static HashMap<String, CommBuildData> commBDMap;

    /**
     * 
     * @param rep
     *            репозиторий где есть метод на получение списка с данными о всех коммерческих строениях
     * 
     * @return карту с ключами -
     */
    public static HashMap<String, CommBuildData> getCommBuildData(BuildingDataRep rep) {
	if (commBDMap == null) {
	    commBDMap = new HashMap<String, CommBuildData>();

	    ArrayList<CommBuildData> data = (ArrayList<CommBuildData>) rep.getCommBuildDataList();
	    for (CommBuildData d : data) {
		commBDMap.put(d.getCommBuildType().name(), d);
	    }
	}
	return commBDMap;
    }
}
