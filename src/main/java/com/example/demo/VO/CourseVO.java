package com.example.demo.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class CourseVO {
    private String lesson_id;
    private String lesson_name;
    private int lesson_point;
    private int lesson_time;
    private String teacher;
    private String classroom;
    private String isShared;
    private int chooseCount;
}
