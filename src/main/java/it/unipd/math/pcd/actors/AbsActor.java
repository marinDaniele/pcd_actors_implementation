/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T> {


    /**
     * Container for PostedBy objects
     */
    protected final MailBox<T, ActorRef<T> > mailBox;

    /**
     * variabile booleana che segnala se l'attore è attivo o no
     */
    private volatile boolean active;

    /**
     * variabile booleana che segnala la fine della processazione dei messaggi
     */
    private volatile boolean finished;

    /**
     * Self-reference of the actor
     */
    protected ActorRef<T> self;

    /**
     * Sender of the current message
     */
    protected ActorRef<T> sender;


    /**
     * costruttore
     */
    public AbsActor(){
        mailBox = new MailBoxImpl<>();
        active = true;
        finished = false;
        startReciveProcess();
    }


    private void startReciveProcess() {

        /**
         * Creo ed avvio il thread che durante tutta la vita dell'Actor
         * si occuperà di svuotare la mailBox e di invocare per ogni messaggio
         * il metodo receve(Message) dell'Actor
         */
        Thread reciveProcess = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // fino a che l'attore è attivo
                    while ( active ) {
                        // se la mailBox è vuota e l'attore è attivo
                        while ( mailBox.isEmpty() && active) {
                            synchronized (mailBox) {
                                // mi metto in attesa sulla mailBox
                                mailBox.wait();

                            }
                        }
                        // se l'attore è attivo e la mailbox non è vuota
                        if (!mailBox.isEmpty()){
                            synchronized (mailBox) {
                                // recupero il messagio più vecchio presente nella mailBox
                                PostedBy<T,ActorRef<T>> posted = mailBox.removeLast();
                                // imposto sender con il riferimento del sender del messaggio
                                sender = posted.getSender();
                                // invoco il metodo recive passandoli il messaggio
                                receive(posted.getMessage());
                            }
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    // se il thread viene interrotto inaspettatamente disattivo l'attore
                    deactiveActor();
                }
                finally {
                    // L'attore non è più attivo quindi devo svuotare la mailBox
                    while ( !mailBox.isEmpty() ) {
                        synchronized (AbsActor.this) {
                            // recupero il messagio più vecchio presente nella mailBox
                            PostedBy<T,ActorRef<T>> posted = mailBox.removeLast();
                            // imposto sender con il riferimento del sender del messaggio
                            sender = posted.getSender();
                            // invoco il metodo recive passandoli il messaggio
                            receive(posted.getMessage());
                        }
                    }

                    // mi sincronizzo sull'atore
                    synchronized (AbsActor.this){
                        // segnalo la ternimazione del processo
                        AbsActor.this.finished = true;
                        // notifico al thread in attesa sull'attore
                        AbsActor.this.notifyAll();
                    }
                }
            }
        });

        // avvio il thread reciveProcess
        reciveProcess.start();
    }


    /**
     * Restituisce lo stato dell'Actor, attivo o no
     * @return true se l'attore è attivo, false altrimenti
     */
    public boolean isActive() { return active; }

    /**
     * Disattiva l'attore su cui viene invocato
     * e si occupa di svuotare la mailBox dai messaggi rimanenti
     */
    public synchronized void deactiveActor() throws NoSuchActorException{
        // disattivo l'attore mettendolo a false
        if (active) {
            // disattivo l'attore
            active = false;
            // se l'attore è in wait e nessuno invia messaggi devo notificarlo
            synchronized (mailBox) {
                mailBox.notifyAll();
            }
        }
        else {
            // se l'attore è gia stato stoppato l'ancio un eccezione
            throw new NoSuchActorException("Attore gia stoppato!");
        }
    }

    /**
     * Restitisce lo stato del processo di gestione
     * dei messaggi da pare dell'attore
     * @return true se il processo è finto, false altimenti
     */
    public boolean haveFinished() {
        return finished;
    }

    /**
     * Metodo che avvia un thread per aggiungere un messaggio alla mailBox
     * Il messaggio verrà inserito alla mailBox solamente se l'attore è attivo
     * @param message oggetto di tipo derivato da Message
     * @param send oggetto di tipo derivato da ActorRef
     */
    public void addToMailBox(final T message, final ActorRef<T> send) {

        // Aggiunge un messaggio alla mailBox
        // solamente se l'attore è ancora attivo
        if( active ) {
            synchronized (mailBox) {
                // creo un oggetto PostedBy
                PostedBy<T,ActorRef<T>> posted = new PostedBy<>(message, send);
                // aggiungo l'oggetto alla mailBox
                mailBox.add(posted);
                // notifico a chi è in attesa sulla mailBox che c'è un nuovo messaggio
                mailBox.notifyAll();
            }
        }
    }

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

}
