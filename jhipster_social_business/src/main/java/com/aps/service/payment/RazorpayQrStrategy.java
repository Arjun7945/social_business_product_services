package com.aps.service.payment;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.service.WhatsAppService;
import com.aps.service.CustomerMessageService;
import com.aps.service.DeliveryPersonMessageService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Strategy for handling Razorpay QR Code payments.
 */
@Component("razorpayQrStrategy")
public class RazorpayQrStrategy implements PaymentStrategy {

    private final RazorpayService razorpayService;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService customerMessageService;
    private final DeliveryPersonMessageService deliveryPersonMessageService;

    public RazorpayQrStrategy(
            RazorpayService razorpayService,
            @Lazy WhatsAppService whatsAppService,
            CustomerMessageService customerMessageService,
            DeliveryPersonMessageService deliveryPersonMessageService) {
        this.razorpayService = razorpayService;
        this.whatsAppService = whatsAppService;
        this.customerMessageService = customerMessageService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
    }

    @Override
    public void initiatePayment(CustomerOrder order, DeliveryPerson deliveryPerson) {
        Customer customer = order.getCustomer();

        // Create Razorpay Customer
        String razorpayCustId = razorpayService.createCustomer(customer.getName(), customer.getPhoneNumber());

        // Generate QR
        String qrUrl = razorpayService.createQrCode(order.getId(), order.getTotalAmount().doubleValue(),
                razorpayCustId);

        if (qrUrl != null) {
            // Send to Delivery Person
            whatsAppService.sendImageMessage(deliveryPerson.getWaPhoneNumber(), qrUrl, "📷 Scan to Pay");

            // Send to Customer
            String caption = customerMessageService.getPaymentQrCaption(order.getTotalAmount().doubleValue());
            whatsAppService.sendImageMessage(customer.getWaPhoneNumber(), qrUrl, caption);

            // Send Wait Message to Delivery Person
            String waitMsg = deliveryPersonMessageService.getPaymentWaitMessageQr();
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), waitMsg);
        } else {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    "⚠️ Failed to generate QR Code. (Check Settings)");
        }
    }

    @Override
    public com.aps.domain.enumeration.PaymentMode getPaymentMode() {
        return com.aps.domain.enumeration.PaymentMode.QR;
    }
}
