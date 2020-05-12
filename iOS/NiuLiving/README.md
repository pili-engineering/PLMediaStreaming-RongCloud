# 说明
## 集成说明
* 您需要到 [融云开发者网站](https://developer.rongcloud.cn/signin?returnUrl=%2F )注册开发者，并创建应用。可参考[这里](https://docs.rongcloud.cn/v3/views/im/ui/guide/quick/premise/ios.html)注册开发者和应用。

* 在 `AppDelegate.h` 文件中您需要配置您自己的 APPKey（上一步操作获取到的 AppKey） 和 APPServer（您自己的 APPServer）。您只需配置好这两项，即可运行代码实现聊天室的功能。

```
#define RCIMAPPKey @"您的 APPKey"
#define APPSERVER @"您的 APPASerVer"
```
* 如果您需要实现远程推送功能，您需要参考[这里](https://docs.rongcloud.cn/v3/views/im/ui/guide/private/notify/push/ios.html)进行配置。


## 代码结结构说明
`chatRoom` 文件夹下是关于聊天功能的全部代码。` RCChatRoomView.m` 文件里实现了直播页面的关于聊天功能的 UI ，集成中您只需在界面中使用这个类便能实现聊天室功能。


* **InputBar:** 聊天输入框展示的功能。
* **Danmaku:** 弹幕展示相关内容。
* **Manager：** 链接融云的管理类
* **message：**  进入直播间、点赞等自定义消息信令。
* **model：**  礼物信息、角色信息、房间信息等数据模型。
* **utils：** 通用的一些工具类。
* **View** 礼物选择和展示相关内容。

### 使用到的融云产品
* **即时通讯 IMLib SDK**  可详细查看 [IMLib SDK 开发指南](https://www.rongcloud.cn/docs/ios.html)

### 使用到的七牛推流 SDK
**有关于七牛推流 SDK 的功能请参考[七牛推流介绍文档](https://developer.qiniu.com/pili/sdk/3715/PLDroidMediaStreaming-overview)**
