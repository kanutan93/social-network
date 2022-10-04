package ru.hl.socialnetwork.mapper.friend;

import org.springframework.jdbc.core.RowMapper;
import ru.hl.socialnetwork.dao.FriendDao;
import ru.hl.socialnetwork.dao.UserDao;
import ru.hl.socialnetwork.enums.SexEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendDaoRowMapper implements RowMapper<FriendDao> {

  @Override
  public FriendDao mapRow(ResultSet rs, int rowNum) throws SQLException {
    FriendDao friendDao = new FriendDao();

    friendDao.setId(rs.getInt("id"));
    friendDao.setSenderId(rs.getInt("sender_id"));
    friendDao.setReceiverId(rs.getInt("receiver_id"));
    friendDao.setApproved(rs.getBoolean("is_approved"));

    return friendDao;
  }
}
