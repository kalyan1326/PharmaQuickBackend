package store.pharmaquick.product.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import store.pharmaquick.product.dto.ImageUploadResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class ImageKitService {

    @Value("${imagekit.private-key}")
    private String privateKey;

    @Value("${imagekit.url-endpoint}")
    private String urlEndpoint;

    private static final String IMAGEKIT_UPLOAD_URL =
            "https://upload.imagekit.io/api/v1/files/upload";

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg",
            "image/png",
            "image/webp"
    };

    private final RestTemplate restTemplate =
            new RestTemplate();


    // =========================
    // UPLOAD IMAGE
    // =========================

    public ImageUploadResponse uploadImage(
            MultipartFile file,
            String category) throws IOException {

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Image file is required"
            );
        }

        // Validate category
        if (category == null || category.isBlank()) {
            throw new RuntimeException(
                    "Product category is required"
            );
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(
                    "Image size must not exceed 5 MB"
            );
        }

        // Validate file type
        String contentType = file.getContentType();

        boolean allowedType = false;

        for (String allowedContentType :
                ALLOWED_CONTENT_TYPES) {

            if (allowedContentType.equalsIgnoreCase(
                    contentType)) {

                allowedType = true;
                break;
            }
        }

        if (!allowedType) {
            throw new RuntimeException(
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }

        // Convert image to Base64
        String encodedFile =
                Base64.getEncoder()
                        .encodeToString(file.getBytes());

        // ImageKit authentication
        String auth =
                Base64.getEncoder()
                        .encodeToString(
                                (privateKey + ":")
                                        .getBytes(
                                                StandardCharsets.UTF_8
                                        )
                        );

        // Create category folder
        String folder =
                "/pharmaquick/products/" +
                        createFolderName(category);

        // HTTP headers
        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        headers.set(
                "Authorization",
                "Basic " + auth
        );

        // Request body
        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add(
                "file",
                encodedFile
        );

        body.add(
                "fileName",
                file.getOriginalFilename()
        );

        body.add(
                "folder",
                folder
        );

        // HTTP request
        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        // Send request to ImageKit
        Map<String, Object> response =
                restTemplate.postForObject(
                        IMAGEKIT_UPLOAD_URL,
                        request,
                        Map.class
                );

        // Validate ImageKit response
        if (response == null ||
                !response.containsKey("url") ||
                !response.containsKey("fileId")) {

            throw new RuntimeException(
                    "Image upload failed"
            );
        }

        // Get ImageKit URL
        String imageUrl =
                response.get("url").toString();

        // Get ImageKit file ID
        String imageFileId =
                response.get("fileId").toString();

        // Return both values
        return new ImageUploadResponse(
                imageUrl,
                imageFileId
        );
    }

    // =========================
    //      DELETE IMAGE
    // =========================

    public void deleteImage(String imageFileId) {

        if (imageFileId == null || imageFileId.isBlank()) {
            return;
        }

        String auth =
                Base64.getEncoder()
                        .encodeToString(
                                (privateKey + ":")
                                        .getBytes(
                                                StandardCharsets.UTF_8
                                        )
                        );

        HttpHeaders headers = new HttpHeaders();

        headers.set(
                "Authorization",
                "Basic " + auth
        );

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        String deleteUrl =
                "https://api.imagekit.io/v1/files/"
                        + imageFileId;

        try {

            restTemplate.exchange(
                    deleteUrl,
                    org.springframework.http.HttpMethod.DELETE,
                    request,
                    Void.class
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Failed to delete image from ImageKit"
            );
        }
    }


    // =========================
    // CREATE FOLDER NAME
    // =========================

    private String createFolderName(
            String category) {

        return category
                .trim()
                .toLowerCase()
                .replaceAll(
                        "[^a-z0-9]+",
                        "-"
                );
    }
}