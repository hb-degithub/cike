# 「此刻」图文分享社区 API 接口文档

## 概述

- **基础 URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **认证方式**: Token（Header: `Authorization: Bearer <token>`）

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码：200成功，400参数错误，401未授权，500服务器错误 |
| message | string | 提示信息 |
| data | object | 响应数据 |

---

## 1. 用户模块

### 1.1 用户注册

```
POST /api/user/register
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |
| nickname | string | 是 | 昵称 |

**请求示例：**

```json
{
  "phone": "13800138000",
  "password": "123456",
  "nickname": "新用户"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "nickname": "新用户",
    "avatar": "",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

### 1.2 用户登录

```
POST /api/user/login
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |

**响应示例：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "nickname": "小明同学",
    "avatar": "https://...",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

### 1.3 获取用户信息

```
GET /api/user/info
```

**请求头：**

```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "nickname": "小明同学",
    "avatar": "https://...",
    "bio": "热爱生活",
    "noteCount": 5,
    "likeTotal": 128
  }
}
```

### 1.4 更新用户信息

```
PUT /api/user/info
```

**请求头：**

```
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | string | 否 | 昵称 |
| avatar | string | 否 | 头像URL |
| bio | string | 否 | 个人简介 |

---

## 2. 笔记模块

### 2.1 获取笔记列表

```
GET /api/note/list
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| topicId | long | 否 | 话题ID筛选 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "records": [
      {
        "id": 1,
        "title": "周末的早餐时光",
        "content": "今天早上...",
        "coverUrl": "https://...",
        "viewCount": 156,
        "likeCount": 23,
        "collectCount": 5,
        "commentCount": 3,
        "createTime": "2026-08-20 10:00:00",
        "user": {
          "id": 1,
          "nickname": "小明同学",
          "avatar": "https://..."
        },
        "topics": ["#生活记录", "#好物分享"]
      }
    ]
  }
}
```

### 2.2 获取笔记详情

```
GET /api/note/{id}
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | long | 笔记ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "周末的早餐时光",
    "content": "今天早上做了一份简单的早餐...",
    "images": [
      "https://...",
      "https://..."
    ],
    "viewCount": 156,
    "likeCount": 23,
    "collectCount": 5,
    "commentCount": 3,
    "createTime": "2026-08-20 10:00:00",
    "user": {
      "id": 1,
      "nickname": "小明同学",
      "avatar": "https://..."
    },
    "topics": ["#生活记录"],
    "isLiked": false,
    "isCollected": false
  }
}
```

### 2.3 发布笔记

```
POST /api/note/publish
```

**请求头：**

```
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 标题 |
| content | string | 是 | 内容 |
| images | array | 否 | 图片URL列表 |
| topicIds | array | 否 | 话题ID列表 |
| visible | int | 否 | 可见性：1公开，2私密 |

**请求示例：**

```json
{
  "title": "新笔记",
  "content": "笔记内容...",
  "images": ["https://..."],
  "topicIds": [1, 2],
  "visible": 1
}
```

### 2.4 删除笔记

```
DELETE /api/note/{id}
```

**请求头：**

```
Authorization: Bearer <token>
```

### 2.5 点赞笔记

```
POST /api/note/{id}/like
```

**请求头：**

```
Authorization: Bearer <token>
```

### 2.6 取消点赞

```
DELETE /api/note/{id}/like
```

**请求头：**

```
Authorization: Bearer <token>
```

### 2.7 收藏笔记

```
POST /api/note/{id}/collect
```

**请求头：**

```
Authorization: Bearer <token>
```

### 2.8 取消收藏

```
DELETE /api/note/{id}/collect
```

**请求头：**

```
Authorization: Bearer <token>
```

---

## 3. 评论模块

### 3.1 获取评论列表

```
GET /api/comment/list
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| noteId | long | 是 | 笔记ID |
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "content": "看起来好好吃！",
        "createTime": "2026-08-20 10:00:00",
        "user": {
          "id": 2,
          "nickname": "美食达人",
          "avatar": "https://..."
        },
        "replies": [
          {
            "id": 2,
            "content": "是的，味道很不错",
            "user": {
              "id": 1,
              "nickname": "小明同学"
            }
          }
        ]
      }
    ]
  }
}
```

### 3.2 发表评论

```
POST /api/comment/publish
```

**请求头：**

```
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| noteId | long | 是 | 笔记ID |
| content | string | 是 | 评论内容 |
| parentId | long | 否 | 父评论ID（回复时使用） |
| replyUserId | long | 否 | 回复目标用户ID |

### 3.3 删除评论

```
DELETE /api/comment/{id}
```

**请求头：**

```
Authorization: Bearer <token>
```

---

## 4. 话题模块

### 4.1 获取话题列表

```
GET /api/topic/list
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "topicName": "#美食探店",
      "noteCount": 15
    },
    {
      "id": 2,
      "topicName": "#旅行日记",
      "noteCount": 12
    }
  ]
}
```

### 4.2 获取话题详情

```
GET /api/topic/{id}
```

---

## 5. 草稿模块

### 5.1 保存草稿

```
POST /api/draft/save
```

**请求头：**

```
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 否 | 标题 |
| content | string | 否 | 内容 |
| images | array | 否 | 图片列表 |
| topicIds | array | 否 | 话题ID列表 |
| visible | int | 否 | 可见性 |

### 5.2 获取草稿

```
GET /api/draft
```

**请求头：**

```
Authorization: Bearer <token>
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权/登录过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
