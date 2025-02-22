package com.gayatech.product_service.services;

import com.gayatech.product_service.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IProductService {

    public List<Product> getAll();
}
