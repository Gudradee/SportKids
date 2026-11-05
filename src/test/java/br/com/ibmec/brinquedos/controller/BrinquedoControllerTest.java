package br.com.ibmec.brinquedos.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BrinquedoControllerTest {
    private static final String JSON_VALIDO = """
            {"nome":"Bola de Vôlei Kids","tipo":"Bola","classificacao":8,"tamanho":"M","preco":79.90}
            """;

    private static final String JSON_INVALIDO = """
            {"nome":"","tipo":"Bola","classificacao":20,"tamanho":"XX","preco":0}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /brinquedos sem autenticacao -> 401")
    void semAutenticacaoRetorna401() throws Exception {
        mvc.perform(get("/brinquedos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Senha errada -> 401")
    void senhaErradaRetorna401() throws Exception {
        mvc.perform(get("/brinquedos").with(httpBasic("user", "errada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER pode ler, mas POST/PUT/DELETE -> 403")
    void userNaoPodeAlterar() throws Exception {
        mvc.perform(get("/brinquedos").with(httpBasic("user", "user123")))
                .andExpect(status().isOk());
        mvc.perform(post("/brinquedos").with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        mvc.perform(put("/brinquedos/1").with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/brinquedos/1").with(httpBasic("user", "user123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("READ: lista, busca por id, filtro e 404")
    void leitura() throws Exception {
        mvc.perform(get("/brinquedos").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(4)));
        mvc.perform(get("/brinquedos/1").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/brinquedos?tipo=bola").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("Bola"));
        mvc.perform(get("/brinquedos/999").with(httpBasic("user", "user123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(containsString("999")));
    }

    @Test
    @DisplayName("CREATE -> UPDATE -> DELETE com ADMIN")
    void cicloCompletoAdmin() throws Exception {
        String location = mvc.perform(post("/brinquedos").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Bola de Vôlei Kids"))
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        mvc.perform(put("/brinquedos/" + id).with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Bola de Vôlei Kids Pro","tipo":"Bola","classificacao":9,"tamanho":"G","preco":99.90}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bola de Vôlei Kids Pro"))
                .andExpect(jsonPath("$.tamanho").value("G"));

        mvc.perform(delete("/brinquedos/" + id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/brinquedos/" + id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /brinquedos/faixa-preco (EntityManager + JPQL): filtra, ordena e valida parametros")
    void faixaDePreco() throws Exception {
        mvc.perform(get("/brinquedos/faixa-preco?min=50&max=130").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].preco").value(59.90))
                .andExpect(jsonPath("$[1].preco").value(129.90));
        mvc.perform(get("/brinquedos/faixa-preco?min=300&max=100").with(httpBasic("user", "user123")))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/brinquedos/faixa-preco?min=10").with(httpBasic("user", "user123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(containsString("max")));
    }

    @Test
    @DisplayName("POST com \"id\" no JSON: aceito sem erro, mas o id e sempre gerado pelo banco")
    void idNoJsonEhIgnorado() throws Exception {
        mvc.perform(post("/brinquedos").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"nome":"Bola de Basquete Kids","tipo":"Bola","classificacao":7,"tamanho":"P","preco":69.90}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not(1)));
        mvc.perform(get("/brinquedos/1").with(httpBasic("user", "user123")))
                .andExpect(jsonPath("$.nome").value("Bola de Futebol Infantil"));
    }

    @Test
    @DisplayName("POST invalido -> 400 com mapa de erros do Bean Validation")
    void validacao() throws Exception {
        mvc.perform(post("/brinquedos").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_INVALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasKey("nome")))
                .andExpect(jsonPath("$.erros", hasKey("classificacao")))
                .andExpect(jsonPath("$.erros", hasKey("tamanho")))
                .andExpect(jsonPath("$.erros", hasKey("preco")));
    }

    @Test
    @DisplayName("POST /brinquedos/lote grava a lista inteira")
    void lote() throws Exception {
        mvc.perform(post("/brinquedos/lote").with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brinquedos\":[" + JSON_VALIDO + "," + JSON_VALIDO + "]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Landing page e login sao publicos; area logada redireciona ao login")
    void paginasWeb() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/app/brinquedos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/login")));
    }

    @Test
    @DisplayName("Tela de ADMIN: USER -> 403, ADMIN -> 200")
    void areaAdminWeb() throws Exception {
        mvc.perform(get("/app/admin/brinquedos/novo")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/app/admin/brinquedos/novo")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }
}

/*
 * RESUMO: Esta classe contem os testes automatizados de integracao do projeto, escritos com JUnit 5 e
 * MockMvc. A anotacao @SpringBootTest sobe a aplicacao inteira (banco H2, seguranca, controllers) e
 * @AutoConfigureMockMvc permite simular requisicoes HTTP sem abrir uma porta de rede real. Os testes
 * reproduzem o mesmo roteiro feito no Postman: verificam o CRUD completo (criar com 201 + Location,
 * ler, atualizar, excluir com 204 e 404 apos excluir), a validacao com Bean Validation (400 com mapa de
 * erros), o cadastro em lote, a busca por faixa de preco feita com EntityManager, o id enviado no JSON
 * sendo ignorado e as regras do Spring Security (401 sem login ou com senha errada, 403 quando o
 * USER tenta alterar dados, acesso ao formulario web apenas para ADMIN, paginas publicas liberadas e
 * redirecionamento ao login na area restrita). Com @Transactional, cada teste e desfeito ao final, entao a
 * ordem de execucao nao interfere no resultado. Rode com "./mvnw test".
 */
