package it.unipd.math.pcd.actors.utils;

import it.unipd.math.pcd.actors.*;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;
import it.unipd.math.pcd.actors.utils.actors.MyTestActor;
import it.unipd.math.pcd.actors.utils.actors.TrivialActor;

/**
 * Created by Daniele Marin on 04/02/16.
 */
public class MyTestActorSystem extends BasicActorSystem {


    //-----------------------------
    // solo per test
    private volatile int counterOfRecived=0;

    /**
     * Incrementa il numero dei messaggi processati dall'attore
     */
    public void incReciveMessages() { counterOfRecived++; }

    /**
     * Restituisce il numero di messaggi processati dall'attore
     * @return il numero di messaggi processati dall'attore
     */
    public int getRecivedMessage() { return counterOfRecived; }

    /**
     * Restituisce il numero di messaggi inviati all'attore
     * che sono stati effettivamente messi nella mailBox
     * @return il numero dei messaggi spediti all'attore
     */
    public int getNumSendMessages() { return numSendMessages; }

    /**
     * Questo metodo rispecchia il funzionamento del metodo stop(actor)
     * della classe BasicActorSystem, ma in più incrementa un contatore
     * che tiene conto dei messaggi inviati
     * @param actor
     */
    @Override
    public void stop(ActorRef<?> actor) {

        if(actors.containsKey(actor)) {
            // recupero l'Actor relativo al ActorRef
            AbsActor attore = (AbsActor) giveMeActor(actor);

            if (attore.isActive()) {

                synchronized (attore) {
                    // Disattivo l'Actor
                    attore.deactiveActor();

                    // attendo che l'attore processi tutti i messaggi
                    while (!attore.haveFinished()) {
                        try {
                            // aspetto che l'attore abbia terminato di processare i messaggi
                            attore.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    /**
                     * Comando che incrementa la variabile utilizzata nel test AllMessagesProcessedTest
                     * É neccessario verificare che il tipo dell'attore sia MyTestActor
                     * perché i test costruendo l'actorSystem utilizzando una fectory
                     * creano un MyTestActorSysytem e non un BaseActorSystem
                     * quindi i test che utilizzano un TrivialActor per il loro svolgimento
                     * non possono eseguire l'istruzione seguente perché lancerebbero una
                     * ClassCastExeption
                     */
                    if (attore instanceof MyTestActor)
                        numSendMessages = ((MyTestActor) attore).getNumeroMessaggiInviati();


                    // Rimuovo L'Actor dal sistema
                    actors.remove(actor);
                }
            }
            else {
                throw new NoSuchActorException();
            }

        }
        else {
            throw new NoSuchActorException();
        }

    }

    /**
     * Variabile creata per il test AllMessagesProcessedTest
     * Contatore per i messaggi inviati
     */
    public int numSendMessages;

    //------------------------------
}
