package com.example.demo.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class StudentVO {
    private String id;
    private String name;
    private String sex;
    private String department;
    private String account;
    private String password;
}
