package com.andpedroso.java.webcafe.service;

import com.andpedroso.java.webcafe.TesteUtil;
import com.andpedroso.java.webcafe.repo.ComandaRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ComandaServiceTest {
    @Autowired
    private ComandaService service;
    @Autowired
    private ComandaRepo repo;
    @BeforeEach
    void criarComanda(){
        service.salvar(TesteUtil.criaComanda());
    }
    @AfterEach
    void excluirComanda(){
        repo.deleteAll();
    }
    @Test
    void listarComanda(){
        var resultado = service.listar(0, 10, "mesa");
        assertThat(resultado.isEmpty()).isFalse();
    }
    @Test
    void localizarPorMesa(){
        var comanda = service.localizar(1);
        assertThat(comanda.isPresent()).isTrue();
    }
    @Test
    void verificarItens(){
        var comanda = service.localizar(1);
        assertThat(comanda.isPresent()).isTrue();
        var itens = comanda.get().getItens();
        assertThat(itens.size() == 3).isTrue();
        assertThat(itens.stream().filter(item -> item.getDescricao()
                        .equals("Cappuccino"))
                .count() == 1
        ).isTrue();
    }
    @Test
    void removerComanda(){
        var comanda = service.localizar(1);
        assertThat(comanda.isPresent()).isTrue();
        var resultado = service.remover(comanda.get().getId());
        assertThat(resultado).isTrue();
    }
}
