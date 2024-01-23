package com.cbt.paymentservicejan242;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController
{
    @Autowired
    ProductofferRepository productofferRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentwalletlinkRepository paymentwalletlinkRepository;
    @Autowired
    UsernamewalletlinkRepository usernamewalletlinkRepository;
    @Autowired
    PaymentRepository paymentRepository;

    Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("create/payment/{offerid}/{orderid}")
    public ResponseEntity<String> createPayment(@PathVariable String offerid,
                                                @PathVariable String orderid)
    {

        String sellername = productofferRepository.findById(offerid).get().getSellername();
        String buyername = orderRepository.findById(orderid).get().getBuyername();

        Paymentwalletlink paymentwalletlink = new Paymentwalletlink();
        paymentwalletlink.setLinkid(String.valueOf((int) (Math.random() * 10000)));
        paymentwalletlink.setPaymenttype("ORDER");
        paymentwalletlink.
                setPayerwallet(usernamewalletlinkRepository.findById(buyername).get().getWalletid());
        paymentwalletlink.
                setPayeewallet(usernamewalletlinkRepository.findById(sellername).get().getWalletid());
        paymentwalletlink.
                setEscrowwallet(usernamewalletlinkRepository.findById("indiagator").get().getWalletid());
        paymentwalletlink.setAmount(orderRepository.findById(orderid).get().getBid());


        Payment payment = new Payment();

        payment.setId(String.valueOf((int) (Math.random() * 10000)));
        payment.setOrderid(orderid);
        payment.setOfferid(offerid);
        payment.setStatus("DUE");
        payment.setPaymentwalletlink(paymentwalletlink.getLinkid());

        paymentwalletlink.setPaymentrefid(payment.getId());


        paymentwalletlinkRepository.save(paymentwalletlink);
        paymentRepository.save(payment);

        logger.info("payment created with status due");

        return ResponseEntity.ok("Order Accepted and Payment Created");

    }

}
