package com.java.batchqueu;

import java.util.Arrays;

//dummy commit for cr
public class Test {

    public static void main(String[] args) throws InterruptedException {
        try (
                BatchQueue<String> batch = new BatchQueue<>(4, 100, items -> {
                    System.out.println(Arrays.toString(items.toArray()));
                    System.out.println("=================");
                })
        ) {

            batch.push("1");
            batch.push("2");
            Thread.sleep(90);
            batch.push("3");
            batch.push("4");
            batch.push("5");
            batch.push("6");
            batch.push("7");
            Thread.sleep(500);
        }
    }
}
