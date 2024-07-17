package com.example.taskmanager.enumerator;

public enum RetryNumber {
    _0(0),
    _1(1),
    _2(2),
    _3(3),
    _4(4),
    _5(5);

    private final int number;

    RetryNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public RetryNumber getRetryNumber(int number){
        switch (number){
            case 1:
                return RetryNumber._1;
            case 2:
                return RetryNumber._2;
            case 3:
                return RetryNumber._3;
            case 4:
                return RetryNumber._4;
            case 5:
                return RetryNumber._5;
            default:
                return RetryNumber._0;
        }
    }
}
