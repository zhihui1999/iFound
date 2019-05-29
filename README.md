# iFound
智能定位器
# 贡献者
请阅读CONTRIBUTING.md 查阅为该项目做出贡献的开发者。
# 版本控制
该项目使用SemVer进行版本管理。您可以在repository参看当前可用版本。
# 目录结构说明
    Libs: 包括 GizWifiSDK 在内的的第三方库目录
    assets: 包含 UIConfig.json 配置文件
    DIYClass:包含成员管理，单设备等界面
    GizOpenSource 组成模块
    CommonModule // 公共方法类、资源文件读取类
    ConfigModule // 设备配置模块，包含 AirLink 及 SoftAP
    ControlModule // 控制模块，包含页面控制代码
    DeviceModule // 设备模块，包含 设备列表
    PushModule // 推送模块，包含 百度和极光的推送 SDK 集成封装
    SettingsModule // 设置模块，包含 设置菜单 及其 包含的子菜单项（关于等）
    sharingdevice//设备分享模块，包括设备分享功能，绑定用户管理功能
    ThirdAccountModule // 第三方登录模块， 包含 第三方登录（QQ、微信等）
    UserModule // 用户模块，包含 用户登录、用户注册、找回密码
    view // 自定义控件
    utils // 工具类
    wxapi // 微信集成包
    zxing // 扫描二维码
    MessageCenter//为了避免机智云 SDK 与其他第三方 SDK 冲突，故采用单例模式在 Activity 中启动机智云 SDK。
# APP 配置文件说明
#### 在工程 assets/UIConfig.json 
    "app_id":机智云 app id
    "app_secret":机智云 app secret
    "product_key":机智云 product key
    "openAPI_URL": openAPI 域名及端口，格式：“api.gizwits.com:80”，不写端口默认 80
    "site_URL": site 域名及端口，格式：“site.gizwits.com:80”，不写端口默认 80
    "push_URL":推送绑定服务器 域名及端口，格式：“push.gizwits.com:80”，不写端口默认 80
    "UsingTabSet": true,
    "wifi_type_select":默认配置模块 wifi 模组选择功能是否开启
    "tencent_app_id": qq 登录 app id
    "wechat_app_id":微信登录 app id
    "wechat_app_secret":微信登录 app secret
    "push_type":推送类型 【0：关闭，1：极光，2：百度】
    "bpush_app_key":百度推送 app key
    "buttonColor":按钮颜色
    "buttonTextColor":按钮文字颜色
    "navigationBarColor":导航栏颜色
    "navigationBarTextColor":导航栏文字颜色,
    "configProgressViewColor":配置中界面 progress view 颜色,
    "addDeviceTitle":添加设备界面 导航栏标题文字,
    "qq":是否打开 QQ 登录图标【true：打开】
    "wechat":是否打开微信登录图标【true：打开】,
    "anonymousLogin":是否打开匿名登录图标【true：打开】
# 作者
莫言情难忘<br>
您也可以在贡献者名单中参看所有参与该项目的开发者。
# 版权说明
该项目签署了MIT 授权许可，详情请参阅 LICENSE.md
# 鸣谢
该项目基于机智云模板开发而来<br>
感谢老师的支持和陪伴
