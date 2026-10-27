package com.caderneta.handler;

import com.br.azevedo.exception.AuthenticationException;
import com.br.azevedo.exception.AuthorizationException;
import com.br.azevedo.exception.BusinessException;
import com.br.azevedo.exception.InternalErrorException;
import com.br.azevedo.exception.NotFoundException;
import com.br.azevedo.model.vo.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("Deve tratar AuthenticationException retornando HTTP 401")
    void handleAuthenticationException_retorna401() {
        AuthenticationException ex = new AuthenticationException("Token expirado");
        StandardError error = exceptionHandler.handleAuthenticationException(ex);

        assertNotNull(error);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        assertEquals("Token expirado", error.getMensagem());
    }

    @Test
    @DisplayName("Deve tratar AuthorizationException retornando HTTP 403")
    void handleAuthorizationException_retorna403() {
        AuthorizationException ex = new AuthorizationException("Acesso negado para o escopo");
        StandardError error = exceptionHandler.handleAuthorizationException(ex);

        assertNotNull(error);
        assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatus());
        assertEquals("Acesso negado para o escopo", error.getMensagem());
    }

    @Test
    @DisplayName("Deve tratar NotFoundException retornando HTTP 404")
    void handleNotFound_retorna404() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");
        StandardError error = exceptionHandler.tNotFound(ex, request);

        assertNotNull(error);
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Recurso não encontrado", error.getMensagem());
    }

    @Test
    @DisplayName("Deve tratar BusinessException retornando HTTP 400")
    void handleBusinessException_retorna400() {
        BusinessException ex = new BusinessException("Regra de negócio violada");
        StandardError error = exceptionHandler.businessException(ex, request);

        assertNotNull(error);
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Regra de negócio violada", error.getMensagem());
    }

    @Test
    @DisplayName("Deve tratar InternalErrorException retornando HTTP 500")
    void handleInternalError_retorna500() {
        InternalErrorException ex = new InternalErrorException("Erro interno de servidor");
        StandardError error = exceptionHandler.internalError(ex, request);

        assertNotNull(error);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        assertEquals("Erro interno de servidor", error.getMensagem());
    }
}
