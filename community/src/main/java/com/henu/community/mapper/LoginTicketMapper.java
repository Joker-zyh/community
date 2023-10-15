package com.henu.community.mapper;

import com.henu.community.pojo.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@Deprecated
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatusByTicket(@Param("ticket") String ticket,@Param("status") int status);

}
