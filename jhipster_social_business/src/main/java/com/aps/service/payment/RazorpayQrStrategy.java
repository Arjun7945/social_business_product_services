package com.aps.service.payment;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.TeamMember;
import com.aps.service.WhatsAppService;
// import com.aps.service.DeliveryFlowService; // Circular dependency if used
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Strategy for handling Razorpay QR Code payments.
 */
@Component("razorpayQrStrategy")
public class RazorpayQrStrategy implements PaymentStrategy {

    private final RazorpayService razorpayService;
    private final WhatsAppService whatsAppService;
    private final com.aps.service.CustomerMessageService customerMessageService;
    private final com.aps.service.DeliveryPersonMessageService deliveryPersonMessageService;

    public RazorpayQrStrategy(RazorpayService razorpayService, @Lazy WhatsAppService whatsAppService,
            com.aps.service.CustomerMessageService customerMessageService,
            com.aps.service.DeliveryPersonMessageService deliveryPersonMessageService) {
        this.razorpayService = razorpayService;
        this.whatsAppService = whatsAppService;
        this.customerMessageService = customerMessageService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
    }

    @Override
    public void initiatePayment(CustomerOrder order, TeamMember deliveryPerson) {
        Customer customer = order.getCustomer();

        // Create Razorpay Customer
        String razorpayCustId = razorpayService.createCustomer(customer.getName(),
                customer.getPhoneNumber());

        // Generate QR
        String qrUrl = razorpayService.createQrCode(order.getId(),
                order.getTotalAmount().doubleValue(), razorpayCustId);

        if (qrUrl != null) {
            // Send to Delivery Person
            whatsAppService.sendImageMessage(deliveryPerson.getWaPhoneNumber(),
                    qrUrl, "📷 Scan to Pay");

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
    public String getPaymentMethodName() {
        return "QR";
    }
}
