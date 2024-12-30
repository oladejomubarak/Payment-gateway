package com.celertech.webpay.dto.request;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class InitiateIswWebPayRequest {

	@SerializedName("expiryDate")
	@NotEmpty
	@NotNull
	@NotBlank
	private String expiryDate;

	@SerializedName("cvv2")
	@NotEmpty
	@NotNull
	@NotBlank
	private String cvv2;

	@SerializedName("pin")
	@NotEmpty
	@NotNull
	@NotBlank
	private String pin;

	@SerializedName("pan")
	@NotEmpty
	@NotNull
	@NotBlank
	private String pan;

	@SerializedName("amount")
	@NotEmpty
	@NotNull
	@NotBlank
	private String amount;

	@SerializedName("currency")
	@NotEmpty
	@NotNull
	@NotBlank
	private String currency;

	@SerializedName("customerId")
	@NotEmpty
	@NotNull
	@NotBlank
	private String customerId;

	@SerializedName("paymentLinkId")
	@NotEmpty
	@NotNull
	@NotBlank
	private String paymentLinkId;

	private String callbackUrl;

	DeviceInformation deviceInformation;

}
