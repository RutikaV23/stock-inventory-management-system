package com.rutika.inventory.constants;

public final class ApiConstants {

    private ApiConstants() {}

    public static final String BASE_PATH = "/api/v1";

    public static final String PRODUCT_PATH = BASE_PATH + "/products";
    public static final String STOCK_IN_PATH = BASE_PATH + "/stock-in";
    public static final String STOCK_OUT_PATH = BASE_PATH + "/stock-out";


    public static final String ID_PATH_VARIABLE = "/{id}";
    public static final String SEARCH = "/search";
    public static final String PAGE_DEFAULT = "0";
    public static final String SIZE_DEFAULT = "10";
    public static final String SORT_DEFAULT = "id,asc";
    public static final String STOCK_IN_HISTORY_SORT_DEFAULT = "stockInDate,desc";
    public static final String STOCK_OUT_HISTORY_SORT_DEFAULT = "stockOutDate,desc";
    public static final String DASHBOARD_PATH = BASE_PATH + "/dashboard";
    public static final String AUTH_PATH = BASE_PATH + "/auth";
}
