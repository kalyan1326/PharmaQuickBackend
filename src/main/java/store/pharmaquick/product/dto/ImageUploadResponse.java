package store.pharmaquick.product.dto;

public class ImageUploadResponse {

    private String imageUrl;
    private String imageFileId;

    public ImageUploadResponse() {
    }

    public ImageUploadResponse(
            String imageUrl,
            String imageFileId) {

        this.imageUrl = imageUrl;
        this.imageFileId = imageFileId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFileId() {
        return imageFileId;
    }

    public void setImageFileId(String imageFileId) {
        this.imageFileId = imageFileId;
    }
}