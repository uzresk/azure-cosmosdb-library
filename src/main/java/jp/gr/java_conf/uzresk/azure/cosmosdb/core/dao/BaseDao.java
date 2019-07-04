package jp.gr.java_conf.uzresk.azure.cosmosdb.core.dao;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class BaseDao<T> {

    private String containerName;

    private Class<T> entityClass;

    public BaseDao() {

        Class<?> clazz = this.getClass();
        // ここではBaseDao<?>がとれる
        Type type = clazz.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        // BaseDaoの型変数に対するバインドされた型がとれる
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        @SuppressWarnings("unchecked") Class<T> entityClass = (Class<T>) actualTypeArguments[0];
        // annotationからコンテナ名を取得する
        final jp.gr.java_conf.uzresk.azure.cosmosdb.core.annotation.Document annotation
                = entityClass.getAnnotation(jp.gr.java_conf.uzresk.azure.cosmosdb.core.annotation.Document.class);
        if (annotation != null && !annotation.container().isEmpty()) {
            this.containerName = annotation.container();
        }
        this.entityClass = entityClass;
    }

//    protected void insert(T objectToSave, PartitionKey partitionKey) {
//
//        try {
//            final RequestOptions options = getRequestOptions(partitionKey, null);
//            ResourceResponse<Document> response = CosmosDBClient.getClient().createDocument(
//                    getCollectionLink(), objectToSave, options,
//                    true);
//            log.info("[Create]RequestCharge:" + response.getRequestCharge());
//        } catch (DocumentClientException e) {
//            throw new RuntimeException(e);
//        }
//    }

    protected void save(T objectToSave, PartitionKey partitionKey) {
        try {
            final RequestOptions options = getRequestOptions(partitionKey);
            ResourceResponse<Document> response = CosmosDBClient.getClient().upsertDocument(
                    getCollectionLink(), objectToSave, options,
                    true);
            log.info("[Upsert]RequestCharge:" + response.getRequestCharge());
        } catch (DocumentClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自動採番されたIDを使ってUserを作成する。
     */
//    public void createUserAutomaticGenerationId(T objectToSave) {
//        try {
//            CosmosDBClient.getClient().createDocument(
//                    getCollectionLink(), objectToSave, null,
//                    false).getResource();
//        } catch (DocumentClientException e) {
//            e.printStackTrace();
//        }
//    }
    protected T findById(String id) {

        try {
            PartitionKey partitionKey = new PartitionKey(id);
            final RequestOptions options = getRequestOptions(partitionKey);

            // DocumentIDにはidを指定する
            ResourceResponse<Document> resourceResponse =
                    CosmosDBClient.getClient().readDocument(getDocumentLink(id), options);
            log.info("[FindById]RequestCharge:" + resourceResponse.getRequestCharge());
            return new DocumentConverter().read(entityClass, resourceResponse.getResource());

        } catch (DocumentClientException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<T> find(SqlQuerySpec sqlQuerySpec, FeedOptions options) {
        FeedResponse<Document> feedResponse =
                CosmosDBClient.getClient().queryDocuments(getCollectionLink(), sqlQuerySpec, options);
        log.info("[Query]" + sqlQuerySpec + ", "
                + "[RequestCharge]" + feedResponse.getRequestCharge());

        Iterator<Document> docs = feedResponse.getQueryIterator();

        List<T> results = new ArrayList<>();
        while (docs.hasNext()) {
            Document doc = docs.next();
            results.add(new DocumentConverter().read(entityClass, doc));
        }
        return results;
    }

    public List<T> findAll() {
        String query = "SELECT * FROM " + containerName;
        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);
        return find(new SqlQuerySpec(query), options);
    }

    public void delete(String key) {
        try {
            // パーティションキーがあるコンテナはパーティションキーを指定する必要がある。
            PartitionKey partitionKey = new PartitionKey(key);
            final RequestOptions options = getRequestOptions(partitionKey);
            CosmosDBClient.getClient().deleteDocument(getDocumentLink(key), options);
        } catch (DocumentClientException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDatabaseLink() {
        return "dbs/" + CosmosDBConfiguration.getDatabase();
    }

    private String getCollectionLink() {
        return getDatabaseLink() + "/colls/" + containerName;
    }

    private String getDocumentLink(Object documentId) {
        return getCollectionLink() + "/docs/" + documentId;
    }

    private RequestOptions getRequestOptions(PartitionKey key) {
        final RequestOptions options = new RequestOptions();
        if (key != null) {
            options.setPartitionKey(key);
        }
        // TODO offer throughputを後で指定するパターン（必要になったら追加する）
//        if (requestUnit != null) {
//            options.setOfferThroughput(requestUnit);
//        }
        return options;
    }
}
