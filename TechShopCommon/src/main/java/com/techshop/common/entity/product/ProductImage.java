package com.techshop.common.entity.product;

import com.techshop.common.entity.IdBasedEntity;

import javax.persistence.*;

import static com.techshop.common.Constants.S3_BASE_URI;

@Entity
@Table(name = "product_image")
public class ProductImage extends IdBasedEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage() {
    }

    public ProductImage(Long id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public String getImagePath() {
        return S3_BASE_URI +"/product-images/" + getProduct().getId() + "/extras/" + this.name;
    }
}
