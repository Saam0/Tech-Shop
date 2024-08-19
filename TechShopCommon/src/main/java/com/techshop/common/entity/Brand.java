package com.techshop.common.entity;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.techshop.common.Constants.S3_BASE_URI;

@Entity
@Table(name = "brands")
public class Brand  extends IdBasedEntity{
    @Column(name = "name", nullable = false, unique = true, length = 45)
    private String name;

    @Column(name = "logo", length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "categories_id"))
    private Set<Category> categories = new LinkedHashSet<>();

    public Brand() {
    }

    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Transient
    public String getLogoPath() {
        if (this.logo == null || this.logo.equals("")) return "/images/image-thumbnail.png";

        return S3_BASE_URI +"/brand-logos/" + this.id + "/" + this.logo;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", categories=" + categories +
                '}';
    }
}
