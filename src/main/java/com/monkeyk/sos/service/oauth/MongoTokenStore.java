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


import com.monkeyk.sos.domain.oauth.AccessToken;
import com.monkeyk.sos.domain.oauth.AccessTokenRepository;
import com.monkeyk.sos.domain.oauth.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 2015/10/28
 *
 * @author Shengzhao Li
 */
public class MongoTokenStore implements TokenStore, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MongoTokenStore.class);

    private AccessTokenRepository tokenRepository;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private ExtractTokenKeyDigester extractTokenKeyDigester = new MD5ExtractTokenKeyDigester();

    public MongoTokenStore() {
    }


    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        final String authenticationId = authenticationKeyGenerator.extractKey(authentication);
        OAuth2AccessToken accessToken = null;

        try {
            AccessToken token = tokenRepository.findAccessTokenByAuthenticationId(authenticationId);
            accessToken = token != null ? token.token() : null;
        } catch (IllegalArgumentException e) {
            LOG.error("Could not extract access token for authentication {}", authentication);
        }

        if (accessToken != null
                && !authenticationId.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }

        return accessToken;
    }


    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        LOG.debug("Call storeAccessToken, token = {}, authentication = {}", token, authentication);
        String refreshToken = token.getRefreshToken() != null ? token.getRefreshToken().getValue() : null;

        AccessToken accessToken = new AccessToken()
                .tokenId(extractTokenKey(token.getValue()))
                .token(token)
                .authenticationId(authenticationKeyGenerator.extractKey(authentication))
                .username(authentication.isClientOnly() ? null : authentication.getName())
                .clientId(authentication.getOAuth2Request().getClientId())
                .authentication(authentication)
                .refreshToken(extractTokenKey(refreshToken));

        tokenRepository.saveAccessToken(accessToken);
    }


    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        LOG.debug("Call readAccessToken, tokenValue = {}", tokenValue);
        OAuth2AccessToken token = null;

        try {
            final String tokenId = extractTokenKey(tokenValue);

            final AccessToken accessToken = tokenRepository.findAccessToken(tokenId);
            token = accessToken == null ? null : accessToken.token();
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for {}", tokenValue);
            removeAccessToken(tokenValue);
        }

        return token;
    }


    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }


    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        LOG.debug("Call readAuthentication, token = {}", token);
        OAuth2Authentication authentication = null;
        try {
            final String tokenId = extractTokenKey(token);

            AccessToken accessToken = tokenRepository.findAccessToken(tokenId);
            authentication = accessToken == null ? null : accessToken.authentication();

        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize authentication for {}", token);
            removeAccessToken(token);
        }

        return authentication;
    }


    protected void removeAccessToken(String tokenValue) {
        final String tokenId = extractTokenKey(tokenValue);
        tokenRepository.removeAccessToken(tokenId);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        LOG.debug("Call storeRefreshToken, refreshToken = {}, authentication = {}", refreshToken, authentication);

        RefreshToken token = new RefreshToken()
                .tokenId(extractTokenKey(refreshToken.getValue()))
                .token(refreshToken)
                .authentication(authentication);

        tokenRepository.saveRefreshToken(token);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        LOG.debug("Call readRefreshToken, tokenValue = {}", tokenValue);
        OAuth2RefreshToken refreshToken = null;

        try {
            final String tokenId = extractTokenKey(tokenValue);

            RefreshToken refreshTokenFounded = tokenRepository.findRefreshToken(tokenId);
            refreshToken = refreshTokenFounded == null ? null : refreshTokenFounded.token();
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize refresh token for token {}", tokenValue);
            removeRefreshToken(tokenValue);
        }

        return refreshToken;
    }


    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }


    protected void removeRefreshToken(String token) {
        final String tokenId = extractTokenKey(token);
        tokenRepository.removeRefreshToken(tokenId);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    protected OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
        OAuth2Authentication authentication = null;

        try {
            final String tokenId = extractTokenKey(tokenValue);
            RefreshToken refreshToken = tokenRepository.findRefreshToken(tokenId);

            authentication = refreshToken == null ? null : refreshToken.authentication();
        } catch (IllegalArgumentException e) {
            LOG.warn("Failed to deserialize access token for {}", tokenValue);
            removeRefreshToken(tokenValue);
        }

        return authentication;
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    protected void removeAccessTokenUsingRefreshToken(String refreshTokenValue) {
        final String refreshToken = extractTokenKey(refreshTokenValue);
        tokenRepository.removeAccessTokenByRefreshToken(refreshToken);
    }


    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        LOG.debug("Call findTokensByClientId, clientId = {}", clientId);
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        List<AccessToken> tokenList = tokenRepository.findAccessTokensByClientId(clientId);
        for (AccessToken token : tokenList) {
            final OAuth2AccessToken accessToken = token.token();
            if (accessToken != null) {
                accessTokens.add(accessToken);
            }
        }

        return accessTokens;
    }


    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        LOG.debug("Call findTokensByUserName, clientId = {}, username = {}", clientId, userName);
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        List<AccessToken> tokenList = tokenRepository.findAccessTokensByClientIdAndUsername(clientId, userName);
        for (AccessToken token : tokenList) {
            final OAuth2AccessToken accessToken = token.token();
            if (accessToken != null) {
                accessTokens.add(accessToken);
            }
        }

        return accessTokens;
    }


    protected String extractTokenKey(String value) {
        return this.extractTokenKeyDigester.digest(value);
    }

    public void setTokenRepository(AccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setExtractTokenKeyDigester(ExtractTokenKeyDigester extractTokenKeyDigester) {
        this.extractTokenKeyDigester = extractTokenKeyDigester;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenRepository);
        Assert.notNull(authenticationKeyGenerator);
    }
}
