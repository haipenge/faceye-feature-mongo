git pull
mvn clean -P product
mvn test-compile -P product 
mvn compile  package install -D maven.test.skip=true -P product
exit 0
