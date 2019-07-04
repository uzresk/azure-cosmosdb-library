package jp.gr.java_conf.uzresk.azure.cosmosdb.core.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.documentdb.Document;
import org.json.JSONObject;

import java.io.IOException;

class DocumentConverter {

    private final ObjectMapper objectMapper;

    DocumentConverter() {
        this.objectMapper = new ObjectMapper();
    }

    <R> R read(Class<R> type, Document sourceDocument) {

        // 存在しないプロパティを無視してMapping
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            final JSONObject jsonObject = new JSONObject(sourceDocument.toJson());
            return objectMapper.readValue(jsonObject.toString(), type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read the source document " + sourceDocument.toJson()
                    + "  to target type " + type, e);
        }


    }
}
