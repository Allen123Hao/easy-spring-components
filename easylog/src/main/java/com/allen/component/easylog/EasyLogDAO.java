package com.allen.component.easylog;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/5/8 11:38
 */
@Slf4j
public class EasyLogDAO {

    private static final String TABLE_NAME = "user_log";

    private DataSource dataSource;

    public EasyLogDAO(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public int save(UserOperationLog userOperationLog){
        try {
            // 使用数据源
            Db db = Db.use(dataSource);

            Entity entity = Entity.create(TABLE_NAME).parseBean(userOperationLog,true,false);
            return db.insert(entity);
        } catch (Exception e){
            log.error("插入用户操作日志异常",e);
        }
        return 0;
    }

    public int update(UserOperationLog userOperationLog){
        try {
            // 使用数据源
            Db db = Db.use(dataSource);
            Entity where = Entity.create(TABLE_NAME);
            where.put("uuid",userOperationLog.getUuid());
            Entity userLog = Entity.create(TABLE_NAME).parseBean(userOperationLog,true,false);
            return db.update(userLog,where);
        } catch (Exception e){
            log.error("更新用户操作日志异常",e);
        }
        return 0;
    }
}
