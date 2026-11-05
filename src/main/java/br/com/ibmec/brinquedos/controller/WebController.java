package br.com.ibmec.brinquedos.controller;

import br.com.ibmec.brinquedos.dto.BrinquedoRequest;
import br.com.ibmec.brinquedos.dto.BrinquedoResponse;
import br.com.ibmec.brinquedos.service.BrinquedoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {
    private static final String VIEW_FORM = "admin/form";
    private static final String REDIRECT_LISTA = "redirect:/app/brinquedos";

    private final BrinquedoService service;

    public WebController(BrinquedoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String landing(Model model) {
        List<BrinquedoResponse> todos = service.listar(null, null, null);
        model.addAttribute("total", todos.size());
        model.addAttribute("destaques", todos.stream().limit(3).toList());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/app/brinquedos")
    public String listar(@RequestParam(required = false) String tipo, Model model) {
        model.addAttribute("brinquedos", service.listar(tipo, null, null));
        model.addAttribute("tipo", tipo);
        return "brinquedos";
    }

    @GetMapping("/app/admin/brinquedos/novo")
    public String novo(Model model) {
        model.addAttribute("brinquedo", new BrinquedoRequest());
        return VIEW_FORM;
    }

    @PostMapping("/app/admin/brinquedos")
    public String criar(@Valid @ModelAttribute("brinquedo") BrinquedoRequest dados, BindingResult result,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return VIEW_FORM;
        }
        BrinquedoResponse salvo = service.criar(dados);
        redirect.addFlashAttribute("sucesso", "Brinquedo \"" + salvo.nome() + "\" cadastrado com sucesso!");
        return REDIRECT_LISTA;
    }

    @GetMapping("/app/admin/brinquedos/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        BrinquedoResponse atual = service.buscarPorId(id);
        model.addAttribute("brinquedoId", id);
        model.addAttribute("brinquedo", new BrinquedoRequest(
                atual.nome(), atual.tipo(), atual.classificacao(), atual.tamanho(), atual.preco()));
        return VIEW_FORM;
    }

    @PostMapping("/app/admin/brinquedos/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("brinquedo") BrinquedoRequest dados, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("brinquedoId", id);
            return VIEW_FORM;
        }
        service.atualizar(id, dados);
        redirect.addFlashAttribute("sucesso", "Brinquedo #" + id + " atualizado com sucesso!");
        return REDIRECT_LISTA;
    }

    @PostMapping("/app/admin/brinquedos/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("sucesso", "Brinquedo #" + id + " excluído com sucesso!");
        return REDIRECT_LISTA;
    }
}

/*
 * RESUMO: Esta classe e o Controller MVC (@Controller) da interface web feita com Thymeleaf. Diferente do
 * controller REST, seus metodos devolvem o NOME de um template HTML (ex.: "index", "login", "brinquedos")
 * que o Thymeleaf preenche com os dados colocados no Model. Ela atende a landing page publica ("/"), a
 * tela de login ("/login"), a listagem de brinquedos para qualquer usuario logado ("/app/brinquedos") e as
 * telas exclusivas do ADMIN para cadastrar, editar e excluir ("/app/admin/brinquedos/..."). Os formularios
 * sao validados com @Valid + BindingResult: se houver erro, o formulario e reexibido com as mensagens do
 * Bean Validation; se der certo, aplica o padrao Post/Redirect/Get, redirecionando para a listagem com uma
 * mensagem de sucesso (flash attribute) para evitar reenvio do formulario ao atualizar a pagina. Toda a
 * regra de negocio e delegada ao BrinquedoService.
 */
