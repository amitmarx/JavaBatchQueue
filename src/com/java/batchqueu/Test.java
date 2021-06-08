package com.java.batchqueu;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        try (
                BatchQueue<String> batch = new BatchQueue<>(4, 10, items -> {
                    System.out.println(Arrays.toString(items.toArray()));
                    System.out.println("=================");
                })
        ) {

            int i =0;
            Thread.sleep(300);
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            Thread.sleep(11);
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            batch.push(Integer.toString(i++));
            Thread.sleep(11);
        }
    }
}
