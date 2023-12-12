package com.example.javabasic.test;

public class Alo {
    public static void main(String[] args) {
        int[] numbers = {1,2,2,2,2,1};
        int i = 0;
        int j = numbers.length - 1;
        while (i <= j) {
            if (numbers[i] == numbers[j]) {
                j--;
                i++;
            } else {
                numbers[i] = Integer.MAX_VALUE;
                numbers[j] = Integer.MAX_VALUE;
            }
        }
        for (int n : numbers) {
            if (n != Integer.MAX_VALUE) {
                System.out.println(n);;
            }
        }
    }
}
