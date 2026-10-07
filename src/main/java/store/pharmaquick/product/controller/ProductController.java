package store.pharmaquick.product.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import store.pharmaquick.product.dto.ProductRequest;
import store.pharmaquick.product.dto.ProductResponse;
import store.pharmaquick.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    // =========================
    // ADMIN - CREATE PRODUCT
    // =========================

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @ModelAttribute ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET ALL PRODUCTS
    // =========================

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }


    // =========================
    // GET PRODUCT BY ID
    // =========================

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                productService.getProductById(productId)
        );
    }


    // =========================
    // SEARCH PRODUCTS
    // =========================

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.searchProducts(name)
        );
    }


    // =========================
    // PRODUCTS BY CATEGORY
    // =========================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(category)
        );
    }


    // =========================
    // PRODUCTS BY BRAND
    // =========================

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(
            @PathVariable String brand) {

        return ResponseEntity.ok(
                productService.getProductsByBrand(brand)
        );
    }


    // =========================
    // ADMIN - UPDATE PRODUCT
    // =========================

    @PutMapping(
            value = "/{productId}",
            consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @ModelAttribute ProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        productId,
                        request
                )
        );
    }


    // =========================
    // ADMIN - DELETE PRODUCT
    // =========================

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId) {

        productService.deleteProduct(productId);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }
}