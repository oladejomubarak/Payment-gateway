/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/2/24, 2:24 PM
 *  
 */

package com.celertech.webpay.controller;


import com.celertech.webpay.dto.request.AuthorizeIswRequest;
import com.celertech.webpay.dto.request.InitiateIswWebPayRequest;
import com.celertech.webpay.dto.request.IswResendOtpRequest;
import com.celertech.webpay.service.WebPayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/webpay")
public class WebPayController {

    private final WebPayService webPayService;


    @PostMapping("/initiatePayment")
    public ResponseEntity<?> initiatePayment (@RequestBody @Valid InitiateIswWebPayRequest initiateIswWebPayRequest){

        return ResponseEntity.ok(webPayService.initiatePayment(initiateIswWebPayRequest));
    }

    @PostMapping("/authorizeTransaction")
    public ResponseEntity<?> authorizeTransaction (@RequestBody @Valid AuthorizeIswRequest verifyIswOtpRequest){

        return ResponseEntity.ok(webPayService.authorizeTransaction(verifyIswOtpRequest));
    }


    @PostMapping("/resendOtp")
    public ResponseEntity<?> resendOtp (@RequestBody @Valid IswResendOtpRequest resendOtpRequest){

        return ResponseEntity.ok(webPayService.resendOtp(resendOtpRequest));
    }

    @GetMapping("/status/verify")
    public ResponseEntity<?> statusVerify (@RequestParam String tranReference, @RequestParam String amount){

        return ResponseEntity.ok(webPayService.statusVerify(tranReference, amount));
    }
}
