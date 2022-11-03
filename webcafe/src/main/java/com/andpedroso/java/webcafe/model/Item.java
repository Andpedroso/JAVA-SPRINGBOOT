package com.andpedroso.java.webcafe.model;

import com.andpedroso.java.webcafe.validator.Valor;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, message = "A descrção é inválida")
    @Column(nullable = false)
    private String descricao;
    @Valor(min = 0.50, message = "O valor não pode ser menor do que R$ 0,50")
    @Builder.Default
    private BigDecimal valor = BigDecimal.ZERO;
    @Min(value = 1, message = "A quantidade tem que ser pelo menos 1")
    @Max(value = 20, message = "A quantidade não pode ser maior do que 20")
    private int quantidade;
    public BigDecimal getTotal(){
        return valor.multiply(new BigDecimal(quantidade));
    }
}
