### 规则核验工具 agent
1. 实现一个agent,功能是利用 json 对比校验库里的规则数据是否和 excel 输入的数据一致

### 启动所依赖的中间件
1. mysql,用作对话记忆存储
   1. 创建数据库spring-ai-chat-memory
2. redis-stack,存储知识库信息


### 启动须知
启动此包下的包需要注意,要先启动 spring-ai-mcp 包下的 spring-ai-mcp-server中的两个 mcp server,因为会调用到这两个 mcp server