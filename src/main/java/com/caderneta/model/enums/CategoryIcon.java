package com.caderneta.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum CategoryIcon {

    // =====================
    // Alimentação
    // =====================
    ALIMENTACAO(
            "Alimentação", "Utensils",
            "text-orange-600 dark:text-orange-400",
            "bg-orange-100 dark:bg-orange-900/30",
            "hover:border-orange-300 dark:hover:border-orange-700"
    ),
    RESTAURANTES("Restaurantes", "Utensils", ALIMENTACAO),
    MERCADO("Mercado", "ShoppingCart", ALIMENTACAO),

    // =====================
    // Moradia
    // =====================
    MORADIA(
            "Moradia", "Home",
            "text-blue-600 dark:text-blue-400",
            "bg-blue-100 dark:bg-blue-900/30",
            "hover:border-blue-300 dark:hover:border-blue-700"
    ),
    ALUGUEL_CASA("Aluguel", "Home", MORADIA),
    ENERGIA("Energia", "Zap", MORADIA),
    AGUA("Água", "Droplet", MORADIA),
    TELEFONIA("Telefonia", "Phone", MORADIA),
    INTERNET("Internet", "Wifi", MORADIA),

    // =====================
    // Transporte
    // =====================
    TRANSPORTE(
            "Transporte", "Car",
            "text-green-600 dark:text-green-400",
            "bg-green-100 dark:bg-green-900/30",
            "hover:border-green-300 dark:hover:border-green-700"
    ),
    COMBUSTIVEL("Combustível", "Fuel", TRANSPORTE),
    UBER("Uber", "Car", TRANSPORTE),
    TRANSPORTE_ESCOLAR("Transporte Escolar", "Bus", TRANSPORTE),

    // =====================
    // Lazer
    // =====================
    LAZER(
            "Lazer", "Ticket",
            "text-purple-600 dark:text-purple-400",
            "bg-purple-100 dark:bg-purple-900/30",
            "hover:border-purple-300 dark:hover:border-purple-700"
    ),
    CINEMA("Cinema", "Film", LAZER),
    FESTAS("Festas", "PartyPopper", LAZER),

    // =====================
    // Saúde
    // =====================
    SAUDE(
            "Saúde", "Heart",
            "text-red-600 dark:text-red-400",
            "bg-red-100 dark:bg-red-900/30",
            "hover:border-red-300 dark:hover:border-red-700"
    ),
    FARMACIA("Farmácia", "Pill", SAUDE),
    CONSULTAS("Consultas", "Stethoscope", SAUDE),

    // =====================
    // Educação
    // =====================
    EDUCACAO(
            "Educação", "GraduationCap",
            "text-yellow-600 dark:text-yellow-400",
            "bg-yellow-100 dark:bg-yellow-900/30",
            "hover:border-yellow-300 dark:hover:border-yellow-700"
    ),
    ESCOLA("Escola", "GraduationCap", EDUCACAO),
    CURSOS("Cursos", "BookOpen", EDUCACAO),
    LIVROS("Livros", "Book", EDUCACAO),

    // =====================
    // Vestuário
    // =====================
    VESTUARIO(
            "Vestuário", "Shirt",
            "text-pink-600 dark:text-pink-400",
            "bg-pink-100 dark:bg-pink-900/30",
            "hover:border-pink-300 dark:hover:border-pink-700"
    ),
    ROUPAS("Roupas", "Shirt", VESTUARIO),
    ACESSORIOS("Acessórios", "Watch", VESTUARIO),

    // =====================
    // Investimentos
    // =====================
    INVESTIMENTOS(
            "Investimentos", "TrendingUp",
            "text-emerald-600 dark:text-emerald-400",
            "bg-emerald-100 dark:bg-emerald-900/30",
            "hover:border-emerald-300 dark:hover:border-emerald-700"
    ),
    APORTES("Aportes", "TrendingUp", INVESTIMENTOS),
    RESERVAS("Reservas", "PiggyBank", INVESTIMENTOS),

    // =====================
    // Outros
    // =====================
    OUTROS(
            "Outros", "DollarSign",
            "text-gray-600 dark:text-gray-400",
            "bg-gray-100 dark:bg-gray-900/30",
            "hover:border-gray-300 dark:hover:border-gray-700"
    );

    private final String categoryName;
    private final String iconName;
    private final String color;
    private final String bg;
    private final String borderHover;

    // Categoria "raiz"
    CategoryIcon(String categoryName, String iconName, String color, String bg, String borderHover) {
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.color = color;
        this.bg = bg;
        this.borderHover = borderHover;
    }

    // Categoria "filha" herda o estilo
    CategoryIcon(String categoryName, String iconName, CategoryIcon parent) {
        this(
                categoryName,
                iconName,
                parent.color,
                parent.bg,
                parent.borderHover
        );
    }

    // =====================
    // Helpers
    // =====================
    public static CategoryIcon fromCategory(String category) {
        return Arrays.stream(values())
                .filter(c -> c.categoryName.equalsIgnoreCase(category))
                .findFirst()
                .orElse(OUTROS);
    }

    public static String getIconForCategory(String category) {
        return fromCategory(category).iconName;
    }

    /**
     * Retorna estrutura pronta para o front
     */
    public static List<Map<String, String>> toList() {
        return Arrays.stream(values())
                .map(c -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("id", c.categoryName);
                    map.put("label", c.categoryName);
                    map.put("icon", c.iconName);
                    map.put("color", c.color);
                    map.put("bg", c.bg);
                    map.put("borderHover", c.borderHover);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
