package com.andpedroso.java.webcafe;

import com.andpedroso.java.webcafe.repo.ComandaRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ComandaApplicationTests {
	@Autowired
	private ComandaRepo repo;
	@BeforeEach
	void criaComanda(){
		repo.saveAndFlush(TesteUtil.criaComanda());
	}
	@AfterEach
	void removeComandas(){
		repo.deleteAll();
	}
	@Test
	void listaComandas(){
		var resultado = repo.findAll();
		assertThat(resultado).isNotNull();
		assertThat(resultado).isNotEmpty();
	}
	@Test
	void pesquisarMesa(){
		var resultado = repo.findByMesa(1);
		assertThat(resultado.isPresent()).isTrue();
	}
	@Test
	void localizaItem(){
		var resultado = repo.findByMesa(1);
		assertThat(resultado.isPresent()).isTrue();
		var comanda = resultado.get();
		var itens = comanda.getItens();
		assertThat(itens.size() == 3).isTrue();
		assertThat(itens.stream()
				.filter(item -> item.getDescricao().equals("Cappuccino"))
				.count() == 1).isTrue();
	}
	@Test
	void removeComanda(){
		var resultado = repo.findByMesa(1);
		assertThat(resultado.isPresent()).isTrue();
		repo.delete(resultado.get());
		resultado = repo.findByMesa(1);
		assertThat(resultado.isPresent()).isFalse();
	}
	@Test
	void verificaTotais(){
		var resultado = repo.findByMesa(1);
		assertThat(resultado.isPresent()).isTrue();
		var comanda = resultado.get();
		var totaisItens = BigDecimal.ZERO;
		for (var item : comanda.getItens()) {
			totaisItens = totaisItens.add(item.getTotal());
		}
		assertThat(comanda.getTotal().equals(totaisItens)).isTrue();
	}
}
