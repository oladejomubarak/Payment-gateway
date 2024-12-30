/*
 * *
 *  * Created by Mubarak Oladejo
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 8/7/24, 2:58 AM
 *
 */

package com.celertech.webpay.repository;

import com.celertech.webpay.model.WebRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebRequestsRepository extends JpaRepository<WebRequests, Long> {
    Optional<WebRequests> findFirstByPaymentId(String paymentId);

    Optional<WebRequests> findByMd(String md);
}
