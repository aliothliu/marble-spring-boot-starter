### 一个开箱即用的RBAC框架

![JDK](https://img.shields.io/badge/JDK-1.8+-green?logo=appveyor)
![SpringBoot](https://img.shields.io/badge/springboot-%202.x-green?logo=appveyor)

### Marble

Marble是一个基于Spring Boot的开箱即用的RBAC框架，允许开发者通过简单配置即可进行业务逻辑开发，重点关注在应用系统建设中必要的菜单管理、角色管理、权限过滤等内容，以帮助开发者避免在系统中引入重复的权限管理相关代码。

* 支持按钮级别功能管理
* 支持基于策略的数据权限

#### 设计示意图

![img](https://github.com/aliothliu/marble-spring-boot-starter/docs/architecture.png)

* Resource: 描述系统内需要作为权限管理的资源，包括菜单、页面、元素等
* Casbin: 访问控制模型的基础框架，更多信息可点击[参考链接](https://github.com/casbin)
* Subject: 权限的集合主体，可以是角色、角色组等

### 开始使用