package br.com.gpds.web.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebResourceUtilsTest {

    @Test
    @DisplayName("Should return descending order when order field is not empty and direction is descending")
    void getOrdersWhenOrderFieldIsNotEmptyAndDirectionIsDescending() {
        var orderField = "name";
        var order = "desc";

        var result = WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(orderField, order);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor(orderField).getDirection());
    }

    @Test
    @DisplayName("Should return ascending order when order field is not empty and direction is ascending")
    void getOrdersWhenOrderFieldIsNotEmptyAndDirectionIsAscending() {
        var orderField = "name";
        var order = "asc";

        var result = WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(orderField, order);

        assertNotNull(result);
        assertEquals(Sort.Direction.ASC, result.getOrderFor(orderField).getDirection());
    }

}
