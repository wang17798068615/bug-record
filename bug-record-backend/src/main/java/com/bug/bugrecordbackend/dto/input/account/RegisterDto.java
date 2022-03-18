package com.bug.bugrecordbackend.dto.input.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RegisterDto {
    @NotNull(message = "用户名不能为空")
    @Length(min = 5,max = 20,message = "用户名长度必须在5-20个字符之间")
    private String username;
    @NotNull(message = "密码不能为空")
    @Length(min = 6,max = 20,message = "密码长度必须在6-20个字符之间")
    private String password;
    @NotNull(message = "角色不能为空")
    private List<String> roles;
}
