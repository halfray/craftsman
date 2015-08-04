package com.bigbata.craftsman.dao.mybatis.dialect;

import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.data.domain.Pageable;


/**
 * @author badqiu
 * @author miemiedev
 */
public class MySQLDialect extends Dialect {

    public MySQLDialect(MappedStatement mappedStatement, Object parameterObject, Pageable pageable) {
        super(mappedStatement, parameterObject, pageable);
    }

    protected String getLimitString(String sql, String offsetName, int offset, String limitName, int limit) {
        StringBuffer buffer = new StringBuffer(sql.length() + 20).append(sql);
        if (offset > 0) {
            buffer.append(" limit ?, ?");
            setPageParameter(offsetName, offset, Integer.class);
            setPageParameter(limitName, limit, Integer.class);
        } else {
            buffer.append(" limit ?");
            setPageParameter(limitName, limit, Integer.class);
        }
        return buffer.toString();
    }

}
