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


import com.monkeyk.sos.domain.shared.Repository;

import java.util.List;

/**
 * 2015/10/29
 *
 * @author Shengzhao Li
 */

public interface AccessTokenRepository extends Repository {

    void saveAccessToken(AccessToken accessToken);

    AccessToken findAccessToken(String tokenId);

    void removeAccessToken(String tokenId);

    void saveRefreshToken(RefreshToken refreshToken);

    RefreshToken findRefreshToken(String tokenId);

    void removeRefreshToken(String tokenId);

    void removeAccessTokenByRefreshToken(String refreshToken);

    AccessToken findAccessTokenByRefreshToken(String refreshToken);

    AccessToken findAccessTokenByAuthenticationId(String authenticationId);

    List<AccessToken> findAccessTokensByUsername(String userName);

    List<AccessToken> findAccessTokensByClientId(String clientId);

    List<AccessToken> findAccessTokensByClientIdAndUsername(String clientId, String userName);
}