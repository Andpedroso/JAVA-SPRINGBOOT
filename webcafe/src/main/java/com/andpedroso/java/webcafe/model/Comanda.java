package com.andpedroso.java.webcafe.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Range(min = 1, max = 20, message = "O número da mesa é inválido")
    private int mesa;
    private boolean vip;
    private LocalDate data;
    @Size(min = 1, message = "A comanda deve conter pelo menos 1 item")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idComanda")
    @Fetch(FetchMode.JOIN)
    private List<Item> itens;
    public BigDecimal getTotal(){
        return itens.stream()
                .map(Item::getTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
    public void removeItem(Long idItem){
        itens = itens.stream()
                .filter(item -> !item.getId().equals(idItem))
                .collect(Collectors.toList());
    }
}
