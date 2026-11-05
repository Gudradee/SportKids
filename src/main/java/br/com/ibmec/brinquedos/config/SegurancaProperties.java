package br.com.ibmec.brinquedos.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.seguranca")
public record SegurancaProperties(
        @Valid @NotNull Credencial usuario,
        @Valid @NotNull Credencial admin) {
    public record Credencial(
            @NotBlank String username,
            @NotBlank String senha) {
    }
}

/*
 * RESUMO: Este record le as configuracoes de seguranca do application.properties (chaves que comecam com
 * "app.seguranca") e as transforma em um objeto Java tipado, gracas a @ConfigurationProperties. Ele guarda
 * o nome de usuario e a senha do perfil USER e do perfil ADMIN. Dessa forma as credenciais nao ficam
 * escritas dentro do codigo Java: elas podem ser trocadas no arquivo de propriedades ou, melhor ainda, por
 * variaveis de ambiente (APP_USER_PASSWORD e APP_ADMIN_PASSWORD) sem recompilar o projeto. As anotacoes
 * @Validated, @NotNull e @NotBlank fazem a aplicacao falhar logo na inicializacao caso alguma credencial
 * esteja vazia, evitando subir o sistema com um usuario sem senha.
 */
