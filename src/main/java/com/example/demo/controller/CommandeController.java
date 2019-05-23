package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.CrudAction;
import com.example.demo.model.CrudEnum;
import com.example.demo.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Controller REST.
 */
@RestController
@RequestMapping("api/commande")
public class CommandeController {

    /**
     * Instance du repository de commandes.
     */
    @Autowired
    private CommandeRepository repo;

    /**
     * Processor permettant d'émettre des événements.
     */
    private DirectProcessor<Commande> processor;

    /**
     * Date du jour au format String JJ/MM/AAAA.
     */
    private String dateJour;

    @PostConstruct
    private void init() {
        this.processor = DirectProcessor.create();
        this.dateJour = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }


    /**
     * Retourne la commande en base pour le paramètre donné.
     * @param clientDate donné.
     * @return la commande en base pour le paramètre donné.
     */
    @GetMapping("/{clientDate}")
    public Mono<Commande> read(@PathVariable String clientDate) {
        return this.repo.findByClientDate(clientDate);
    }

    /**
     * Retourne l'instance de commande en base créée pour la commande passée en paramètre.
     * @param commande donnée.
     * @return l'instance de commande en base créée pour la commande passée en paramètre.
     */
    @PostMapping
    public Mono<Commande> create(@RequestBody Commande commande) {
        return this.repo.insert(commande);
    }

    /**
     * Retourne un flux Server Sent Event résultant du merge du flux de commande @Taillable
     * en provenance de la base et du flux émis sur notre processor.
     * @return un flux Server Sent Event résultant du merge du flux de commande @Taillable
     *       en provenance de la base et du flux émis sur notre processor.
     */
    @GetMapping("/message")
    public Flux<ServerSentEvent<CrudAction<Commande>>> fetchAll() {
        return Flux.merge(
         this.repo
                .findByDateCommandeAndTraiteeIsFalse(dateJour)
                .map(commande ->
                        new CrudAction<Commande>(commande, CrudEnum.READ))
                .map(crudAction ->
                        ServerSentEvent.<CrudAction<Commande>>builder().data(crudAction).build())
        ,
        this.processor
                .map(commande ->
                        new CrudAction<Commande>(commande, CrudEnum.UPDATE))
                .map(crudAction ->
                        ServerSentEvent.<CrudAction<Commande>>builder().data(crudAction).build())
        );
    }

    /**
     * Retourne l'instance de commande maj en base et émise sur le flux processor, pour la
     * commande passée en paramètre.
     * @param commande donnée.
     * @return l'instance de commande maj en base et émise sur le flux processor, pour la
     *      commande passée en paramètre.
     */
    @PutMapping
    public Mono<Commande> update(@RequestBody Commande commande) {
        return this.repo
                .save(commande)
                .doOnSuccess(this.processor::onNext);
    }
}
