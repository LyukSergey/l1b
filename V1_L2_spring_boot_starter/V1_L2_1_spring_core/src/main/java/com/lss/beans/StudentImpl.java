package com.lss.beans;

import com.lss.annotations.InjectRandomInt;
import com.lss.annotations.PostProxy;
import com.lss.annotations.Benchmark;
import jakarta.annotation.PostConstruct;

@Benchmark
/*@DeprecatedClass(newImpl = T1000.class)*/
public class StudentImpl implements Student {

    private String message;

    @InjectRandomInt(min = 1, max = 10)
    private int repeat;

    @PostConstruct
    public void init() {
        System.out.printf("Phase 2. StudentQuoter init method %s%n", repeat);
    }

    public StudentImpl() {
        System.out.printf("Phase 1. StudentQuoter constructor %s%n", repeat);
    }

    @Override
    @PostProxy
    public void sayQuote() {
        System.out.printf("Phase 3. StudentQuoter sayQuote %s%n", repeat);
        for (int i = 0; i < repeat; i++) {
            System.out.printf("message = %s_%s%n", message, i);
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
}
