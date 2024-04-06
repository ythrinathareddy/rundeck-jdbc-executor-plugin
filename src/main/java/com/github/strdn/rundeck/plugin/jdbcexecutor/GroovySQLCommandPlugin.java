package com.github.strdn.rundeck.plugin.jdbcexecutor;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.execution.service.NodeExecutor;
import com.dtolabs.rundeck.core.execution.service.NodeExecutorResult;
import com.dtolabs.rundeck.core.execution.service.NodeExecutorResultImpl;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepFailureReason;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.PluginException;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants;
import com.dtolabs.rundeck.core.plugins.configuration.DescriptionBuilder;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyBuilder;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import org.apache.commons.lang.StringUtils;

import javax.script.ScriptException;

/**
 * GroovySQLCommandPlugin - A NodeExecutor plugin for Rundeck that executes SQL commands via JDBC.
 */
@Plugin(name = GroovySQLCommandPlugin.SERVICE_PROVIDER_TYPE, service = ServiceNameConstants.NodeExecutor)
@PluginDescription(title = "Groovy SQL Command Executor", description = "Executes an inline Groovy SQL statement on the database using JDBC.")
public class GroovySQLCommandPlugin implements NodeExecutor, Describable {
    static final String SERVICE_PROVIDER_TYPE = "GroovySQLCommandExecutor";
    private Framework framework;

    public GroovySQLCommandPlugin(final Framework framework) {
        this.framework = framework;
    }

    // Plugin description with configuration options
    static final Description DESC = DescriptionBuilder.builder()
        .name(SERVICE_PROVIDER_TYPE)
        .title("Groovy SQL Command Executor")
        .description("Executes an inline Groovy SQL statement on the database using JDBC.")
        .property(PropertyBuilder.builder()
            .string("jdbcUrl")
            .title("JDBC URL")
            .description("The JDBC connection URL for the database.")
            .required(true))
        .property(PropertyBuilder.builder()
            .string("dbUser")
            .title("Database User")
            .description("The username for the database connection.")
            .required(true))
        .property(PropertyBuilder.builder()
            .string("dbPassword")
            .title("Database Password")
            .description("The password for the database connection.")
            .required(true)
            .renderingOption(StringRenderingConstants.DISPLAY_TYPE_KEY, StringRenderingConstants.DISPLAY_TYPE_PASSWORD))
        .build();

    @Override
    public Description getDescription() {
        return DESC;
    }

    @Override
    public NodeExecutorResult executeCommand(final ExecutionContext context, final String[] command, final INodeEntry node) {
        // Example of retrieving a property value
        String jdbcUrl = context.getFrameworkProjectMgr().getFrameworkProject(context.getFrameworkProject()).getProperty("project.jdbc.url");

        // Additional logic to handle connection and execution
        try {
            // Placeholder for the actual connection and execution logic
            // For example: executeStatement(jdbcUrl, command);
            return NodeExecutorResultImpl.createSuccess(node);
        } catch (Exception e) { // Catch a more specific exception
            return NodeExecutorResultImpl.createFailure(StepFailureReason.PluginFailed, e.getMessage(), node);
        }
    }

    // Add your method to establish connection and execute SQL command here
    // For instance, a simplified version of what could be your `executeStatement` method
    private void executeStatement(String jdbcUrl, String[] command) throws Exception {
        // Implementation of JDBC connection and statement execution
    }

    private static String buildStatement(String[] args) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
