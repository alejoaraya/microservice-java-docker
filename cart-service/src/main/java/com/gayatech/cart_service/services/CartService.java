package com.gayatech.cart_service.services;

import com.gayatech.cart_service.dtos.DTOsModels.ProductDTO;
import com.gayatech.cart_service.dtos.DTOsRequest.CartRequestDTO;
import com.gayatech.cart_service.dtos.DTOsResponse.CartResponseDTO;
import com.gayatech.cart_service.exceptions.CustomException;
import com.gayatech.cart_service.models.Cart;
import com.gayatech.cart_service.repositories.ICartRepository;
import com.gayatech.cart_service.repositories.IProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService implements ICartService{

    private final ICartRepository repoCart;
    private final IProductRepository repoProduct;

    public CartService(ICartRepository repoCart, IProductRepository repoProduct){
        this.repoCart = repoCart;
        this.repoProduct = repoProduct;
    }

    @Override
    public List<CartResponseDTO> getAll() {
        //Obtenemos los Carts
        List<Cart> cartList = repoCart.findAll();

        List<CartResponseDTO> cartResponseDTOSList = new ArrayList<>();

        //obtenemos por Cart la lista de longs de productos
        cartList.forEach(cart -> {
                    //le pasamos a products-service cada lista de longs para obtener la lista de productos
                    List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(cart.getIdProductList());

                    //mapeamos cada DTO y guardamos las listas
                    CartResponseDTO cartDTO = new CartResponseDTO(cart.getId(), productDTOList);
                    cartResponseDTOSList.add(cartDTO);
                });

        //retornar el list de CartDTOs
        return cartResponseDTOSList;
    }

    @Override
    public CartResponseDTO getOneCart(Long id) {
        //Buscamos el Cart y si no lo encuentra lanza exception custom manejada por controller advice
        Cart cart = repoCart.findById(id)
                .orElseThrow(() -> new CustomException("Cart not found", HttpStatus.NOT_FOUND));
        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(cart.getIdProductList());

        //Instanciamos un CartResponseDTO con el id y la lista de productos y lo retornamos
        return new CartResponseDTO(cart.getId(), productDTOList);
    }

    @Override
    public CartResponseDTO createCart(CartRequestDTO cartRequestDTO) {
        //convertimos el DTO en Model y lo mandamos a guardar
        Cart cart = repoCart.save(new Cart(null, cartRequestDTO.getIdProductList()));

        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(cart.getIdProductList());

        //definimos el CartResponseDTO con el id del Cart guardado y la lista de productos
        return new CartResponseDTO(cart.getId(), productDTOList);
    }

    @Override
    public CartResponseDTO updateCart(Long idUpdate, CartRequestDTO cartRequestDTO) {
        //Buscamos el Cart y si no lo encuentra lanza exception custom manejada por controller advice
        Cart cart = repoCart.findById(idUpdate)
                .orElseThrow(() -> new CustomException("Cart not found", HttpStatus.NOT_FOUND));

        //actualizamos la lista de longs
        cart.setIdProductList(cartRequestDTO.getIdProductList());

        //Guardamos el objeto actualizado
        Cart cartSaved = repoCart.save(cart);

        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(cartSaved.getIdProductList());

        return new CartResponseDTO(cartSaved.getId(), productDTOList);
    }

    @Override
    public void deleteCart(Long id) {
        if (repoCart.existsById(id)) throw new CustomException("Cart not Found", HttpStatus.NOT_FOUND);
        repoCart.deleteById(id);
    }
}
