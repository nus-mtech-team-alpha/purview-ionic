package com.apple.jmet.purview;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TestUtils() {
    }
}