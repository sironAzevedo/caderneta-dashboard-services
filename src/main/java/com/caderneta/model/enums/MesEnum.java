package com.caderneta.model.enums;

import com.caderneta.model.MesDTO;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MesEnum {

    TODOS(0, "Todos os meses"),
    JANEIRO(1, "Janeiro"),
    FEVEREIRO(2, "Fevereiro"),
    MARCO(3, "Março"),
    ABRIL(4, "Abril"),
    MAIO(5, "Maio"),
    JUNHO(6, "Junho"),
    JULHO(7, "Julho"),
    AGOSTO(8, "Agosto"),
    SETEMBRO(9, "Setembro"),
    OUTUBRO(10, "Outubro"),
    NOVEMBRO(11, "Novembro"),
    DEZEMBRO(12, "Dezembro");

    private final int codigo;
    private final String descricao;

    MesEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /* =========================================
       DE → PARA: código → enum
       ========================================= */
    public static MesEnum fromCodigo(int codigo) {
        return Arrays.stream(values())
                .filter(m -> m.codigo == codigo)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Código de mês inválido: " + codigo)
                );
    }

    /* =========================================
       DE → PARA: código → nome do mês
       ========================================= */
    public static String nomePorCodigo(int codigo) {
        return fromCodigo(codigo).getDescricao();
    }

    public static MesEnum fromDescricao(String descricao) {
        return Arrays.stream(values())
                .filter(m -> m.descricao.equalsIgnoreCase(descricao))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Mês inválido: " + descricao)
                );
    }

    public static MesDTO enumToObjeto(MesEnum mes) {
        return new MesDTO((long) mes.codigo, mes.descricao);
    }
}

