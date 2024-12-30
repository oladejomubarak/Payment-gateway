package com.celertech.webpay.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyIswOtpRequest{
	private String paymentId;
	private String otp;
}
