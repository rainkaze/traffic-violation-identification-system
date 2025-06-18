

# 城市交通智能违法识别与管理系统

这是一个全栈Web应用项目，旨在提供一个现代化、数据驱动的城市交通违法行为识别与管理解决方案。系统前端采用 Vue 3 和 Tailwind CSS 构建，后端则基于 Spring Boot 和 MyBatis，数据库使用 MySQL。

该平台整合了实时监控、违法记录管理、数据统计分析、执法流程模拟等多种功能，为交通管理部门提供了一个高效、直观的操作界面。

## ✨ 核心功能

* **仪表盘 (Dashboard):** 实时展示今日违法总数、处理状态、严重违法行为等关键指标。通过图表直观显示违法类型分布，并提供实时预警信息流。
* **认证与授权 (Authentication & Authorization):**
    * **登录/注册:** 提供基于 JWT 的安全用户认证。新用户注册需通过邮箱验证，并由管理员审批后方可登录。
    * **角色权限:** 内置管理员、警员等多级角色，通过 Spring Security 实现精细化的后端接口访问控制。
* **用户与管理 (User & Admin):**
    * **个人中心:** 用户可以更新个人资料和修改密码。
    * **用户管理:** 管理员可审批新用户、查看、创建、编辑和删除所有用户。
    * **测试账号:** 这里提供管理员账号admin，密码为12345678。
* **违法记录管理 (Violations Management):** 支持对所有交通违法记录进行分页、查询和筛选。用户可以根据车牌号、违法类型、处理状态和月份进行精确查找。
* **统计分析 (Statistics Analysis):** 提供多维度的数据可视化分析，所有数据均从后端动态获取。包括：
    * 违法趋势折线图
    * 高峰时段柱状图
    * 违法类型与区域分布饼图/环形图
    * 违法高发地点 Top 5 列表
* **模拟与展示 (Simulations):**
    * **实时监控 (Real-time Monitoring):** 模拟展示各路口摄像头的实时视频流，并同步显示该摄像头捕捉到的最新违法行为。
    * **线上执法流程 (Enforcement Workflow):** 模拟一个完整的交通违法案件处理流程，包括案件立案、AI合规性审查、处罚建议生成和线上多级审批。
* **响应式布局:** 界面使用 Tailwind CSS 构建，完美适配桌面和移动设备，支持移动端侧边栏导航。

## 🛠️ 技术栈

| 类别 | 技术                                                                   |
| :--- |:---------------------------------------------------------------------|
| **后端** | Java 17, Spring Boot 3, Spring Security, MyBatis, MySQL, Maven, JWT  |
| **前端** | Vue 3, Vite, Vue Router, Axios, Tailwind CSS, Chart.js, Font Awesome |
| **开发工具** | Node.js, ESLint, Prettier,IEAD                                       |

## 🚀 快速开始

### 环境要求

* Java 17+
* Maven 3.3+
* Node.js 18+
* MySQL 8.0+

### 后端启动

1.  **克隆项目**
    ```bash
    git clone <https://gitee.com/rainkaze/traffic-violation-identification-system.git>
    cd traffic-violation-identification-system
    ```
2.  **云端数据库**
    * 这里默认使用云端数据库，如需配置数据库，请按照下面的要求进行配置。
    
2.  **配置本地数据库**
    * 在您的MySQL服务器中创建一个新的数据库，名称为 `traffic_violation_system`。
    * 导入数据库结构：执行 `src/main/resources/sql/schema.sql` 文件中的SQL语句。
    * 导入初始数据：执行 `src/main/resources/sql/data.sql` 文件中的SQL语句。
    * 修改配置文件 `src/main/resources/application.properties`，填入您的数据库URL、用户名和密码，并取消对本地数据库相关代码的注释。

3.  **运行后端服务**
    在项目根目录下，使用Maven运行Spring Boot应用，或者在IDEA中运行 `TrafficViolationIdentificationSystemApplication` 类。
    ```bash
    ./mvnw spring-boot:run
    ```
    服务默认运行在 `http://localhost:8080`。

### 前端启动

1.  **进入前端目录**
    ```bash
    cd frontend
    ```

2.  **安装依赖**
    ```bash
    npm install
    ```

3.  **启动开发服务器**
    ```bash
    npm run dev
    ```
    前端服务将运行在 `http://localhost:5173`。

4.  **访问系统**
    在浏览器中打开 `http://localhost:5173` 即可访问系统。

## 📁 项目结构

```
traffic-violation-identification-system/
├── frontend/                     # Vue 3 前端项目
│   ├── public/                   # 静态资源
│   ├── src/
│   │   ├── assets/               # CSS, 字体等资源 (main.css)
│   │   ├── components/           # Vue 公共组件 (Sidebar.vue, Header.vue)
│   │   ├── router/               # Vue Router 路由配置 (index.js)
│   │   ├── services/             # API 服务 (api.js)
│   │   ├── store/                # 全局状态管理 (auth.js)
│   │   ├── views/                # 页面级组件 (DashboardView.vue, etc.)
│   │   ├── App.vue               # 根组件
│   │   └── main.js               # 应用入口
│   ├── package.json              # 前端依赖与脚本配置
│   └── vite.config.js            # Vite 配置文件
│
├── src/                          # Spring Boot 后端项目
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/cupk/trafficviolationidentificationsystem/
│   │   │       ├── config/       # 配置类 (SecurityConfig.java)
│   │   │       ├── controller/   # API 控制器 (AuthController.java, AdminController.java)
│   │   │       ├── dto/          # 数据传输对象 (LoginDto.java, ViolationDetailDto.java)
│   │   │       ├── model/        # 数据模型 (User.java)
│   │   │       ├── repository/   # MyBatis Mapper 接口 (UserMapper.java)
│   │   │       ├── security/     # 安全相关 (JwtTokenProvider.java)
│   │   │       └── service/      # 服务层 (AuthService.java, ViolationService.java)
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML 文件 (UserMapper.xml)
│   │       ├── sql/              # 数据库脚本 (schema.sql, data.sql)
│   │       └── application.properties # 核心配置文件
│
├── .gitignore                    # Git 忽略配置
├── pom.xml                       # Maven 项目配置文件
└── README.md                     # 项目说明文档
```

## 🌐 API 接口

系统后端提供了一系列RESTful API供前端调用。CORS已通过 `SecurityConfig.java` 配置，允许来自 `http://localhost:5173` 的请求。所有需要认证的接口都通过JWT进行保护。

| 方法 | 路径 | 描述 | 权限 |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | 用户登录 | 公开 |
| `POST` | `/api/auth/register` | 提交用户注册申请 | 公开 |
| `POST` | `/api/auth/send-verification-code` | 发送邮箱验证码 | 公开 |
| `GET` | `/api/violations` | 获取违法记录列表（分页和筛选） | 任意已登录用户 |
| `GET` | `/api/statistics` | 获取统计分析数据 | 任意已登录用户 |
| `GET` | `/api/users/profile` | 获取当前用户信息 | 任意已登录用户 |
| `PUT` | `/api/users/profile` | 更新当前用户信息 | 任意已登录用户 |
| `POST` | `/api/users/profile/change-password` | 修改当前用户密码 | 任意已登录用户 |
| `GET` | `/api/admin/users` | 获取所有用户列表 | 管理员 |
| `POST` | `/api/admin/users` | 创建一个新用户 | 管理员 |
| `PUT` | `/api/admin/users/{userId}` | 更新指定用户信息 | 管理员 |
| `DELETE` | `/api/admin/users/{userId}` | 删除指定用户 | 管理员 |
| `POST` | `/api/admin/users/{userId}/approve` | 批准用户注册 | 管理员 |
| `POST` | `/api/admin/users/{userId}/reject` | 拒绝用户注册 | 管理员 |


## 🎨 代码规范
* 注释风格: JavaDoc


## 📄 许可

本项目采用 MIT 许可证。详情请参阅 `LICENSE` 文件。
