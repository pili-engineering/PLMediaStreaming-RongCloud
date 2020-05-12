## 接入自己的融云 IM 聊天室
本 Demo 内置了融云 IM 聊天室所使用的 AppKey 和服务器地址，如果您需要接入到您自己的配置，您需要做以下操作。  

1. [注册融云开发者](https://developer.rongcloud.cn/signup/?utm_source=demogithub&utm_term=demosign)，创建应用后获取 APPKey。
2. 部署 [SealLive-Server](https://github.com/rongcloud/demo-chatroom/tree/v2.0/app-server)，此 Demo 中的 App 服务器使用融云 SealLive 的 Server。
3. 服务部署完毕之后，请分别将源码中的 `APP_KEY`,`APPSERVER` 改为您自己的  
参见源码中文件  `com.qiniu.droid.niuliving.im.DataInterface`

### 功能模块介绍
融云 IM 聊天室相关代码目录是在 com.qiniu.droid.niuliving.im 包中，由 adapter、 danmu、 gift、 like、 message、messageview、model、panel、utils、DataInterface、ChatroomKit 等模块组成

* **adapter:** 聊天列表和礼物框适配器。
* **danmu:** 弹幕展示相关内容。
* **gift：** 礼物选择和展示相关内容。
* **like:** 点赞相关内容。
* **message：**  进入直播间、点赞等自定义消息信令。
* **messageview：** 自定义消息展示View。
* **model：**  礼物信息、角色信息、房间信息等数据模型。
* **panel：** 底部操作栏相关内容。
* **utils：** 通用的一些工具类。
* **DataInterface** 直播功能中所有用到的数据和 AppKey 等信息。
* **ChatroomKit：** 对融云 IM 引擎的封装方便调用。

### 使用到的融云产品
* **即时通讯 IMLib SDK**  可详细查看 [IMLib SDK 开发指南](https://www.rongcloud.cn/docs/android.html)

### 使用到的七牛推流 SDK
**有关于七牛推流 SDK 的功能请参考[七牛推流介绍文档](https://developer.qiniu.com/pili/sdk/3715/PLDroidMediaStreaming-overview)**
