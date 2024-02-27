package com.barry.spring.boot.starter.amqp.plus;

import com.barry.common.core.util.StringUtils;
import com.barry.common.core.util.UUIDUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 失败消息存储到数据库
 *
 * @author barry chen
 * @date 2021/3/12 17:39
 */
public class DatabaseFailMessageProcessor extends AbstractFailMessageProcessor {

    public static final String DEFAULT_TABLE_NAME = "mq_fail_message";
    private static final String TABLE_NAME = "mq_fail_message";
    private static final String INSERT_SQL = "INSERT INTO `mq_fail_message` (`id`,`message_id`, `ori_exchange`, `ori_route_key`, `ori_queue`, `message_body`, " +
            "`message_properties`, `message_binary`,`exception_type`, `exception_detail`, `retry_count`, `fail_time`, `redeliver_time`, `redeliver_user`) " +
            "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseFailMessageProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void doProcess(FailMessage failMessage, Exception ex) {
        jdbcTemplate.update(INSERT_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, UUIDUtils.uuid());
                ps.setString(2, failMessage.getMessageId());
                ps.setString(3, failMessage.getOriExchange());
                ps.setString(4, failMessage.getOriRouteKey());
                ps.setString(5, failMessage.getOriQueue());
                ps.setString(6, StringUtils.left(failMessage.getMessageBody(), 5120));
                ps.setString(7, StringUtils.left(failMessage.getMessageProperties(), 5120));
                ps.setObject(8, failMessage.getMessage());
                ps.setString(9, StringUtils.left(failMessage.getExceptionType(), 256));
                ps.setString(10, StringUtils.left(failMessage.getExceptionDetail(), 2048));
                ps.setInt(11, failMessage.getRetryCount());
                ps.setDate(12, new Date(failMessage.getFailTime().getTime()));
                ps.setNull(13, Types.DATE);
                ps.setNull(14, Types.VARCHAR);
            }
        });
    }
}
