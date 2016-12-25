package com.monkeyk.sos.domain.oauth;

import com.monkeyk.sos.domain.shared.Repository;

import java.util.List;

/**
 * @author Shengzhao Li
 */
public interface OauthRepository extends Repository {

    OauthClientDetails findOauthClientDetails(String clientId);

    List<OauthClientDetails> findAllOauthClientDetails();

    boolean updateOauthClientDetailsArchive(String clientId, boolean archive);

    void saveOauthClientDetails(OauthClientDetails clientDetails);

    boolean removeOauthClientDetails(OauthClientDetails clientDetails);

    void saveAuthorizationCode(AuthorizationCode authorizationCode);

    AuthorizationCode removeAuthorizationCode(String code);
}