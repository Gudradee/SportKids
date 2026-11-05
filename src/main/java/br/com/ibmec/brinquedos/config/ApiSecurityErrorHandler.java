package br.com.ibmec.brinquedos.config;

import br.com.ibmec.brinquedos.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ApiSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final JsonMapper jsonMapper;

    public ApiSecurityErrorHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"brinquedos\"");
        escrever(response, request, HttpStatus.UNAUTHORIZED,
                "Não autenticado: informe usuário e senha válidos (Basic Auth)");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escrever(response, request, HttpStatus.FORBIDDEN,
                "Acesso negado: seu perfil não tem permissão para esta operação");
    }

    private void escrever(HttpServletResponse response, HttpServletRequest request,
                          HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ErroResponse corpo = ErroResponse.of(status.value(), mensagem, request.getRequestURI());
        jsonMapper.writeValue(response.getOutputStream(), corpo);
    }
}

/*
 * RESUMO: Esta classe personaliza as respostas de erro de SEGURANCA da API REST. Ela implementa duas
 * interfaces do Spring Security: AuthenticationEntryPoint, chamada quando alguem acessa /brinquedos sem se
 * autenticar (ou com senha errada), devolvendo 401 Unauthorized; e AccessDeniedHandler, chamada quando o
 * usuario esta autenticado mas nao tem o perfil exigido (por exemplo, o USER tentando criar ou excluir),
 * devolvendo 403 Forbidden. Em vez da resposta vazia padrao, ela escreve um JSON no mesmo formato
 * ErroResponse usado pelo resto da API, usando o JsonMapper (Jackson 3) configurado pelo Spring Boot. O
 * header WWW-Authenticate indica ao cliente que a API usa autenticacao HTTP Basic.
 */
