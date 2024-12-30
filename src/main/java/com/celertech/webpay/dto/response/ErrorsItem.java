package com.celertech.webpay.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorsItem{
	private String code;
	private String message;
}
