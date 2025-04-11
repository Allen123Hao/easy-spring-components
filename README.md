# Easy Spring Components

Easy Spring Components 是一个基于Spring Boot的轻量级开发组件库，旨在简化日常开发中的常见需求。目前包含以下核心组件：

## 组件列表

### 1. EasyExport
Excel导出组件，通过注解方式快速实现Excel导出功能。

#### 主要特性
- 支持通过注解方式配置导出
- 支持同步/异步导出模式
- 支持分页导出大数据量
- 支持动态配置导出列（支持注解配置和nacos动态配置）
- 自动适配列宽
- 支持自定义文件名
- 基于EasyExcel，性能优异
- 支持异步导出时自动上传到OSS

#### 使用示例
```java
@GetMapping("/list")
@SupportExcelExport(
    configStrategy = ExportConfigStrategy.AnnotationConfig,
    enablePaging = true,
    exportFileName = "用户列表",
    exportColumnConfig = {
        @ExportColumnConfig(fieldName = "name", columnName = "姓名"),
        @ExportColumnConfig(fieldName = "age", columnName = "年龄")
    }
)
public ExcelExportResult<UserVO> list(PageParam pageParam) {
    // 你的业务查询逻辑
}
```

### 2. EasyLog
操作日志组件，通过注解方式快速实现操作日志记录。

#### 主要特性
- 支持通过注解方式配置日志记录
- 自动记录接口调用信息（请求参数、执行时间等）
- 支持自定义操作内容解析（基于Aviator表达式）
- 支持异常信息记录
- 支持服务名、模块名配置
- 支持TraceId跟踪
- 支持数据库存储

#### 使用示例
```java
@PostMapping("/create")
@OperationLog(
    serviceName = "用户服务",
    moduleName = "用户管理",
    operationType = "CREATE",
    content = "创建用户：${user.name}"
)
public Result createUser(@RequestBody User user) {
    // 你的业务逻辑
}
```

## 快速开始

### Maven依赖
```xml
<dependency>
    <groupId>com.allen.component</groupId>
    <artifactId>easyexport</artifactId>
    <version>${version}</version>
</dependency>

<dependency>
    <groupId>com.allen.component</groupId>
    <artifactId>easylog</artifactId>
    <version>${version}</version>
</dependency>
```

### 配置说明

#### EasyLog配置
```yaml
easy-log:
  default-config:
    service-name: your-service-name
    module-name: your-module-name
  db-config:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### OSS配置（用于异步导出Excel）
```yaml
ali:
  oss:
    server:
      endpoint: oss-cn-beijing.aliyuncs.com  # OSS服务的Endpoint
      accessKey: your-access-key  # 您的AccessKey ID
      accessSecret: your-access-secret  # 您的AccessKey Secret
      bucketName: your-bucket-name  # Bucket名称
      cdnEndpoint: your-cdn-domain  # CDN域名（如果有）
server:
  cdn:
    url: http://your-cdn-domain/  # CDN访问地址
```

注意：请将OSS配置中的敏感信息（如accessKey、accessSecret）替换为您自己的配置，并确保这些信息的安全性。建议在生产环境中使用配置中心或环境变量来管理这些敏感信息。

## 环境要求
- JDK 17+
- Spring Boot 3.0.0+
- MySQL 8.0+（如果使用EasyLog组件）
- 阿里云OSS（如果使用异步导出功能）

## 依赖说明
- hutool-all: 5.8.15
- easyexcel: 3.3.2
- aviator: 5.3.3
- nacos-client: 2.2.1（可选，用于动态配置）
- aliyun-sdk-oss: 3.17.0（可选，用于异步导出）

## 许可证
Apache License 2.0

## 作者
Allen

## 贡献
欢迎提交Issue和Pull Request 