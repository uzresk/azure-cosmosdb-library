package jp.gr.java_conf.uzresk.azure.cosmosdb.core.dao;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import org.apache.commons.lang3.StringUtils;

class CosmosDBClient {

    private static String MASTER_KEY = System.getProperty("ACCOUNT_KEY",
            StringUtils.defaultString(StringUtils.trimToNull(
                    System.getenv().get("ACCOUNT_KEY")), ""));

    private static String COSMOSDB_URI = System.getProperty("COSMOSDB_URI",
            StringUtils.defaultString(StringUtils.trimToNull(
                    System.getenv().get("COSMOSDB_URI")), CosmosDBConfiguration.getUri()));

    private static DocumentClient documentClient = new DocumentClient(COSMOSDB_URI, MASTER_KEY,
            ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);

    static DocumentClient getClient() {
        return documentClient;
    }

}
