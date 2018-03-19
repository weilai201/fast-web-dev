# fast-web-dev

本项目主要为java后端人员开发做参考使用。
工程是基于spring boot 搭建。主要包括便捷的数据库操作以及清晰的开发分层结构。

关键包说明

* **com.fastweb.core**

常用的工具

* **com.fastweb.web.controller**

spring mvc 控制层

* **com.fastweb.web.service**

spring mvc 服务层（业务层）

* **com.fastweb.web.dao**

数据访问层


## 开发说明

操作数据库相关的方式全部在com.fastweb.core.jdbc包中。

此包主要包含两个关键类，CrudRepository与Model。

* **CrudRepository**

包括单表的增、删、改、查等基础功能。

* **Model**

在开发中，我们往往涉及到复杂的数据库表关联查询，那么，可以使用Model类直接操作数据库。

## 启动

直接运行```` com.fastweb.web.BootApplication```` 类。
