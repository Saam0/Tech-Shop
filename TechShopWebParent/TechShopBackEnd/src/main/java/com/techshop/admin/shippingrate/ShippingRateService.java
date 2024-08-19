package com.techshop.admin.shippingrate;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.product.ProductRepository;
import com.techshop.admin.setting.country.CountryRepository;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.ShippingRate;
import com.techshop.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShippingRateService {
    public static final int RATES_PER_PAGE = 10;
    public final static int DIM_DIVISOR = 139;


    @Autowired
    private ShippingRateRepository shippingRateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ProductRepository productRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, RATES_PER_PAGE, shippingRateRepository);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {

        ShippingRate rateInDB = shippingRateRepository.findByCountryIdAndState(rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = (rateInForm.getId() == null && rateInDB != null);
        boolean foundDifferentExistingRateInEditMode = (rateInForm.getId() != null && rateInDB != null && rateInDB.getId() != rateInForm.getId());

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There is already a rate with the same country: " + rateInForm.getCountry().getName() +
                    " and state: " + rateInForm.getState());
        }
        shippingRateRepository.save(rateInForm);
    }

    public void updateCODSupport(Long id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with ID " + id);
        }
        shippingRateRepository.updateCODSupport(id, codSupported);
    }

    public void delete(Long id) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with ID " + id);
        }
        shippingRateRepository.deleteById(id);
    }

    public ShippingRate get(Long id) throws ShippingRateNotFoundException {
        return shippingRateRepository.findById(id)
                .orElseThrow(() -> new ShippingRateNotFoundException("Could not find any shipping rate with ID " + id));
    }

    public float calculateShippingCost(Long productId, Long countryId, String state) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryIdAndState(countryId, state);
        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("No shipping rate found for country id: " + countryId + " and state: " + state + ". " +
                    "You have to enter shipping cost manually.");
        }

        Product product = productRepository.findById(productId).get();
        float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
        float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

        return finalWeight * shippingRate.getRate();
    }
}
