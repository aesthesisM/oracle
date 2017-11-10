package com.dm.oracle;

import java.io.Serializable;

/**
 * Created by khobsyzl28 on 11/8/2017.
 */
public class Runner implements Serializable{

    public static void main(String... args) throws ClassNotFoundException {
        System.out.println("Oracle opened eyes");
        Oracle oracle = Oracle.getInstance();
        System.out.println("Oracle closed eyes and following trials to see future");
    }
}
