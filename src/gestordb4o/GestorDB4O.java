package gestordb4o;

import com.db4o.ext.Db4oException;
import ioc.dam.m6.exemples.db4o.Article;
import ioc.dam.m6.exemples.db4o.Envas;
import ioc.dam.m6.exemples.db4o.Estoc;
import ioc.dam.m6.exemples.db4o.Magatzem;
import ioc.dam.m6.exemples.db4o.Producte;
import ioc.dam.m6.exemples.db4o.ProducteAGranel;
import ioc.dam.m6.exemples.db4o.ProducteEnvasat;
import ioc.dam.m6.exemples.db4o.UnitatDeMesura;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ale
 */
public class GestorDB4O {

    private String dbFile;

    public GestorDB4O(String dbFile) {
        this.dbFile = dbFile;
    }

    public void instanciar() {
        GestorProductes gestor = null;
        try {
            gestor = new GestorProductes(dbFile);

            //Crea unitats de mesura
            UnitatDeMesura kg = gestor.obtenirObjecte(new UnitatDeMesura("kg", "quilogram"));
            UnitatDeMesura g = gestor.obtenirObjecte(new UnitatDeMesura("g", "gram"));
            UnitatDeMesura l = gestor.obtenirObjecte(new UnitatDeMesura("l", "litre"));

            //Crea els envasos
            Envas llauna = gestor.obtenirObjecte(new Envas("llauna", 500, g));
            Envas botella = gestor.obtenirObjecte(new Envas("botella", 1.5, l));
            Envas bric = gestor.obtenirObjecte(new Envas("bric", 0.75, l));

            //Crea el magatzem
            Magatzem magatzem = gestor.obtenirObjecte(new Magatzem("1", "Magatzem 1"));

            //Crea articles i productes
            Article galletes = gestor.obtenirObjecte(new Article("Galletes"));
            gestor.obtenirObjecte(new Producte(galletes, "Maria", 1.99));
            gestor.obtenirObjecte(new Producte(galletes, "Integrals", 1.40));
            gestor.obtenirObjecte(new Producte(galletes, "Sense Gluten", 2.10));

            Article aigua = gestor.obtenirObjecte(new Article("Aigua"));
            gestor.obtenirObjecte(new ProducteEnvasat(aigua, "Fontvella", 1.22, botella));
            gestor.obtenirObjecte(new ProducteEnvasat(aigua, "Veri", 0.99, botella));

            Article llet = gestor.obtenirObjecte(new Article("Llet"));
            gestor.obtenirObjecte(new ProducteEnvasat(llet, "Semidesnatada Hacendado", 0.70, bric));
            gestor.obtenirObjecte(new ProducteEnvasat(llet, "Entera Asturiana", 1.60, botella));

            Article menjarGats = gestor.obtenirObjecte(new Article("Menjar per a gats"));
            gestor.obtenirObjecte(new ProducteEnvasat(menjarGats, "Friskies", 0.77, llauna));

            Article ceba = gestor.obtenirObjecte(new Article("Ceba"));
            gestor.obtenirObjecte(new ProducteAGranel(ceba, 0.94, kg));

            Article tomatiga = gestor.obtenirObjecte(new Article("Tomatiga"));
            gestor.obtenirObjecte(new ProducteAGranel(tomatiga, 1.56, kg));

            gestor.guardaCanvis();

            //Obté els productes per crear l'estoc
            List<Producte> productes = gestor.obtenirProductes();

            //Guarda la quantitat de productes de l'estoc
            double[] quantitat = new double[]{12.0, 5.0, 10.0, 25.0, 30.0,
                2.5, 4.8, 29.5, 12.5, 1.25};

            //Recorre tots els productes
            for (int i = 0; i < productes.size(); i++) {
                Estoc estoc;
                Producte producte = productes.get(i);
                //Crea l'estoc a partir del producte i la quantitat i el guarda
                //en el magatzem
                magatzem.assignarEstoc(producte, quantitat[i]);
            }

            //Actualitza el magatzem
            gestor.actualitzaObjecte(magatzem);

            //Mostrar el magatzem
            magatzem = gestor.obtenirObjecte(new Magatzem("1"));
            System.out.println(magatzem);
            Map<Producte, Estoc> estoc = magatzem.getEstoc();
            for (Map.Entry<Producte, Estoc> entry : estoc.entrySet()) {
                System.out.println(entry.getValue());
            }

        } catch (Db4oException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (gestor != null) {
                gestor.tancar();
            }
        }
    }

    public void modificar() {
        GestorProductes gestor = null;
        try {
            gestor = new GestorProductes(dbFile);

            //Incrementa el preu de les galletes un 5%
            List<Producte> productes = gestor.obtenirProductePerArticle(new Article("Galletes"));
            for (Producte producte : productes) {
                producte.setPreu(producte.getPreu() * 1.05);
            }

            gestor.guardaCanvis();

            //Mostram els productes canviats
            System.out.println("Incrementam el preu de les galletes un 5%:");
            productes = gestor.obtenirProductePerArticle(new Article("Galletes"));
            for (Producte producte : productes) {
                System.out.println(producte);
            }

            //Incrementam l'estoc de tres productes del magatzem
            Magatzem magatzem = gestor.obtenirObjecte(new Magatzem("1"));
            productes = gestor.obtenirProductePerArticle(new Article("Galletes"));
            for (Producte producte : productes) {
                magatzem.incrementarEstocProducte(producte, 10.0);
            }

            gestor.guardaCanvis();

            //Mostram l'estoc del magatzem
            System.out.println("\nIncrementam l'estoc de les galletes");
            magatzem = gestor.obtenirObjecte(new Magatzem("1"));
            Map<Producte, Estoc> estoc = magatzem.getEstoc();
            for (Map.Entry<Producte, Estoc> entry : estoc.entrySet()) {
                System.out.println(entry.getValue());
            }

            //Decrement de l'estoc
            magatzem = gestor.obtenirObjecte(new Magatzem("1"));
            productes = gestor.obtenirProductePerArticle(new Article("Llet"));
            for (Producte producte : productes) {
                magatzem.decrementarEstocProducte(producte, 1.0);
            }

            gestor.guardaCanvis();

            //Mostram l'estoc del magatzem
            System.out.println("\nDisminuim l'estoc de la llet");
            magatzem = gestor.obtenirObjecte(new Magatzem("1"));
            estoc = magatzem.getEstoc();
            for (Map.Entry<Producte, Estoc> entry : estoc.entrySet()) {
                System.out.println(entry.getValue());
            }

            //Guarda els canvis
            gestor.guardaCanvis();

        } catch (Db4oException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (gestor != null) {
                gestor.tancar();
            }
        }
    }

    public void mostarProductesEstocMenorQueDeu() {
        GestorProductes gestor = null;
        try {
            gestor = new GestorProductes(dbFile);
            List<Producte> productes = gestor.obtenirEstocZero("1", 10.0);
            for (Producte producte : productes) {
                System.out.println(producte.toString());
            }
        } finally {
            if (gestor != null) {
                gestor.tancar();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GestorDB4O gestor = new GestorDB4O("productes.db");
        gestor.instanciar();
        gestor.modificar();
        gestor.mostarProductesEstocMenorQueDeu();

        /*Comprovació de obtenirProductesDunArticlePerPreu
        GestorProductes gestorProductes = new GestorProductes("productes.db");
        List<Producte> productes = gestorProductes.obtenirProductesDunArticlePerPreu("Galletes", 1.5, 2.1);
        for (Producte producte : productes) {
            System.out.println(producte);
        }*/
        
        //Comprovació de eliminar
        /*GestorProductes gestorProductes = new GestorProductes("productes.db");
        List<Producte> productes = gestorProductes.obtenirProductePerArticle(new Article("Llet"));
        for (Producte producte : productes) {
            //Eliminarem els productes de la marca Hacendado
            if (producte.getMarca().contains("Hacendado")) {
                gestorProductes.eliminar(producte);
            }
        }
        gestorProductes.guardaCanvis();
        
        //Mostram els objectes del magatzem
        System.out.println("\nComprovam eliminacio");
        Magatzem magatzem = gestorProductes.obtenirObjecte(new Magatzem("1"));
        Map<Producte, Estoc> estoc = magatzem.getEstoc();
        for (Map.Entry<Producte, Estoc> entry : estoc.entrySet()) {
            System.out.println(entry.getValue());
        }
        gestorProductes.tancar();*/

    }
}
