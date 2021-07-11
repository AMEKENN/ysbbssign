# 米游社签到(miHoYoSign)

**基于SpringBoot 和 CQHTTP 的 米游社自动签到机器人,支持原神和崩坏3**

- 此软件仅供学习参考,不得用于非法盈利
- 本来只是为了自己用的,不是重大问题,大概率不会继续更新
- 写的很烂,有很多地方不符合规范,还望海涵

## 一. 功能
- 支持多用户,一个QQ号可以绑定一个米游社账号,让你的小伙伴可以一起享受自动签到的乐趣
- 管理员模式（管理员QQ 可以以某个QQ号的身份 向本机器人发送消息）
- 能发送通知,签到是否成功,领了个啥（默认关,且需要加为好友）

## 二. 使用
### 1. 准备
- java运行环境
- MySQL 或 SQLite（SQlite数据库会自动创建,可以不用管）
- OneBot Mirai (CQHTTP Mirai) 或 go-cqhttp 等 OneBot标准环境
- 注意:OneBot Mirai 的群临时消息类型不为private,而为group,不符合OneBot标准,所以必须加为好友才能使用除帮助外的命令
### 2. 编译配置文件
1.新建application.yml,放到mihoyosign.jar的同级目录

application.yml
```yml
debug: false
server:
  # 签到服务器端口
  port: 4404

#[sqlite]删除下面的注释
spring:
  datasource:
    # 数据库驱动
    driver-class-name: org.sqlite.JDBC
    # 数据库地址
    url: jdbc:sqlite:mihoyo_sign.sqlite
  jpa:
    database-platform: com.ame.mihoyosign.config.SQLiteDialect
    hibernate:
      ddl-auto: update
    open-in-view: true

##[mysql]删除下面的注释并修改参数
#spring:
#  datasource:
#    # 数据库驱动
#    driver-class-name: com.mysql.cj.jdbc.Driver
#    # 数据库地址
#    url: jdbc:mysql://192.168.1.103:3306/mihoyo_sign?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
#    # 数据库用户名
#    username: root
#    # 数据库密码
#    password: "123456"
#  jpa:
#    hibernate:
#      ddl-auto: update
#    open-in-view: true

app-config:
  # 帮助的回复
  help: |
    用户命令:
       帮助
       绑定 [*cookie]
       解绑
       开启签到 [游戏名] [游戏角色UID]
       关闭签到 [游戏名] [游戏角色UID]
       签到 [游戏名] [游戏角色UID]
       开启通知 [游戏名] [游戏角色UID]
       关闭通知 [游戏名] [游戏角色UID]
       所有角色
       已开启角色

    管理员命令:
       所有用户角色
       通知 [通知内容] [QQ号]
       全部签到

    例如:
       开启签到 原神 100000000
       开启指定角色的签到

       开启签到 原神
       开启指定游戏的所有角色签到

       开启签到 100000000
       开启指定UID的所有角色签到(如uid重复会都开启)

       开启签到
       开启所有角色签到
    注意:
       带*的为必填参数,不带*的参数可以不写,但不能调换参数的顺序

  # OneBot Mirai地址
  url: http://192.168.1.103:4400
  # 管理员的QQ号
  admin-qq-id: 123456789
  # 日志发送到哪个群
  log-group-id: 987654321
  # 签到计划执行时间
  sign-cron: 0 30 0 * * ?
  # 命令
  #   收到以左列关键字开头的消息,执行对应右列功能
  #   左列不能重复,右列可以重复
  #   记得结尾打英文逗号,最后一行不要打逗号
  commands: "{
    '帮助' : '帮助',
    '绑定' : '绑定',
    '解绑' : '解绑',
    '开启签到' : '开启签到',
    '关闭签到' : '关闭签到',
    '签到' : '签到',
    '开启通知' : '开启通知',
    '关闭通知' : '关闭通知',
    '所有角色' : '所有角色',
    '已开启角色' : '已开启角色',
    '所有用户角色' : '所有用户角色',
    '通知' : '通知',
    '全部签到' : '全部签到'
  }"


# 这些不需要改
ys:
  salt: h8w582wxwgqvahcdkpvdhbh2w9casgfl
  headers: "{
    'User_Agent': 'Mozilla/5.0 (Linux; Android 10; MIX 2 Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.101 Mobile Safari/537.36 miHoYoBBS/2.3.0',
    'Accept': 'application/json, text/plain, */*',
    'Content_Type': 'application/json;charset=UTF-8',
    'Connection': 'keep-alive',
    'Origin': 'https://webstatic.mihoyo.com',
    'X_Requested_With': 'com.mihoyo.hyperion',
    'Sec_Fetch_Site': 'same-site',
    'Sec_Fetch_Mode': 'cors',
    'Sec_Fetch_Dest': 'empty',
    'Accept_Encoding': 'gzip,deflate',
    'Accept_Language': 'zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7',

    'Content-Length': '66',
    'x-rpc-client_type': '5',
    'Referer': 'https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=e202009291139501&utm_source=bbs&utm_medium=mys&utm_campaign=icon',
    'x-rpc-app_version': '2.3.0'
  }"
bh3:
  headers: "{
    'User_Agent': 'Mozilla/5.0 (Linux; Android 10; MIX 2 Build/QKQ1.190825.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.101 Mobile Safari/537.36 miHoYoBBS/2.7.0',
    'Accept': 'application/json, text/plain, */*',
    'Content_Type': 'application/json;charset=UTF-8',
    'Connection': 'keep-alive',
    'Origin': 'https://webstatic.mihoyo.com',
    'X_Requested_With': 'com.mihoyo.hyperion',
    'Sec_Fetch_Site': 'same-site',
    'Sec_Fetch_Mode': 'cors',
    'Sec_Fetch_Dest': 'empty',
    'Accept_Encoding': 'gzip,deflate',
    'Accept_Language': 'zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7',

    'Content-Length': '60',
    'Referer': 'https://webstatic.mihoyo.com/bh3/event/euthenia/index.html?bbs_presentation_style=fullscreen&bbs_game_role_required=bh3_cn&bbs_auth_required=true&act_id=e202104072769&utm_source=bbs&utm_medium=mys&utm_campaign=icon'
  }"
```

2.编辑OneBot Mirai 的 sitting.yml（go-cqhttp等类推）

```yml
debug: false
proxy: ""
#机器人QQ号
'123456789':
  cacheImage: false
  cacheRecord: false
  heartbeat:
    enable: false
    interval: 15000
  http:
    #需要开启
    enable: true
    host: 0.0.0.0
    #HTTP API服务器监听端口
    port: 4400
    accessToken: ""
    postUrl: ""
    postMessageFormat: string
    secret: ""
  ws_reverse:
    # 开启反向代理
    - enable: true
      postMessageFormat: string
      # 签到服务器ip
      reverseHost: 0.0.0.0
      # 签到服务器端口
      reversePort: 4440
      accessToken: ''
      # 反向Websocket路径
      reversePath: /mihoyosign
      # 反向Websocket Api路径
      reverseApiPath: /api
      # 反向Websocket Event路径
      reverseEventPath: /event
      useUniversal: true
      useTLS: false
      # 反向 WebSocket 客户端断线重连间隔，单位毫秒
      reconnectInterval: 3000

    #其他反向代理设置
    - enable: false
      postMessageFormat: string
      ...
      ...
```
具体参数参考[OneBot Mirai](https://github.com/yyuueexxiinngg/onebot-kotlin)

### 3. 运行jar包
命令:
```
java -jar miHoYoSign.jar
```
### 4.运行mirai

## 三. 命令格式
```
用户命令:
    帮助
    绑定 [*cookie]
    解绑
    开启签到 [游戏名] [游戏角色UID]
    关闭签到 [游戏名] [游戏角色UID]
    签到 [游戏名] [游戏角色UID]
    开启通知 [游戏名] [游戏角色UID]
    关闭通知 [游戏名] [游戏角色UID]
    所有角色
    已开启角色

管理员命令:
    所有用户角色
    通知 [通知内容] [QQ号]
    全部签到

例如:
    开启签到 原神 100000000
    开启指定角色的签到
		
    开启签到 原神
    开启指定游戏的所有角色签到	
	
    开启签到 100000000
    开启指定UID的所有角色签到(如uid重复会都开启)
		
    开启签到
    开启所有角色签到
    
    (QQ:123456789)开启签到
    以某个QQ号的身份 向本机器人开启签到
注意:
    带*的为必填参数,不带*的参数可以不写,但不能调换参数的顺序
  ```
## 四. 预览图
[登录](https://gitee.com/amekenn/image/raw/master/ysbbssign/login.png)
[签到](https://gitee.com/amekenn/image/raw/master/ysbbssign/sign.jpg)

## 五. 注意事项
- 注意服务器时间要为中国时间

## 直接或间接引用到的其他开源项目
- [OneBot Mirai](https://github.com/yyuueexxiinngg/onebot-kotlin)
- [genshin-impact-helper](https://github.com/y1ndan/genshin-impact-helper)
