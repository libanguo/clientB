package com.example.demo.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ChooseVO {
    private String student_id;
    private String lesson_id;
    private int grade;

    public void setCourseId(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getCourseId() {
        return lesson_id;
    }

    public String getStudentId() {
        return student_id;
    }

    public void setStudentId(String student_id) {
        this.student_id = student_id;
    }
}
