package com.caderneta.service.impl;

import com.br.azevedo.utils.MoedaUtils;
import com.caderneta.model.*;
import com.caderneta.model.enums.MesEnum;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.service.IDashboardFaturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.caderneta.model.enums.MesEnum.TODOS;
import static com.caderneta.util.Utils.REAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardFaturaServiceImpl implements IDashboardFaturaService {
    private final IFaturaRecuperadaRepository faturaRecuperadaRepository;

    @Override
    public DashboardFaturaResponse getDashboardSummary(String email, Integer mes, Integer ano, HeaderInfoDTO headerInfo) {
        Mono<List<Integer>> anosFaturasRecuperadas = faturaRecuperadaRepository.getAnosFaturasRecuperadas(email, headerInfo);
        Mono<List<FaturasPorAnoResponse>> gastosPorAno = faturaRecuperadaRepository.getFaturasPorAno(email, ano, headerInfo);


        return Mono.zip(anosFaturasRecuperadas, gastosPorAno).map(tuple -> {
            List<Integer> years = tuple.getT1();
            List<FaturasPorAnoResponse> faturasList = tuple.getT2();

            List<MesDTO> meses = extrairMeses(faturasList);

            if (ObjectUtils.isNotEmpty(mes) && TODOS.getCodigo() != mes) {
                String nomeMes = MesEnum.nomePorCodigo(mes);
                List<FaturasPorAnoResponse> faturasPorAnoResponses = faturasList.stream()
                        .filter(m -> m.mes().equalsIgnoreCase(nomeMes))
                        .toList();

                List<StatsResponse> stats = buildStats(faturasPorAnoResponses);
                return new DashboardFaturaResponse(years, meses, stats, faturasPorAnoResponses);
            }
            List<StatsResponse> stats = buildStats(faturasList);
            return new DashboardFaturaResponse(years, meses, stats, faturasList);
        }).block();
    }

    public static List<MesDTO> extrairMeses(List<FaturasPorAnoResponse> faturas) {
        return Stream.concat(
                        // Meses vindos das faturas
                        CollectionUtils.isEmpty(faturas) ? Stream.empty() :
                                faturas.stream()
                                        .map(FaturasPorAnoResponse::mes)
                                        .filter(Objects::nonNull)
                                        .map(String::trim)
                                        .map(String::toUpperCase)
                                        .map(MesEnum::fromDescricao)
                                        .distinct(),

                        // Mês "TODOS"
                        Stream.of(TODOS)
                )
                .sorted(Comparator.comparingInt(MesEnum::getCodigo)) // 🔥 agora ordena tudo
                .map(MesEnum::enumToObjeto)
                .toList();
    }

    private List<StatsResponse> buildStats(List<FaturasPorAnoResponse> evolucao) {

        //1. Qtd de faturas
        Integer qtdFatura = evolucao.stream()
                .map(FaturasPorAnoResponse::quantidade)
                .reduce(0, Integer::sum);

        // 2. Total do ano
        BigDecimal totalAno = evolucao.stream()
                .map( r -> MoedaUtils.stringToBigDecimal(r.valorTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Total do ano
        BigDecimal totalPrevisao = evolucao.stream()
                .map( r -> MoedaUtils.stringToBigDecimal(r.previsao()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return List.of(
                new StatsResponse("Total de Faturas", String.valueOf(qtdFatura), "+12%", "DollarSign", "up"),
                new StatsResponse("Valor Total", REAL.concat(MoedaUtils.bigDecimalToString(totalAno)), "+8%", "TrendingUp", "up"),
                new StatsResponse("Previsão",  REAL.concat(MoedaUtils.bigDecimalToString(totalPrevisao)), "-5%", "BarChart3", "down")
        );
    }
}
