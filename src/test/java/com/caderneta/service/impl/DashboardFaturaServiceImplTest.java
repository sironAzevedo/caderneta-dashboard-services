package com.caderneta.service.impl;

import com.caderneta.model.DashboardFaturaResponse;
import com.caderneta.model.FaturaResponse;
import com.caderneta.model.FaturasPorAnoResponse;
import com.caderneta.model.HeaderInfoDTO;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardFaturaServiceImplTest {

    @Mock
    private IFaturaRecuperadaRepository faturaRecuperadaRepository;

    @InjectMocks
    private DashboardFaturaServiceImpl dashboardFaturaService;

    @Test
    @DisplayName("Deve retornar valorPendente nulo quando todas as faturas estao nao pagas (somaNaoPagas == valorTotal)")
    void getDashboardSummary_todasNaoPagas_retornaValorPendenteNulo() {
        FaturaResponse f1 = new FaturaResponse("1", "Mercado", "50,00", LocalDate.now(), "N", null, null, "Alimentação", null);
        FaturaResponse f2 = new FaturaResponse("2", "Farmacia", "50,00", LocalDate.now(), "N", null, null, "Saúde", null);

        FaturasPorAnoResponse faturaAno = new FaturasPorAnoResponse("JANEIRO", "100,00", "100,00", 2, List.of(f1, f2));

        HeaderInfoDTO headerInfo = new HeaderInfoDTO("token", "tx-123");
        when(faturaRecuperadaRepository.getFaturasPorAno(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of(faturaAno)));

        DashboardFaturaResponse response = dashboardFaturaService.getDashboardSummary("test@email.com", null, 2026, headerInfo);

        assertNotNull(response);
        assertNotNull(response.faturas());
        assertEquals(1, response.faturas().size());

        FaturasPorAnoResponse item = response.faturas().get(0);
        assertNull(item.valorPendente(), "Se a soma for igual a valorTotal, valorPendente deve ser nulo");
    }

    @Test
    @DisplayName("Deve retornar soma das nao pagas quando parte das faturas foi paga (somaNaoPagas != valorTotal)")
    void getDashboardSummary_partePaga_retornaValorPendenteSoma() {
        FaturaResponse f1 = new FaturaResponse("1", "Mercado", "60,00", LocalDate.now(), "S", null, null, "Alimentação", null);
        FaturaResponse f2 = new FaturaResponse("2", "Farmacia", "40,00", LocalDate.now(), "N", null, null, "Saúde", null);

        FaturasPorAnoResponse faturaAno = new FaturasPorAnoResponse("JANEIRO", "100,00", "100,00", 2, List.of(f1, f2));

        HeaderInfoDTO headerInfo = new HeaderInfoDTO("token", "tx-123");
        when(faturaRecuperadaRepository.getFaturasPorAno(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of(faturaAno)));

        DashboardFaturaResponse response = dashboardFaturaService.getDashboardSummary("test@email.com", null, 2026, headerInfo);

        assertNotNull(response);
        assertNotNull(response.faturas());
        assertEquals(1, response.faturas().size());

        FaturasPorAnoResponse item = response.faturas().get(0);
        assertEquals("40,00", item.valorPendente());
    }

    @Test
    @DisplayName("Deve retornar '0,00' quando todas as faturas estao pagas (somaNaoPagas = 0 != valorTotal)")
    void getDashboardSummary_todasPagas_retornaValorPendenteZero() {
        FaturaResponse f1 = new FaturaResponse("1", "Mercado", "50,00", LocalDate.now(), "S", null, null, "Alimentação", null);
        FaturaResponse f2 = new FaturaResponse("2", "Farmacia", "50,00", LocalDate.now(), "S", null, null, "Saúde", null);

        FaturasPorAnoResponse faturaAno = new FaturasPorAnoResponse("JANEIRO", "100,00", "100,00", 2, List.of(f1, f2));

        HeaderInfoDTO headerInfo = new HeaderInfoDTO("token", "tx-123");
        when(faturaRecuperadaRepository.getFaturasPorAno(anyString(), anyInt(), any(HeaderInfoDTO.class)))
                .thenReturn(Mono.just(List.of(faturaAno)));

        DashboardFaturaResponse response = dashboardFaturaService.getDashboardSummary("test@email.com", null, 2026, headerInfo);

        assertNotNull(response);
        assertNotNull(response.faturas());

        FaturasPorAnoResponse item = response.faturas().get(0);
        assertEquals("0,00", item.valorPendente());
    }
}
