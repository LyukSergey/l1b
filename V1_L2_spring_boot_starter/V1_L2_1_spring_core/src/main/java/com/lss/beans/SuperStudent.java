package com.lss.beans;

public class SuperStudent extends StudentImpl implements Student {

    @Override
    public void sayQuote() {
        System.out.println("T1000 sayQuote");
    }
}
