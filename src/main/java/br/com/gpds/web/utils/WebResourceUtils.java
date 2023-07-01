package br.com.gpds.web.utils;

import org.springframework.data.domain.Sort;

public final class WebResourceUtils {
    private WebResourceUtils() {
    }

    public static Sort getOrdersWhenOrderFieldIsNotEmpty(String orderField, String order) {
        return Sort.Direction.fromString(order).isAscending()
            ? Sort.by(Sort.Order.asc(orderField))
            : Sort.by(Sort.Order.desc(orderField));
    }
}
