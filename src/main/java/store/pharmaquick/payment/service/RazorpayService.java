package store.pharmaquick.payment.service;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String keySecret;


    // =========================================================
    // Constructor
    // =========================================================

    public RazorpayService(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret
    ) throws Exception {

        this.razorpayClient = new RazorpayClient(
                keyId,
                keySecret
        );
    }


    // =========================================================
    // Create Razorpay Order
    // =========================================================

    /**
     * Creates an order in Razorpay Test Mode.
     *
     * @param amount  Amount in INR
     * @param receipt Unique receipt/reference
     * @return Razorpay Order
     */
    public com.razorpay.Order createRazorpayOrder(
            BigDecimal amount,
            String receipt
    ) throws Exception {

        // -----------------------------------------------------
        // 1. Validate amount
        // -----------------------------------------------------

        if (amount == null) {
            throw new IllegalArgumentException(
                    "Payment amount cannot be null"
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }


        // -----------------------------------------------------
        // 2. Convert INR to paise
        // -----------------------------------------------------

        /*
         * Razorpay expects amount in paise.
         *
         * ₹1 = 100 paise
         *
         * Example:
         *
         * ₹499
         * ↓
         * 499 × 100
         * ↓
         * 49900 paise
         */

        long amountInPaise = amount
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();


        // -----------------------------------------------------
        // 3. Create Razorpay request
        // -----------------------------------------------------

        JSONObject orderRequest = new JSONObject();

        orderRequest.put(
                "amount",
                amountInPaise
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                receipt
        );


        // -----------------------------------------------------
        // 4. Send request to Razorpay
        // -----------------------------------------------------

        return razorpayClient
                .orders
                .create(orderRequest);
    }


    // =========================================================
    // Verify Razorpay Payment Signature
    // =========================================================

    /**
     * Verifies the Razorpay payment signature.
     *
     * Razorpay signature is generated using:
     *
     * HMAC-SHA256(
     *      razorpayOrderId + "|" + razorpayPaymentId,
     *      keySecret
     * )
     *
     * @param razorpayOrderId   Razorpay Order ID
     * @param razorpayPaymentId Razorpay Payment ID
     * @param razorpaySignature  Signature returned by Razorpay
     * @return true if signature is valid
     */
    public boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {

        try {

            // -------------------------------------------------
            // 1. Validate input
            // -------------------------------------------------

            if (razorpayOrderId == null
                    || razorpayOrderId.isBlank()) {

                return false;
            }

            if (razorpayPaymentId == null
                    || razorpayPaymentId.isBlank()) {

                return false;
            }

            if (razorpaySignature == null
                    || razorpaySignature.isBlank()) {

                return false;
            }


            // -------------------------------------------------
            // 2. Create Razorpay signature payload
            // -------------------------------------------------

            String payload =
                    razorpayOrderId
                            + "|"
                            + razorpayPaymentId;


            // -------------------------------------------------
            // 3. Generate signature using our secret
            // -------------------------------------------------

            String generatedSignature =
                    hmacSha256(
                            payload,
                            keySecret
                    );


            // -------------------------------------------------
            // 4. Compare signatures
            // -------------------------------------------------

            return generatedSignature.equals(
                    razorpaySignature
            );

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // HMAC SHA-256
    // =========================================================

    /**
     * Generates HMAC-SHA256 hash.
     */
    private String hmacSha256(
            String data,
            String secret
    ) throws Exception {

        Mac mac = Mac.getInstance(
                "HmacSHA256"
        );


        SecretKeySpec secretKey =
                new SecretKeySpec(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );


        mac.init(secretKey);


        byte[] hash =
                mac.doFinal(
                        data.getBytes(
                                StandardCharsets.UTF_8
                        )
                );


        // -----------------------------------------------------
        // Convert byte[] to hexadecimal string
        // -----------------------------------------------------

        StringBuilder hexString =
                new StringBuilder();


        for (byte b : hash) {

            String hex =
                    Integer.toHexString(
                            0xff & b
                    );

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }


        return hexString.toString();
    }
}