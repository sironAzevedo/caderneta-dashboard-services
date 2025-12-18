package com.caderneta.model.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum CategoryIcon {
    TELEFONIA("Telefonia", "Phone"),
    ENERGIA("Energia", "Zap"),
    CARTAO_CREDITO("Cartão de Credito", "CreditCard"),
    ESCOLA("Escola", "GraduationCap"),
    ALUGUEL_CASA("Aluguel de casa", "Home"),
    TRANSPORTE_ESCOLAR("Transporte Escolar", "Bus"),
    COMBUSTIVEL("Combustivel", "Fuel");

    private final String categoryName;
    private final String iconName;

    CategoryIcon(String categoryName, String iconName) {
        this.categoryName = categoryName;
        this.iconName = iconName;
    }

    public static String getIconForCategory(String category) {
        for (CategoryIcon categoryIcon : values()) {
            if (categoryIcon.getCategoryName().equalsIgnoreCase(category)) {
                return categoryIcon.getIconName();
            }
        }
        return "DollarSign"; // Ícone padrão
    }

    public static Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (CategoryIcon categoryIcon : values()) {
            map.put(categoryIcon.getCategoryName(), categoryIcon.getIconName());
        }
        return map;
    }
}
