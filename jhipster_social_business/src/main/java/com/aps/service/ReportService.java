package com.aps.service;

import com.aps.domain.CustomerOrder;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.repository.CustomerOrderRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final CustomerOrderRepository orderRepository;

    public ReportService(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public byte[] generateTodaysOrdersReport(String issueToName) {
        log.info("Generating Today's Orders Report...");
        try {
            LocalDate today = LocalDate.now();
            Instant start = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant end = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

            List<CustomerOrder> orders = orderRepository.findAllByOrderTimeBetween(start, end);
            return generateReport(orders, "Today's Orders", issueToName);
        } catch (Exception e) {
            log.error("Failed to generate Today's Orders report", e);
            throw new RuntimeException("Report generation failed", e);
        }
    }

    public byte[] generateCompletedOrdersReport(String issueToName) {
        log.info("Generating Completed Orders Report...");
        try {
            List<CustomerOrder> orders = orderRepository.findAllByStatus(OrderStatus.ORDER_DELIVERED_SUCESSFULLY);
            return generateReport(orders, "Completed Orders", issueToName);
        } catch (Exception e) {
            log.error("Failed to generate Completed Orders report", e);
            throw new RuntimeException("Report generation failed", e);
        }
    }

    public byte[] generateUnpaidOrdersReport(String issueToName) {
        log.info("Generating Unpaid Orders Report...");
        try {
            // Assuming DELIVERY_ONWAY + ORDER_NOT_TAKEN acts as proxy for unpaid/active
            // orders for now
            List<CustomerOrder> orders = orderRepository.findAllByStatus(OrderStatus.ORDER_FAILED);
            return generateReport(orders, "Unpaid Orders (Failed)", issueToName);
        } catch (Exception e) {
            log.error("Failed to generate Unpaid Orders report", e);
            throw new RuntimeException("Report generation failed", e);
        }
    }

    private byte[] generateReport(List<CustomerOrder> orders, String title, String issueToName)
            throws JRException, FileNotFoundException {
        if (orders == null || orders.isEmpty()) {
            return null;
        }

        // Calculate Totals
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalUnpaid = BigDecimal.ZERO;

        for (CustomerOrder order : orders) {
            if (order.getStatus() == OrderStatus.ORDER_DELIVERED_SUCESSFULLY) {
                totalPaid = totalPaid.add(order.getTotalAmount());
            } else {
                totalUnpaid = totalUnpaid.add(order.getTotalAmount());
            }
        }

        // Load .jrxml template
        File file = ResourceUtils.getFile("classpath:templates/reports/orders.jrxml");
        JasperDesign design = JRXmlLoader.load(file);
        JasperReport report = JasperCompileManager.compileReport(design);

        // Parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", title);
        parameters.put("IssueTo", "Accounts Team / " + issueToName);
        parameters.put("InvoiceNo", "INV-" + System.currentTimeMillis());
        parameters.put("DateIssued", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        parameters.put("TotalPaid", totalPaid);
        parameters.put("TotalUnpaid", totalUnpaid);

        // Load Logos
        try {
            parameters.put("BrandLogo", this.getClass().getResourceAsStream("/images/brandlogo.png"));
            parameters.put("FooterLogo", this.getClass().getResourceAsStream("/images/footerlogo.jpg"));
        } catch (Exception e) {
            log.warn("Could not load logo images", e);
        }

        // DataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);

        // Fill Report
        JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(print);
    }
}
