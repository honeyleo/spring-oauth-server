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


import com.monkeyk.sos.domain.shared.BeanProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * 2015/10/28
 *
 * @author Shengzhao Li
 */
public abstract class AbstractMongoSupport {

    protected static final String ID = "_id";


    private MongoTemplate mongoTemplate;


    protected AbstractMongoSupport() {
    }

    protected MongoTemplate mongoTemplate() {
        if (mongoTemplate == null) {
            mongoTemplate = BeanProvider.getBean(MongoTemplate.class);
        }
        return mongoTemplate;
    }

    protected <T extends Serializable> T findById(Class<T> clazz, Object id) {
        Query query = new Query(new Criteria(ID).is(id));
        return this.mongoTemplate().findOne(query, clazz);
    }


    protected Query createIDQuery(Object id) {
        final Criteria criteria = new Criteria(ID).is(id);
        return new Query(criteria);
    }


    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
