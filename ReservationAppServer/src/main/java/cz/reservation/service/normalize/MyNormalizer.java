package cz.reservation.service.normalize;

import java.text.Normalizer;

public class MyNormalizer {

    private MyNormalizer() {

    }

    public static String normalizeFileName(String fileName) {
        return Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+", "")
                .replaceAll("\\[^a-zA-Z0-9\\s-]", "")
                .replace("\u00A0", "-")
                .replaceAll("\\s+", "-")
                .toLowerCase()
                .trim();
    }
}
