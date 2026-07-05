# Campus Community 校园社区

基于 Spring Boot 构建的校园社区后端服务，提供优惠券领取与使用管理系统。

## 技术栈

- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0 + MyBatis
- **缓存**: Redis
- **语言**: Java 8
- **构建工具**: Maven

## 功能模块

### 用户模块
- 用户注册与登录
- 用户信息查询

### 优惠券模块
- 优惠券列表查询
- 优惠券详情查询
- 优惠券创建与删除

### 订单模块
- 优惠券领取
- 订单列表查询
- 优惠券使用

### 秒杀服务
- 高并发秒杀抢购
- Redis 分布式锁

### 签到服务
- 每日签到奖励

## 快速开始

### 环境要求
- JDK 8+
- MySQL 8.0+
- Redis 6.0+

### 数据库配置

创建数据库 `campus`，并修改 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 运行项目

```bash
mvn spring-boot:run
```

服务启动后访问：`http://localhost:9090`

## API 接口

### 用户接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/user/login | 用户登录 |
| POST | /api/user/register | 用户注册 |
| GET | /api/user/profile | 获取用户信息 |

### 优惠券接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/coupon/list | 获取优惠券列表 |
| GET | /api/coupon/{id} | 获取优惠券详情 |
| POST | /api/coupon | 创建优惠券 |
| DELETE | /api/coupon/{id} | 删除优惠券 |

### 订单接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/order/claim | 领取优惠券 |
| GET | /api/order/list | 获取订单列表 |
| PUT | /api/order/use/{id} | 使用优惠券 |

## 项目结构

```
src/main/java/com/campus/
├── controller/     # 控制层
├── service/        # 业务逻辑层
├── mapper/         # 数据访问层
├── entity/         # 实体类
├── dto/            # 数据传输对象
├── config/         # 配置类
└── CampusApplication.java
```

## License

MIT License