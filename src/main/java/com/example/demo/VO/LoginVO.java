package com.example.demo.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class LoginVO {
    private String account;
    private String password;
}
