package com.robot.dao;

import com.robot.pojo.Authorization;
import org.springframework.stereotype.Service;

public interface AuthorizationDao {
    int add(Authorization authorization);

    int update(Authorization authorization);

    int delete(int id);

    Authorization findAuthor(int id);

    int selectCount(Authorization authorization);
}
