package com.monkeyk.sos.domain.user;

import com.monkeyk.sos.domain.shared.Repository;

import java.util.List;

/**
 * @author Shengzhao Li
 */

public interface UserRepository extends Repository {

    User findByGuid(String guid);

    void saveUser(User user);

    boolean updateUser(User user);

    User findByUsername(String username);

    List<User> findAllUsers();

    boolean removeUser(User user);

    List<User> findUsersByUsername(String username);
}