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
