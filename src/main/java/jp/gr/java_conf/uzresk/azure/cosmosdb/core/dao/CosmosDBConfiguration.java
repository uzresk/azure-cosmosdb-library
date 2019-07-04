package jp.gr.java_conf.uzresk.azure.cosmosdb.core.dao;

import java.util.ResourceBundle;

class CosmosDBConfiguration {

    private static final ResourceBundle rb = ResourceBundle.getBundle("cosmosdb");

    static String getUri() {
        return rb.getString("azure.cosmosdb.uri");
    }

    static String getDatabase() {
        return rb.getString("azure.cosmosdb.database");
    }
}
