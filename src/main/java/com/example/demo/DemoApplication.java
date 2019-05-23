package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class DemoApplication {
    public static void main(String[] args) {
        ApplicationContext application =
                SpringApplication.run(DemoApplication.class, args);


        MongoOperations mongo = application.getBean(MongoOperations.class);
        if (mongo.collectionExists("commande")) {
            mongo.dropCollection("commande");
        }
        mongo.createCollection("commande", CollectionOptions.empty().capped().size(99999L).maxDocuments(999L));


    }

}
