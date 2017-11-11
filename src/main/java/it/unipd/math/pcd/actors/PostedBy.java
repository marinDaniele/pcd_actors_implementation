package it.unipd.math.pcd.actors;

/**
 * Created by Daniele Marin on 26/01/16.
 */
public final class PostedBy< M extends Message, S extends ActorRef<M> > {
    private final M message;
    private final S sender;

    public PostedBy(M message_, S sender_) {
        this.message = message_;
        this.sender = sender_;
    }

    public  M getMessage() { return message; }
    public  S getSender() { return sender; }

}
