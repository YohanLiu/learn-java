# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and test

- This is a Gradle multi-project Spring Boot repo. Java 17 is required; the root `build.gradle` sets both `sourceCompatibility` and `targetCompatibility` to 17.
- Subprojects use custom build file names like `learn-java-basic.gradle` and `spring-ai-rag.gradle`. That mapping is configured in `settings.gradle`, so do not assume every module uses a local `build.gradle`.
- The root project disables its own jar and bootJar tasks, so prefer module-scoped Gradle commands.
- In this checkout `gradlew` is not executable. Use `sh ./gradlew ...` or add the executable bit locally before using `./gradlew`.

Common commands:

```bash
# list all modules
sh ./gradlew projects

# build a specific module
sh ./gradlew :learn-java-basic:build
sh ./gradlew :learn-spring-ai:spring-ai-rag:build

# run tests (tests currently live under learn-java-basic)
sh ./gradlew :learn-java-basic:test

# run a single test class or method
sh ./gradlew :learn-java-basic:test --tests "com.yohan.javabasic.java.juc.AtomicTest"
sh ./gradlew :learn-java-basic:test --tests "com.yohan.javabasic.java.juc.AtomicTest.someMethod"

# start a Spring Boot demo module
sh ./gradlew :learn-middleware:learn-apollo:bootRun
sh ./gradlew :learn-spring-ai:spring-ai-chat:deepseek-chat:bootRun
sh ./gradlew :learn-spring-ai:spring-ai-mcp:spring-ai-mcp-server:mcp-server-lark:bootRun
sh ./gradlew :learn-spring-ai:spring-ai-case:spring-ai-configuration-verification:bootRun
```

No dedicated lint, Checkstyle, Spotless, or PMD task was found in the Gradle files that were inspected.

## Repository structure

- `learn-java-basic`: standalone demos for core Java and Spring mechanics. Most automated tests live here. Topics include JUC/concurrency, file APIs, reflection, AOP, ByteBuddy, crypto, and Excel handling.
- `learn-middleware`: isolated middleware demos. Current submodules cover Apollo config, Elasticsearch, and XXL-Job.
- `learn-spring-ai`: Spring AI demos ranging from basic chat calls to multi-tool agent workflows.

## Architecture notes

The repository is organized as many independent Spring Boot demo applications, not one integrated product. Each leaf module usually has its own `*Application` entrypoint and its own `application.yml`.

Shared Gradle behavior lives in the root `build.gradle`: every project gets Java, Spring Boot, dependency-management, common repositories including Aliyun mirrors, and common starter dependencies such as web, test, and Lombok. BOM imports pin Spring Cloud, Spring Cloud Alibaba, Spring AI Alibaba, and Spring AI versions globally.

`learn-spring-ai` contains the most cross-module behavior:

- `spring-ai-chat/deepseek-chat` shows direct model access with Spring AI's `ChatClient` and `DeepSeekChatModel`, including standard request/response and streaming endpoints.
- `spring-ai-chat-memory` shows in-memory conversation memory via `MessageWindowChatMemory`.
- `spring-ai-rag` shows local RAG with `SimpleVectorStore`, and persists the vector store to `learn-spring-ai/spring-ai-rag/src/main/resources/spring-ai-rag.json`.
- `spring-ai-mcp` is split into MCP servers and an MCP client. The server modules expose Spring AI `@Tool` methods over SSE (`mcp-server-lark` and `mcp-server-db-rule`), and the client connects to them through `spring.ai.mcp.client.sse.connections`.
- `spring-ai-case/spring-ai-configuration-verification` is the composed demo. It combines MySQL-backed chat memory, Redis vector retrieval, MCP tool calls to the local rule/lark servers, and a remote JSON diff MCP service to drive a rule-verification workflow.

## Runtime dependencies and config

Several modules need local services or API keys before `bootRun` will work:

- DashScope-backed Spring AI modules expect `AI_DASHSCOPE_API_KEY`.
- The DeepSeek chat demo expects `AI_DEEPSEEK_API_KEY` and uses the DashScope compatible base URL.
- `spring-ai-configuration-verification` expects local MySQL on the `spring-ai-chat-memory` database, local Redis on `localhost:6379`, and the two local MCP servers running first.
- The configuration verification README explicitly says to start these modules before the case module:
  - `:learn-spring-ai:spring-ai-mcp:spring-ai-mcp-server:mcp-server-db-rule`
  - `:learn-spring-ai:spring-ai-mcp:spring-ai-mcp-server:mcp-server-lark`
- `learn-apollo` is wired to an external Apollo config service in its `application.yml`.
- `learn-xxljob` expects a reachable XXL-Job admin instance.

When changing Spring AI code, inspect both the Java wiring and the corresponding `application.yml`; a lot of the actual behavior is controlled through YAML configuration rather than only through Java classes.
