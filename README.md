# azure-cosmosdb-library

It is a simple library of documentdb

# Usage 

## install library

```
git clone https://github.com/uzresk/azure-cosmosdb-library.git
mvn install
```

## exec test

1. create cosmosdb container User(partition key = user_id)

2. Move directory to samplecode

3. Change test/resources/cosmosdb.properties according to your environment.

```
azure.cosmosdb.uri=https://[account].documents.azure.com:443/
azure.cosmosdb.database=[database name]
```

4. Set the cosmos DB key to the environment variable.

```
export ACCOUNT_KEY=xxxxxx
```

5. Exec test

```
mvn test
```

# License

MIT

