package com.techshop.admin.brand;

import com.techshop.common.entity.Brand;
import com.techshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testCreateBrand1(){
        Category laptops = new Category(19L);
        Brand acer=new Brand("Acer");
        acer.getCategories().add(laptops);

        Brand savedBrand = brandRepository.save(acer);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2(){
        Category cellphones = new Category(8L);
        Category tablets = new Category(20L);
        Brand apple =new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(apple);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getCategories().size()).isEqualTo(2);
    }

    @Test
    public void testCreateBrand3(){
        Brand samsung =new Brand("Samsung");

        samsung.getCategories().add(new Category(29L));
        samsung.getCategories().add(new Category(24L));

        Brand savedBrand = brandRepository.save(samsung);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getCategories().size()).isEqualTo(2);
    }

    @Test
    public void testFindAllBrands(){
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);
        assertThat(brands).isNotEmpty();
    }


    @Test
    public void testGetById(){
        Brand brand = brandRepository.getById(1L);
        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    public void testUpdateName(){
        String newName = "Samsung Electronics";
        Brand samsung = brandRepository.getById(4L);
        samsung.setName(newName);

        Brand savedBrand = brandRepository.save(samsung);
        assertThat(savedBrand.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete(){
        Long id = 2L;
        brandRepository.deleteById(id);

        Optional<Brand> result = brandRepository.findById(id);

        assertThat(result).isEmpty();


    }
}
