package com.techshop.site.shoppingcart;

import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.product.Product;
import com.techshop.site.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Long productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem item = cartItemRepository.findByCustomerAndProduct(customer, product);
        if (item != null){
            updatedQuantity= item.getQuantity() + quantity;

            if (updatedQuantity > 5) {
                throw new ShoppingCartException("Could not add more " +quantity+ " items of this product because it exceeds the limit of 5 items.");
            }
        }else {
            item = new CartItem();
            item.setProduct(product);
            item.setCustomer(customer);
        }

        item.setQuantity(updatedQuantity);
        cartItemRepository.save(item);
        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer){
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Long productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).get();
        float subtotal = quantity * product.getDiscountPrice();
        return subtotal;
    }

    public void removeProduct(Long productId, Customer customer) {
        cartItemRepository.deleteCartItemByCustomer_IdAndProduct_Id(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteCartItemByCustomer_Id(customer.getId());
    }
}
