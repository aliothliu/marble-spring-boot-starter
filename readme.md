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

#### 一、安装依赖
```
<dependency>
    <groupId>io.github.aliothliu</groupId>
    <artifactId>marble</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### 二、启用配置

```
@SpringBootApplication(
	scanBasePackageClasses = {
		MarbleConfiguration.class,
		<YourSpringApplication> .class
	}
)
public class YourSpringApplication {
	....
}
```

**注意：** 如使用ACL则进行额外的配置

```
@EnableJpaRepositories(basePackages = {"Your package"},  repositoryFactoryBeanClass = AclJpaRepositoryFactoryBean.class)
```

#### 三、配置文件

```
spring:
  jpa:
    hibernate:
      naming:
        # 配置命名策略
        physical-strategy: io.github.aliothliu.marble.infrastructure.jpa.ConfigurableSpringPhysicalNamingStrategy
marble:
  rbac:
    web:
      # 配置api路径前缀
      prefix: api
    jpa:
      # 配置数据库表名前缀
      ruleTableName: rbac_rule
      roleTableName: rbac_role
      menuTableName: rbac_menu
      menuPathTableName: rbac_menu_path
      pageTableName: rbac_page
      pageElementTableName: rbac_page_element
      pageElementRefTableName: rbac_ref_page_element
```

#### 四、RBAC使用

1. 如何使用 Subject

`Subject subject = new RbacSubject("user identity"); 用以构建Subject对象，其中构造函数参数为用户的唯一标识，可以通过Spring Security或者Apache Shiro获取当前登录用户

2. 查询当前登录用户的菜单树

```
Subject subject = new RbacSubject("user identity");
subject.loadMenuForest();
```

#### 五、ACL使用

1. 配置JpaAclStrategy

```
@Component(value = "CreatorJpaAclStrategy")
public class CreatorJpaAclStrategy<T> implements JpaAclStrategy<T> {

    @Override
    public Optional<Specification<T>> criteria() {
        return Optional.of((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("createdBy"), "admin");
        });
    }
}
```

"admin"字符串可以替换成当前登录用户的ID，实现登录用户的数据隔离。此策略可以定义多种不同的实现，如可以配置基于角色控制的数据隔离。

2. 配置Entity

```
@Entity
@Acl(strategy = CreatorJpaAclStrategy.class)
public class Custom {

    @Id
    private String id;

    private String name;

    private String department;

    private String createdBy;
```

使用注解 @Acl 来注释当前实体对应的数据隔离权限的策略，若实体不需要数据权限隔离，可以不注解或者使用@NoAcl


