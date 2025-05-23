// ============= DifficultyLevel.java =============
package br.com.dio.enums;

/**
 * Enum para níveis de dificuldade
 */
public enum DifficultyLevel {
    EASY(1, "Fácil"),
    MEDIUM(2, "Médio"),
    HARD(3, "Difícil");

    private final int level;
    private final String description;

    DifficultyLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public static String[] getDescriptions() {
        return new String[]{EASY.description, MEDIUM.description, HARD.description};
    }

    public static DifficultyLevel fromIndex(int index) {
        switch (index) {
            case 0: return EASY;
            case 1: return MEDIUM;
            case 2: return HARD;
            default: return EASY;
        }
    }
}