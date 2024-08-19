package com.techshop.admin.product;

import com.techshop.admin.AmazonS3Util;
import com.techshop.admin.FileUploadUtil;
import com.techshop.common.entity.product.Product;
import com.techshop.common.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    static void deleteExtraImagesWereRemovedOnForm(Product product) {
//        String extraImageDir = "TechShopWebParent/product-images/" + product.getId() + "/extras";
        String extraImageDir = "product-images/" + product.getId() + "/extras";
        List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

        for (String objectKey : listObjectKeys) {
            String fileName = objectKey.substring(objectKey.lastIndexOf("/") + 1);
            if (!product.containsImageName(fileName)) {
                AmazonS3Util.deleteFile(objectKey);
                LOGGER.info("Deleted extra image: " + fileName);
            }
        }
//        Path dirPath = Paths.get(extraImageDir);
//        try {
//            Files.list(dirPath).forEach(file -> {
//                String fileName = file.toFile().getName();
//                if (!product.containsImageName(fileName)) {
//                    try {
//                        Files.delete(file);
//                        LOGGER.info("Deleted extra image: " + fileName);
//                    } catch (IOException e) {
//                        LOGGER.error("Could not delete extra image: " + fileName);
//                    }
//                }
//            });
//        } catch (IOException e) {
//            LOGGER.error("Could not list directory: " + dirPath);
//
//
//        }

    }

    static void setExistingExtraImageNames(Long[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs != null) {

            Set<ProductImage> images = new HashSet<>();
            for (int i = 0; i < imageIDs.length; i++) {
                Long id = imageIDs[i];
                String name = imageNames[i];
                images.add(new ProductImage(id, name, product));
            }
            product.setProductImages(images);
        }

    }

    static void setProductDetails(Long[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            Long id = detailIDs[i];
            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    static void saveUploadedImages(MultipartFile multipartFile, MultipartFile[] multipartFiles, Product savedProduct) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String uploadDir = "product-images/" + savedProduct.getId();
            List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir+"/");

            for (String objectKey : listObjectKeys) {
                if (!objectKey.contains("/extras/")) {
                    AmazonS3Util.deleteFile(objectKey);
                    LOGGER.info("Deleted extra image: " + fileName);
                }
            }
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

//            String uploadDir = "TechShopWebParent/product-images/" + savedProduct.getId();
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        if (multipartFiles.length > 0) {
//            String uploadDir = "TechShopWebParent/product-images/" + savedProduct.getId() + "/extras";
            String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipart : multipartFiles) {
                if (multipart.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipart.getOriginalFilename()));
//                FileUploadUtil.cleanDir(uploadDir);
                AmazonS3Util.uploadFile(uploadDir, fileName, multipart.getInputStream());
//                FileUploadUtil.saveFile(uploadDir, fileName, multipart);
            }
        }

    }

    static void setNewExtraImageNames(MultipartFile[] multipartFiles, Product product) {
        if (multipartFiles.length > 0) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    static void setMainImageName(MultipartFile multipartFile, Product product) {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }
}
