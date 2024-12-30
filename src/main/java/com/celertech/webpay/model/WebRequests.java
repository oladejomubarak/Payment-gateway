/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 8/7/24, 2:45 AM
 *
 */

package com.celertech.webpay.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "web_requests")
public class WebRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String amount;
    @Lob
    @Column(length = 16777216)
    private String authData;
    private String customerId;
    private String transactionRef;
    private String currency;
    private String callbackUrl;
    private String paymentId;
    private String responseCode;
    private String responseMessage;
    private String retrievalReferenceNumber;
    private String terminalId;
    private String token;
    private String stan;
    private String panLast4Digits;
    private String bankCode;
    private String tokenExpiryDate;
    private String transactionIdentifier;
    private String cardType;
    private String md;
    private String transactionId;

    @Type(JsonType.class)
    @Column(columnDefinition="json")
    private String deviceInformation;
    private String paymentLinkId;


    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updatedOn;

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated = LocalDateTime.now();

    @PrePersist
    public void onPrePersist() {
        setDateCreated(LocalDateTime.now());
    }
    @PreUpdate
    public void onPreUpdate() {
        setUpdatedOn(LocalDateTime.now());
    }

}
