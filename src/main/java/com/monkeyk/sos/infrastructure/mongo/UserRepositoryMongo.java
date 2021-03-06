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


import com.monkeyk.sos.domain.user.User;
import com.monkeyk.sos.domain.user.UserRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2015/10/28
 *
 * @author Shengzhao Li
 */
@Repository("userRepositoryMongo")
public class UserRepositoryMongo extends AbstractMongoSupport implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryMongo.class);


    @Override
    public User findByGuid(String guid) {
        LOG.debug("Call findByGuid, guid = {}", guid);
        return findById(User.class, guid);
    }

    @Override
    public void saveUser(User user) {
        this.mongoTemplate().insert(user);
    }

    @Override
    public boolean updateUser(User user) {
        Update update = new Update();
        update.set("password", user.password())
                .set("phone", user.phone())
                .set("lastLoginTime", user.lastLoginTime())
                .set("archived", user.archived())
                .set("email", user.email());

        update.set("privileges", user.privileges());

        Query query = createIDQuery(user.guid());
        this.mongoTemplate().updateFirst(query, update, User.class);
        return true;
    }

    @Override
    public User findByUsername(String username) {
        LOG.debug("Call findByUsername, username = {}", username);
        Query query = new Query(new Criteria("username").is(username));
        return this.mongoTemplate().findOne(query, User.class);
    }


    @Override
    public List<User> findAllUsers() {
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "createTime"));
        return mongoTemplate().find(query, User.class);
    }

    @Override
    public boolean removeUser(User user) {
        LOG.debug("Call removeUser, user = {}", user);
        mongoTemplate().remove(user);
        return true;
    }

    @Override
    public List<User> findUsersByUsername(String username) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(username)) {
            query.addCriteria(Criteria.where(username).regex("/*" + username + "/*"));
        }
        return mongoTemplate().find(query, User.class);
    }
}
