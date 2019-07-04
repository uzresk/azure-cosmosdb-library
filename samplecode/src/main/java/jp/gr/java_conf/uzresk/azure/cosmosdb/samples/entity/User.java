package jp.gr.java_conf.uzresk.azure.cosmosdb.samples.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jp.gr.java_conf.uzresk.azure.cosmosdb.core.annotation.Document;
import jp.gr.java_conf.uzresk.azure.cosmosdb.core.entity.EntityBase;
import lombok.Data;

import java.util.Date;

@Data
@Document(container = "User")
public class User extends EntityBase {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("registration_date")
    private Date registrationDate;
}
