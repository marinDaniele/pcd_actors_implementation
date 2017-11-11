package it.unipd.math.pcd.actors;

import java.util.LinkedList;

/**
 * Created by Daniele Marin on 26/01/16.
 */
public class MailBoxImpl<M extends Message, S extends ActorRef<M> > implements MailBox<M, S> {

    LinkedList< PostedBy<M, S> > mailBox;

    public MailBoxImpl() {
        mailBox = new LinkedList< PostedBy<M,S> >();
    }

    @Override
    public void add(PostedBy<M, S> posted) {
        mailBox.addFirst(posted);
    }

    @Override
    public PostedBy<M, S> removeLast() {
        return mailBox.removeLast();
    }

    @Override
    public boolean isEmpty() {
        return mailBox.isEmpty();
    }
}
