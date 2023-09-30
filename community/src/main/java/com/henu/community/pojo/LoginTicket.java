package com.henu.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    private Integer status;
    private Date expired;
}
