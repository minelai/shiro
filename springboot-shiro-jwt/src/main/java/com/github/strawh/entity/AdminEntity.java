package com.github.strawh.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: straw
 * @date: 2020/6/7 14:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEntity {

    private String uuid;

    private String username;

    private String password;
}
