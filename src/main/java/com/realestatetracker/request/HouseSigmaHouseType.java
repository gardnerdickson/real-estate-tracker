package com.realestatetracker.request;

public enum HouseSigmaHouseType {
    DETACHED("D."),
    CONDO_APT("C."),
    SEMI_DETACHED("S."),
    FREE_HOLD_TOWNHOUSE("A."),
    CONDO_TOWN_HOUSE("T."),
    LINK("L.");

    private String typeCode;

    HouseSigmaHouseType(String typeCode) {
        this.typeCode = typeCode;
    }

    public String typeCode() {
        return typeCode;
    }

}
