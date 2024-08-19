package com.techshop.admin.brand;

import com.techshop.admin.AbstractExporter;
import com.techshop.common.entity.Brand;
import com.techshop.common.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {


    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response,"text/csv",".csv","brands_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader={"Brand ID", "Brand name", "Categories name"};
        String[] fieldMapping = {"id","name","categories"};

        csvBeanWriter.writeHeader(csvHeader);

        for (Brand brand: brandList){
            csvBeanWriter.write(brand,fieldMapping);
        }
        csvBeanWriter.close();
    }
}
