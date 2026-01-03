package com.caderneta.service.impl;

import com.br.azevedo.utils.MoedaUtils;
import com.caderneta.model.*;
import com.caderneta.model.enums.CategoryIcon;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.repository.IFaturaRepository;
import com.caderneta.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static com.caderneta.util.Utils.*;
import static com.caderneta.util.Utils.REAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

	private final IFaturaRepository faturaRepository;
	private final IFaturaRecuperadaRepository faturaRecuperadaRepository;

	@Override
	public DashboardResponse getDashboardSummary(String email, int mes, int ano, HeaderInfoDTO headerInfo) {

		Mono<List<ExternalEvolucaoMensalResponse>> evolucaoMensal = faturaRecuperadaRepository.getEvolucaoMensal(email, ano, headerInfo);
		Mono<List<ResumoPorCategoriaDTO>> resumoPorCategoria = faturaRecuperadaRepository.getResumoPorCategoria(email, ano, headerInfo);
		Mono<ProximasFaturasResponse> faturasAtuaisPendentes = faturaRecuperadaRepository.getFaturasPorMes(email, mes, ano, headerInfo, null);


		Mono<List<CategoriaResponse>> categoriasFaturas = faturaRepository.getCategoria(email, headerInfo);
		Mono<List<FaturaListUserResponse>> faturaByuser = faturaRepository.getFatura(email, headerInfo);

		return Mono.zip(resumoPorCategoria, evolucaoMensal, faturasAtuaisPendentes, categoriasFaturas, faturaByuser).map(tuple -> {

			List<GastosCategoriaResponse> gastosCategoria = fetchAndMapGastos(tuple.getT1());
			List<ExternalEvolucaoMensalResponse> evolucao = tuple.getT2();
			List<EvolucaoMensalResponse> evolucaoPorMes = fetchAndMapEvolucao(evolucao);
			List<FaturaResponse> faturas = tuple.getT3().faturas();
			List<CategoriaResponse> categoriaResponses = tuple.getT4();
			List<FaturaListUserResponse> listUserFatura = tuple.getT5();

			SetupBeginResponse setup = fetchSetup(categoriaResponses, listUserFatura);
			boolean hasData = !CollectionUtils.isEmpty(faturas);

			List<StatsResponse> statsResponses = buildStats(evolucao);

			return new DashboardResponse(
					setup,
					hasData,
					!hasData? "Comece agora a sua organizar sua vida financeira" : "",
					statsResponses,
					gastosCategoria.stream().limit(5).toList(),
					evolucaoPorMes,
					fetchListFaturasPendente(faturas));
		}).block();
	}

	private List<FaturaResponse> fetchListFaturasPendente(List<FaturaResponse> faturas) {
		if (CollectionUtils.isEmpty(faturas)) {
			return List.of();
		}
		return faturas.stream()
				.limit(5)
				.filter(f -> "N".equals(f.pagamentoRealizado()))
				.map(f -> f.withIcon(CategoryIcon.getIconForCategory(f.categoria())))
				.toList();
	}

	private List<GastosCategoriaResponse> fetchAndMapGastos(List<ResumoPorCategoriaDTO> gastos) {
		return IntStream.range(0, gastos.size())
				.mapToObj(i -> {
					ResumoPorCategoriaDTO g = gastos.get(i);
					String color = BACKGROUND_COLOR.get(i % BACKGROUND_COLOR.size()); // ciclo de cores
					return new GastosCategoriaResponse(
							g.getCategoria(),
							ObjectUtils.defaultIfNull(g.getPercentualDoSalario(), BigDecimal.ZERO),
							g.getValorTotal(),
							color);
				})
				.toList();
	}

	private List<EvolucaoMensalResponse> fetchAndMapEvolucao(List<ExternalEvolucaoMensalResponse> evolucao) {
		return evolucao.stream().map(e ->
						new EvolucaoMensalResponse(
								e.mes(),
								MoedaUtils.stringToBigDecimal(e.valorTotal()),
								BigDecimal.valueOf(16000)) //valor mocado
						)
				.toList();
	}

	private SetupBeginResponse fetchSetup(List<CategoriaResponse> categoriaResponses, List<FaturaListUserResponse> listUserFatura) {
		return new SetupBeginResponse(
				CollectionUtils.isEmpty(categoriaResponses),
				GESTAO_CATEGORIA.stream().limit(10).toList(),
				CollectionUtils.isEmpty(listUserFatura)
		);
	}

	private List<StatsResponse> buildStats(List<ExternalEvolucaoMensalResponse> evolucao) {

		// 1. Receita total
		var receitasTotais = BigDecimal.ZERO;

		// 2. Despesas Totais
		BigDecimal totalAno = evolucao.stream()
				.map(vt -> MoedaUtils.stringToBigDecimal(vt.valorTotal()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);


		// 3. Previsão
		BigDecimal previsao = evolucao.stream()
				.map(vt -> MoedaUtils.stringToBigDecimal(vt.previsao()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// 4. Impacto no salario
		BigDecimal impactoSalario = BigDecimal.ZERO;

		// Os valores de 'change', 'icon' e 'trend' são exemplos conforme solicitado
		return List.of(
				new StatsResponse("Receita total", REAL.concat(MoedaUtils.bigDecimalToString(receitasTotais)), "+12%", "DollarSign", "up"),
				new StatsResponse("Despesas Totais", REAL.concat(MoedaUtils.bigDecimalToString(totalAno)), "+8%", "TrendingUp", "up"),
				new StatsResponse("Previsão", REAL.concat(MoedaUtils.bigDecimalToString(previsao)), "-5%", "BarChart3", "down"),
				new StatsResponse("Impacto nas receita total", REAL.concat(MoedaUtils.bigDecimalToString(impactoSalario)), "+12%", "Calendar", "neutral")
		);
	}
}