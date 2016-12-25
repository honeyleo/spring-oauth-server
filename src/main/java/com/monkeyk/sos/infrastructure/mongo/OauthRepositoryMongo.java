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
import com.monkeyk.sos.domain.oauth.OauthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2015/10/28
 *
 * @author Shengzhao Li
 */
@Repository("oauthRepositoryMongo")
public class OauthRepositoryMongo extends AbstractMongoSupport implements OauthRepository {


    private static final Logger LOG = LoggerFactory.getLogger(OauthRepositoryMongo.class);


    @Override
    public OauthClientDetails findOauthClientDetails(String clientId) {
        LOG.debug("Call findOauthClientDetails, clientId = {}", clientId);
        return findById(OauthClientDetails.class, clientId);
    }

    @Override
    public List<OauthClientDetails> findAllOauthClientDetails() {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        return this.mongoTemplate().find(query, OauthClientDetails.class);
    }

    @Override
    public boolean updateOauthClientDetailsArchive(String clientId, boolean archive) {
        LOG.debug("Call updateOauthClientDetailsArchive, clientId = {}, archive = {}", clientId, archive);
        Update update = new Update();
        update.set("archived", archive);

        Query query = createIDQuery(clientId);

        this.mongoTemplate().updateFirst(query, update, OauthClientDetails.class);
        return true;
    }


    @Override
    public void saveOauthClientDetails(OauthClientDetails clientDetails) {
        this.mongoTemplate().insert(clientDetails);
    }

    @Override
    public boolean removeOauthClientDetails(OauthClientDetails clientDetails) {
        LOG.debug("Call removeOauthClientDetails, clientDetails = {}", clientDetails);
        mongoTemplate().remove(clientDetails);
        return true;
    }

    @Override
    public void saveAuthorizationCode(AuthorizationCode authorizationCode) {
        this.mongoTemplate().save(authorizationCode);
    }

    @Override
    public AuthorizationCode removeAuthorizationCode(String code) {
        final AuthorizationCode authorizationCode = findById(AuthorizationCode.class, code);
        this.mongoTemplate().remove(authorizationCode);
        return authorizationCode;
    }
}
