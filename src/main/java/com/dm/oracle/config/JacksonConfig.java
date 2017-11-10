/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dm.oracle.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author khobsyzl28
 */
public class JacksonConfig {

    private ObjectMapper mapper = new ObjectMapper();

    public JacksonConfig() {
        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.EAGER_SERIALIZER_FETCH, true);
        mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER,true);
    }

    public ObjectMapper getContext() {
        return mapper;
    }
}
