# SealClass-Server

## 项目介绍
* SealClass-Server 是基于 SpringBoot 框架实现
* 依赖于 MySql 的数据存储，Redis 的数据缓存
* 依赖白板服务，创建、销毁白板
* 依赖融云 IM 服务，收发信令

## 使用方法
* 使用 mysql 执行项目目录下的 [tools/sealclass.sql](https://github.com/rongcloud/sealclass-server/blob/v2.0/tools/sealclass.sql) 和 [sealclass-02.sql](https://github.com/rongcloud/sealclass-server/blob/v2.0/tools/sealclass-02.sql)，创建数据库
* 通过 mvn package 编译出 jar 或者 IntelliJ IDE 运行工程
* 通过 java -jar SealClass-1.0.0-SNAPSHOT.jar 启动服务，默认启用 9999 端口，默认是 HTTP 请求
* 强烈建议开启 HTTPS: [application.properties](https://github.com/rongcloud/sealclass-server/blob/master/src/v2.0/resources/application.properties) 中的 server.ssl.enabled=false，否则可能会出现 web 端无法看到音视频流
* 若您开启了 HTTPS，目前项目中默认使用的是 [sealclass.key](https://github.com/rongcloud/sealclass-server/blob/v2.0/src/main/resources/sealclass.key) 自签证书，Web 端需要添加信任，可以替换成您的正式证书
* 去融云官网注册、申请 AppKey 和 Secret
* 启动服务之后，调用 `Http POST http://localhost:9999/school/create` 创建学校，客户端使用生成的 schoolId 使用。
```json
{
  "appkey": "rongcloud appkey",
  "secret": "rongcloud secret",
  "name": "your school name"
}
```

## 设计文档
* [详细设计文档](https://github.com/rongcloud/sealclass-server/blob/v2.0/tools/%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3.md)

## API

项目使用了 Swagger 的方式进行，您可以通过 `http://localhost:9999/api/v2/swagger-ui.html` 查看响应接口定义。
