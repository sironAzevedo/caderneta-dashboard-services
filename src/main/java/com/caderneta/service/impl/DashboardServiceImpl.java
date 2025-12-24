package com.caderneta.service.impl;

import com.br.azevedo.utils.MoedaUtils;
import com.caderneta.model.*;
import com.caderneta.model.enums.CategoryIcon;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.repository.IFaturaRepository;
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
import static com.caderneta.util.Utils.GESTAO_CATEGORIA;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

	private final IFaturaRepository faturaRepository;
	private final IFaturaRecuperadaRepository faturaRecuperadaRepository;

	@Override
	public DashboardResponse getDashboardSummary(String email, int mes, int ano, HeaderInfoDTO headerInfo) {
		Mono<List<ExternalGastosCategoriaResponse>> gastosPorCategoria = faturaRecuperadaRepository.getGastosPorCategoria(email, ano, headerInfo);
		Mono<List<ExternalEvolucaoMensalResponse>> evolucaoMensal = faturaRecuperadaRepository.getEvolucaoMensal(email, ano, headerInfo);
		Mono<ProximasFaturasResponse> faturasAtuaisPendentes = faturaRecuperadaRepository.getFaturasPorMes(email, mes, ano, headerInfo, null);
		Mono<List<Integer>> anosFaturasRecuperadas = faturaRecuperadaRepository.getAnosFaturasRecuperadas(email, headerInfo);
		Mono<List<CategoriaResponse>> categoriasFaturas = faturaRepository.getGastosPorCategoria(email, headerInfo);
		Mono<List<FaturaListUserResponse>> faturaByuser = faturaRepository.getFatura(email, headerInfo);

		return Mono.zip(gastosPorCategoria, evolucaoMensal, faturasAtuaisPendentes, anosFaturasRecuperadas, categoriasFaturas, faturaByuser).map(tuple -> {
			List<GastosCategoriaResponse> gastosCategoria = fetchAndMapGastos(tuple.getT1());
			List<EvolucaoMensalResponse> evolucaoPorMes = fetchAndMapEvolucao(tuple.getT2());
			ProximasFaturasResponse fPendentes = tuple.getT3();
			List<CategoriaResponse> categoriaResponses = tuple.getT5();
			List<FaturaListUserResponse> listUserFatura = tuple.getT6();

			SetupBeginResponse setup = fetchSetup(categoriaResponses, listUserFatura);

			List<FaturaResponse> faturas = fPendentes.faturas();
			boolean hasData = !CollectionUtils.isEmpty(faturas);
			return new DashboardResponse(
					setup,
					hasData,
					!hasData? "Comece agora a sua organizar sua vida financeira" : "",
					gastosCategoria,
					evolucaoPorMes,
					fetchListFaturasPendente(faturas),
					tuple.getT4());
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

	private SetupBeginResponse fetchSetup(
			List<CategoriaResponse> categoriaResponses,
			List<FaturaListUserResponse> listUserFatura) {

		return new SetupBeginResponse(
				CollectionUtils.isEmpty(categoriaResponses),
				GESTAO_CATEGORIA.stream().limit(10).toList(),
				CollectionUtils.isEmpty(listUserFatura)
		);
	}
}