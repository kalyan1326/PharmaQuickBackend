package store.pharmaquick.product.service;

import store.pharmaquick.exception.ResourceNotFoundException;
import store.pharmaquick.product.dto.ProductRequest;
import store.pharmaquick.product.dto.ProductResponse;
import store.pharmaquick.product.entity.Product;
import store.pharmaquick.product.repository.ProductRepository;
import store.pharmaquick.product.dto.ImageUploadResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageKitService imageKitService;

    public ProductService(
            ProductRepository productRepository,
            ImageKitService imageKitService) {

        this.productRepository = productRepository;
        this.imageKitService = imageKitService;
    }


    // =========================
    // CREATE PRODUCT
    // =========================

    public ProductResponse createProduct(
            ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());


        // =========================
        // UPLOAD IMAGE
        // =========================

        MultipartFile image = request.getImage();

        if (image != null && !image.isEmpty()) {

            try {

                ImageUploadResponse uploadResponse =
                        imageKitService.uploadImage(
                                image,
                                request.getCategory()
                        );

                product.setImageUrl(
                        uploadResponse.getImageUrl()
                );

                product.setImageFileId(
                        uploadResponse.getImageFileId()
                );

            } catch (IOException exception) {

                throw new RuntimeException(
                        "Product image upload failed"
                );
            }
        }


        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }


    // =========================
    // GET PRODUCT BY ID
    // =========================

    public ProductResponse getProductById(
            Long productId) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        return mapToResponse(product);
    }


    // =========================
    // GET ALL PRODUCTS
    // =========================

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
// UPDATE PRODUCT
// =========================

    public ProductResponse updateProduct(
            Long productId,
            ProductRequest request) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        // Save old ImageKit file ID
        String oldImageFileId =
                product.getImageFileId();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());


        // =========================
        // NEW IMAGE
        // =========================

        MultipartFile image =
                request.getImage();

        if (image != null && !image.isEmpty()) {

            try {

                ImageUploadResponse uploadResponse =
                        imageKitService.uploadImage(
                                image,
                                request.getCategory()
                        );

                product.setImageUrl(
                        uploadResponse.getImageUrl()
                );

                product.setImageFileId(
                        uploadResponse.getImageFileId()
                );

            } catch (IOException exception) {

                throw new RuntimeException(
                        "Product image upload failed"
                );
            }
        }


        // =========================
        // SAVE PRODUCT
        // =========================

        Product updatedProduct =
                productRepository.save(product);


        // =========================
        // DELETE OLD IMAGE
        // =========================

        if (image != null &&
                !image.isEmpty() &&
                oldImageFileId != null &&
                !oldImageFileId.isBlank()) {

            imageKitService.deleteImage(
                    oldImageFileId
            );
        }

        return mapToResponse(updatedProduct);
    }


    // =========================
    // DELETE PRODUCT
    // =========================

    public void deleteProduct(
            Long productId) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        // Get ImageKit file ID
        String imageFileId =
                product.getImageFileId();


        // Delete product from database
        productRepository.delete(product);


        // Delete image from ImageKit
        if (imageFileId != null &&
                !imageFileId.isBlank()) {

            imageKitService.deleteImage(
                    imageFileId
            );
        }
    }


    // =========================
    // GET PRODUCTS BY CATEGORY
    // =========================

    public List<ProductResponse> getProductsByCategory(
            String category) {

        return productRepository
                .findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // SEARCH PRODUCTS
    // =========================

    public List<ProductResponse> searchProducts(
            String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // GET PRODUCTS BY BRAND
    // =========================

    public List<ProductResponse> getProductsByBrand(
            String brand) {

        return productRepository
                .findByBrand(brand)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private ProductResponse mapToResponse(
            Product product) {

        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getImageFileId(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}