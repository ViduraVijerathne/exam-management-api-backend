package com.vidura.exam.myUtils;

import java.util.Random;

public class MyUtils {
    public static String generateCode() {
        Random random = new Random();
        // 0 සිට 999,999 දක්වා අහඹු අංකයක් ලබා ගනී
        int number = random.nextInt(1000000);

        // අංකය ඉලක්කම් 6ක් වන පරිදි සකසයි (උදා: 123 නම් 000123 ලෙස පෙන්වීමට)
        return String.format("%06d", number);
    }
}
