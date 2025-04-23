package com.gayatech.cart_service.services;

import com.gayatech.cart_service.dtos.DTOsModels.ProductDTO;
import com.gayatech.cart_service.dtos.DTOsRequest.CartRequestDTO;
import com.gayatech.cart_service.dtos.DTOsResponse.CartResponseDTO;
import com.gayatech.cart_service.exceptions.CustomException;
import com.gayatech.cart_service.exceptions.PartialContentException;
import com.gayatech.cart_service.models.Cart;
import com.gayatech.cart_service.repositories.ICartRepository;
import com.gayatech.cart_service.repositories.IProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallbackResponseListCart")
    @Retry(name = "product-service")
    public List<CartResponseDTO> getAll() {
        //Obtenemos los Carts
        System.out.println("GET ALL");
        List<Cart> cartList = repoCart.findAll();

        System.out.println("DTO");
        List<CartResponseDTO> cartResponseDTOSList = new ArrayList<>();

        System.out.println("GET PRODUCTS ID LIST");
        //obtenemos por Cart la lista de longs de productos
        cartList.forEach(cart -> {
                    //le pasamos a products-service cada lista de longs para obtener la lista de productos
                    List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(new CartRequestDTO(cart.getIdProductList()));

                    //mapeamos cada DTO y guardamos las listas
                    CartResponseDTO cartDTO = new CartResponseDTO(cart.getId(), productDTOList);
                    cartResponseDTOSList.add(cartDTO);
                });

        System.out.println("RETURN");
        //retornar el list de CartDTOs
        return cartResponseDTOSList;
    }

    @Override
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallbackResponseCart")
    @Retry(name = "product-service")
    public CartResponseDTO getOneCart(Long id) {
        //Buscamos el Cart y si no lo encuentra lanza exception custom manejada por controller advice
        Cart cart = repoCart.findById(id)
                .orElseThrow(() -> new CustomException("Cart not found", HttpStatus.NOT_FOUND));
        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(new CartRequestDTO(cart.getIdProductList()));

        //Instanciamos un CartResponseDTO con el id y la lista de productos y lo retornamos
        return new CartResponseDTO(cart.getId(), productDTOList);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallbackCreateCart")
    @Retry(name = "product-service")
    public CartResponseDTO createCart(CartRequestDTO cartRequestDTO) {
        //convertimos el DTO en Model y lo mandamos a guardar
        System.out.println("CREATE DTO");
        Cart cart = repoCart.save(new Cart(null, cartRequestDTO.getIdProductList()));
        System.out.println(cart.getIdProductList());
        System.out.println("SEARCH PRODUCTS");
        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(new CartRequestDTO(cart.getIdProductList()));
//        List<ProductDTO> productDTOList = List.of(
//                new ProductDTO(2L, "P001", "Laptop Gamer", "ASUS", 1500.99),
//                new ProductDTO(3L, "P002", "Smartphone", "Samsung", 799.99),
//                new ProductDTO(4L, "P003", "Auriculares Bluetooth", "Sony", 199.99)
//        );

        productDTOList.forEach(System.out::println);
        System.out.println("CREATE CART WITH PRODUCTS");
        //definimos el CartResponseDTO con el id del Cart guardado y la lista de productos
        return new CartResponseDTO(cart.getId(), productDTOList);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallbackUpdateCart")
    @Retry(name = "product-service")
    public CartResponseDTO updateCart(Long idUpdate, CartRequestDTO cartRequestDTO) {
        //Buscamos el Cart y si no lo encuentra lanza exception custom manejada por controller advice
        Cart cart = repoCart.findById(idUpdate)
                .orElseThrow(() -> new CustomException("Cart not found", HttpStatus.NOT_FOUND));

        //actualizamos la lista de longs
        cart.setIdProductList(cartRequestDTO.getIdProductList());

        //Guardamos el objeto actualizado
        Cart cartSaved = repoCart.save(cart);

        //buscamos la lista de productos llamando al "products-service" mandando la lista de longs
        List<ProductDTO> productDTOList = repoProduct.getListProductsByListId(new CartRequestDTO(cart.getIdProductList()));

        return new CartResponseDTO(cartSaved.getId(), productDTOList);
    }

    @Override
    @Transactional
    public void deleteCart(Long id) {
        if (!repoCart.existsById(id)) throw new CustomException("Cart not found", HttpStatus.NOT_FOUND);
        repoCart.deleteById(id);
    }

    public CartResponseDTO getCartResponseDTO(Cart cart){
        List<ProductDTO> productDTOList = new ArrayList<>();
        cart.getIdProductList().forEach(id -> {
            productDTOList.add(new ProductDTO(id, null, null, null, null));
        });
        return new CartResponseDTO(cart.getId(), productDTOList);
    }

    public List<CartResponseDTO> fallbackResponseListCart( Throwable e){
//        List<CartResponseDTO> cartResponseDTOList = ;
//        cartList.forEach(cart -> {
//                   CartResponseDTO cartResponseDTO = getCartResponseDTO(cart);
//                   cartResponseDTOList.add(cartResponseDTO);
//                });

        return new ArrayList<>();
    }

    public CartResponseDTO fallbackCreateCart(CartRequestDTO cartRequestDTO, Throwable e){
        System.out.println("FALLBACK → createCart");
//        CartResponseDTO cartResponseDTO = getCartResponseDTO(cartRequestDTO);
        return new CartResponseDTO(null, new ArrayList<>());
//        throw new PartialContentException(cartRequestDTO);
    }

    public CartResponseDTO fallbackUpdateCart(Long idUpdate, CartRequestDTO cartRequestDTO, Throwable e){
        System.out.println("FALLBACK → updateCart");

//        System.out.println(cart);
        return new CartResponseDTO(idUpdate, new ArrayList<>());

//        CartResponseDTO cartResponseDTO = getCartResponseDTO(cart);
//        throw new PartialContentException(cartResponseDTO);
    }

}
