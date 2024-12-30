package com.celertech.webpay.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorizeIswRequest {
	private String paymentId;
	private String otp;
	private String transactionId;
	private String eciFlag;
	private String authData;
	private String md;
}
