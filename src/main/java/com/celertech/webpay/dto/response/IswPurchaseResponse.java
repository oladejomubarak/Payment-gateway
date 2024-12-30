package com.celertech.webpay.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IswPurchaseResponse{
	private String amount;
	private String paymentId;
	private String transactionRef;
	private String plainTextSupportMessage;
	private String message;
	private String responseCode;
	private String MD;
	private String transactionId;
	private String TermUrl;
	private String jwt;
	private String ACSUrl;
	private String eciFlag;
	private String bankCode;

}
