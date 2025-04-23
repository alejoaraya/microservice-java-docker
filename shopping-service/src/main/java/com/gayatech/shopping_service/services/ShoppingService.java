package com.gayatech.shopping_service.services;

import com.gayatech.shopping_service.dtos.ShoppingDTO;
import com.gayatech.shopping_service.exceptions.CustomException;
import com.gayatech.shopping_service.modelmappers.ShoppingMapper;
import com.gayatech.shopping_service.models.Shopping;
import com.gayatech.shopping_service.repositories.IShoppingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingService implements IShoppingService {

    @Autowired
    private IShoppingRepository shoppingRepository;

    @Autowired
    private ShoppingMapper shoppingMapper;



    @Override
    public List<ShoppingDTO> getAll() {
        List<Shopping> entities = shoppingRepository.findAll();
        return shoppingMapper.convertToDTOList(entities);
    }

    @Override
    public ShoppingDTO getOne(Long id) {
        Shopping entity = shoppingRepository.findById(id)
                .orElseThrow(() -> new CustomException("Product ID not found", HttpStatus.NOT_FOUND));
        return shoppingMapper.convertToDTO(entity);
    }

    @Override
    @Transactional
    public ShoppingDTO create(ShoppingDTO shoppingDTO) {
        // cartId exist
        Shopping entitySaved = shoppingRepository.save(new Shopping(null, shoppingDTO.getDeliveryDate(), shoppingDTO.getCartId()));


        return shoppingMapper.convertToDTO(entitySaved);
    }

    @Override
    @Transactional
    public ShoppingDTO update(Long idUpdate, ShoppingDTO shoppingDTO) {
        Shopping existingEntity = shoppingRepository.findById(idUpdate)
                .orElseThrow(() -> new CustomException("Product ID not found", HttpStatus.NOT_FOUND));
        Shopping newEntity = new Shopping(existingEntity.getId(),existingEntity.getDeliveryDate(), shoppingDTO.getCartId());
        Shopping updatedEntity = shoppingRepository.save(newEntity);
        
        return shoppingMapper.convertToDTO(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!shoppingRepository.existsById(id)) {
            throw new CustomException("Product ID not found", HttpStatus.NOT_FOUND);
        }
        shoppingRepository.deleteById(id);
    }

    @Override
    public List<ShoppingDTO> getListProductByListID(List<Long> longList) {
        List<Shopping> entities = shoppingRepository.findAllById(longList);
        return entities.stream()
                .map(entity -> shoppingMapper.convertToDTO(entity))
                .collect(Collectors.toList());
    }
}
