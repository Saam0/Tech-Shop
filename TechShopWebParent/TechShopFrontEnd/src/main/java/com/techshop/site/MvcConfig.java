package com.techshop.site;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

//@Configuration
public class MvcConfig implements WebMvcConfigurer {
    //    @Value("${static.userPhotos.path}")
//    private  String dirName;
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        exposeDirectory("TechShopWebParent/category-images",registry);
//        exposeDirectory("TechShopWebParent/brand-logos",registry);
//        exposeDirectory("TechShopWebParent/product-images",registry);
//        exposeDirectory("TechShopWebParent/site-logo",registry);
//
//    }
//
//    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
//        Path path = Paths.get(pathPattern);
//        String absolutePath=path.toFile().getAbsolutePath();
//
//        String logicalPath = Arrays.stream(pathPattern.split("/")).reduce((first,second)->second).orElse(null) + "/**";
//        registry.addResourceHandler(logicalPath)
//                .addResourceLocations("file:/" + absolutePath + "/");
//
//
//    }

}
