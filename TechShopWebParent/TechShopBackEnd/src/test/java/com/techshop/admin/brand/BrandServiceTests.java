package com.techshop.admin.brand;

import com.techshop.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTests {

    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void testCheckUniqueInNewModeReturnDuplicate() {
        Long id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(id, name);

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOk() {
        Long id = null;
        String name = "AMD";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicate() {
        Long id = 1L;
        String name = "Canon";
        Brand brand = new Brand(id,name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(2L, name);

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInEditModeReturnOk() {
        Long id = 1L;
        String name = "Acer";
        Brand brand = new Brand(id,name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(id, name);

        assertThat(result).isEqualTo("OK");
    }
}