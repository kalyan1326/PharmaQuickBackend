package store.pharmaquick.payment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import store.pharmaquick.payment.dto.CreatePaymentRequest;
import store.pharmaquick.payment.dto.PaymentOrderResponse;
import store.pharmaquick.payment.dto.VerifyPaymentRequest;
import store.pharmaquick.payment.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    // =========================================================
    // Create Razorpay Order
    // =========================================================

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            org.springframework.security.core.Authentication authentication
    ) {

        try {

            String userId = authentication.getName();

            PaymentOrderResponse response =
                    paymentService.createPaymentOrder(
                            userId,
                            request
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "Unable to create payment order"
                    ));
        }
    }


    // =========================================================
    // Verify Razorpay Payment
    // =========================================================

    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            org.springframework.security.core.Authentication authentication
    ) {

        try {

            String userId = authentication.getName();

            PaymentOrderResponse response =
                    paymentService.verifyPayment(
                            userId,
                            request
                    );

            return ResponseEntity
                    .ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "Unable to verify payment"
                    ));
        }
    }
}