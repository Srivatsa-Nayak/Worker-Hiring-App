package com.workerhiring.job;

/** The kinds of work small shops and households typically hire for. */
public enum JobCategory {
    SHOP_ASSISTANT("Shop assistant"),
    CASHIER("Cashier"),
    DELIVERY("Delivery"),
    KITCHEN("Cook / kitchen helper"),
    CLEANING("Cleaning"),
    DRIVER("Driver"),
    TAILORING("Tailoring"),
    ELECTRICIAN("Electrician"),
    PLUMBER("Plumber"),
    CARPENTER("Carpenter"),
    PAINTER("Painter"),
    SECURITY("Security guard"),
    FARM("Farm work"),
    OTHER("Other");

    private final String label;

    JobCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
