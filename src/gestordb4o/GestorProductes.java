/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestordb4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.Db4oException;
import com.db4o.ext.Db4oIOException;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import ioc.dam.m6.exemples.db4o.Article;
import ioc.dam.m6.exemples.db4o.Envas;
import ioc.dam.m6.exemples.db4o.Magatzem;
import ioc.dam.m6.exemples.db4o.Producte;
import ioc.dam.m6.exemples.db4o.UnitatDeMesura;
import java.util.ArrayList;
import java.util.List;

/**
 *Classe encarregada de gestionar els productes a la base de dades.
 */
public class GestorProductes {

    ObjectContainer db;

    /**
     * Obre connexió amb la base de dades i la configura
     * @param dbFile Nom del fitxer on es guardaran els objectes
     */
    public GestorProductes(String dbFile) {
        EmbeddedConfiguration conf = Db4oEmbedded.newConfiguration();
        conf.common().objectClass(Magatzem.class).cascadeOnDelete(true);
        conf.common().activationDepth(8);
        conf.common().updateDepth(8);
        db = Db4oEmbedded.openFile(conf, dbFile);
    }

    /**
     * Obté un article guardat a la BD. Si l'article no es troba a la BD, el
     * guarda.
     *
     * @param obj Article que es vol recuperar.
     * @return Article recuperat.
     * @throws Db4oException
     */
    public Article obtenirObjecte(Article obj) throws Db4oException {
        return (Article) obtenirUnObjecte(obj);
    }

    /**
     * Obté un envas guardat a la BD. Si l'envas no es troba a la BD, el guarda.
     *
     * @param obj Envas que es vol recuperar.
     * @return Envas recuperat.
     * @throws Db4oException
     */
    public Envas obtenirObjecte(Envas obj) throws Db4oException {
        return (Envas) obtenirUnObjecte(obj);
    }

    /**
     * Obté un magatzem guardat a la BD. Si el magatzem no es troba a la BD, el
     * guarda.
     *
     * @param obj Magatzem que es vol recuperar.
     * @return Magatzem recuperat.
     * @throws Db4oException
     */
    public Magatzem obtenirObjecte(Magatzem obj) throws Db4oException {
        return (Magatzem) obtenirUnObjecte(obj);
    }

    /**
     * Obté un producte guardat a la BD. Si el producte no es troba a la BD, el
     * guarda.
     *
     * @param obj Producte que es vol recuperar.
     * @return Producte recuperat.
     * @throws Db4oException
     */
    public Producte obtenirObjecte(Producte obj) throws Db4oException {
        return (Producte) obtenirUnObjecte(obj);
    }

    /**
     * Obté una unitat de mesura guardada a la BD. Si la unitat de mesura no es
     * troba a la BD, la guarda.
     *
     * @param obj Unitat de mesura que es vol recuperar.
     * @return Unitat de mesura recuperada.
     * @throws Db4oException
     */
    public UnitatDeMesura obtenirObjecte(UnitatDeMesura obj) throws Db4oException {
        return (UnitatDeMesura) obtenirUnObjecte(obj);
    }

    /**
     * Recupera tots els articles guardats a la BD.
     * @return LLista d'articles guardats a la BD.
     */
    public List<Article> obtenirArticles() {
        return db.queryByExample(new Article());
    }

    /**
     * Recupera tots els envasos guardats a la BD.
     *
     * @return LLista d'envasos guardats a la BD.
     */
    public List<Envas> obtenirEnvasos() {
        return db.queryByExample(new Envas());
    }

    /**
     * Recupera tots els magatzems guardats a la BD.
     * @return LLista de magatzems guardats a la BD.
     */
    public List<Magatzem> obtenirMagatzems() {
        return db.queryByExample(new Magatzem());
    }

    /**
     * Recupera tots els productes guardats a la BD.
     * @return LLista de productes guardats a la BD.
     */
    public List<Producte> obtenirProductes() {
        return db.queryByExample(new Producte());
    }

    /**
     * Recupera totes les unitats de mesura guardades a la BD.
     * @return LLista d'unitats de mesura guardats a la BD.
     */
    public List<UnitatDeMesura> obtenirUnitats() {
        return db.queryByExample(new UnitatDeMesura());
    }

    /**
     * Recupera tots els productes d'un determinat article.
     * @param article Article del qual volem recuperar els productes.
     * @return Llista de productes de l'articles que hem passat per paràmetre.
     */
    public List<Producte> obtenirProductePerArticle(Article article) {
        Query query = db.query();
        query.constrain(Producte.class);
        query.descend("article").descend("id").constrain(article.getId());
        return query.execute();
    }

    /**
     * Recupera tots els productes els quals el nom del seu article comenci per una cadena
     * determinada, i el preu dels quals es trobi entre un preu minim i un preu
     * màxim.
     * @param iniciId Cadena per la qual començarà el denominador de l'article.
     * @param minim Preu minim del producte.
     * @param maxim Preu màxim del producte.
     * @return Llista de productes que compleixin amb aquesta condició.
     */
    public List<Producte> obtenirProductesDunArticlePerPreu(String iniciId,
            double minim, double maxim) {
        Query query = db.query();
        query.constrain(Producte.class);
        query.descend("article").descend("id").constrain(iniciId).startsWith(true);
        query.descend("preu").constrain(minim).equal().greater();
        query.descend("preu").constrain(maxim).equal().smaller();
        return query.execute();
    }

    /**
     * Recupera tots els productes d'un magatzem que tinguin un estoc (la
     * quantitat de existències d'un producte en el magatzem) menor o igual a un
     * determinat valor
     * @param idMagatzem id del magatzem
     * @param quantitat Quantitat màxima d'estoc dels productes.
     * @return Llista de productes que compleixin amb la condició.
     */
    public List<Producte> obtenirEstocZero(String idMagatzem, double quantitat) {
        final Magatzem magatzem;
        ObjectSet<Magatzem> osMagatzems = db.queryByExample(new Magatzem(idMagatzem));

        if (osMagatzems.hasNext()) {
            magatzem = osMagatzems.next();
            if (osMagatzems.hasNext()) {
                throw new Db4oException("S'ha trobat més d'un magatzem amb aquest id");
            }
        } else {
            return new ArrayList<>();
        }

        ObjectSet<Producte> osProductes = db.query(new Predicate<Producte>() {
            @Override
            public boolean match(Producte producte) {
                return magatzem.getEstoc(producte).getQuantitat() <= quantitat;
            }
        });
        return osProductes;
    }

    /**
     * Elimina un objecte de la base de dades. Si es produeix algun error a
     * l'hora d'eliminar l'objecte, es retorna a l'estat original de la BD
     *
     * @param obj Objecte a eliminar.
     */
    public void eliminar(Object obj) {
        try {
            db.delete(obj);
        } catch (Db4oIOException ex) {
            db.rollback();
        }
        db.commit();
    }

    /**
     * Tanca la connexió si està oberta, forçant que es guardin els canvis ja
     * realitzats a la bd
     */
    public void tancar() {
        if (!db.ext().isClosed()) {
            db.commit();
            db.close();
        }
    }

    /**
     * Obté l'objecte de la BD que hem passat per paràmetre. Si l'objecte no es
     * troba a la BD, el guarda.
     * @param obj Objecte que volem recuperar
     * @return Objecte recuperat.
     * @throws Db4oException
     */
    private Object obtenirUnObjecte(Object obj) throws Db4oException {
        ObjectSet objectSet = null;
        objectSet = db.queryByExample(obj);
        //Comprova que s'ha recuperat almenys un objecte
        if (objectSet.hasNext()) {
            obj = objectSet.next();
            //Comprova si s'ha recuperat més de un objecte
            if (objectSet.hasNext()) {
                throw new Db4oException("S'ha trobat més de un resultat per aquesta consulta");
            }
            //Guarda l'objecte a la base de dades si no existeix
        } else {
            db.store(obj);
            db.commit();
        }
        return obj;
    }

    /**
     * Guarda els canvis realitzats a la BD.
     */
    public void guardaCanvis() {
        db.commit();
    }
    
    /**
     * Actualitza els canvis que s'hagin realitzats a un objecte recuperat de la 
     * BD a la BD. Alerta, que si l'objecte no es troba ja a la BD, es guarda a la BD.
     * @param obj Objecte que volem actualitzar a la BD.
     */
    public void actualitzaObjecte(Object obj) {
        db.store(obj);
        db.commit();
    }
}
