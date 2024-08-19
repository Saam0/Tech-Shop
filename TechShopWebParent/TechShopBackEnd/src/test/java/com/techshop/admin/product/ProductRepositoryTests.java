package com.techshop.admin.product;

import com.techshop.common.entity.Brand;
import com.techshop.common.entity.Category;
import com.techshop.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 11L);
        Category category = entityManager.find(Category.class, 18L);

        Product product = new Product();
        product.setName("HP Pavilon++");
        product.setAlias("HP_pavilon++");
        product.setShortDescription("Short description for HP Pavilon");
        product.setFullDescription("full description for HP Pavilon");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(650);
        product.setCost(268);
        product.setEnabled(true);
        product.setInStock(true);


        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProduct() {
        List<Product> productList = productRepository.findAll();

        productList.forEach(System.out::println);
    }

    @Test
    public void testGetProduct() {
        Long id = 2L;
//        Product product = productRepository.findById(id).get();
        Product product = productRepository.getById(id);
        System.err.println(product);
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Long id = 2L;
//        Product product = productRepository.findById(id).get();
        Product product = productRepository.getById(id);
        product.setPrice(601);
        productRepository.save(product);

        Product savedProduct = entityManager.find(Product.class, id);
        assertThat(savedProduct.getPrice()).isEqualTo(601);
    }

    @Test
    public void testDeleteProduct() {
        Long id = 6L;
        productRepository.deleteById(id);
        Optional<Product> product = productRepository.findById(id);
        assertThat(product).isEmpty();
    }

    @Test
    public void testSaveProductWithImages(){
        Long id = 6L;
        Product product = productRepository.findById(id).get();
        product.setMainImage("mainImage.jpg");
        product.addExtraImage("extra_image_1.jpg");
        product.addExtraImage("extra_image_2.jpg");
        product.addExtraImage("extra_image_3.jpg");
        product.addExtraImage("extra_image_4.jpg");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getProductImages().size()).isEqualTo(4);
    }
    @Test
    public void testSaveProductWithDetails() {
        Long id = 11L;
        Product product = productRepository.findById(id).get();
        product.addDetail("Device memory", "128 GB");
        product.addDetail("CPU model", "MediaTek");
        product.addDetail("OS", "Windows 11");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getProductDetails().size()).isEqualTo(3);
    }
}
