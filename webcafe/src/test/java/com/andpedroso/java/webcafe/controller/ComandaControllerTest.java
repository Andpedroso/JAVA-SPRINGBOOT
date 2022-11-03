package com.andpedroso.java.webcafe.controller;

import com.andpedroso.java.webcafe.model.ComandaEItem;
import com.andpedroso.java.webcafe.model.Item;
import com.andpedroso.java.webcafe.repo.ComandaRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.andpedroso.java.webcafe.TesteUtil.asJsonString;
import static com.andpedroso.java.webcafe.TesteUtil.criaComanda;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ComandaControllerTest {
    @Autowired
    private MockMvc mock;
    @Autowired
    private ComandaRepo repo;
    @BeforeEach
    void criarComanda() throws Exception{
        mock.perform(post("/api/incluirComanda")
                .content(asJsonString(criaComanda()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @AfterEach
    void excluirComanda(){ repo.deleteAll(); }
    @Test
    void listarComanda() throws Exception {
        mock.perform(get("/api/listarComandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void localizarPorMesa() throws Exception {
        mock.perform(get("/api/localizar")
                        .param("mesa", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void removerComanda() throws Exception {
        var comanda = repo.findByMesa(1);
        assertThat(comanda.isPresent()).isTrue();
        mock.perform(delete("/api/removerComanda/{id}",
                        comanda.get().getId())
                        .content(asJsonString(criaComanda()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void incluirItem() throws Exception {
        var comanda = repo.findByMesa(1);
        assertThat(comanda.isPresent()).isTrue();
        var item = Item.builder()
                .descricao("Empada de carne")
                .quantidade(2)
                .valor(new BigDecimal("7.50"))
                .build();
        var comandaEItem = ComandaEItem.builder()
                .idComanda(comanda.get().getId())
                .item(item)
                        .build();
        mock.perform(post("/api/incluirItem")
                        .content(asJsonString(comandaEItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void removerItem() throws Exception {
        var comanda = repo.findByMesa(1);
        assertThat(comanda.isPresent()).isTrue();
        var itens = comanda.get().getItens();
        mock.perform(delete("/api/removerItem")
                        .param("idComanda", comanda.get().getId().toString())
                        .param("idItem", itens.get(1).getId().toString())
                        .content(asJsonString(criaComanda()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
