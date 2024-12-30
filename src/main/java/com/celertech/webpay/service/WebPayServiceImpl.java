/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 8/7/24, 2:58 AM
 *
 */
package com.celertech.webpay.service;

import com.celertech.webpay.dto.response.*;
import com.google.gson.Gson;
import com.interswitch.techquest.auth.Interswitch;
import com.celertech.webpay.dto.request.*;
import com.celertech.webpay.http.HttpClient;
import com.celertech.webpay.model.WebRequests;
import com.celertech.webpay.repository.WebRequestsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPayServiceImpl implements WebPayService {

    private static String AUTH_TOKEN = "";

    private final Environment env;

    private final HttpClient httpClient;

    private final Gson gson;
    private final ModelMapper mapper;

    private final OkHttpClient client;
    private final WebRequestsRepository webRequestsRepository;

    Interswitch interswitch;


    @PostConstruct
    public void init() {
        this.interswitch = new Interswitch(env.getProperty(""), env.getProperty(""), "PRODUCTION");
    }


    @Override
    public Object initiatePayment(InitiateIswWebPayRequest initiateIswWebPayRequest) {
        IswPurchaseResponse iswPurchaseResponse = new IswPurchaseResponse();
        //todo get auth token
        IswAuthWebPayResponse iswAuthWebPayResponse = getAuthToken();
        if (Objects.isNull(iswAuthWebPayResponse)) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + iswAuthWebPayResponse.getAccessToken());

        IswPurchaseRequest iswPurchaseRequest = new IswPurchaseRequest();
        iswPurchaseRequest.setAmount(initiateIswWebPayRequest.getAmount());
        iswPurchaseRequest.setCustomerId(initiateIswWebPayRequest.getCustomerId());
        iswPurchaseRequest.setCurrency(initiateIswWebPayRequest.getCurrency());
        try {
            iswPurchaseRequest.setAuthData(getAuthData("1", initiateIswWebPayRequest.getPan(), initiateIswWebPayRequest.getPin(),
                    reverseExpiryDate(initiateIswWebPayRequest.getExpiryDate()), initiateIswWebPayRequest.getCvv2()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        iswPurchaseRequest.setTransactionRef("MW"+RandomStringUtils.randomAlphanumeric(10));

        iswPurchaseRequest.setDeviceInformation(initiateIswWebPayRequest.getDeviceInformation());
//        iswPurchaseRequest.setCallbackUrl("https://webhook.site/59d01e26-feca-408b-ab7f-d1cd0afc8a21");
        iswPurchaseRequest.setCallbackUrl(StringUtils.isEmpty(initiateIswWebPayRequest.getCallbackUrl())?"https://webhook.site/59d01e26-feca-408b-ab7f-d1cd0afc8a21":initiateIswWebPayRequest.getCallbackUrl());
       WebRequests initiateWebRequest =  mapper.map(iswPurchaseRequest, WebRequests.class);
        initiateWebRequest.setDeviceInformation(gson.toJson(iswPurchaseRequest.getDeviceInformation()));
        initiateWebRequest.setPaymentLinkId(initiateIswWebPayRequest.getPaymentLinkId());
        WebRequests webRequests = webRequestsRepository.save(initiateWebRequest);




        String reqBody = gson.toJson(iswPurchaseRequest);

        String url = env.getProperty("ISW_PURCHASEURL");
        String rspBody = "";
        String msg = "Request Failed, kindly try again";
        String code = "82";
        try {
            Response response = httpClient.post(headers, reqBody, url);
            rspBody = response.body().string();
            msg = response.message();
            code = String.valueOf(response.code());
            String rspCode  = code.equalsIgnoreCase("202")?"00":"82";
            log.info("Purchase Response:{} | {} | {}", code, msg, rspBody);
            iswPurchaseResponse = gson.fromJson(rspBody, IswPurchaseResponse.class);
            webRequests.setPaymentId(iswPurchaseResponse.getPaymentId());
            webRequests.setResponseCode(iswPurchaseResponse.getResponseCode());
            webRequests.setMd(StringUtils.isEmpty(iswPurchaseResponse.getMD())?"N/A": iswPurchaseResponse.getMD());
            webRequests.setTransactionId(StringUtils.isEmpty(iswPurchaseResponse.getTransactionId())?"N/A": iswPurchaseResponse.getTransactionId());
            webRequestsRepository.save(webRequests);
//            return rspBody;
            return new BaseResponse(rspCode,msg,iswPurchaseResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }

        iswPurchaseResponse.setResponseCode(code);
        iswPurchaseResponse.setMessage(msg);
        iswPurchaseResponse.setTransactionRef(iswPurchaseRequest.getTransactionRef());
        return new BaseResponse(code,msg,iswPurchaseResponse);
    }

    @Override
    public Object authorizeTransaction(AuthorizeIswRequest authorizeIswRequest) {
        IswAuthWebPayResponse iswAuthWebPayResponse = getAuthToken();
        if (Objects.isNull(iswAuthWebPayResponse)) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + iswAuthWebPayResponse.getAccessToken());


        WebRequests webRequests = webRequestsRepository.findFirstByPaymentId(authorizeIswRequest.getPaymentId()).orElse(null);
        String url = env.getProperty("ISW_AUTHVERIFYURL");
        if (StringUtils.isEmpty(authorizeIswRequest.getOtp())) {
            assert webRequests != null;
            authorizeIswRequest.setAuthData(webRequests.getAuthData());
            authorizeIswRequest.setEciFlag("07");
        }
        String reqBody = gson.toJson(authorizeIswRequest);
        IswAuthorizeResponse iswAuthorizeResponse = new IswAuthorizeResponse();
        String rspBody = "";
        String msg = "Request Failed, kindly try again";
        String code = "82";
        try {
            Response response = httpClient.post(headers, reqBody, url);
            rspBody = response.body().string();
            msg = response.message();
            code = String.valueOf(response.code());
            String rspCode  = code.equalsIgnoreCase("200")?"00":"82";
            log.info("authorizeTransaction Response:{} | {} | {}", code, msg, rspBody);

            iswAuthorizeResponse= gson.fromJson(rspBody, IswAuthorizeResponse.class);

            webRequests.setResponseCode(iswAuthorizeResponse.getResponseCode());
            webRequests.setResponseMessage(iswAuthorizeResponse.getMessage());
            webRequests.setStan(iswAuthorizeResponse.getStan());
            webRequests.setCardType(iswAuthorizeResponse.getCardType());
            webRequests.setTransactionIdentifier(iswAuthorizeResponse.getTransactionIdentifier());
            webRequests.setBankCode(iswAuthorizeResponse.getBankCode());
            webRequests.setTerminalId(iswAuthorizeResponse.getTerminalId());
            webRequests.setPanLast4Digits(iswAuthorizeResponse.getPanLast4Digits());
            webRequests.setToken(iswAuthorizeResponse.getToken());
            webRequests.setTokenExpiryDate(iswAuthorizeResponse.getTokenExpiryDate());
            webRequests.setRetrievalReferenceNumber(iswAuthorizeResponse.getRetrievalReferenceNumber());
            webRequestsRepository.save(webRequests);
            return new BaseResponse(rspCode,msg,iswAuthorizeResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        iswAuthorizeResponse.setMessage(msg);
        iswAuthorizeResponse.setResponseCode(code);
        return new BaseResponse(code,msg,iswAuthorizeResponse);
    }

    @Override
    public Object statusVerify(String tranReference, String amount) {
        IswAuthWebPayResponse iswAuthWebPayResponse = getAuthToken();
        if (Objects.isNull(iswAuthWebPayResponse)) {
            return null;
        }

        IswTranStatusResponse iswStatusResponse =new IswTranStatusResponse();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + iswAuthWebPayResponse.getAccessToken());

        String minorAmount = String.valueOf(Double.parseDouble(amount) * 100).replace(".0","");


        String url = env.getProperty("ISW_STATUSCHECKURL") + tranReference + "&amount=" + minorAmount;
        String rspBody = "";
        String msg = "Request Failed, kindly try again";
        String code = "82";

        try {
            Response response = httpClient.getNoParam(headers, url);
            rspBody = response.body().string();
            code = String.valueOf(response.code());
            String rspCode  = code.equalsIgnoreCase("202")?"00":"82";
            log.info("statusVerify Response:{} | {} | {}", code, msg, rspBody);

            iswStatusResponse =  gson.fromJson(rspBody, IswTranStatusResponse.class);
            return new BaseResponse(rspCode,msg,iswStatusResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        iswStatusResponse.setResponseCode(code);
        iswStatusResponse.setResponseDescription(msg);
        return new BaseResponse(code,msg,iswStatusResponse);
    }

    @Override
    public Object resendOtp(IswResendOtpRequest resendOtpRequest) {
        IswAuthWebPayResponse iswAuthWebPayResponse = getAuthToken();
        if (Objects.isNull(iswAuthWebPayResponse)) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + iswAuthWebPayResponse.getAccessToken());


        String reqBody = gson.toJson(resendOtpRequest);
        String url = env.getProperty("ISW_RESENDOTPURL");


        IswAuthorizeResponse iswAuthorizeResponse = new IswAuthorizeResponse();
        String rspBody = "";
        String msg = "Request Failed, kindly try again";
        String code = "82";
        try {
            Response response = httpClient.post(headers, reqBody, url);
            rspBody = response.body().string();
            msg = response.message();
            code = String.valueOf(response.code());
            String rspCode  = code.equalsIgnoreCase("202")?"00":"82";
            log.info("resendOtp Response:{} | {} | {}", code, msg, rspBody);

            iswAuthorizeResponse =  gson.fromJson(rspBody, IswAuthorizeResponse.class);
            return new BaseResponse(rspCode,msg,iswAuthorizeResponse) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        iswAuthorizeResponse.setMessage(msg);
        iswAuthorizeResponse.setResponseCode(code);
        return new BaseResponse(code,msg,iswAuthorizeResponse) ;
    }

    private IswAuthWebPayResponse getAuthToken() {

        try {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(env.getProperty("ISW_AUTHURL"))
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", env.getProperty("ISW_BASICAUTH"))
                    .build();
            Response authResponse = client.newCall(request).execute();

            String rspBody = authResponse.body().string();
            String msg = authResponse.message();
            String code = String.valueOf(authResponse.code());

            log.info("Auth Response:{} | {} | {} | {}", env.getProperty("ISW_AUTHURL"), code, msg, rspBody);
            if (authResponse.isSuccessful()) {
                IswAuthWebPayResponse iswAuthWebPayResponse = gson.fromJson(rspBody, IswAuthWebPayResponse.class);
                AUTH_TOKEN = iswAuthWebPayResponse.getAccessToken();
                return iswAuthWebPayResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAuthData(String version, String pan, String pin, String expiryDate, String cvv2) throws Exception {
        String authData = "";
        String authDataCipher = version + "Z" + pan + "Z" + pin + "Z" + expiryDate + "Z" + cvv2;

        log.info(authDataCipher);
        // The Modulus and Public Exponent will be supplied by Interswitch. please ask for one
        String modulus = env.getProperty("ISW_MODULUS");
        String publicExponent = env.getProperty("ISW_PUBLIC_EXPONENT");

        Security.addProvider(new BouncyCastleProvider());
        RSAPublicKeySpec publicKeyspec = new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
        KeyFactory factory = KeyFactory.getInstance("RSA"); //, "JHBCI");
        PublicKey publicKey = factory.generatePublic(publicKeyspec);
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] authDataBytes = encryptCipher.doFinal(authDataCipher.getBytes(StandardCharsets.UTF_8));
        authData = Base64.getEncoder().encodeToString(authDataBytes).replaceAll("\\r|\\n", "");
        return authData;
    }


    public String reverseExpiryDate(String expDate) {
        expDate = expDate.replace("/", "");
        String firstTwo = expDate.substring(0, 2);
        String lastTwo = expDate.substring(4, 6);
        log.info(lastTwo + firstTwo);
        return lastTwo + firstTwo;
    }

}
