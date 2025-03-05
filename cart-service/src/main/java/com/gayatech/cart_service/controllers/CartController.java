package com.gayatech.cart_service.controllers;

import com.gayatech.cart_service.dtos.DTOsRequest.CartRequestDTO;
import com.gayatech.cart_service.dtos.DTOsResponse.CartResponseDTO;
import com.gayatech.cart_service.services.ICartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CartResponseDTO>> getAllCarts(){
        return ResponseEntity.ok(cartService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> getOneCart(@PathVariable Long id){
        return ResponseEntity.ok(cartService.getOneCart(id));
    }

    @PostMapping("/")
    public ResponseEntity<CartResponseDTO> createCart(@RequestBody CartRequestDTO cartRequestDTO){
        CartResponseDTO cartResponseDTO = cartService.createCart(cartRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> updateCart(@PathVariable Long id, @RequestBody CartRequestDTO cartRequestDTO){
        return ResponseEntity.ok(cartService.updateCart(id, cartRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CartResponseDTO> deleteCart(@PathVariable Long id){
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
