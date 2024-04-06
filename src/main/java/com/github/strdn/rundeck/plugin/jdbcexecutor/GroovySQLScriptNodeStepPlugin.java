package com.github.strdn.rundeck.plugin.jdbcexecutor;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepFailureReason;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.PluginException;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.StringRenderingConstants;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.RenderingOption;
import com.dtolabs.rundeck.plugins.descriptions.SelectValues;
import com.dtolabs.rundeck.plugins.descriptions.TextArea;
import com.dtolabs.rundeck.plugins.step.NodeStepPlugin;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.strdn.rundeck.plugin.jdbcexecutor.GroovySQLStatementExecutor.executeStatement;

@Plugin(name = GroovySQLScriptNodeStepPlugin.SERVICE_PROVIDER_NAME, service = ServiceNameConstants.WorkflowNodeStep)
@PluginDescription(title = "Groovy SQL Script Executor", description = "Executes a Groovy SQL script using JDBC.")
public class GroovySQLScriptNodeStepPlugin implements NodeStepPlugin, DescriptionBuilder.Collaborator {

    public static final String FILE_SOURCE = "File";
    public static final String INLINE_SOURCE = "Inline";

    @PluginProperty(title = "Script source",
            description = "Select the source of the script",
            required = true,
            defaultValue = FILE_SOURCE)
    @SelectValues(values = {FILE_SOURCE, INLINE_SOURCE})
    private String scriptSource;

    @PluginProperty(title = "Groovy SQL script path",
            description = "Provide the path to the Groovy SQL script file",
            required = false)
    private String scriptPath;

    @PluginProperty(title = "Groovy SQL inline script",
            description = "Enter the Groovy SQL script directly",
            required = false)
    @TextArea
    @RenderingOption(key = StringRenderingConstants.DISPLAY_TYPE_KEY, value = "CODE")
    private String scriptBody;

    @PluginProperty(title = "Script arguments",
            description = "Provide any arguments needed for the script",
            required = false)
    private String scriptArgs;

    public static final String SERVICE_PROVIDER_NAME = "GroovySQLScriptNodeStepPlugin";

    public void buildWith(DescriptionBuilder builder) {
        builder
            .name(SERVICE_PROVIDER_NAME)
            .title("Groovy SQL Script Executor")
            .description("Executes a Groovy SQL script using JDBC.")
            .build();
    }

    @Override
    public void executeNodeStep(PluginStepContext context, Map<String, Object> configuration, INodeEntry entry)
            throws NodeStepException {
        try {
            if (FILE_SOURCE.equals(scriptSource)) {
                executeFileScript(context, entry);
            } else if (INLINE_SOURCE.equals(scriptSource)) {
                executeInlineScript(context, entry);
            } else {
                throw new NodeStepException("Invalid script source", StepFailureReason.ConfigurationFailure, entry.getNodename());
            }
        } catch (Exception e) {
            throw new NodeStepException(e, StepFailureReason.PluginFailed, entry.getNodename());
        }
    }

    private void executeFileScript(PluginStepContext context, INodeEntry entry) throws IOException, ScriptException, ConfigurationException {
        if (scriptPath == null || scriptPath.trim().isEmpty()) {
            throw new ConfigurationException("Script path is not provided.");
        }

        Path scriptFilePath = Path.of(scriptPath);
        if (!Files.isRegularFile(scriptFilePath) || !Files.isReadable(scriptFilePath)) {
            throw new IOException("Cannot read script file: " + scriptPath);
        }

        executeStatement(new SqlConnectionBuilder(context.getExecutionContext(), entry, context.getFramework()).build(),
                scriptFilePath.toAbsolutePath().toString(), scriptArgs);
    }

    private void executeInlineScript(PluginStepContext context, INodeEntry entry) throws ScriptException, ConfigurationException {
        if (scriptBody == null || scriptBody.trim().isEmpty()) {
            throw new ConfigurationException("Inline script is empty.");
        }

        executeStatement(new SqlConnectionBuilder(context.getExecutionContext(), entry, context.getFramework()).build(),
                scriptBody, scriptArgs);
    }
}
