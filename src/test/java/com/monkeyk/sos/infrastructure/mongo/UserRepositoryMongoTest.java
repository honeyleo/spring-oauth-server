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


import com.monkeyk.sos.domain.user.Privilege;
import com.monkeyk.sos.domain.user.User;
import com.monkeyk.sos.infrastructure.AbstractRepositoryTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/*
  * @author Shengzhao Li
  */
public class UserRepositoryMongoTest extends AbstractRepositoryTest {


    private UserRepositoryMongo userRepository;

    private void prepTest() {
        if (userRepository == null) {
            userRepository = new UserRepositoryMongo();
            userRepository.setMongoTemplate(mongoTemplate());
        }
    }

    @Test
    public void testSaveUser() throws Exception {

        prepTest();

        User user = new User("user", "pass", "13302585", "ddedd@ddd.com");
        user.privileges().add(Privilege.MOBILE);

        userRepository.saveUser(user);

        final User user1 = userRepository.findByGuid(user.guid());
        assertNotNull(user1);
        assertNotNull(user1.username());
        assertNotNull(user1.createTime());


        userRepository.removeUser(user1);


    }


    @Test
    public void findByUsername() throws Exception {

        prepTest();

        User user = new User("user", "pass", "13302585", "ddedd@ddd.com");
        user.privileges().add(Privilege.MOBILE);

        userRepository.saveUser(user);

        final User user1 = userRepository.findByUsername(user.username());
        assertNotNull(user1);
        assertNotNull(user1.username());
        assertNotNull(user1.createTime());


        userRepository.removeUser(user1);


    }

    @Test
    public void findAllUsers() throws Exception {

        prepTest();

        final List<User> list = userRepository.findAllUsers();
        assertNotNull(list);

        assertTrue(list.isEmpty());

    }
}