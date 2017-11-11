package it.unipd.math.pcd.actors;

/**
 * Created by Daniele Marin on 26/01/16.
 */
public interface MailBox<M extends Message, S extends ActorRef<M> > {

    /**
     * Aggiunge un oggetto di tipo PostedBy in testa alla MailBox
     * @param posted oggetto di tipo PostedBy
     */
    public void add(PostedBy<M,S> posted);

    /**
     * Rimuove un oggetto di tipo PostedBy dalla testa della MailBox
     * @return restituisce l'oggetto rimosso
     */
    public PostedBy<M,S> removeLast();

    /**
     * Verifica se la MailBox è vuota
     * @return true se la MailBox è vuota false altrimenti
     */
    public boolean isEmpty();


}
