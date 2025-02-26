package com.gayatech.product_service.services;

import com.gayatech.product_service.dtos.ProductDTO;
import com.gayatech.product_service.exceptions.CustomException;
import com.gayatech.product_service.models.Product;
import com.gayatech.product_service.repositories.IProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService implements IProductService{

    @Autowired
    private IProductRepository productRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<Product> getAll() {
        try{
            return productRepository.findAll();
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Product getOneProduct(Long id) {
        try{
            if (productRepository.existsById(id)){
                return productRepository.findById(id).get();
            }else {
                throw new CustomException("Id not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        try {
            Product newProduct = modelMapper.map(productDTO, Product.class);

            Product productSaved = productRepository.save(newProduct);
            return productSaved;
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long idUpdate, ProductDTO productDTO) {
        try{
            if (productRepository.existsById(idUpdate)){
                this.createProduct(productDTO);
                return this.getOneProduct(idUpdate);
            }else {
                throw new CustomException("Id not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        try{
            if(productRepository.existsById(id)){
                productRepository.deleteById(id);
            }else {
                throw new CustomException("Id not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
