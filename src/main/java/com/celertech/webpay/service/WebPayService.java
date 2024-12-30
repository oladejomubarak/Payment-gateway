/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 8/7/24, 2:58 AM
 *
 */
package com.celertech.webpay.service;

import com.celertech.webpay.dto.request.AuthorizeIswRequest;
import com.celertech.webpay.dto.request.InitiateIswWebPayRequest;
import com.celertech.webpay.dto.request.IswResendOtpRequest;

public interface WebPayService {
    Object initiatePayment(InitiateIswWebPayRequest initiateIswWebPayRequest);

    Object authorizeTransaction(AuthorizeIswRequest verifyIswOtpRequest);

    Object statusVerify(String tranReference, String amount);

    Object resendOtp(IswResendOtpRequest resendOtpRequest);

}
