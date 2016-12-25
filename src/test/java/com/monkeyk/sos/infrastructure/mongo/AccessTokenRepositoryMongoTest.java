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
package com.monkeyk.sos.infrastructure.mongo;


import com.monkeyk.sos.domain.oauth.AccessToken;
import com.monkeyk.sos.domain.oauth.RefreshToken;
import com.monkeyk.sos.domain.shared.GuidGenerator;
import com.monkeyk.sos.infrastructure.AbstractRepositoryTest;
import com.monkeyk.sos.infrastructure.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/*
  * @author Shengzhao Li
  */
public class AccessTokenRepositoryMongoTest extends AbstractRepositoryTest {


    private AccessTokenRepositoryMongo tokenRepositoryMongo;

    private void prepTest() {
        if (tokenRepositoryMongo == null) {
            tokenRepositoryMongo = new AccessTokenRepositoryMongo();
            tokenRepositoryMongo.setMongoTemplate(mongoTemplate());
        }
    }


    @Test
    public void removeAccessToken() {
        prepTest();

        String tokenId = GuidGenerator.generate();
        final String refreshToken = GuidGenerator.generate();

        OAuth2Request req = new OAuth2Request() {
        };
        Authentication userAu = new OAuth2Authentication(req, null);
        OAuth2Authentication auth = new OAuth2Authentication(req, userAu);

        OAuth2AccessToken tok = new DefaultOAuth2AccessToken(GuidGenerator.generate());
        final String clientId = "client253";
        final String username = "username1452";
        final String authenticationId = GuidGenerator.generate();

        AccessToken accessToken = new AccessToken()
                .authentication(auth)
                .token(tok)
                .authenticationId(authenticationId)
                .refreshToken(refreshToken)
                .tokenId(tokenId).clientId(clientId).username(username);

        tokenRepositoryMongo.saveAccessToken(accessToken);


        final AccessToken token = tokenRepositoryMongo.findAccessToken(tokenId);
        assertNotNull(token);
        assertNotNull(token.createTime());
        assertNotNull(token.clientId());
        assertNotNull(token.authentication());
        assertNotNull(token.token());


        final AccessToken accessTokenByRefreshToken = tokenRepositoryMongo.findAccessTokenByRefreshToken(refreshToken);
        assertNotNull(accessTokenByRefreshToken);


        final List<AccessToken> list = tokenRepositoryMongo.findAccessTokensByClientId(clientId);
        assertEquals(list.size(), 1);

        final List<AccessToken> tokenList = tokenRepositoryMongo.findAccessTokensByUsername(username);
        assertEquals(tokenList.size(), 1);

        final AccessToken accessTokenByAuthenticationId = tokenRepositoryMongo.findAccessTokenByAuthenticationId(authenticationId);
        assertNotNull(accessTokenByAuthenticationId);


        tokenRepositoryMongo.removeAccessToken(token.tokenId());

    }


    @Test
    public void removeRefreshToken() {
        prepTest();

        String tokenId = GuidGenerator.generate();

        OAuth2Request req = new OAuth2Request() {
        };
        Authentication userAu = new OAuth2Authentication(req, null);
        OAuth2Authentication auth = new OAuth2Authentication(req, userAu);

        OAuth2RefreshToken toks = new DefaultExpiringOAuth2RefreshToken(GuidGenerator.generate(), new Date());

        RefreshToken refreshToken = new RefreshToken()
                .tokenId(tokenId).token(toks)
                .authentication(auth);


        tokenRepositoryMongo.saveRefreshToken(refreshToken);


        final RefreshToken token = tokenRepositoryMongo.findRefreshToken(tokenId);
        assertNotNull(token);
        assertNotNull(token.tokenId());
        assertNotNull(token.token());
        assertNotNull(token.authentication());


        tokenRepositoryMongo.removeRefreshToken(token.tokenId());

    }

}