package com.upemor.app_nutriologo.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

    public static String generarContrasena() {
        final String MAYUS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String MINUS = "abcdefghijklmnopqrstuvwxyz";
        final String NUM = "0123456789";
        final String ESPEC = "!@#$%^&*()-_=+[]{};:,.<>?/";

        final String TODOS = MAYUS + MINUS + NUM + ESPEC;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        // Garantizar que tenga al menos uno de cada tipo
        password.append(MAYUS.charAt(random.nextInt(MAYUS.length())));
        password.append(MINUS.charAt(random.nextInt(MINUS.length())));
        password.append(NUM.charAt(random.nextInt(NUM.length())));
        password.append(ESPEC.charAt(random.nextInt(ESPEC.length())));

        // Completar hasta 8 caracteres
        for (int i = 4; i < 8; i++) {
            password.append(TODOS.charAt(random.nextInt(TODOS.length())));
        }

        // Mezclar los caracteres para que no queden en orden predecible
        return mezclar(password.toString(), random);
    }

    private static String mezclar(String input, SecureRandom random) {
        char[] arr = input.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return new String(arr);
    }
}
