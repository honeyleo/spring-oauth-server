package com.monkeyk.sos.infrastructure;

import com.monkeyk.sos.ContextTest;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Shengzhao Li
 */
public abstract class AbstractRepositoryTest extends ContextTest {



    private MongoTemplate mongoTemplate;


    //get MongoTemplate
    protected MongoTemplate mongoTemplate() {
        if (mongoTemplate == null) {
            mongoTemplate = applicationContext.getBean(MongoTemplate.class);
        }
        return mongoTemplate;
    }

}