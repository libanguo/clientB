package com.example.demo.dataImpl;

import com.example.demo.VO.*;
import com.example.demo.data.MyMapper;
import com.example.demo.tool.Tool;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class MyMapperImpl implements MyMapper {
    @Autowired
    private Jdbc jdbc;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseVO validate(LoginVO loginVO) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 账户表 where 账户名='" + loginVO.getAccount() + "' and 密码='" + loginVO.getPassword() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            int a=Integer.valueOf(resultSet.getString("级别"));
            return ResponseVO.buildSuccess(a);
        }
        else {
            return ResponseVO.buildSuccess(0);
        }
    }

    @Override
    public ResponseVO getAdmInfo(String account) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 账户表 where 账户名='" + account + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            AdminVO adminVO = new AdminVO();
            adminVO.setAccount(account);
            adminVO.setPassword(resultSet.getString("密码"));
            return ResponseVO.buildSuccess(adminVO);
        } else {
            return ResponseVO.buildFailure("出现错误");
        }
    }

    @Override
    public ResponseVO getStuInfoForAdm() throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        //TODO 这里要更改院系名
        String sql = "select * from 学生表 where 专业='计算机科学'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<StudentVO> studentVOS = new ArrayList<>();
        while (resultSet.next()) {
            StudentVO studentVO = new StudentVO();
            studentVO.setName(resultSet.getString("姓名"));
            studentVO.setDepartment(resultSet.getString("专业"));
            studentVO.setSex(resultSet.getString("性别"));
            studentVO.setId(resultSet.getString("学号"));
            studentVOS.add(studentVO);
        }
        return ResponseVO.buildSuccess(studentVOS);
    }

    @Override
    public ResponseVO getLessonInfoForAdm() throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<CourseVO> courseVOS = new ArrayList<>();
        while (resultSet.next()) {
            CourseVO courseVO = new CourseVO();
            String cno = resultSet.getString("编号");
            courseVO.setLesson_name(resultSet.getString("名称"));
            courseVO.setLesson_id(cno);
            courseVO.setLesson_point(Integer.valueOf(resultSet.getString("学分")));
            courseVO.setLesson_time(Integer.valueOf(resultSet.getString("课时")));
            courseVO.setClassroom(resultSet.getString("地点"));
            courseVO.setTeacher(resultSet.getString("老师"));
            courseVO.setIsShared(resultSet.getString("共享"));
            int chooseCount = 0;
            sql = "select count(*) as count from 选课表 where 课程编号='" + cno + "'";
            Statement statement1 = connection.createStatement();
            ResultSet resultSet1 = statement1.executeQuery(sql);
            if (resultSet1.next()) {
                chooseCount += resultSet1.getInt("count");
            }
            courseVO.setChooseCount(chooseCount);
            courseVOS.add(courseVO);
        }
        return ResponseVO.buildSuccess(courseVOS);
    }

    @Override
    public ResponseVO changeAdmPassword(AdminVO adminVO) throws SQLException {
        if (adminVO.getPassword().length() > 12) {
            return ResponseVO.buildFailure("新密码长度不能超过12");
        }
        if (adminVO.getPassword().length() == 0) {
            return ResponseVO.buildFailure("新密码不能为空");
        }
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 账户表 where 账户名='" + adminVO.getAccount() + " 'and 密码='" + adminVO.getPassword() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            return ResponseVO.buildFailure("密码不能与现密码一致");
        } else {
            sql = "update 账户表 set 密码='" + adminVO.getPassword() + "' where 账户名='" + adminVO.getAccount() + "'";
            statement.executeUpdate(sql);
            return ResponseVO.buildSuccess();
        }
    }

    @Override
    public ResponseVO changeAdmLesson(CourseVO courseVO) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表 where 编号='" + courseVO.getLesson_id() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            sql = "update 课程表 set 名称='" + courseVO.getLesson_name() +"', 学分='" + String.valueOf(courseVO.getLesson_point()) +"',课时='"+String.valueOf(courseVO.getLesson_time())+ "', 老师='" + courseVO.getTeacher() + "', 地点='" + courseVO.getClassroom() + "', 共享='" + courseVO.getIsShared() + "' where 编号='" + courseVO.getLesson_id() + "'";
            statement.executeUpdate(sql);
            return ResponseVO.buildSuccess();
        } else {
            try {
                sql = "insert into 课程表(名称,学分,课时,老师,地点,共享,编号)values ('" + courseVO.getLesson_name() + "','" + String.valueOf(courseVO.getLesson_point()) +"','"+String.valueOf(courseVO.getLesson_time())+ "','" + courseVO.getTeacher() + "','" + courseVO.getClassroom() + "','" + courseVO.getIsShared() + "','" + courseVO.getLesson_id() + "')";
                statement.execute(sql);
                return ResponseVO.buildSuccess();
            } catch (Exception e) {
                return ResponseVO.buildFailure(e.getMessage());
            }
        }
    }

    @Override
    public ResponseVO deleteLesson(String courseId) throws SQLException {
        Connection connection= jdbc.getConnection();
        Statement statement=connection.createStatement();
        String sql="delete from 课程表 where 编号='"+courseId+"'";
        statement.execute(sql);
        return ResponseVO.buildSuccess();
    }

    @Override
    public ResponseVO getStuInfo(String account) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql1="select * from 账户表 where 账户名='"+account+"'";
        ResultSet resultSet1=statement.executeQuery(sql1);
        String sno="";
        String pwd="";
        if(resultSet1.next()){
            sno=resultSet1.getString("客体");
            pwd=resultSet1.getString("密码");
        }
        String sql = "select * from 学生表 where 学号='" + sno + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        StudentVO studentVO = new StudentVO();
        if (resultSet.next()) {
            studentVO.setId(resultSet.getString("学号"));
            studentVO.setAccount(account);
            studentVO.setName(resultSet.getString("姓名"));
            studentVO.setDepartment(resultSet.getString("专业"));
            studentVO.setSex(resultSet.getString("性别"));
            studentVO.setPassword(pwd);
        }
        return ResponseVO.buildSuccess(studentVO);
    }

    @Override
    public ResponseVO getLessonInfo() throws SQLException, DocumentException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<CourseVO> courseVOS = new ArrayList<>();
        while (resultSet.next()) {
            CourseVO courseVO = new CourseVO();
            courseVO.setTeacher(resultSet.getString("老师"));
            courseVO.setLesson_id(resultSet.getString("编号"));
            courseVO.setLesson_name(resultSet.getString("名称"));
            courseVO.setLesson_point(Integer.valueOf(resultSet.getString("学分")));
            courseVO.setLesson_time(Integer.valueOf(resultSet.getString("课时")));
            courseVO.setIsShared(resultSet.getString("共享"));
            courseVO.setClassroom(resultSet.getString("地点"));
            courseVOS.add(courseVO);
        }
        //TODO 服务端传来其他课程的数据
        String url="http://localhost:8093/integrate/api/getShareLessonFromAandC";
        ResponseEntity<ResponseVO> responseEntity=restTemplate.getForEntity(url,ResponseVO.class);
        ResponseVO responseVO=responseEntity.getBody();
        String xml=(String) responseVO.getContent();
        Tool tool=new Tool();
        tool.stringToFile(xml,"doc/1.xml");
        SAXReader reader=new SAXReader();
        Document document=reader.read(new File("doc/1.xml"));
        Element classes=document.getRootElement();
        Iterator iterator=classes.elementIterator();
        while (iterator.hasNext()){
            CourseVO courseVO=new CourseVO();
            Element class1=(Element) iterator.next();
            Iterator iterator1=class1.elementIterator();
            while (iterator1.hasNext()){
                Element element=(Element) iterator1.next();
                switch (element.getName()){
                    case "编号":
                        courseVO.setLesson_id(element.getStringValue());
                        break;
                    case "名称":
                        courseVO.setLesson_name(element.getStringValue());
                        break;
                    case "学分":
                        courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                        break;
                    case "课时":
                        courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                        break;
                    case "老师":
                        courseVO.setTeacher(element.getStringValue());
                        break;
                    case "地点":
                        courseVO.setClassroom(element.getStringValue());
                        break;
                    case "共享":
                        courseVO.setIsShared(element.getStringValue());
                }
            }
            courseVOS.add(courseVO);
        }
        return ResponseVO.buildSuccess(courseVOS);
    }

    @Override
    public ResponseVO changePassword(LoginVO loginVO) throws SQLException {
        if (loginVO.getPassword().length() > 12) {
            return ResponseVO.buildFailure("新密码长度不能超过12");
        }
        if (loginVO.getPassword().length() == 0) {
            return ResponseVO.buildFailure("新密码不能为空");
        }
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 账户表 where 账户名='" + loginVO.getAccount() + "' and 密码='" + loginVO.getPassword() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            return ResponseVO.buildFailure("密码不能与现密码一致");
        } else {
            sql = "update 账户表 set 密码='" + loginVO.getPassword() + "' where 账户名='" + loginVO.getAccount() + "'";
            statement.executeUpdate(sql);
            return ResponseVO.buildSuccess();
        }
    }

    @Override
    public ResponseVO chooseLesson(ChooseVO chooseVO) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表 where 编号='" + chooseVO.getCourseId() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            sql = "insert into 选课表(课程编号,学号)values ('" + chooseVO.getCourseId() + "','" + chooseVO.getStudentId() + "')";
            Statement statement1 = connection.createStatement();
            statement1.execute(sql);
            return ResponseVO.buildSuccess();
        } else {
            //TODO 交给集成服务端处理其他院系的选课
            Document document=DocumentHelper.createDocument();
            document.setXMLEncoding("UTF-8");
            Element choices=document.addElement("choices");
            Element choice=choices.addElement("choice");
            choice.addElement("学号").addText(chooseVO.getStudentId());
            choice.addElement("课程编号").addText(chooseVO.getCourseId());
            choice.addElement("得分").addText(String.valueOf(chooseVO.getGrade()));
            String xml=document.asXML();
            String url="http://localhost:8093/integrate/api/chooseLessonAandC";
            restTemplate.postForEntity(url,xml,ResponseVO.class);
            return ResponseVO.buildSuccess();
        }
    }

    @Override
    public ResponseVO getChoosedLesson(String stuId) throws SQLException, DocumentException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 选课表 where 学号='" + stuId + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<CourseVO> courseVOS = new ArrayList<>();
        while (resultSet.next()) {
            Statement statement1 = connection.createStatement();
            sql = "select * from 课程表 where 编号='" + resultSet.getString("课程编号") + "'";
            ResultSet resultSet1 = statement1.executeQuery(sql);
            if (resultSet1.next()) {
                CourseVO courseVO = new CourseVO();
                courseVO.setTeacher(resultSet1.getString("老师"));
                courseVO.setLesson_id(resultSet1.getString("编号"));
                courseVO.setLesson_name(resultSet1.getString("名称"));
                courseVO.setLesson_point(Integer.valueOf(resultSet1.getString("学分")));
                courseVO.setLesson_time(Integer.valueOf(resultSet1.getString("课时")));
                courseVO.setIsShared(resultSet1.getString("共享"));
                courseVO.setClassroom(resultSet1.getString("地点"));
                courseVOS.add(courseVO);
            }
        }
        //TODO 从其他院系得到该同学的选课记录
        String url="http://localhost:8093/integrate/api/getChoosedLessonAandC/"+stuId;
        String xml=(String) restTemplate.getForEntity(url,ResponseVO.class).getBody().getContent();
        Tool tool=new Tool();
        tool.stringToFile(xml,"doc/1.xml");
        SAXReader reader=new SAXReader();
        Document document=reader.read(new File("doc/1.xml"));
        Element classes=document.getRootElement();
        Iterator iterator=classes.elementIterator();
        while (iterator.hasNext()){
            CourseVO courseVO=new CourseVO();
            Element class1=(Element) iterator.next();
            Iterator iterator1=class1.elementIterator();
            while (iterator1.hasNext()) {
                Element element = (Element) iterator1.next();
                switch (element.getName()) {
                    case "编号":
                        courseVO.setLesson_id(element.getStringValue());
                        break;
                    case "名称":
                        courseVO.setLesson_name(element.getStringValue());
                        break;
                    case "学分":
                        courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                        break;
                    case "课时":
                        courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                        break;
                    case "老师":
                        courseVO.setTeacher(element.getStringValue());
                        break;
                    case "地点":
                        courseVO.setClassroom(element.getStringValue());
                        break;
                    case "共享":
                        courseVO.setIsShared(element.getStringValue());
                }
            }
            courseVOS.add(courseVO);
        }
        return ResponseVO.buildSuccess(courseVOS);
    }

    @Override
    public ResponseVO dropLesson(ChooseVO chooseVO) throws SQLException, DocumentException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "delete from 选课表 where 学号='" + chooseVO.getStudentId() + "' and 课程编号='" + chooseVO.getCourseId() + "'";
        statement.execute(sql);
        sql = "select * from 课程表 where 编号='" + chooseVO.getCourseId() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        CourseVO courseVO = new CourseVO();
        if (resultSet.next()) {
            courseVO.setClassroom(resultSet.getString("地点"));
            courseVO.setLesson_id(resultSet.getString("编号"));
            courseVO.setTeacher(resultSet.getString("老师"));
            courseVO.setLesson_point(Integer.valueOf(resultSet.getString("学分")));
            courseVO.setLesson_time(Integer.valueOf(resultSet.getString("课时")));
            courseVO.setIsShared(resultSet.getString("共享"));
            courseVO.setLesson_name(resultSet.getString("名称"));
        }
        else {
            //TODO 有可能数据在其他院系，向集成服务器发送信息
            String xml="";
            Document document=DocumentHelper.createDocument();
            document.setXMLEncoding("UTF-8");
            Element choices=document.addElement("choices");
            Element choice=choices.addElement("choice");
            choice.addElement("学号").addText(chooseVO.getStudentId());
            choice.addElement("课程编号").addText(chooseVO.getCourseId());
            choice.addElement("得分").addText(String.valueOf(chooseVO.getGrade()));
            xml=document.asXML();
            String url="http://localhost:8093/integrate/api/dropLessonAandC";
            ResponseEntity<ResponseVO> responseEntity=restTemplate.postForEntity(url,xml,ResponseVO.class);
            String result=(String) responseEntity.getBody().getContent();
            Tool tool=new Tool();
            tool.stringToFile(result,"doc/1.xml");
            SAXReader reader=new SAXReader();
            Document document1=reader.read(new File("doc/1.xml"));
            Element classes=document1.getRootElement();
            Iterator iterator=classes.elementIterator();
            while (iterator.hasNext()){
                Element class1=(Element) iterator.next();
                Iterator iterator1=class1.elementIterator();
                while (iterator1.hasNext()) {
                    Element element = (Element) iterator1.next();
                    switch (element.getName()) {
                        case "编号":
                            courseVO.setLesson_id(element.getStringValue());
                            break;
                        case "名称":
                            courseVO.setLesson_name(element.getStringValue());
                            break;
                        case "学分":
                            courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                            break;
                        case "课时":
                            courseVO.setLesson_point(Integer.valueOf(element.getStringValue()));
                            break;
                        case "老师":
                            courseVO.setTeacher(element.getStringValue());
                            break;
                        case "地点":
                            courseVO.setClassroom(element.getStringValue());
                            break;
                        case "共享":
                            courseVO.setIsShared(element.getStringValue());
                    }
                }
            }
        }
        return ResponseVO.buildSuccess(courseVO);
    }

    @Override
    public ResponseVO dropLesson_clientB(String xml) throws DocumentException, SQLException {
        Tool tool = new Tool();
        tool.stringToFile(xml, "doc/1.xml");
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File("doc/1.xml"));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        ChooseVO chooseVO = new ChooseVO();
        while (iterator.hasNext()) {
            Element choose = (Element) iterator.next();
            Iterator iterator1 = choose.elementIterator();
            while (iterator1.hasNext()) {
                Element stuChild = (Element) iterator1.next();
                switch (stuChild.getName()) {
                    case "课程编号":
                        chooseVO.setCourseId(stuChild.getStringValue());
                        break;
                    case "学号":
                        chooseVO.setStudentId(stuChild.getStringValue());
                        break;
                }
            }
        }
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "delete from 选课表 where 学号='" + chooseVO.getStudentId() + "' and 课程编号='" + chooseVO.getCourseId() + "'";
        statement.execute(sql);
        sql = "select * from 课程表 where 编号='" + chooseVO.getCourseId() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        String requestXml = "";
        if (resultSet.next()) {
            Document document1 = DocumentHelper.createDocument();
            document1.setXMLEncoding("UTF-8");
            Element classes = document1.addElement("classes");
            Element class1 = classes.addElement("class");
            class1.addElement("编号").addText(resultSet.getString("编号"));
            class1.addElement("名称").addText(resultSet.getString("名称"));
            class1.addElement("学分").addText(resultSet.getString("学分"));
            class1.addElement("课时").addText(resultSet.getString("课时"));
            class1.addElement("老师").addText(resultSet.getString("老师"));
            class1.addElement("地点").addText(resultSet.getString("地点"));
            class1.addElement("共享").addText(resultSet.getString("共享"));
            requestXml = document1.asXML();
        }
        return ResponseVO.buildSuccess(requestXml);
    }

    @Override
    public ResponseVO getChoosedLesson_clientB(String stuId) throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 选课表 where 学号='" + stuId + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<CourseVO> courseVOS = new ArrayList<>();
        while (resultSet.next()) {
            CourseVO courseVO = new CourseVO();
            String cno = resultSet.getString("课程编号");
            Statement statement1 = connection.createStatement();
            sql = "select * from 课程表 where 编号='" + cno + "'";
            ResultSet resultSet1 = statement1.executeQuery(sql);
            if (resultSet1.next()) {
                courseVO.setLesson_name(resultSet1.getString("名称"));
                courseVO.setLesson_id(resultSet1.getString("编号"));
                courseVO.setLesson_point(Integer.valueOf(resultSet1.getString("学分")));
                courseVO.setLesson_time(Integer.valueOf(resultSet1.getString("课时")));
                courseVO.setClassroom(resultSet1.getString("地点"));
                courseVO.setTeacher(resultSet1.getString("老师"));
                courseVO.setIsShared(resultSet1.getString("共享"));
                courseVOS.add(courseVO);
            }
        }
        String requestXml = "";
        Document document1 = DocumentHelper.createDocument();
        document1.setXMLEncoding("UTF-8");
        Element classes = document1.addElement("classes");
        for (int i = 0; i < courseVOS.size(); i++) {
            Element class1 = classes.addElement("class");
            CourseVO courseVO = courseVOS.get(i);
            class1.addElement("编号").addText(courseVO.getLesson_id());
            class1.addElement("名称").addText(courseVO.getLesson_name());
            class1.addElement("学分").addText(String.valueOf(courseVO.getLesson_point()));
            class1.addElement("课时").addText(String.valueOf(courseVO.getLesson_time()));
            class1.addElement("老师").addText(courseVO.getTeacher());
            class1.addElement("地点").addText(courseVO.getClassroom());
            class1.addElement("共享").addText(courseVO.getIsShared());
        }
        requestXml = document1.asXML();
        return ResponseVO.buildSuccess(requestXml);
    }

    @Override
    public ResponseVO chooseLesson_clientB(String xml) throws DocumentException, SQLException {
        Tool tool = new Tool();
        tool.stringToFile(xml, "doc/1.xml");
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File("doc/1.xml"));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        ChooseVO chooseVO = new ChooseVO();
        while (iterator.hasNext()) {
            Element choose = (Element) iterator.next();
            Iterator iterator1 = choose.elementIterator();
            while (iterator1.hasNext()) {
                Element stuChild = (Element) iterator1.next();
                switch (stuChild.getName()) {
                    case "课程编号":
                        chooseVO.setCourseId(stuChild.getStringValue());
                        break;
                    case "学号":
                        chooseVO.setStudentId(stuChild.getStringValue());
                        break;
                }
            }
        }
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表 where 编号='" + chooseVO.getCourseId() + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            sql = "select * from 学生表 where 学号='" + chooseVO.getStudentId() + "'";
            ResultSet resultSet1=statement.executeQuery(sql);
            if(!resultSet1.next()){
                sql = "insert into 学生表 (学号)values('" + chooseVO.getStudentId() + "')";
                statement.execute(sql);
            }
            sql = "insert into 选课表(课程编号,学号) values ('" + chooseVO.getCourseId() + "','" + chooseVO.getStudentId() + "')";
            statement.execute(sql);
        }
        return ResponseVO.buildSuccess();
    }

    @Override
    public ResponseVO getShareLesson_clientB() throws SQLException {
        Connection connection = jdbc.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select * from 课程表 where 共享='1'";
        ResultSet resultSet = statement.executeQuery(sql);
        String string = "";
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("UTF-8");
        Element classes = document.addElement("classes");
        while (resultSet.next()) {
            Element class1 = classes.addElement("class");
            class1.addElement("编号").addText(resultSet.getString("编号"));
            class1.addElement("名称").addText(resultSet.getString("名称"));
            class1.addElement("学分").addText(resultSet.getString("学分"));
            class1.addElement("课时").addText(resultSet.getString("课时"));
            class1.addElement("老师").addText(resultSet.getString("老师"));
            class1.addElement("地点").addText(resultSet.getString("地点"));
            class1.addElement("共享").addText(resultSet.getString("共享"));
        }
        string = document.asXML();
        return ResponseVO.buildSuccess(string);
    }
}