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
 * Strategy for handling Razorpay Payment Links.
 */
@Component("razorpayLinkStrategy")
public class RazorpayLinkStrategy implements PaymentStrategy {

    private final RazorpayService razorpayService;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService customerMessageService;
    private final DeliveryPersonMessageService deliveryPersonMessageService;

    public RazorpayLinkStrategy(
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

        // Generate Link
        String link = razorpayService.createPaymentLink(order.getId(), order.getTotalAmount().doubleValue(), customer);

        if (link != null) {
            // Send to Delivery Person
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), "🔗 *Payment Link:*\n" + link);

            // Send to Customer
            String message = customerMessageService.getPaymentLinkMessage(link, order.getTotalAmount().doubleValue(),
                    deliveryPerson.getName());
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), message);

            // Send Wait Message to Delivery Person
            String waitMsg = deliveryPersonMessageService.getPaymentWaitMessageLink();
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), waitMsg);
        } else {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), "⚠️ Failed to generate Payment Link.");
        }
    }

    @Override
    public com.aps.domain.enumeration.PaymentMode getPaymentMode() {
        return com.aps.domain.enumeration.PaymentMode.LINK;
    }
}
