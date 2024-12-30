package com.celertech.webpay.dto.response;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class IswTranStatusResponse{

	@SerializedName("CardNumber")
	private String cardNumber;

	@SerializedName("MerchantReference")
	private String merchantReference;

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("PaymentReference")
	private String paymentReference;

	@SerializedName("Amount")
	private Integer amount;

	@SerializedName("Channel")
	private String channel;

	@SerializedName("SplitAccounts")
	private List<Object> splitAccounts;

	@SerializedName("PaymentId")
	private Integer paymentId;

	@SerializedName("Stan")
	private String stan;

	@SerializedName("BankCode")
	private String bankCode;

	@SerializedName("RemittanceAmount")
	private Integer remittanceAmount;

	@SerializedName("RetrievalReferenceNumber")
	private String retrievalReferenceNumber;

	@SerializedName("TerminalId")
	private String terminalId;

	@SerializedName("ResponseDescription")
	private String responseDescription;

	@SerializedName("TransactionDate")
	private String transactionDate;
}
