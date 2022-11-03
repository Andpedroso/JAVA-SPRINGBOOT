package com.andpedroso.java.webcafe;

import com.andpedroso.java.webcafe.model.Comanda;
import com.andpedroso.java.webcafe.model.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TesteUtil {
    public static String asJsonString(final Object obj){
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception ex){
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
    public static Comanda criaComanda(){
        var itens = List.of(
                Item.builder()
                        .descricao("Cappuccino")
                        .quantidade(3)
                        .valor(new BigDecimal("5.40"))
                        .build(),
                Item.builder()
                        .descricao("Coxinha de frango com catupiri")
                        .quantidade(2)
                        .valor(new BigDecimal("6.40"))
                        .build(),
                Item.builder()
                        .descricao("Empada de frango")
                        .quantidade(1)
                        .valor(new BigDecimal("7.40"))
                        .build()
        );
        return Comanda.builder()
                .mesa(1)
                .vip(false)
                .itens(itens)
                .data(LocalDate.now())
                .build();
    }
}
