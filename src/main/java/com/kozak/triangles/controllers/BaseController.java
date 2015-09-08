package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.ReProposalRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.repositories.VmapRep;

public abstract class BaseController {
    @Autowired
    protected UserRep userRep;
    @Autowired
    protected TransactionRep trRep;
    @Autowired
    protected BuildingDataRep buiDataRep;
    @Autowired
    protected VmapRep vmRep;
    @Autowired
    protected ReProposalRep rePrRep;
    @Autowired
    protected PropertyRep prRep;
    @Autowired
    protected ConstructionProjectRep consProjectRep;
}
