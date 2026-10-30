<div align="center">

# ☕ Java AI Engineer — Volume 3: Agentic AI & MCP
### *Tool Calling, ReAct Loops, Model Context Protocol & Multi-Agent Workflows*

[![Volume](https://img.shields.io/badge/Volume-3%20of%206-blue?style=for-the-badge)](https://javapuzzle.com)
[![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![MCP](https://img.shields.io/badge/Anthropic%20MCP-Protocol-purple?style=for-the-badge)](https://modelcontextprotocol.io)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-Supported-yellowgreen?style=for-the-badge)](https://github.com/langchain4j/langchain4j)

**Part of the 6-volume [Java AI Engineer](https://javapuzzle.com) series by Java Puzzle**

</div>

---

## 📖 About This Volume

Volume 2 built a knowledge assistant that could answer questions, but when a customer asks *"Where is my order?"* or *"Cancel item #4412,"* describing documents is not enough. The assistant must be able to **act**.

Across 20 chapters, Volume 3 moves from passive LLM generation to autonomous Java agents:
- **Tool Calling & Reasoning**: Function execution schemas, parameter validation, and ReAct (Reason + Act) loops.
- **Agent Workflows**: Memory management, state graphs, task decomposition, reflection loops, and saga checkpoints.
- **Model Context Protocol (MCP)**: Building production MCP Clients and Servers in Java over stdio and SSE transports.
- **Multi-Agent Systems & Guardrails**: Specialized supervisor/worker agents, inter-agent messaging, deterministic code-level guardrails, and distributed tracing.

---

## 🗺️ Chapter & Code Index

| Chapter | Title | Focus Area / Key Mechanism |
|:---:|---|---|
| **01** | `chapter-01` | **Why Agents: From Chatbots to Autonomous Systems** — Describing vs acting; agent autonomy spectrum |
| **02** | `chapter-02` | **Tool Calling and Function Execution Fundamentals** — Java methods exposed as JSON schema tool definitions |
| **03** | `chapter-03` | **Designing Tool Contracts for Reliability** — Schema validation, error recovery, and robust tool signatures |
| **04** | `chapter-04` | **The ReAct Pattern: Reasoning and Acting** — Thought-Action-Observation loops implemented in pure Java |
| **05** | `chapter-05` | **Building Your First Java AI Agent** — Assembling a deterministic order lookup and status agent |
| **06** | `chapter-06` | **Agent Memory and State Management** — Short-term scratchpads, long-term memory, stateful conversation |
| **07** | `chapter-07` | **Planning Agents and Task Decomposition** — Breaking multi-step user intents into executable dependency DAGs |
| **08** | `chapter-08` | **LangChain4j and Comparing Agent Frameworks** — Architecture comparison: Spring AI vs LangChain4j |
| **09** | `chapter-09` | **Workflow Patterns: Chaining, Routing, Parallelization** — Deterministic orchestration patterns in Java |
| **10** | `chapter-10` | **Self-Planning Agents and Reflection Loops** — Self-critique, plan validation, and iterative refinement |
| **11** | `chapter-11` | **Introducing the Model Context Protocol (MCP)** — Anthropic's open standard for tool and resource integration |
| **12** | `chapter-12` | **Building an MCP Client** — Connecting Java clients to remote MCP servers over JSON-RPC & SSE |
| **13** | `chapter-13` | **Building an MCP Server for Acme Retail** — Exposing inventory, orders, and fulfillment tools via MCP |
| **14** | `chapter-14` | **MCP Resources, Prompts, and Transport Options** — Stdio vs HTTP/SSE transports, dynamic prompt templates |
| **15** | `chapter-15` | **Multi-Agent Orchestration Fundamentals** — Supervisor patterns, hierarchical coordination, specialist agents |
| **16** | `chapter-16` | **State Graphs, Checkpointing, and Sagas** — Long-running agent transactions, compensations, and crash recovery |
| **17** | `chapter-17` | **Agent-to-Agent Communication Patterns** — Protocols, structured message envelopes, and consensus |
| **18** | `chapter-18` | **Guardrails: Constraining Agent Behavior Safely** — Code-enforced policies: refund ceilings, action authorization |
| **19** | `chapter-19` | **Debugging and Tracing Multi-Agent Systems** — OpenTelemetry spans, step-by-step agent execution traces |
| **20** | `chapter-20` | **Acme Retail's Autonomous Ops Agent (Capstone)** — Multi-agent system diagnosing inventory and issuing safe actions |

---

## 🛠️ Tech Stack & Prerequisites

- **Java**: 21 LTS or newer
- **Frameworks**: Spring AI 1.0+, LangChain4j
- **Protocols**: Model Context Protocol (MCP 1.0)
- **Build Tool**: Maven 3.9+

## 🚀 Building & Running Chapter Code

```bash
mvn clean test
```

Step into any chapter to run tests:
```bash
cd chapter-13
mvn test
```

---

<div align="center">
<sub>© Java Puzzle. All rights reserved. Visit <a href="https://javapuzzle.com">javapuzzle.com</a> to explore the entire 6-volume series.</sub>
</div>
