package com.caderneta.service.impl;

import com.br.azevedo.utils.MoedaUtils;
import com.caderneta.model.*;
import com.caderneta.model.enums.CategoryIcon;
import com.caderneta.model.enums.MesEnum;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.service.IDashboardFaturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.caderneta.model.enums.MesEnum.TODOS;
import static com.caderneta.util.Utils.BACKGROUND_COLOR;
import static com.caderneta.util.Utils.REAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardFaturaServiceImpl implements IDashboardFaturaService {
    private final IFaturaRecuperadaRepository faturaRecuperadaRepository;

    @Override
    public DashboardFaturaResponse getDashboardSummary(String email, Integer mes, Integer ano, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaRepository
                .getFaturasPorAno(email, ano, headerInfo)
                .map(faturasList -> {

                    List<MesDTO> meses = extrairMeses(faturasList);
                    List<FaturasPorAnoResponse> filtradas =
                            filtrarPorMesSeNecessario(faturasList, mes);

                    List<StatsResponse> stats = buildStats(filtradas);

                    return new DashboardFaturaResponse(
                            meses,
                            stats,
                            getIconCategory(filtradas)
                    );
                })
                .block();
    }

    @Override
    public Flux<FaturaCategoriaDetalheDTO> getReports(String email, String categoria, Integer ano, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaRepository
                .getReport(email, categoria, ano, headerInfo)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::enrichWithIconAndColor)
                .sequential();
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

    private List<FaturasPorAnoResponse> getIconCategory(List<FaturasPorAnoResponse> faturas) {
        return faturas.stream()
                .map(f -> {
                    List<FaturaResponse> updated = f.faturas()
                            .stream()
                            .parallel()
                            .map(fc -> fc.withIcon(CategoryIcon.getIconForCategory(fc.categoria())))
                            .toList();
                    return f.withFaturas(updated);
                })
                .toList();
    }

    private List<FaturasPorAnoResponse> filtrarPorMesSeNecessario(List<FaturasPorAnoResponse> faturas, Integer mes) {
        if (ObjectUtils.isEmpty(mes) || TODOS.getCodigo() == mes) {
            return faturas;
        }

        String nomeMes = MesEnum.nomePorCodigo(mes);

        return faturas.stream()
                .filter(f -> f.mes().equalsIgnoreCase(nomeMes))
                .toList();
    }

    private FaturaCategoriaDetalheDTO enrichWithIconAndColor(FaturaCategoriaDetalheDTO dto) {

        int index = Math.abs(dto.getCategoria().hashCode()) % BACKGROUND_COLOR.size();
        String color = BACKGROUND_COLOR.get(index);

        return FaturaCategoriaDetalheDTO.builder()
                .categoria(dto.getCategoria())
                .icon(CategoryIcon.getIconForCategory(dto.getCategoria()))
                .color(color)
                .valorTotal(dto.getValorTotal())
                .percentualSalario(dto.getPercentualSalario())
                .faturas(dto.getFaturas())
                .build();
    }
}
