package com.celertech.webpay.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IswAuthorizeResponse {
	private String panLast4Digits;
	private String bankCode;
	private String tokenExpiryDate;
	private String amount;
	private String transactionIdentifier;
	private String cardType;
	private String transactionRef;
	private String retrievalReferenceNumber;
	private String terminalId;
	private String message;
	private String token;
	private String responseCode;
	private String stan;

	private String paymentId;
	private List<ErrorsItem> errors;
}
