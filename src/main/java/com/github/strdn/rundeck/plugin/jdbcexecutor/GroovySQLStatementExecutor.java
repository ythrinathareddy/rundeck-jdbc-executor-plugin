package com.github.strdn.rundeck.plugin.jdbcexecutor;

import groovy.sql.Sql;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

class GroovySQLStatementExecutor {
    static void executeStatement(final Sql sql, final String groovyStatementScript, final String args) throws ScriptException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        final Map<String, Object> engineBindings = new SimpleBindings();
        engineBindings.put("sql", sql);
        if (args != null) {
            engineBindings.put("args", args.split(" "));
        }

        try {
            engine.eval(groovyStatementScript, engineBindings);
        } catch (ScriptException se) {
            // Log the error and rethrow for the calling method to handle
            throw new ScriptException("Error executing Groovy SQL statement: " + se.getMessage());
        } finally {
            closeSql(sql);
        }
    }

    static void executeStatement(final Sql sql, final Path groovyStatementScriptPath, final String args) throws ScriptException, IOException {
        final String fileContent = new String(Files.readAllBytes(groovyStatementScriptPath));
        executeStatement(sql, fileContent, args);
    }

    private static void closeSql(Sql sql) {
        try {
            if (sql != null) {
                sql.close();
            }
        } catch (Exception e) {
            // Log any error that occurs during closing
            System.err.println("Error closing SQL connection: " + e.getMessage());
        }
    }
}
