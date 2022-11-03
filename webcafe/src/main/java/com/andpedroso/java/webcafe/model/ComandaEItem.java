package com.andpedroso.java.webcafe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComandaEItem {
    private Long idComanda;
    private Item item;
}
