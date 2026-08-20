# Cike - 此刻图文分享社区（学习项目）

## 项目简介

「此刻」是一个图文分享社区，用户可以发布图文动态、浏览他人分享、进行互动交流。本项目是前端训练营 DAY05 的全栈项目，包含前端 Vue3 应用和后端 Spring Boot 服务。

> **注意：本项目为学习项目，仅用于学习交流。**

## 技术栈及说明

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架，Composition API |
| Vite | 5.x | 构建工具 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| CSS3 | - | 样式 |

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 后端框架 |
| MyBatis-Plus | 3.x | ORM 框架 |
| MySQL | 8.x | 数据库 |
| Maven | 3.x | 构建工具 |

## 启动步骤

### 前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发环境运行（默认 http://localhost:5173）
npm run dev

# 构建生产版本
npm run build
```

### 后端

```bash
# 进入后端目录
cd backend

# 编译打包
mvn clean package

# 运行（默认 http://localhost:8080）
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

或使用 IDE 直接运行 `BackendApplication.java`

## 目录结构及说明

```
cike/
├── frontend/                   # 前端项目
│   ├── public/                 # 静态资源
│   │   └── favicon.svg         # 网站图标
│   ├── src/
│   │   ├── components/         # 公共组件
│   │   │   ├── AppNav.vue      # 导航栏组件
│   │   │   └── NoteCard.vue    # 笔记卡片组件
│   │   ├── views/              # 页面视图
│   │   │   ├── LoginView.vue   # 登录注册页
│   │   │   ├── HomeView.vue    # 首页
│   │   │   ├── PublishView.vue # 发布页
│   │   │   ├── DetailView.vue  # 详情页
│   │   │   └── ProfileView.vue # 个人中心页
│   │   ├── router/             # 路由配置
│   │   │   └── index.js        # 路由定义
│   │   ├── stores/             # 状态管理
│   │   │   ├── user.js         # 用户状态
│   │   │   └── data.js         # 数据状态
│   │   ├── mock/               # 模拟数据
│   │   │   └── notes.js        # 笔记数据
│   │   ├── styles/             # 样式文件
│   │   │   └── main.css        # 全局样式
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── index.html              # HTML 模板
│   ├── package.json            # 项目配置
│   └── vite.config.js          # Vite 配置
│
├── backend/                    # 后端项目
│   ├── src/main/java/com/cike/backend/
│   │   ├── common/             # 公共类
│   │   │   ├── Result.java     # 统一响应结果
│   │   │   ├── BizException.java # 业务异常
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   ├── config/             # 配置类
│   │   │   └── CorsConfig.java # 跨域配置
│   │   ├── controller/         # 控制器
│   │   │   ├── UserController.java # 用户接口
│   │   │   └── NoteController.java # 笔记接口
│   │   ├── dto/                # 数据传输对象
│   │   │   ├── LoginDTO.java   # 登录参数
│   │   │   ├── RegisterDTO.java # 注册参数
│   │   │   └── NotePublishDTO.java # 发布参数
│   │   ├── entity/             # 实体类
│   │   │   ├── User.java       # 用户实体
│   │   │   ├── Note.java       # 笔记实体
│   │   │   └── Comment.java    # 评论实体
│   │   ├── mapper/             # 数据访问层
│   │   │   ├── UserMapper.java # 用户 Mapper
│   │   │   ├── NoteMapper.java # 笔记 Mapper
│   │   │   └── CommentMapper.java # 评论 Mapper
│   │   ├── service/            # 业务逻辑层
│   │   │   ├── UserService.java # 用户服务
│   │   │   └── NoteService.java # 笔记服务
│   │   └── BackendApplication.java # 启动类
│   ├── src/main/resources/
│   │   ├── static/img/         # 静态图片
│   │   └── application.properties # 配置文件
│   └── pom.xml                 # Maven 配置
│
├── 原型图/                     # 设计原型图
│   ├── 01-登录注册页.png
│   ├── 02-首页.png
│   ├── 03-发布页.png
│   ├── 04-详情页.png
│   └── 05-个人中心页.png
│
└── README.md                   # 项目说明
```

## 功能特性

- **用户系统**：注册、登录、个人信息管理
- **笔记发布**：图文内容发布与编辑
- **内容浏览**：首页推荐、详情查看
- **互动交流**：评论、点赞
- **个人中心**：我的发布、我的收藏

## 页面展示

| 页面 | 说明 |
|------|------|
| 登录注册页 | 用户登录与注册 |
| 首页 | 笔记列表展示 |
| 发布页 | 图文内容发布 |
| 详情页 | 笔记详情与评论 |
| 个人中心 | 用户个人信息 |

## 相关仓库

| 仓库 | 说明 |
|------|------|
| [homework](https://github.com/hb-degithub/homework) | 前端训练营每日作业 |
| [portfolio](https://github.com/hb-degithub/portfolio) | DAY04 Vue3 个人作品集 |

## 开源项目的版权声明

MIT License

Copyright (c) 2026 胡澜译

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
