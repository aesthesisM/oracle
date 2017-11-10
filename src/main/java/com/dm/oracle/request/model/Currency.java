/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dm.oracle.request.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;
import java.sql.Timestamp;

/**
 * @author khobsyzl28
 */
public class Currency implements Comparable<Currency> {

    @JsonProperty("MarketName")
    public String marketName;
    @JsonProperty("High")
    public float high;
    @JsonProperty("Low")
    public float low;
    public transient Float volume;
    @JsonProperty("Last")
    public float last;
    @JsonProperty("BaseVolume")
    public float baseVolume;
    @JsonProperty("TimeStamp")
    public Timestamp timeStamp;
    @JsonProperty("Bid")
    public float bid;
    @JsonProperty("Ask")
    public float ask;
    public transient Integer  openBuyOrders;
    public transient Integer openSellOrders;
    @JsonProperty("PrevDay")
    public transient float prevDay;
    public transient Timestamp created;

    @Override
    public int compareTo(Currency o) {
        return (int) (o.baseVolume - this.baseVolume);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "marketName='" + marketName + '\'' +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", last=" + last +
                ", baseVolume=" + baseVolume +
                ", timeStamp=" + timeStamp +
                ", bid=" + bid +
                ", ask=" + ask +
                ", openBuyOrders=" + openBuyOrders +
                ", openSellOrders=" + openSellOrders +
                ", prevDay=" + prevDay +
                ", created=" + created +
                '}'+"\n";
    }
}
