/*
 * Copyright (c) 2015 MONKEYK Information Technology Co. Ltd
 * www.monkeyk.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * MONKEYK Information Technology Co. Ltd ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with MONKEYK Information Technology Co. Ltd.
 */
package com.monkeyk.sos.domain.oauth;

import com.monkeyk.sos.infrastructure.DateUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Date;

/**
 * 2015/10/29
 *
 * @author Shengzhao Li
 */
@Document(collection = "RefreshToken")
public class RefreshToken implements Serializable {
    private static final long serialVersionUID = 5529390717010301531L;


    @Id
    private String tokenId;


    @CreatedDate
    private Date createTime = DateUtils.now();

    @Version
    private Long version;


    private byte[] token;

    private byte[] authentication;


    public RefreshToken() {
    }

    public String tokenId() {
        return tokenId;
    }

    public RefreshToken tokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    public Date createTime() {
        return createTime;
    }


    public Long version() {
        return version;
    }


    public OAuth2RefreshToken token() {
        return SerializationUtils.deserialize(token);
    }

    public RefreshToken token(OAuth2RefreshToken token) {
        this.token = SerializationUtils.serialize(token);
        return this;
    }

    public OAuth2Authentication authentication() {
        return SerializationUtils.deserialize(authentication);
    }

    public RefreshToken authentication(OAuth2Authentication authentication) {
        this.authentication = SerializationUtils.serialize(authentication);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "tokenId='" + tokenId + '\'' +
                ", createTime=" + createTime +
                ", version=" + version +
                '}';
    }
}
