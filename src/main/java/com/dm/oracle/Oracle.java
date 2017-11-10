/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dm.oracle;

import com.dm.oracle.config.Constants;
import com.dm.oracle.config.JacksonConfig;
import com.dm.oracle.config.Storage;
import com.dm.oracle.decision.BollingerBands;
import com.dm.oracle.decision.CommodityChannelIndex;
import com.dm.oracle.request.model.Currency;
import com.dm.oracle.response.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * @author khobsyzl28
 */
public final class Oracle {

    private static final Oracle __INSTANCE = new Oracle();
    //currency information holders
    private static List<Currency> ALL_CURRENCIES;
    private static List<Currency> TOP_CURRENCIES;

    private Evaluater evaluater;
    private Collector[] collectors;
    private Timer timer;

    private Oracle() {
        super();
        initialize();
        begin();

    }

    public static Oracle getInstance() {
        return __INSTANCE;
    }

    private void initialize() {
        getAllCurrencies();
        getTopCurrencies();
    }

    private void getAllCurrencies() {

        List<Currency> top = null;
        //using spring http requester
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        JacksonConfig config = new JacksonConfig();

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<String> resp = null;
        ResponseBody responseBody = null;
        try {

            resp = template.exchange(Constants.TOP_CURRENCY_URI, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = config.getContext();
            responseBody = mapper.readValue(resp.getBody(), ResponseBody.class);
        } catch (IOException ex) {

        }
        int size = responseBody.result.size();
        String marketName = null;

        for (int i = 0; i < size; i++) {
            marketName = responseBody.result.get(i).marketName;
            if (!marketName.startsWith("BTC")) {
                responseBody.result.remove(i);
                size = responseBody.result.size();
                i--;
            }
        }

        responseBody.result.trimToSize();
        Collections.sort(responseBody.result);
        ALL_CURRENCIES = responseBody.result;
        //System.out.println(ALL_CURRENCIES);
    }

    private void getTopCurrencies() {
        if (ALL_CURRENCIES != null && ALL_CURRENCIES.size() > Constants.TOP_CURRENCY_COUNT) {
            TOP_CURRENCIES = new ArrayList<Currency>();
            for (int i = 0; i < Constants.TOP_CURRENCY_COUNT; i++) {
                TOP_CURRENCIES.add(ALL_CURRENCIES.get(i));
            }
            System.out.println("Top Currencies ---------------------->\n" + TOP_CURRENCIES);
            return;
        }
        System.out.println("getTopCurrencies!! ALL_CURRENCIES IS NOT WHAT ITS SUPPOSED TO BE ");
        System.exit(0);
    }

    private void begin() {
        timer = new Timer();
        prepareCollectorsForHunt();
    }

    private void prepareCollectorsForHunt() {
        collectors = new Collector[Constants.TOP_CURRENCY_COUNT];
        for (int i = 0; i < Constants.TOP_CURRENCY_COUNT; i++) {
            Currency temp = TOP_CURRENCIES.get(i);
            collectors[i] = new Collector(Constants.CURRENCY_VALUE_URI + temp.marketName.toLowerCase(), i);
            timer.scheduleAtFixedRate(collectors[i], 100, Constants.COLLECTION_TIME_PERIOD);
        }
        evaluater = new Evaluater();
        timer.scheduleAtFixedRate(evaluater, Constants.COLLECTION_EVALUATION_PERIOD, Constants.COLLECTION_EVALUATION_PERIOD);
    }

    class Collector extends TimerTask {

        String uri;
        int currencyNo;

        Collector(String uri, int currencyNo) {
            this.uri = uri;
            this.currencyNo = currencyNo;
        }

        public void run() {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            JacksonConfig config = new JacksonConfig();

            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<String> resp = null;
            ResponseBody responseBody = null;
            FileWriter fileWriter = null;
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                resp = template.exchange(uri, HttpMethod.GET, entity, String.class);
                ObjectMapper mapper = config.getContext();
                responseBody = mapper.readValue(resp.getBody(), ResponseBody.class);
                String pairName = uri.substring(uri.indexOf("=") + 1, uri.length());
                if (Constants.DATA_STORAGE == Storage.FILE) {
                    File f = new File(Constants.DATA_STORAGE_FILE_PATH + pairName + ".json");
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    fileWriter = new FileWriter(f, true);
                    fileWriter.append(mapper.writeValueAsString(responseBody.result.get(0)) + "\n");
                } else if (Constants.DATA_STORAGE == Storage.DATABASE) {


                    conn = JDBCUtil.getDBConnection();
                    ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_INSERT_CURRENCY_CURRENT_VALUE_SQL);
                    ps.setString(1, pairName);
                    ps.setFloat(2, responseBody.result.get(0).high);
                    ps.setFloat(3, responseBody.result.get(0).low);
                    ps.setFloat(4, responseBody.result.get(0).last);
                    ps.setFloat(5, responseBody.result.get(0).baseVolume);
                    ps.setTimestamp(6, responseBody.result.get(0).timeStamp);
                    ps.setFloat(7, responseBody.result.get(0).bid);
                    ps.setFloat(8, responseBody.result.get(0).ask);

                    ps.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //log ex
            } finally {
                JDBCUtil.close(conn, ps);
                try {
                    if (fileWriter != null)
                        fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Evaluater runs for 1 min calculating pairs mean,highest and lowest points.
    //after calculation puts that record to minuteCurrency table and gets latest 20 records from minuteCurrency Table
    //then we start calculating BollingerBrands points and figure out if our next target is gonna cross the borders ::=))))

    class Evaluater extends TimerTask {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        @Override
        public void run() {
            runOneMinuteTask();
            runDecisionMethods();
        }

        private void runOneMinuteTask() {
            List<Currency> tempList = null;
            try {
                conn = JDBCUtil.getDBConnection();
                for (int i = 0; i < Constants.TOP_CURRENCY_COUNT; i++) {
                    float high = 0;
                    float low = 0;
                    float last = 0;
                    float volume = 0;
                    String pairName = TOP_CURRENCIES.get(i).marketName.toUpperCase();
                    ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_SELECT_CURRENCY_CURRENT_VALUE_SQL);
                    ps.setString(1, pairName);
                    ps.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 60 * 1000));
                    rs = ps.executeQuery();
                    tempList = new ArrayList<>();
                    while (rs.next()) {
                        Currency temp = new Currency();
                        temp.last = rs.getFloat("LAST");
                        temp.high = rs.getFloat("HIGH");
                        temp.low = rs.getFloat("LOW");
                        temp.baseVolume = rs.getFloat("BASEVOLUME");
                        low = temp.low;
                        tempList.add(temp);
                    }
                    ps.close();
                    rs.close();
                    for (int j = 0; j < tempList.size(); j++) {
                        high = tempList.get(j).high > high ? tempList.get(j).high : high; //max high
                        low = tempList.get(j).low < low ? tempList.get(j).low : low; //min low
                        last += tempList.get(j).last;
                        volume += tempList.get(j).baseVolume;
                    }
                    last /= tempList.size();
                    volume /= tempList.size();

                    ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_INSERT_CURRENCY_MINUTE_VALUE_SQL);
                    ps.setString(1, pairName);
                    ps.setFloat(2, high);
                    ps.setFloat(3, low);
                    ps.setFloat(4, last);
                    ps.setFloat(5, volume);
                    ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();

                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCUtil.close(conn, ps, rs);
            }
        }

        private void runDecisionMethods() {
            Connection conn = null;
            List<Currency> currencyPeriodicValues = null;
            try {
                conn = JDBCUtil.getDBConnection();
                for (int i = 0; i < Constants.TOP_CURRENCY_COUNT; i++) {
                    String pairName = TOP_CURRENCIES.get(i).marketName.toUpperCase();
                    currencyPeriodicValues = readFromDB(pairName, conn);
                    runBollingerPointsTask(pairName, currencyPeriodicValues, conn);
                    runCCITask(pairName, currencyPeriodicValues, conn);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCUtil.close(conn);
            }
            Runtime.getRuntime().gc();
        }


        private void runBollingerPointsTask(String pairName, List<Currency> currencyPeriodicValues, Connection conn) {
            if (System.currentTimeMillis() - Constants.ORACLE_AWAKENING < 20 * 60 * 1000) //at least 20 min records needed for calculation
                return;
            BollingerBands bollingerBands = BollingerBands.getInstance();
            bollingerBands.calculate(currencyPeriodicValues);
            float standartDeviation = bollingerBands.currencyStandartDeviation;
            float sma = bollingerBands.currencyMean;
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_INSERT_CURRENCY_BOLLINGER_PREDICTIONS);
                ps.setString(1, pairName);
                ps.setFloat(2, sma + 2 * standartDeviation);
                ps.setFloat(3, sma);
                ps.setFloat(4, sma - 2 * standartDeviation);
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                ps.executeUpdate();

                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCUtil.close(ps);
            }
        }

        private void runCCITask(String pairName, List<Currency> currencyPeriodicValues, Connection conn) {
            if (System.currentTimeMillis() - Constants.ORACLE_AWAKENING < 20 * 60 * 1000) //at least 20 min records needed for calculation
                return;
            CommodityChannelIndex cci = CommodityChannelIndex.getInstance();
            cci.calculate(currencyPeriodicValues);
            float cciPoint = cci.cciPoint;
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_INSERT_CURRENCY_CCI_PREDICTIONS);
                ps.setString(1, pairName);
                ps.setFloat(2, cciPoint);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();

                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCUtil.close(ps);
            }
        }

        private List<Currency> readFromDB(String pairName, Connection conn) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            List<Currency> currencyPeriodicValues = null;
            try {
                conn = JDBCUtil.getDBConnection();
                ps = conn.prepareStatement(Constants.DATA_STORAGE_DATABASE_SELECT_CURRENCY_MINUTE_VALUE_SQL);
                ps.setString(1, pairName);
                rs = ps.executeQuery();
                currencyPeriodicValues = new ArrayList<>();
                while (rs.next()) {
                    Currency temp = new Currency();
                    temp.high = rs.getFloat("HIGH");
                    temp.low = rs.getFloat("LOW");
                    temp.last = rs.getFloat("LAST");
                    currencyPeriodicValues.add(temp);
                }

                ps.close();
                rs.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCUtil.close(ps, rs);
            }
            return currencyPeriodicValues;
        }
    }
}
