package com.java.batchqueu;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        try (
                BatchQueue<String> batch = new BatchQueue<>(4, 1, items -> {
                    if(items.size()==0){
                        System.out.println("ZEROOOO");
                    }
                    System.out.println(Arrays.toString(items.toArray()));
                    System.out.println("=================");
                })
        ) {

            for (int i =0; i<10000; i++){
                batch.push(Integer.toString(i));
            }
            Thread.sleep(500);
        }
    }
}
