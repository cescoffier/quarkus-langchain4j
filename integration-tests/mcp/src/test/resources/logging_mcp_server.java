///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.24.0}@pom
//DEPS io.quarkiverse.mcp:quarkus-mcp-server-stdio:1.3.0
//DEPS io.quarkiverse.mcp:quarkus-mcp-server-sse:1.3.0
//Q:CONFIG quarkus.mcp.server.client-logging.default-level=DEBUG

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.quarkiverse.mcp.server.McpLog;
import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;

public class logging_mcp_server {
    
    @Tool(description = "Log a DEBUG-level message")
    public String debug(McpLog log) {
        log.debug("HELLO DEBUG");
        return "ok";
    }

}
