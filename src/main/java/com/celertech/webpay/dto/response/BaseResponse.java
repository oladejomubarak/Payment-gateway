/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 10/4/24, 10:49 AM
 *
 */

package com.celertech.webpay.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponse {
    protected String respCode;
    protected String respDescription;
    protected Object respBody;

    public BaseResponse(String responseCode, String responseDescription, Object responseBody) {
        this.respCode = responseCode;
        this.respDescription = responseDescription;
        this.respBody = responseBody;
    }
}
