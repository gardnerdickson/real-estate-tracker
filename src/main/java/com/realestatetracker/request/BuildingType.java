package com.realestatetracker.request;


public enum BuildingType {
    ANY(0),
    HOUSE(1),
    DUPLEX(2),
    RESIDENTIAL_COMMERCIAL_MIX(5),
    MOBILE_HOME(6),
    SPECIAL_PURPOSE(12),
    OTHER(14),
    TOWN_HOUSE(16),
    APARTMENT(17),
    FOURPLEX(19),
    GARDEN_HOME(20),
    MANUFACTURED_HOME_MOBILE(27),
    COMMERCIAL_APARTMENT(28),
    MANUFACTURED_HOME(29);

    private Integer typeId;
    BuildingType(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer typeId() {
        return typeId;
    }
}
