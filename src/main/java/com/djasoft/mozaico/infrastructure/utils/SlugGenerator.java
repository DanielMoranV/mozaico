package com.djasoft.mozaico.infrastructure.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilidad para generar slugs únicos a partir de nombres
 */
public class SlugGenerator {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    /**
     * Genera un slug a partir de un texto
     *
     * Ejemplo:
     * "Restaurante Mozaico" -> "restaurante-mozaico"
     * "Pizzería Mamá Mía 2024" -> "pizzeria-mama-mia-2024"
     * "Café Central #1" -> "cafe-central-1"
     *
     * @param input Texto a convertir en slug
     * @return Slug generado (lowercase, sin espacios, solo guiones)
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // 1. Convertir a minúsculas
        String slug = input.toLowerCase().trim();

        // 2. Normalizar caracteres (quitar acentos)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", ""); // Eliminar marcas diacríticas

        // 3. Reemplazar espacios con guiones
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // 4. Eliminar caracteres no latinos (excepto guiones y números)
        slug = NONLATIN.matcher(slug).replaceAll("");

        // 5. Reemplazar múltiples guiones consecutivos por uno solo
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");

        // 6. Eliminar guiones al inicio y final
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");

        return slug;
    }

    /**
     * Genera un slug único agregando un sufijo numérico si es necesario
     *
     * @param baseSlug Slug base
     * @param existsFunction Función que verifica si el slug ya existe
     * @return Slug único
     */
    public static String generateUniqueSlug(String baseSlug, java.util.function.Predicate<String> existsFunction) {
        String slug = baseSlug;
        int counter = 1;

        // Si el slug ya existe, agregar un número al final
        while (existsFunction.test(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    /**
     * Valida que un slug tenga el formato correcto
     *
     * @param slug Slug a validar
     * @return true si es válido, false si no
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        // Debe contener solo letras minúsculas, números y guiones
        // No debe empezar ni terminar con guión
        // No debe tener guiones consecutivos
        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$");
    }
}
