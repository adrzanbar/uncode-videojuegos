package com.uncode.videojuegos.model.service.exception;

public class ServiceExceptionMessages {
    public static String exists(Class<?> entity, String attribute, String value) {
        return "Ya existe " + entity.getSimpleName().toLowerCase() + " con " + attribute + " " + value;
    }

    public static String blank(Class<?> entity, String attribute) {
        return attribute + " de " + entity.getSimpleName().toLowerCase() + " no puede estar vacío";

    }

    public static String $null(Class<?> entity, String attribute) {
        return attribute + " de " + entity.getSimpleName().toLowerCase() + " no puede ser nulo";
    }

    public static String notFound(Class<?> entity) {
        return "No se encontró " + entity.getSimpleName().toLowerCase();
    }

    public static String nonNegative(String attribute) {
        return "Valor de " + attribute + " no puede ser negativo";
    }

    public static final String ANY = "No se pudo realizar la operación";
}
