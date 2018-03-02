# workreport
管理工作日志

整理每天的工作日志，后续增加对日志的评论功能

使用maven来管理struts2相关的jar依赖
使用maven来进行持续集成

>团队成员表
CREATE TABLE
    t_employee
    (
        usercode VARCHAR(60) NOT NULL,
        username VARCHAR(60),
        phone VARCHAR(13),
        email VARCHAR(60),
        university VARCHAR(40),
        major VARCHAR(40),
        graduateMonth VARCHAR(10),
        entryDate DATE,
        projectname VARCHAR(40),
        remark VARCHAR(255),
        validStatus VARCHAR(2),
        insertTime DATETIME,
        updateTime DATETIME,
        PRIMARY KEY (usercode)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
