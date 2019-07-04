package jp.gr.java_conf.uzresk.azure.cosmosdb.samples.dao;

import com.microsoft.azure.documentdb.*;
import jp.gr.java_conf.uzresk.azure.cosmosdb.core.dao.BaseDao;
import jp.gr.java_conf.uzresk.azure.cosmosdb.samples.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserDao extends BaseDao<User> {

    public void save(User user) {
        user.setId(user.getUserId());
        PartitionKey partitionKey = new PartitionKey(user.getUserId());
        super.save(user, partitionKey);
    }

    public User findById(String userId) {
        return super.findById(userId);
    }

    /**
     * Emailアドレスで検索します。
     * PartitionKeyを指定
     *
     * @param userId UserId
     * @param email  Email
     * @return List<User>
     */
    public List<User> findByEmail(String userId, String email) {

        String query = "SELECT * FROM User WHERE User.email = @email";
        SqlQuerySpec sqlQuerySpec = new SqlQuerySpec();
        sqlQuerySpec.setQueryText(query);
        sqlQuerySpec.setParameters(new SqlParameterCollection(new SqlParameter("@email", email)));

        FeedOptions options = new FeedOptions();
        options.setPartitionKey(new PartitionKey(userId));

        return find(sqlQuerySpec, options);
    }

    /**
     * Emailアドレスで検索します。
     * Cross Partition Search
     *
     * @param email Email
     * @return List<User>
     */
    public List<User> findByEmail(String email) {

        String query = "SELECT * FROM User WHERE User.email = @email";
        SqlQuerySpec sqlQuerySpec = new SqlQuerySpec();
        sqlQuerySpec.setQueryText(query);
        sqlQuerySpec.setParameters(new SqlParameterCollection(new SqlParameter("@email", email)));

        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        return find(sqlQuerySpec, options);
    }

    public void delete(String userId) {
        super.delete(userId);
    }

    public void deleteAll() {
        findAll().forEach(u -> delete(u.getUserId()));
    }
}
