## Java - Springboot
### Projeto Web Café
### Back-end API e BD
---
### Escopo:
---
#### O projeto Web café deve receber pedidos relacionados a uma mesa. Cada mesa tem uma comanda com um identificador, o número da mesa, se é vip ou não, a data da comanda, os itens e um total. Cada item possui um identificador, uma descrição, um valor, uma quantidade e um total.
---
#### O projeto está dividido no projeto desenhado e testes.
---
#### Deve ser gerado um projeto Springboot com as seguintes dependências iniciais:
- (Springboot)[https://start.spring.io/]
- Spring Web
- Spring Data JPA
- Lombok
- H2 Database
- Validation
---
## Configurações
---
> Dependências
#### build.gradle
```bash
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	implementation 'io.springfox:springfox-boot-starter:3.0.0'
	implementation 'io.springfox:springfox-swagger-ui:3.0.0'
	implementation 'io.springfox:springfox-spring-web:3.0.0'
	implementation 'io.springfox:springfox-bean-validators:3.0.0'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
```
> Propriedades
#### application.properties
```bash
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:file:~/apppedidos
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.archive.autodetection=true

spring.h2.console.enabled=true
server.servlet.context-path=/webcafe

# http://localhost:8080/webcafe/h2-console
# http://localhost:8080/webcafe/swagger-ui/#/comanda-controller
```
---
## Projeto
---
### Package - components
---
#### Contém várias classes para serem feitas validações de erros e tratamento de excessões. Copiar direto do arquivo do projeto e colar na pasta do pacote.
---
### Package - config
---
#### Configuração do Sagger
> SpringFoxConfig
```bash
package com.andpedroso.java.webcafe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.andpedroso.java"))
                .paths(PathSelectors.any())
                .build();
    }
    @Bean
    public InternalResourceViewResolver defaultViewResolver(){
        return new InternalResourceViewResolver();
    }
}
```
---
### Package - controller
---
> ComandaController
```bash
package com.andpedroso.java.webcafe.controller;

import com.andpedroso.java.webcafe.components.DataException;
import com.andpedroso.java.webcafe.components.DataNotFoundException;
import com.andpedroso.java.webcafe.components.DataValidationException;
import com.andpedroso.java.webcafe.model.Comanda;
import com.andpedroso.java.webcafe.model.ComandaEItem;
import com.andpedroso.java.webcafe.service.ComandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api", produces = "application/json;charset=utf-8")
public class ComandaController {
    @Autowired
    private ComandaService service;
    @GetMapping("/listarComandas")
    public ResponseEntity<Page<Comanda>> listarComanda(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "mesa") String sortBy) throws DataException {
        return ResponseEntity.ok(service.listar(pageNo, pageSize, sortBy));
    }
    @GetMapping("/localizar")
    public ResponseEntity<Comanda> localizar(Integer mesa) throws DataException{
        var comanda = service.localizar(mesa);
        if(comanda.isPresent()){
            return ResponseEntity.ok(comanda.get());
        } else {
            throw new DataNotFoundException("Mesa não encontrada");
        }
    }
    @PostMapping("/incluirComanda")
    public ResponseEntity<Comanda> incluirComanda(@RequestBody @Valid Comanda comanda, BindingResult result) throws DataException{
        if(result.hasErrors()){
            throw new DataValidationException("Comanda com dados inválidos", result);
        } else {
            return ResponseEntity.ok(service.salvar(comanda));
        }
    }
    @DeleteMapping("/removerComanda/{id}")
    public ResponseEntity<Object> removerComanda(@PathVariable("id") long id) throws DataException{
        if(service.remover(id)){
            return ResponseEntity.ok().build();
        } else {
            throw new DataNotFoundException("Comanda não encontrada");
        }
    }
    @PostMapping("/incluirItem")
    public ResponseEntity<Comanda> incluirItem(@RequestBody @Valid ComandaEItem comandaEItem, BindingResult result) throws  DataException{
        if(result.hasErrors()){
            throw new DataValidationException(
                    "Item com dados inválidos", result
            );
        } else {
            var aComanda = service.localizar(comandaEItem.getIdComanda());
            if (aComanda.isPresent()){
                var comanda = aComanda.get();
                var item = comandaEItem.getItem();
                comanda.getItens().add(item);
                service.salvar(comanda);
                return ResponseEntity.ok(comanda);
            } else {
                throw new DataNotFoundException("A comanda não foi encontrada");
            }
        }
    }
    @DeleteMapping("/removerItem")
    public ResponseEntity<Comanda> removerItem(@RequestParam long idItem,@RequestParam long idComanda) throws DataException{
        var aComanda = service.localizar(idComanda);
        if (aComanda.isPresent()){
            var comanda = aComanda.get();
            comanda.removeItem(idItem);
            service.salvar(comanda);
            return ResponseEntity.ok(comanda);
        } else {
            throw new DataNotFoundException("A comanda não foi encontrada");
        }
    }
}
```
### Package - model
---
> Comanda
```bash
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
```
> ComandaEItem
```bash
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
```
> Item
```bash
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
```
---
### package - repo (repository)
---
> ComandaRepo (interface)
```bash
package com.andpedroso.java.webcafe.repo;

import com.andpedroso.java.webcafe.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComandaRepo extends JpaRepository<Comanda, Long> {
    Optional<Comanda> findByMesa(int mesa);
}
```
---
### package service
---
> ComandaService
```bash
package com.andpedroso.java.webcafe.service;

import com.andpedroso.java.webcafe.model.Comanda;
import com.andpedroso.java.webcafe.repo.ComandaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ComandaService {
    @Autowired
    private ComandaRepo repo;
    @Transactional(rollbackOn = ChangeSetPersister.NotFoundException.class)
    public Comanda salvar(Comanda obj){
        return repo.save(obj);
    }
    @Transactional(rollbackOn = NoSuchElementException.class)
    public boolean remover(Long id){
        try {
            var comanda = repo.findById(id).orElseThrow();
            repo.delete(comanda);
            return true;
        } catch (NoSuchElementException ex){
            return false;
        }
    }
    public Optional<Comanda> localizar(int mesa){
        return repo.findByMesa(mesa);
    }
    public Optional<Comanda> localizar(Long idComanda){
        return repo.findById(idComanda);
    }
    public Page<Comanda> listar(Integer pageNo, Integer pageSize, String sortBy){
        var paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return repo.findAll(paging);
    }
}
```
---
### package - validator
---
> Valor (annotation)
```bash
package com.andpedroso.java.webcafe.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValorValidador.class)
public @interface Valor {
    double min() default 0d;
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```
> ValorValidador
```bash
package com.andpedroso.java.webcafe.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ValorValidador implements ConstraintValidator<Valor, BigDecimal> {
    private Valor annotation;
    @Override
    public void initialize(Valor annotation) {
        this.annotation = annotation;
    }
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        var min = BigDecimal.valueOf(annotation.min());
        if (value == null) return false;
        else return value.compareTo(min) >= 0;
    }
}
```
---
### Main
---
> WebcafeApplication
```bash
package com.andpedroso.java.webcafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class WebcafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcafeApplication.class, args);
	}

}
```
---
## Testes
---
### package controller
---
> ComandaControllerTest
```bash
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
```
---
### package service
---
> ComandaServiceTest
```bash
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
```
---
### Classe para inserir itens e estruturar os testes
---
> TesteUtil
```bash
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
```
---
### WebcafeApplicationTests
---
> ComandaApplicationTests
```bash
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
```
---
## Rodar o projeto
---
### Para rodar o projeto deve ser executada a classe main e devem ser usadas as urls comentadas em propriedades tanto para o banco de dados quanto para o Swagger.
### Para rodar os testes clicar com o direito na pasta com.andpedroso.java.webcafe e rodar todos os testes. Os testes também podem ser rodados separadamente.
---
## Créditos
---
### André Moura Pedroso
#### Web, Mobile e Games
#### SENAI e Faculdade Descomplica
### SENAI São Paulo

