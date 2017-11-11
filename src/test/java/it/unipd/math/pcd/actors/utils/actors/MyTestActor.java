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
package it.unipd.math.pcd.actors.utils.actors;

import it.unipd.math.pcd.actors.*;
import it.unipd.math.pcd.actors.utils.MyTestActorSystem;
import it.unipd.math.pcd.actors.utils.messages.TrivialMessage;

/**
 * A test actor for my test.
 *
 * @author Daniele Marin
 * @version 1.0
 * @since 1.0
 */
public class MyTestActor extends TrivialActor {

    private AbsActorSystem refAs;

    public void setRefAs(AbsActorSystem as) {
        refAs = as;
    }

    /**
     * Metodo del tutto uguale a quello della classe AbsActor
     * ma che subito dopo l'aggiunta del messagio nella mailBox
     * incrementa un contatore che tiene conto dei messaggi
     * effettivamente aggiunti alla mailBox
     * Questo metodo viene utilizzato esclusivamente
     * nel test AllMessagesProcessedTest
     * @param message oggetto di tipo derivato Message
     * @param send oggetto di tipo derivato da ActorRef
     */
    @Override
    public void addToMailBox(final TrivialMessage message, final ActorRef<TrivialMessage> send) {

        // Aggiunge un messaggio alla mailBox
        // solamente se l'attore è ancora attivo
        if( isActive() ) {
            synchronized (mailBox) {
                PostedBy<TrivialMessage,ActorRef<TrivialMessage>> posted = new PostedBy<>(message, send);
                mailBox.add(posted);
                /**
                 * Decommentare l'operazione solamente per
                 * effettuare il test AllMessagesProcessedTest
                 * L'operazione incrementa un contatore
                 * che conta i pessaggi effettivamenti aggiunti alla mailBox
                 */
                synchronized (this){ numeroMessaggiInviati++;}

                // notifico a chi è in attesa sulla mailBox che 'è un nuovo messaggio
                mailBox.notifyAll();
            }
        }

    }

    /**
     * Variabile privata e metodo utilizzati SOLAMENTE per il test AllMessagesProcessedTest
     * Decommentare la variabile e il metodo per effettuare il test
     */
    private volatile int numeroMessaggiInviati;
    public synchronized int getNumeroMessaggiInviati() { return numeroMessaggiInviati; }

    /**
     * Quando invoco recive(...) incremento il contatore dei messaggi processati
     * @param message The type of messages the actor can receive
     */
    @Override
    public void receive(TrivialMessage message) {
        // increment counter
        ((MyTestActorSystem)refAs).incReciveMessages();
    }
}
