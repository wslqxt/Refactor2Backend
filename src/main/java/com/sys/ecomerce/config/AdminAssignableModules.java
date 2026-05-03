package com.sys.ecomerce.config;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class AdminAssignableModules {

    public static final Set<String> KEYS = Collections.unmodifiableSet(
            new LinkedHashSet<>(
                    java.util.List.of(
                            "admin-dashboard",
                            "product-manage",
                            "category-manage",
                            "order-manage",
                            "inventory-warning",
                            "return-manage",
                            "banner-manage",
                            "image-library",
                            "leisure-manage",
                            "coupon-manage",
                            "sales-stats",
                            "board-manage")));

    private AdminAssignableModules() {}
}
