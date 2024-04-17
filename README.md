# Weixin-Mini-Project-Backend

## 1 概述

本项目是2022微信小程序大赛 后端项目

技术栈：Springboot + MyBatis + Redis+ MySQL

部署： Docker + Jenkins + GitLab

**Jenkins**: The leading open source automation server, Jenkins provides hundreds of plugins to support building, deploying and automating any project. Doc: [Jenkins](https://www.jenkins.io/)

**Docker**: Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure so you can deliver software quickly. With Docker, you can manage your infrastructure in the same ways you manage your applications. By taking advantage of Docker’s methodologies for shipping, testing, and deploying code quickly, you can significantly reduce the delay between writing code and running it in production. Doc: [Docker](https://www.docker.com/)

其他库实现：

1. OCR：PaddleOCR
2. 目前想要实现的中文字符串匹配 + 语义识别：
   1. NLP分词(对备注进行分词) + 提取文本特征
   2. K-means or 其他算法来聚类？
   3. 计算文本相似度？
   4. 语义分析：深度学习模型？短文本语义分析模型？
   5. tag之间的匹配
   6. 物品名称之间的关联度分析
3. Token认证和权限管理：Jwt + SpringSecurity
4. 数据缓存：Redis
5. 文档生成工具：Swagger2 && ApiFox



### 1.1 如何确定接口？

请确定你所需要返回的信息和你想要传递的参数 你可以从数据库设计中的PO的字段中选取你所需要的信息。



## 2 数据库设计

### 2.1 上报表

```mysql
CREATE TABLE IF NOT EXISTS `nucleic_acid_info`
(
	`info_id`             BIGINT                        NOT NULL AUTO_INCREMENT,
	`user_id`             BIGINT                        NOT NULL COMMENT '被通知的用户id',
	`manager_id`          BIGINT      DEFAULT 0         NOT NULL COMMENT '发布该通知的管理员id',
	-- Date 无法设置默认值 TIMESTAMP 可以用 NOW() 来设置 --
	`info_deadline`       DATETIME                      NOT NULL COMMENT '该次上报截止的时间',
	`info_is_open_remind` BOOL        DEFAULT FALSE     NOT NULL COMMENT '是否打开提醒',
	`info_title`          VARCHAR(60) DEFAULT '请填写标题'   NOT NULL COMMENT '上报的标题',
	`info_status`         VARCHAR(10) DEFAULT '进行中'     NOT NULL COMMENT '是否已经完成该通知',
	`info_image_name`     VARCHAR(50) DEFAULT 'UNKNOWN' NOT NULL COMMENT '核酸截图的文件路径',
	CONSTRAINT PRIMARY KEY (`info_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.2 检测表

```mysql
CREATE TABLE IF NOT EXISTS `nucleic_acid_testing`
(
	`testing_id`             BIGINT                       NOT NULL AUTO_INCREMENT,
	`user_id`                BIGINT                       NOT NULL COMMENT '用户id',
	`manager_id`             BIGINT       DEFAULT 0 COMMENT '管理员id',
	`testing_require`        VARCHAR(100) DEFAULT '无要求'   NOT NULL COMMENT '核酸检测要求',
	`testing_title`          VARCHAR(60)  DEFAULT '请填写标题' NOT NULL COMMENT '上报的标题',
	`testing_start_time`     DATETIME                     NOT NULL COMMENT '检测开始时间',
	`testing_end_time`       DATETIME                     NOT NULL COMMENT '检测结束时间',
	`testing_is_open_remind` BOOL         DEFAULT FALSE   NOT NULL COMMENT '是否打开核酸检测提醒',
	`testing_place`          VARCHAR(100) DEFAULT '未指定'   NOT NULL COMMENT '核酸检测地点',
	`testing_finish_status`  VARCHAR(10)  DEFAULT '进行中'   NOT NULL COMMENT '核酸检测完成状态',
	CONSTRAINT PRIMARY KEY (`testing_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.3 预约表

```mysql
DROP TABLE IF EXISTS `nucleic_acid_booking`;
CREATE TABLE IF NOT EXISTS `nucleic_acid_booking`
(
	`booking_id`             BIGINT                    NOT NULL AUTO_INCREMENT,
	`user_id`                BIGINT                    NOT NULL COMMENT '预约通知对应的user id',
	`manager_id`             BIGINT      DEFAULT 0     NOT NULL COMMENT '发布通知对应的manager id',
	`booking_title`          VARCHAR(40) DEFAULT '未指定' COMMENT '通知标题',
	`booking_deadline`       DATETIME                  NOT NULL COMMENT '预约截止时间',
	`booking_is_open_remind` BOOL        DEFAULT FALSE NOT NULL COMMENT '是否打开通知提醒',
	`booking_finish_status`  VARCHAR(10) DEFAULT '进行中' NOT NULL COMMENT '预约完成状态',
	CONSTRAINT PRIMARY KEY (`booking_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.4 用户表

```mysql
CREATE TABLE IF NOT EXISTS `user`
(
	`user_id`        BIGINT                                   NOT NULL AUTO_INCREMENT,
	`user_open_id`   VARCHAR(60)                              NOT NULL COMMENT '微信openid',
	`user_nick_name` VARCHAR(40)            DEFAULT 'UNKNOWN' NOT NULL COMMENT '用户昵称',
	`user_name`      VARCHAR(40)            DEFAULT 'UNKNOWN' NOT NULL COMMENT '用户真实姓名',
	`user_school`    VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户学校',
	`user_institute` VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户院系',
	`user_major`     VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户专业',
	`user_grade`     VARCHAR(10)            DEFAULT '未选定'     NOT NULL COMMENT '用户年级',
	`user_gender`    ENUM ('男', '女', '不清楚') DEFAULT '不清楚'     NOT NULL COMMENT '用户性别',
	user_avatar_url  VARCHAR(200)           DEFAULT ''        NOT NULL,
	CONSTRAINT PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.5 信息表

```mysql
CREATE TABLE IF NOT EXISTS `message`
(
	`message_id`           BIGINT                      NOT NULL AUTO_INCREMENT,
	`message_sender_id`    BIGINT                      NOT NULL COMMENT '发送消息的用户id',
	`message_receiver_id`  BIGINT                      NOT NULL COMMENT '接收消息的用户id',
	`message_content`      VARCHAR(2000) DEFAULT ''    NOT NULL COMMENT '消息内容',
	`message_send_time`    DATETIME                    NOT NULL COMMENT '发送该消息的时间',
	`message_is_show_time` BOOL          DEFAULT FALSE NOT NULL COMMENT '是否显示该消息的时间',
	CONSTRAINT PRIMARY KEY (`message_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.6 帮助信息表

```mysql
CREATE TABLE IF NOT EXISTS `help_info`
(
	`help_id`           BIGINT                     NOT NULL AUTO_INCREMENT,
	`user_id`           BIGINT                     NOT NULL COMMENT '发布该帮忙信息的用户id',
	`help_type`         VARCHAR(10)                NOT NULL COMMENT '帮忙的类型',
	`help_publish_date` DATETIME                   NOT NULL COMMENT '该信息发布的时间',
	`help_name`         VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '帮忙的物品名字',
	`help_deadline`     DATETIME                   NOT NULL COMMENT '帮忙的截止日期',
	`help_comment`      VARCHAR(200) DEFAULT '未指定' NOT NULL COMMENT '备注',
	`help_tag`          VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '所属的tag',
	`help_urgency`      INT          DEFAULT 0     NOT NULL COMMENT '紧急程度 1-5',
	CONSTRAINT PRIMARY KEY (`help_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.7 帮助信息图片表

```mysql
CREATE TABLE IF NOT EXISTS `help_image`
(
	`image_id` BIGINT NOT NULL AUTO_INCREMENT,
	`help_id`  BIGINT NOT NULL COMMENT '帮助通知的id',
	`image_url` VARCHAR(100) DEFAULT '未指定' NOT NULL COMMENT '帮助的图片',
	CONSTRAINT PRIMARY KEY (`image_id`)
)ENGINE = InnoDB CHARSET = utf8;
```

### 2.8 帮助信息搜索历史表

```mysql
CREATE TABLE IF NOT EXISTS `help_search_history`
(
	`history_id`          BIGINT      NOT NULL AUTO_INCREMENT,
	`user_id`             BIGINT      NOT NULL COMMENT '搜索的用户id',
	`history_search_time` DATETIME    NOT NULL COMMENT '搜索的时间',
	`history_keyword`     VARCHAR(30) NOT NULL COMMENT '搜索的关键词',
	CONSTRAINT PRIMARY KEY (`history_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.9 求助信息表

```mysql
CREATE TABLE IF NOT EXISTS `seek_help_info`
(
	`seek_help_id`           BIGINT                     NOT NULL AUTO_INCREMENT,
	`user_id`                BIGINT                     NOT NULL COMMENT '发布该求助信息的用户id',
	`seek_help_type`         VARCHAR(10)                NOT NULL COMMENT '求助的类型',
	`seek_help_publish_date` DATETIME                   NOT NULL COMMENT '该求助信息发布的时间',
	`seek_help_name`         VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '求助的物品名字',
	`seek_help_deadline`     DATETIME                   NOT NULL COMMENT '求助的截止日期',
	`seek_help_comment`      VARCHAR(200) DEFAULT '未指定' NOT NULL COMMENT '备注',
	`seek_help_tag`          VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '所属的tag',
	`seek_help_urgency`      INT          DEFAULT 0     NOT NULL COMMENT '紧急程度 1-5',
	CONSTRAINT PRIMARY KEY (`seek_help_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.10 求助信息图片表

```mysql
CREATE TABLE IF NOT EXISTS `seek_help_image`
(
	`image_id`     BIGINT                     NOT NULL AUTO_INCREMENT,
	`seek_help_id` BIGINT                     NOT NULL COMMENT '帮助通知的id',
	`image_url`    VARCHAR(100) DEFAULT '未指定' NOT NULL COMMENT '帮助的图片',
	CONSTRAINT PRIMARY KEY (`image_id`)
) ENGINE = InnoDB
  CHARSET = utf8
```

### 2.11 求助信息搜索历史表

```mysql
CREATE TABLE IF NOT EXISTS `seek_help_search_history`
(
	`history_id`          BIGINT      NOT NULL AUTO_INCREMENT,
	`user_id`             BIGINT      NOT NULL COMMENT '搜索的用户id',
	`history_search_time` DATETIME    NOT NULL COMMENT '搜索的时间',
	`history_keyword`     VARCHAR(30) NOT NULL COMMENT '搜索的关键词',
	CONSTRAINT PRIMARY KEY (`history_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.12 求助信息用户点击表

```mysql
CREATE TABLE IF NOT EXISTS `seek_help_info_click`
(
	`click_id`         BIGINT NOT NULL AUTO_INCREMENT,
	`user_id`          BIGINT NOT NULL COMMENT '点击的用户id',
	`seek_help_id`     BIGINT NOT NULL COMMENT '帮助信息的id',
	`click_start_time` DATETIME DEFAULT NULL COMMENT '点击该帮助信息的时间',
	`click_end_time`   DATETIME DEFAULT NULL COMMENT '退出该帮助信息页面的时间',
	CONSTRAINT PRIMARY KEY (`click_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

### 2.13 帮助信息用户点击表

```mysql
CREATE TABLE IF NOT EXISTS `help_info_click`
(
	`click_id`         BIGINT NOT NULL AUTO_INCREMENT,
	`user_id`          BIGINT NOT NULL COMMENT '点击的用户id',
	`help_id`          BIGINT NOT NULL COMMENT '帮助信息的id',
	`click_start_time` DATETIME DEFAULT NULL COMMENT '点击该帮助信息的时间',
	`click_end_time`   DATETIME DEFAULT NULL COMMENT '退出该帮助信息页面的时间',
	CONSTRAINT PRIMARY KEY (`click_id`)
) ENGINE = InnoDB
  CHARSET = utf8;
```

## 3 逻辑层设计

### 3.1 UserService

用户的业务主要包括以下几个：

1. 获取用户访问微信后台api的 `access_token`
2. 获取用户能够在微信唯一的`open_id`和`session_key`
3. 在小程序的注册登录功能
4. 注册后快捷登录获取token的功能

### 3.2 TaskService

任务业务主要是能够开启微信端的提醒服务，能够将核酸预约、检测、上报通知通过微信api来通知到用户的微信端，该业务主要包括以下逻辑：

1. 定时发送请求到微信服务端，提醒用户通知
1. 定时转换核酸预约、核酸检测、核酸上报的通知状态
1. 定时转换帮助信息和求助信息的状态
2. 停止还未发送的提醒。

主要实现逻辑是用了线程池和 `ScheduledFuture` 类，该类允许提交一个线程任务并直到某个时间执行。线程池用的是Springboot的定时服务 `ThreadPoolTaskScheduler`

### 3.3 NucleicAcidInfoService

这部分的业务主要是核酸上报的业务逻辑，主要流程为管理端发布核酸上报的通知，微信小程序端的用户可以看到通知的具体信息，并可以选择是否开启提醒等，同时允许用户在小程序客户端上传核酸检测的结果截图，通过OCR进行识别来自动完成核酸上报的通知。该部分用户允许的操作如下：

1. 开启或关闭某个核酸上报通知的提醒
2. 上传核酸检测的结果截图，完成对应的核酸上报通知。
3. 查看自己的核酸上报通知。

主要实现逻辑就是 `Controller` - `Service` - `Mapper` 的分层访问逻辑，下层为上层提供接口服务，上层调用下层的接口来完成对更上层的服务实现，最终将结果存储在数据库中。

### 3.4 NucleicAcidBookingService

这部分的业务主要是核酸预约的业务逻辑，主要流程为管理端发布核酸预约的通知，微信小程序端的用户可以看到通知的具体信息，并可以选择是否开启提醒等，该部分允许用户的操作如下：

1. 开启或关闭某个核酸预约通知的提醒
2. 查看自己的核酸预约通知。
3. 将核酸预约通知设置为已完成

主要实现逻辑就是 `Controller` - `Service` - `Mapper` 的层级访问逻辑，下层为上层提供接口服务，上层调用下层的接口来完成对更上层的服务实现，最终将结果存储在数据库中。

### 3.5 NucleicAcidTestingService

这部分的业务主要是核酸检测的业务逻辑，主要流程为管理端发布核酸检测的通知，微信小程序端的用户可以看到通知的具体信息，并可以选择是否开启提醒等，该部分允许用户的操作如下：

1. 开启或关闭某个核酸检测通知的提醒
2. 查看自己的核酸检测通知。
3. 将核酸检测通知设置为已完成

主要实现逻辑就是 `Controller` - `Service` - `Mapper` 的层级访问逻辑，下层为上层提供接口服务，上层调用下层的接口来完成对更上层的服务实现，最终将结果存储在数据库中。


### 3.6 HelpInfoService

### 3.7 SeekHelpInfoService

### 3.8 OCR介绍

OCR采用了百度的PaddleOCR。

### 3.9 推荐算法介绍

推荐算法采用的主要是 **<span style='color: red'>基于内容</span>** 的推荐算法，
主要思路就是 **物以类聚** 的思想。该算法的一个中心思想就是其推荐的依据是来源于内容之间的是否相似，
而与其他用户的操作是无关的，是一种比较经典但比较传统的推荐算法。

另一种推荐算法（并非本项目采用的）是 **<span style='color: red'>协同过滤</span>** 的算法，
这种算法的主要思想就是 **物以类聚，人以群分** 的思想。主要是通过用户之间的行为相似度来作为推荐的
依据，比如说用户A与用户B之间相似度比较高，而用户B看过某个内容 $a_1$，那么就可以把这个内容推荐给A。

下图是 **基于内容** 的推荐算法的思路图

![](https://img-bed-1309306776.cos.ap-shanghai.myqcloud.com/img/20220511141713.png)

![](https://img-bed-1309306776.cos.ap-shanghai.myqcloud.com/img/20220511141843.png)

本项目的推荐算法主要是通过用户的历史浏览记录、历史搜索记录和历史发布的帮助/求助信息，计算这些
信息与已有的帮助/求助信息之间的相似度来进行推荐。主要步骤有以下几个步骤：
1. 将标的物文本转换为向量表示（计算后可以离线存储，以便后续反复使用，可以用NLP处理）
2. 计算文本之间的相似度（相似度计算可以采用 **余弦相似度** 计算或者 **皮尔逊相关系数** 来计算，都是计算向量之间的相似度。或者直接采用深度学习 or 神经网络模型）
3. 筛选与去重（对结果进行再处理，排除用户已经浏览过的以及一些非预期的）

#### 3.9.1 相似度计算

本项目对标的物的特征转化为向量表示并进行相似度计算主要使用了百度的AI开源技术，其短文本相似度分析技术主要
采用了 **神经网络** 的预训练模型：知识增强语义表示模型——ERNIE（百度自研），下面文章介绍了该模型的原理，推荐文章：

1. [【ERNIE】深度剖析知识增强语义表示模型——ERNIE](https://cloud.tencent.com/developer/article/1557849)
2. [ERNIE: Enhanced Language Representation with Informative Entities](https://arxiv.org/pdf/1905.07129.pdf)
3. [github.com/thunlp/ERNIE](github.com/thunlp/ERNIE)

#### 3.9.2 推荐

在获得了两个标的物之间的相似度之后，只需要设置 **最小的相似度阙值** 来进行筛选和去重即可。
项目中主要是分三个方面来获取推荐内容：
1. 对用户的浏览记录时间进行排序（即获取用户浏览最长的前几个记录），然后获得浏览时间较长的几个信息的相似度较高的标的物
2. 对用户的历史搜索记录获取相似度较高的标的物内容
3. 对用户的历史发布记录获取相似度较高的标的物内容

这三方面是有优先级与权重存在的，比重大概在5:3:2之间。


## 4 接口设计

目前接口主要分成六个大类，分别是：

1. 用户有关接口，前缀为 `/api/user`
2. 上报有关接口，前缀为 `/api/nucleic-acid/info`
3. 检测有关接口，前缀为 `/api/nucleic-acid/testing`
4. 预约有关接口，前缀为 `/api/nucleic-acid/booking`
5. 信息有关接口，前缀为 `/api/message`
6. 任务有关接口，前缀为 `/api/task`
7. 帮助信息有关接口，前缀为 `/api/help-info`
8. 求助信息有关接口，前缀为 `/api/seek-help`

具体可查看 [Swagger](https://xjk-advisor.com/swagger-ui.html)


## 5 开发进度

目前码量大概在12000行左右

Test覆盖率：Build #277

![](https://img-bed-1309306776.cos.ap-shanghai.myqcloud.com/img/20220502193023.png)

## 6 开发包图

Springboot项目的包图主要分成三大部分，一部分是供前端访问的 `controller` 包，一部分是处理业务逻辑的 `service` 包，一部分是访问数据库、对持久化对象进行访问的 `dao` 包。三个包之间的关系可以简单的描述为 `controller` 包访问 `service` 包提供的接口，`service` 包访问 `dao` 包提供的接口，同时，一个 `Controller` 只与一个 `Service` 产生接口的访问与服务的提供，在 `Service` 内部则充满着互相的依赖和调用。同理，一个 `Service` 也只与一个 `dao` 包中的接口产生接口的访问与服务的提供。

为了抽象化接口，`service` 包下提供了具体实现 `impl` 包和抽象化的 `service`。`dao` 包下提供了访问数据的抽象化接口，而 `mapper` 中则是具体的访问数据库的操作，通过 `mybatis` 框架提供的方便操作，可以实现由 `xml` 文件到 jdbc访问数据库的便捷转换操作。

![](https://img-bed-1309306776.cos.ap-shanghai.myqcloud.com/img/wechat-mini-packager.png)
# yixiaobang-ebse
