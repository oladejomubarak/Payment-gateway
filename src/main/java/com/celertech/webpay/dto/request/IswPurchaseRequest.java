package com.celertech.webpay.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IswPurchaseRequest{
	String amount;
	String authData;
	String customerId;
	String transactionRef;
	String currency;
	String callbackUrl;
	DeviceInformation deviceInformation;
}
