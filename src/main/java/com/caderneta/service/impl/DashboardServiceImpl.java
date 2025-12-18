package com.caderneta.service.impl;

import com.br.azevedo.utils.MoedaUtils;
import com.caderneta.model.*;
import com.caderneta.model.enums.CategoryIcon;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static com.caderneta.util.Utils.BACKGROUND_COLOR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

	private final IFaturaRecuperadaRepository faturaRecuperadaRepository;

	@Override
	public DashboardResponse getDashboardSummary(String email, int mes, int ano, HeaderInfoDTO headerInfo) {
		Mono<List<ExternalGastosCategoriaResponse>> gastosPorCategoria = faturaRecuperadaRepository.getGastosPorCategoria(email, ano, headerInfo);
		Mono<List<ExternalEvolucaoMensalResponse>> evolucaoMensal = faturaRecuperadaRepository.getEvolucaoMensal(email, ano, headerInfo);
		Mono<ProximasFaturasResponse> faturasAtuaisPendentes = faturaRecuperadaRepository.getFaturasPorMes(email, mes, ano, headerInfo, "NAO");
		Mono<List<Integer>> anosFaturasRecuperadas = faturaRecuperadaRepository.getAnosFaturasRecuperadas(email, headerInfo);

		return Mono.zip(gastosPorCategoria, evolucaoMensal, faturasAtuaisPendentes, anosFaturasRecuperadas).map(tuple -> {
			List<GastosCategoriaResponse> gastosCategoria = fetchAndMapGastos(tuple.getT1());
			List<EvolucaoMensalResponse> evolucaoPorMes = fetchAndMapEvolucao(tuple.getT2());
			ProximasFaturasResponse fPendentes = tuple.getT3();

			return new DashboardResponse(gastosCategoria, evolucaoPorMes, fetchListFaturasPendente(fPendentes.faturas()), tuple.getT4());
		}).block();
	}

	private List<FaturaResponse> fetchListFaturasPendente(List<FaturaResponse> faturas) {
		if (CollectionUtils.isEmpty(faturas)) {
			return List.of();
		}
		return faturas.stream()
				.limit(5)
				.map(f -> f.withIcon(CategoryIcon.getIconForCategory(f.categoria())))
				.toList();
	}

	private List<GastosCategoriaResponse> fetchAndMapGastos(List<ExternalGastosCategoriaResponse> gastos) {
		return IntStream.range(0, gastos.size())
				.mapToObj(i -> {
					ExternalGastosCategoriaResponse g = gastos.get(i);
					String color = BACKGROUND_COLOR.get(i % BACKGROUND_COLOR.size()); // ciclo de cores
					return new GastosCategoriaResponse(g.nome(), MoedaUtils.stringToBigDecimal(g.valorTotal()), color);
				})
				.toList();
	}

	private List<EvolucaoMensalResponse> fetchAndMapEvolucao(List<ExternalEvolucaoMensalResponse> evolucao) {
		return evolucao.stream().map(e -> new EvolucaoMensalResponse(e.mes(), MoedaUtils.stringToBigDecimal(e.valorTotal())))
				.toList();
	}

	private List<FaturasDashboardResponse> fetchAndMapFaturasAtuais(ProximasFaturasResponse fAtuais) {
		return fAtuais.faturas().stream().map(f -> new FaturasDashboardResponse( f.nome(),
						MoedaUtils.stringToBigDecimal(f.valor()),
						f.dataRecuperacao().format(DateTimeFormatter.ofPattern("dd/MM")),
						"medium"))
				.toList();
	}

	/*private List<StatsResponse> buildStats(List<EvolucaoMensalResponse> evolucao, ProximasFaturasResponse faturasAtuais, ProximasFaturasResponse faturasPendentes) {

		// 1. Total do ano
		BigDecimal totalAno = evolucao.stream()
				.map(EvolucaoMensalResponse::value)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// 2. Gastos este mês
		String gastosMesAtual = ObjectUtils.isNotEmpty(faturasAtuais) ? faturasAtuais.valorTotal() : "0,00";

		// 3. Previsão
		String previsao = ObjectUtils.isNotEmpty(faturasAtuais) ? faturasAtuais.previsao() : "0,00";

		// 4. Faturas Pendentes
		String valorfaturasPendentes = ObjectUtils.isNotEmpty(faturasPendentes) ? faturasPendentes.valorTotal() : "0,00";

		var qtdFaturas = String.valueOf(faturasPendentes.faturas().size()).concat(" faturas");

		// Os valores de 'change', 'icon' e 'trend' são exemplos conforme solicitado
		return List.of(
				new StatsResponse("Total do ano", REAL.concat(MoedaUtils.bigDecimalToString(totalAno)), "+8%", "TrendingUp", "up"),
				new StatsResponse("Previsão", REAL.concat(previsao), "-5%", "BarChart3", "down"),
				new StatsResponse("Gastos este mês", REAL.concat(gastosMesAtual), "+12%", "DollarSign", "up")
		);
	}*/
}