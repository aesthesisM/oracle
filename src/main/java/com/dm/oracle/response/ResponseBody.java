/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dm.oracle.response;

import com.dm.oracle.request.model.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * @author khobsyzl28
 */
public class ResponseBody {

    @JsonProperty("success")
    public boolean success;
    @JsonProperty("message")
    public String message;
    @JsonProperty("result")
    public ArrayList<Currency> result;
}
