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
package com.monkeyk.sos.service.oauth;


import com.monkeyk.sos.domain.oauth.OauthClientDetails;
import com.monkeyk.sos.domain.oauth.OauthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.util.Assert;


/**
 * 2015/10/28
 *
 * @author Shengzhao Li
 */
public class MongoClientDetailsService implements ClientDetailsService, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MongoClientDetailsService.class);

    private OauthRepository oauthRepository;


    public MongoClientDetailsService() {
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        final OauthClientDetails oauthClientDetails = oauthRepository.findOauthClientDetails(clientId);
        if (oauthClientDetails == null || oauthClientDetails.archived()) {
            LOG.warn("Not found ClientDetails by clientId '{}', because null or archived", clientId);
            throw new ClientRegistrationException("Not found ClientDetails by clientId '" + clientId + "', because null or archived");
        }
        return oauthClientDetails.toClientDetails();
    }


    public void setOauthRepository(OauthRepository oauthRepository) {
        this.oauthRepository = oauthRepository;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(oauthRepository);
    }
}
