package com.example.demo.data;

import com.example.demo.VO.*;
import org.dom4j.DocumentException;

import java.sql.SQLException;

public interface MyMapper {

    ResponseVO validate(LoginVO loginVO) throws SQLException;

    ResponseVO getAdmInfo(String account) throws SQLException;

    ResponseVO getStuInfoForAdm() throws SQLException;

    ResponseVO getLessonInfoForAdm() throws SQLException;

    ResponseVO changeAdmPassword(AdminVO adminVO) throws SQLException;

    ResponseVO changeAdmLesson(CourseVO courseVO) throws SQLException;

    ResponseVO getStuInfo(String account) throws SQLException;

    ResponseVO getLessonInfo() throws SQLException, DocumentException;

    ResponseVO changePassword(LoginVO loginVO) throws SQLException;

    ResponseVO chooseLesson(ChooseVO chooseVO) throws SQLException;

    ResponseVO getChoosedLesson(String stuId) throws SQLException, DocumentException;

    ResponseVO dropLesson(ChooseVO chooseVO) throws SQLException, DocumentException;

    ResponseVO dropLesson_clientB(String xml) throws DocumentException, SQLException;

    ResponseVO getChoosedLesson_clientB(String stuId) throws SQLException;

    ResponseVO chooseLesson_clientB(String xml) throws DocumentException, SQLException;

    ResponseVO getShareLesson_clientB() throws SQLException;

    ResponseVO deleteLesson(String courseId)throws SQLException;
}
