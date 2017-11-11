package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by Daniele Marin on 26/01/16.
 *
 * Le istanze della seguente classe possono gestire solamente attori
 * con ActorMode LOCAL
 */
public class BasicActorSystem extends AbsActorSystem {
    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if (mode == ActorMode.LOCAL) {
            // costruisce e restituisce un ActorRefImpl
            return new ActorRefImpl(this);
        }
        else {
            throw new IllegalArgumentException();
        }

    }

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

    @Override
    public void stop() {
        for (ActorRef actorRef : actors.keySet() ) {
            stop(actorRef);
        }

    }

}
