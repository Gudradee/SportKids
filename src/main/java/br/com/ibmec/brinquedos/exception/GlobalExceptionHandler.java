package br.com.ibmec.brinquedos.exception;

import br.com.ibmec.brinquedos.controller.BrinquedoController;
import br.com.ibmec.brinquedos.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = BrinquedoController.class)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError campo : ex.getBindingResult().getFieldErrors()) {
            erros.merge(campo.getField(), campo.getDefaultMessage(), (a, b) -> a + "; " + b);
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(erro -> erros.put(erro.getObjectName(), erro.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Erro de validação", request, erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarJsonInvalido(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "JSON inválido ou com tipos incorretos", request, Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoParametro(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String mensagem = "Valor inválido para o parâmetro '" + ex.getName() + "'";
        return responder(HttpStatus.BAD_REQUEST, mensagem, request, Map.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroAusente(MissingServletRequestParameterException ex,
                                                               HttpServletRequest request) {
        String mensagem = "Parâmetro obrigatório ausente: '" + ex.getParameterName() + "'";
        return responder(HttpStatus.BAD_REQUEST, mensagem, request, Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarRegraDeNegocio(IllegalArgumentException ex,
                                                             HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(BrinquedoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(BrinquedoNaoEncontradoException ex,
                                                            HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request, Map.of());
    }

    private static ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem,
                                                          HttpServletRequest request, Map<String, String> erros) {
        ErroResponse corpo = ErroResponse.of(status.value(), mensagem, request.getRequestURI(), erros);
        return ResponseEntity.status(status).body(corpo);
    }
}

/*
 * RESUMO: Esta classe e um tratador global de excecoes (@RestControllerAdvice) aplicado somente a API REST
 * (BrinquedoController). Ela "intercepta" as excecoes lancadas durante uma requisicao e as transforma em
 * respostas JSON padronizadas (ErroResponse) com o codigo HTTP correto: 400 quando o Bean Validation
 * reprova campos (devolvendo um mapa "campo -> mensagem"), quando o JSON esta malformado ou quando um
 * parametro de URL tem tipo errado ou esta ausente, ou quando uma regra de negocio e violada (por
 * exemplo, preco minimo maior que o maximo); 404 quando o brinquedo nao existe; e 500 para qualquer erro inesperado,
 * que e registrado no log do servidor sem expor stack trace ou detalhes internos ao cliente (boa pratica de
 * seguranca). Assim os controllers ficam limpos, sem blocos try/catch repetidos.
 */
