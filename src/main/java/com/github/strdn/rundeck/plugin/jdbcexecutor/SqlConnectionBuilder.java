package com.github.strdn.rundeck.plugin.jdbcexecutor;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.plugins.PluginException;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import groovy.sql.Sql;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;

class SqlConnectionBuilder {
    private final ExecutionContext context;
    private final INodeEntry node;
    private final Framework framework;

    SqlConnectionBuilder(final ExecutionContext context, final INodeEntry node, final Framework framework) {
        this.context = context;
        this.node = node;
        this.framework = framework;
    }

    public Sql build() throws ConfigurationException, PluginException {
        try {
            final String jdbcConnectionString = resolveProperty(node, "jdbc-connect");
            final String jdbcUsername = resolveProperty(node, "jdbc-username");
            final String jdbcPassword = resolvePassword();
            final String jdbcDriver = resolveDriver();

            return Sql.newInstance(jdbcConnectionString, jdbcUsername, jdbcPassword, jdbcDriver);
        } catch (ClassNotFoundException cnfe) {
            throw new ConfigurationException("JDBC driver class not found: " + cnfe.getMessage(), cnfe);
        } catch (SQLException sqle) {
            throw new PluginException("Failed to prepare SQL connection: " + sqle.getMessage(), sqle);
        }
    }

    private String resolveProperty(final INodeEntry node, final String propertyName) {
        String value = node.getAttributes().get(propertyName);
        if (StringUtils.isBlank(value)) {
            value = context.getDataContext().get(propertyName);
        }
        return value;
    }

    private String resolvePassword() throws ConfigurationException {
        // Implement logic to retrieve password securely
        return "your-secure-password";
    }

    private String resolveDriver() throws ConfigurationException {
        // Implement logic to retrieve driver class
        return "your-driver-class";
    }
}
