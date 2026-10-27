package com.caderneta.service.impl;

import com.caderneta.model.CategoriaResponse;
import com.caderneta.model.DashboardResponse;
import com.caderneta.model.ExternalEvolucaoMensalResponse;
import com.caderneta.model.FaturaListUserResponse;
import com.caderneta.model.FaturaResponse;
import com.caderneta.model.HeaderInfoDTO;
import com.caderneta.model.ProximasFaturasResponse;
import com.caderneta.model.ResumoPorCategoriaDTO;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import com.caderneta.repository.IFaturaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private IFaturaRepository faturaRepository;

    @Mock
    private IFaturaRecuperadaRepository faturaRecuperadaRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    @DisplayName("Deve retornar DashboardResponse completo quando existirem faturas")
    void getDashboardSummary_comFaturas_retornaDashboardCompleto() {
        HeaderInfoDTO headerInfo = new HeaderInfoDTO("tx-123", "Bearer token");
        String email = "teste@email.com";
        int ano = 2026;
        int mes = 10;

        List<ExternalEvolucaoMensalResponse> evolucao = List.of(
                new ExternalEvolucaoMensalResponse("OUTUBRO", "1500,00", "1200,00", 3)
        );
        List<ResumoPorCategoriaDTO> resumo = List.of(
                new ResumoPorCategoriaDTO("Alimentação", new BigDecimal("500.00"), new BigDecimal("10.00"))
        );
        FaturaResponse faturaPendente = new FaturaResponse(
                "1", "Supermercado", "500,00", LocalDate.now(), "N", null, null, "Alimentação", null
        );
        ProximasFaturasResponse proximasFaturas = new ProximasFaturasResponse("500,00", "500,00", List.of(faturaPendente));
        List<CategoriaResponse> categorias = List.of(new CategoriaResponse(1, "Alimentação"));
        List<FaturaListUserResponse> faturasUser = List.of(new FaturaListUserResponse("1", "Supermercado", "CARTAO", "DEBITO", "ATIVO", "FATURA"));

        when(faturaRecuperadaRepository.getEvolucaoMensal(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(evolucao));
        when(faturaRecuperadaRepository.getResumoPorCategoria(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(resumo));
        when(faturaRecuperadaRepository.getFaturasPorMes(anyString(), anyInt(), anyInt(), any(HeaderInfoDTO.class), any()))
                .thenReturn(Mono.just(proximasFaturas));
        when(faturaRepository.getCategoria(anyString(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(categorias));
        when(faturaRepository.getFatura(anyString(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(faturasUser));

        DashboardResponse response = dashboardService.getDashboardSummary(email, mes, ano, headerInfo);

        assertNotNull(response);
        assertTrue(response.hasData());
        assertEquals("", response.messageNotData());
        assertNotNull(response.statsResponses());
        assertEquals(4, response.statsResponses().size());
        assertEquals(1, response.gastosPorCategoria().size());
        assertEquals("Alimentação", response.gastosPorCategoria().get(0).name());
        assertEquals(1, response.faturasMes().size());
    }

    @Test
    @DisplayName("Deve retornar DashboardResponse com hasData false quando nao existirem faturas")
    void getDashboardSummary_semFaturas_retornaHasDataFalse() {
        HeaderInfoDTO headerInfo = new HeaderInfoDTO("tx-123", "Bearer token");
        String email = "novo@email.com";
        int ano = 2026;
        int mes = 1;

        ProximasFaturasResponse proximasFaturas = new ProximasFaturasResponse("0,00", "0,00", List.of());

        when(faturaRecuperadaRepository.getEvolucaoMensal(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of()));
        when(faturaRecuperadaRepository.getResumoPorCategoria(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of()));
        when(faturaRecuperadaRepository.getFaturasPorMes(anyString(), anyInt(), anyInt(), any(HeaderInfoDTO.class), any()))
                .thenReturn(Mono.just(proximasFaturas));
        when(faturaRepository.getCategoria(anyString(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of()));
        when(faturaRepository.getFatura(anyString(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of()));

        DashboardResponse response = dashboardService.getDashboardSummary(email, mes, ano, headerInfo);

        assertNotNull(response);
        assertFalse(response.hasData());
        assertEquals("Comece agora a sua organizar sua vida financeira", response.messageNotData());
        assertTrue(response.faturasMes().isEmpty());
    }
}
