package com.example.demo.repository;

import com.example.demo.model.Commande;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository spring data de requêtes sur la base de données.
 */
@Repository
public interface CommandeRepository extends ReactiveMongoRepository<Commande,String> {

    Mono<Commande> findByClientDate(String clientDate);

    @Tailable
    Flux<Commande> findByDateCommandeAndTraiteeIsFalse(String dateCommande);
}
