/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dm.oracle.config;

import java.sql.Timestamp;

/**
 * @author khobsyzl28
 */
public class Constants {
    //APP Start date
    public static long ORACLE_AWAKENING = System.currentTimeMillis();

    //request options
    public static final int COLLECTION_TIME_PERIOD = 5000;//(5sec) second used to calculate currency's state in that moment
    public static final int COLLECTION_EVALUATION_PERIOD = 61000; //
    public static final int TOP_CURRENCY_COUNT = 10;//top ten currencies for fibo

    //request uris
    public static final String TOP_CURRENCY_URI = "https://bittrex.com/api/v1.1/public/getmarketsummaries";
    public static final String CURRENCY_VALUE_URI = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=";//btc-ltc   

    //storage constants
    public static final Storage DATA_STORAGE = Storage.DATABASE;
    public static final String DATA_STORAGE_FILE_PATH = "D:\\Workspaces\\IntellijIdea\\oracle\\coins_out\\";
    public static final String DATA_STORAGE_DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3307/oracle?" +"user=root";
    public static final String DATA_STORAGE_DATABASE_INSERT_CURRENCY_CURRENT_VALUE_SQL = "INSERT INTO ORACLE.CURRENT_CURRENCY (MARKETNAME,HIGH,LOW,LAST,BASEVOLUME,TIMESTAMP,BID,ASK) VALUES (?,?,?,?,?,?,?,?)";
    public static final String DATA_STORAGE_DATABASE_SELECT_CURRENCY_CURRENT_VALUE_SQL = "SELECT * FROM ORACLE.CURRENT_CURRENCY WHERE MARKETNAME = ? AND TIMESTAMP > ? ORDER BY TIMESTAMP DESC LIMIT 12";// 1 minute calculating value 1 fiddle for 1 min

    public static final String DATA_STORAGE_DATABASE_INSERT_CURRENCY_MINUTE_VALUE_SQL = "INSERT INTO ORACLE.MINUTE_CURRENCY (MARKETNAME,HIGH,LOW,LAST,BASEVOLUME,TIMESTAMP) VALUES (?,?,?,?,?,?)";
    public static final String DATA_STORAGE_DATABASE_SELECT_CURRENCY_MINUTE_VALUE_SQL = "SELECT * FROM ORACLE.MINUTE_CURRENCY WHERE MARKETNAME = ? ORDER BY ID DESC LIMIT 20";

    public static final String DATA_STORAGE_DATABASE_INSERT_CURRENCY_BOLLINGER_PREDICTIONS = "INSERT INTO ORACLE.BOLLINGER_PREDICTIONS (MARKETNAME,HIGH,MEAN,LOW,TIMESTAMP) VALUES (?,?,?,?,?)";
    public static final String DATA_STORAGE_DATABASE_INSERT_CURRENCY_CCI_PREDICTIONS = "INSERT INTO ORACLE.CCI_PREDICTIONS (MARKETNAME,CCI_RATE,TIMESTAMP) VALUES(?.?,?)";

    /*
        Trading constants
    */
    public static final double MINIMUM_PROFIT_PERCENT = 2;//%1.5 percent + 0.5 fee

    /*
        Fibonacci trading constants
    */

    /*
        Bollinger Brands trading constants
    */

    public static final int BOLLINGER_PERIOD = 20; //latest 20 minute used in select query so its pointless to hold it here but just in case ;)
    public static final float CCI_CALCULATE_CONSTANT = 0.015f;


}
