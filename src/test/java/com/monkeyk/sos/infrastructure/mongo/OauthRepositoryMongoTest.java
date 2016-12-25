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


import com.monkeyk.sos.domain.oauth.AuthorizationCode;
import com.monkeyk.sos.domain.oauth.OauthClientDetails;
import com.monkeyk.sos.domain.shared.GuidGenerator;
import com.monkeyk.sos.infrastructure.AbstractRepositoryTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/*
  * @author Shengzhao Li
  */
public class OauthRepositoryMongoTest extends AbstractRepositoryTest {


    private OauthRepositoryMongo oauthRepositoryMongo;


    private void prepTest() {
        if (oauthRepositoryMongo == null) {
            oauthRepositoryMongo = new OauthRepositoryMongo();
            oauthRepositoryMongo.setMongoTemplate(mongoTemplate());
        }
    }

    @Test
    public void testFindOauthClientDetails() throws Exception {
        prepTest();

        final OauthClientDetails clientDetails = oauthRepositoryMongo.findOauthClientDetails("clientId");
        assertNull(clientDetails);

    }

    @Test(enabled = false)
    public void removeAuthorizationCode() throws Exception {
        prepTest();

        final String code = GuidGenerator.generate();

        OAuth2Request request = new OAuth2Request() {
        };
        Authentication userAuth = new OAuth2Authentication(request, null);
        OAuth2Authentication auth = new OAuth2Authentication(request, userAuth);

        AuthorizationCode authorizationCode = new AuthorizationCode()
                .code(code).authentication(auth);


        final AuthorizationCode code1 = oauthRepositoryMongo.removeAuthorizationCode(code);
        assertNull(code1);

        oauthRepositoryMongo.saveAuthorizationCode(authorizationCode);

        final AuthorizationCode code2 = oauthRepositoryMongo.removeAuthorizationCode(code);
        assertNotNull(code2);
        assertNotNull(code2.code());
        assertNotNull(code2.authentication());
        assertNotNull(code2.createTime());

    }

    @Test
    public void findAllOauthClientDetails() throws Exception {
        prepTest();

        final List<OauthClientDetails> list = oauthRepositoryMongo.findAllOauthClientDetails();
        assertNotNull(list);

        assertTrue(list.isEmpty());
    }

    @Test
    public void updateOauthClientDetailsArchive() throws Exception {
        prepTest();

        OauthClientDetails clientDetails = new OauthClientDetails();
        clientDetails.clientId("test").clientSecret("secret");

        oauthRepositoryMongo.saveOauthClientDetails(clientDetails);

        final OauthClientDetails oauthClientDetails = oauthRepositoryMongo.findOauthClientDetails(clientDetails.clientId());
        assertNotNull(oauthClientDetails);
        assertFalse(oauthClientDetails.archived());

        oauthRepositoryMongo.updateOauthClientDetailsArchive(clientDetails.clientId(), true);

        final OauthClientDetails details = oauthRepositoryMongo.findOauthClientDetails(clientDetails.clientId());
        assertNotNull(details);
        assertTrue(details.archived());

    }

    @Test
    public void saveOauthClientDetails() throws Exception {
        prepTest();

        OauthClientDetails clientDetails = new OauthClientDetails();
        clientDetails.clientId("test").clientSecret("secret");

        oauthRepositoryMongo.saveOauthClientDetails(clientDetails);

        final OauthClientDetails oauthClientDetails = oauthRepositoryMongo.findOauthClientDetails(clientDetails.clientId());
        assertNotNull(oauthClientDetails);

        final boolean b = oauthRepositoryMongo.removeOauthClientDetails(oauthClientDetails);
        assertEquals(b, true);

        final OauthClientDetails details = oauthRepositoryMongo.findOauthClientDetails(clientDetails.clientId());
        assertNull(details);
    }


}