# 人脸识别考勤

## 一、设计内容

### 1、原型设计

###### 版本A：

- 考勤系统分为员工端和管理者端。
- 管理者端可以增删改要签到的员工信息（录入新员工、删除员工等），查询签到历史，规定签到时间（如9：00前早签，17:00后晚签）等。
- 员工端负责拍照，人脸识别成功后签到。
- 自己的服务器接受管理者端的请求，存储员工信息，并提供增删改查员工信息的服务。同时转发员工端发送来的签到请求到Face++，得到人脸对比结果，反馈给员工端是否签到成功。
- 人脸识别提供商（Face++）的服务器存储人脸信息以及对应的员工的id。接受自己服务器的注册新人脸、对比人脸的请求，并返回结果。

###### 版本B：

- 只有一个客户端，不仅负责增删改新员工（学生）的信息，还负责拍照、人脸识别签到。
- 服务器的情况与版本A类似，自己的服务器接受所有请求，并存储学生信息。

由于版本A存在重大漏洞：当员工没在公司时也能签到，而且工程量较大，课设时间有限。所以本项目采用版本B。

### 2、业务逻辑设计

主要功能有注册新用户和签到识别

最左边为客户端，中间的服务器是自己的服务器，右边是face++的服务器。

 ![](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/1.png)

### 3、界面设计

- 界面整体采用谷歌推荐的**Meterial Design**设计原则，包含了视觉、运动、互动效果等特性，就是好看！
- 颜色上采用了[配色网](http://www.peise.net)的&quot;[花季少女的心](http://www.peise.net/2015/0629/5018.html)&quot;配色方案，简单而融洽。
- 首先是主界面，采用一个带抽屉的布局，布局最上方为状态栏，剩下的部分为一个随抽屉中选项而切换的碎片，并在界面右下角嵌入一浮动按钮。

界面截图如下：

![抽屉打开时](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/2.png)

![抽屉关闭时（默认为签到历史）](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/3.png)

- 抽屉中的布局：上部分为用户头像，下半部分为功能选项按钮，每个按钮对应一个碎片。
- 在签到历史Fragment中，以每行2项的卡片式布局展示签到历史，每个记录项四周用阴影包围，显得更有层次感。
- 点击浮动按钮跳转到拍照页面，若拍照成功，进入上传页面；若失败则返回之前的页面。

截图如下：

![拍照成功后加载图片](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/4.png)

![点击上传后弹出等待Dialog](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/5.png)

![Dialog消失后返回上个界面，并                                                                                                        提示xxx签到成功](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/6.png)

- 学生列表Fragment主要用与注册新的用户，将新的人脸信息录入
- 点击学生列表浮动按钮跳转到拍照页面，若拍照成功，进入上传页面；若失败则返回之前的页面。

截图如下：

 ![按钮的icon更换为加号](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/7.png)

 ![按钮的icon更换为加号](https://github.com/Paulpaulzmx/FRAmanager/blob/master/images/8.png)

## 二、设计方法

### 1、数据库设计

主要有**学生信息表**和**签到记录表**。

##### 学生信息表（stu_message)：

| 名       | 类型         | 主键 | 非空 | 备注 |
| -------- | ------------ | ---- | ---- | ---- |
| stu_id   | int(10)      | 是   | 是   |      |
| stu_name | varchar(255) | 否   | 是   |      |

此表还可扩展学生性别，学院，班级等项目，这里省略了。

##### 签到记录表(check_on_records)：

| 名      | 类型         | 主键 | 非空 | 备注                       |
| ------- | ------------ | ---- | ---- | -------------------------- |
| co_id   | int(10)      | 是   | 是   | 自动递增                   |
| stu_id  | varchar(255) | 否   | 是   | 执行签到的学生id           |
| co_time | datatime     | 否   | 是   | 自动生成CURRENT\_TIMESTAMP |

### 2、接口设计

#### （1）注册接口

接收客户端发来的注册学生的图片和姓名，转发给Face++，返回注册成功的信息。

方法：POST

uri：服务器地址/register

请求参数：

| 字段      | 说明           | 类型                | 备注 | 是否必填 |
| --------- | -------------- | ------------------- | ---- | -------- |
| user\_img | 注册用图片     | multipart/form-data | .jpg | 是       |
| user\_id  | 注册学生的姓名 | multipart/form-data |      | 是       |

返回参数：

| 字段     | 说明           | 类型   | 备注 |
| -------- | -------------- | ------ | ---- |
| user\_id | 注册学生的姓名 | string |      |

错误情况：

| 字段           | 说明     | 类型   | 备注           |
| -------------- | -------- | ------ | -------------- |
| error\_message | 已知错误 | string | 有错误码       |
| other\_error   | 未知错误 | string | 网络不通等情况 |

#### （2）识别接口

接受用户发送来的要识别的人脸图片，转发给Face++，返回签到用户的id。

方法：POST

uri：服务器地址/transmit

请求参数：

| 字段      | 说明       | 类型                | 备注 | 是否必填 |
| --------- | ---------- | ------------------- | ---- | -------- |
| user\_img | 识别用图片 | multipart/form-data | .jpg | 是       |

返回参数：

| 字段     | 说明                 | 类型   | 备注 |
| -------- | -------------------- | ------ | ---- |
| user\_id | 验证得到的学生的姓名 | string |      |

错误情况：

| 字段         | 说明                   | 类型   | 备注                          |
| ------------ | ---------------------- | ------ | ----------------------------- |
| no\_face     | 未识别到人脸           | string | face++的返回值中没有faces字段 |
| failed       | 验证所得分数未达到阈值 | string | 分数低于80未验证失败          |
| other\_error | 未知错误               | string | 网络不通等情况                |

#### （3）查询历史接口

方法：GET

uri：服务器地址/history

请求参数：无

返回参数：

| 字段          | 说明     | 类型 | 备注                                       |
| ------------- | -------- | ---- | ------------------------------------------ |
| history\_data | 历史纪录 | json | 整体为一个json数组，每条记录为一个数据项。 |

## 三、实现方法

### 1．客户端的实现：

程序采用Android Studio开发，调试、运行环境为小米手机3（Android 6.0）。

#### （1）界面实现：

主界面抽屉布局采用`DrawerLayout`实现，浮动按钮为`FloatingActionButton`，状态栏为`ToolBar`。

历史界面采用`RecyclerView`配合`CardView`实现。

a.为实现在状态栏打开时按返回关闭而不是退出程序，重写`onBackPressed()`方法：

```java
public void onBackPressed() {

    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

        drawerLayout.closeDrawer(GravityCompat.START);

    } else {

        super.onBackPressed();

    }
}
```

b.传统的单`Activity`对应多`Fragment`实现方法是：在一个`FrameLayout`中，每切换一个`Fragment`，就把要切换的`Fragment`放在最顶层，这种方法相当于新建了一个`Fragment`，被切换掉的`Fragment`在切换瞬间的数据并未被保存，所以这里需要改进：

为节省篇幅，不粘代码了，我参考的[这篇博客](https://blog.csdn.net/fan7983377/article/details/51889269)。

c.在显示相机拍出来的照片时，有时文件过大（像素数过多）不能使用`BitMap`加载，这里用`SubsamplingScaleImageView`加载。

#### （2）业务逻辑实现：

1. 与后台交互的逻辑：通过`OKHttp`开启新的线程向后台发送请求，用`Hanlder`模块接受返回的数据，同时将相应逻辑返回给主线程，主线程更新界面UI。
2. 历史`Fragment`界面在接受`json`数据时使用`Gson`模块解析，相当方便。

### 2．后台服务器的实现：

后台采用python语言编写，主要运用模块有`pymysql`（连接`mysql`数据库）、`flask`（Web应用框架）以及Face++官方的SDK。

一些基本概念：

- `face_token`:当对一张图片进行人脸检测时，会将检测到的人脸记录下来，包括人脸在图片中的位置，用一个系统标识face\_token来表示。注意：对同一张图片进行多次人脸检测，对同一个人脸会得到不同的face\_token。
- `FaceSet`:人脸集合（`FaceSet`）是用来存储检测到人脸的存储对象。一个`FaceSet`内`face_token`是不重复的。

后台服务器与Face++服务器的交互时主要用到的API：

| 名称                | 功能                                                         |
| ------------------- | ------------------------------------------------------------ |
| Detect API          | 可以检测图片内的所有人脸，对于每个检测出的人脸，会给出其唯一标识 face\_token，可用于后续的人脸分析、人脸比对等操作。 |
| Search API          | 在一个已有的 `FaceSet` 中找出与目标人脸最相似的一张或多张人脸，返回置信度和不同误识率下的阈值,识别到的`user_id`。 |
| Face SetUserID API  | 为检测出的某一个人脸添加标识信息，该信息会在Search接口结果中返回，用来确定用户身份。 |
| FaceSet AddFace API | 为一个已经创建的 `FaceSet` 添加人脸标识 `face_token`。       |

参考资料：[face++官方API](https://console.faceplusplus.com.cn/documents/5671791)

##### 服务器主要操作逻辑：

1. 使用Detect API获取 `facetoken` 。
2. 根据步骤1得到的`face_token`，使用`Face SetUserID` API设置 `user_id`
3. 利用 `FaceSet AddFace` API 将步骤2中的图片上传到已创建好的 `FaceSet`
4. 用户拍照对比时，利用 `Search` API 方法比对，成功后获得 `user_id` ，利用此 `user_id` 判断哪一个员工签到了。

##### 服务器后台的启动：

本地MySQL、本地web服务程序启动后，使用**natapp**内网穿透使App能正常上传数据。

## 四、心得体会

1. 了解了在网络通信中数据的传输方法(`mutipart-formdata`)，学习了http协议中不同的请求方法和请求头的功能和作用。
2. 界面设计不易，美工和细节的打磨需要耗费时间。
3. 第一次写简单的后台处理程序，基础功能实现很简单，但完善需要花费功夫。
4. 不同版本的安卓、不同的手机在运行同一个app时会存在差异。相比ios，Android开发在适配上的成本要高些。
5. 本以为看了《第一行代码》就能搞定安卓，实际做的时候才发现自己懂得只是皮毛。
6. 时光荏苒，大学时光早已过半，且行且珍惜。
