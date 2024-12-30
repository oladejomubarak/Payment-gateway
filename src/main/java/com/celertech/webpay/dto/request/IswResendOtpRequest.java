package com.celertech.webpay.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IswResendOtpRequest{

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("paymentId")
	private String paymentId;

	@JsonProperty("currency")
	private String currency;
}
