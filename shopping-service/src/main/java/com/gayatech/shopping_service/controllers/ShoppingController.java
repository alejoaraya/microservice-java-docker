package com.gayatech.shopping_service.controllers;

import com.gayatech.shopping_service.models.Shopping;
import com.gayatech.shopping_service.repositories.IShoppingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    public IShoppingRepository shoppingRepository;

    @GetMapping
    public List<Shopping> helloWorld () {
        return shoppingRepository.findAll();
    }

}
