package com.techshop.admin;

import com.techshop.admin.paging.PagingAndSortingArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    //    @Value("${static.userPhotos.path}")
//    private  String dirName;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//        exposeDirectory("user-photos",registry);
//        exposeDirectory("TechShopWebParent/category-images",registry);
//        exposeDirectory("TechShopWebParent/brand-logos",registry);
//        exposeDirectory("TechShopWebParent/product-images",registry);
//        exposeDirectory("TechShopWebParent/site-logo",registry);


    }

//    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
//        Path path = Paths.get(pathPattern);
//        String absolutePath=path.toFile().getAbsolutePath();
//
////        String logicalPath = pathPattern.split("/")[pathPattern.split("/").length-1] + "/**";
//        String logicalPath = Arrays.stream(pathPattern.split("/")).reduce((first,second)->second).orElse(null) + "/**";
//        registry.addResourceHandler(logicalPath)
//                .addResourceLocations("file:/" + absolutePath + "/");
//
//
//    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}
