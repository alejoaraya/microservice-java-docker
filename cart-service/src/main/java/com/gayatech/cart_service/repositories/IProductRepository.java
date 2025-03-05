package com.gayatech.cart_service.repositories;

import com.gayatech.cart_service.dtos.DTOsModels.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface IProductRepository {

    @GetMapping(name = "/{idProduct}")
    public ProductDTO getProductById(@PathVariable ("idProduct") Long idProduct);

    @GetMapping(name = "/list")
    public List<ProductDTO> getListProductsByListId(@RequestBody List<Long> longList);

}
