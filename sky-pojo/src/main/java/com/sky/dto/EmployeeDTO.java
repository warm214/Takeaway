package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("账号")
    private String username;
    @ApiModelProperty("用户昵称")
    private String name;
    @ApiModelProperty("电话")
    private String phone;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("idNumber")
    private String idNumber;

}
