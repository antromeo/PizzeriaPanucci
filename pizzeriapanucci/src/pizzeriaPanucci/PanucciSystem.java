package pizzeriaPanucci;

import com.mysql.cj.xdevapi.Client;

import java.util.*;

public class PanucciSystem {
    /* istanza singleton di BookBoutique */
    private static PanucciSystem instance;
    private static final Object lock = PanucciSystem.class;
    private PanucciSystem() {
    }

    private List<Cliente> listaClienti;
    private List<Comanda> comande;
    private Comanda comandaCorrente;
    private Menu m;
    private Pizza pc;


    /*
     *********************
     * GET ISTANZA SINGLETON E COSTRUTTORE
     *********************
     */

    public static PanucciSystem getIstance(){
        synchronized (lock){
            if (instance == null){
                instance = new PanucciSystem();
                instance.listaClienti = new LinkedList<Cliente>();
                instance.comande = new LinkedList<Comanda>();
                instance.m = Menu.getIstance();
                instance.updateInitialData();
            }
        }
        return instance;
    }



    private void updateInitialData(){
        caricaClienti();
        caricaPizzeMenu();
    }



    private void caricaClienti(){
        DAOFactory mysqlFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = mysqlFactory.getClienteDAO();
        List<Cliente> clienti=clienteDAO.getAllClienti();
        for (Cliente c: clienti){
            this.listaClienti.add(c);
        }


        /*Cliente c1= new Cliente("mario", "rossi", "mrossi@gmail.com", "via marco polo");
        this.listaClienti.add(c1);
        Cliente c2= new Cliente("alfredo", "messina", "alfredomessina@gmail.com", "via alfredo messina");
        this.listaClienti.add(c2);*/

    }

    private void caricaPizzeMenu(){
        //NOTA: Ha senso mettere quantità in pizzeriaPanucci.Ingrediente? Si vedrà negli altri casi d'uso
        Ingrediente funghi=new Ingrediente("funghi", (float) 2.5);
        Ingrediente pomodoro=new Ingrediente("pomodoro", (float) 1.5);
        Ingrediente formaggio=new Ingrediente("formaggio", (float) 1.5);
        Ingrediente uovo=new Ingrediente("uovo", (float) 1.5);

        //aggiunta ingredienti disponibili nel menù
        m.addIngrediente(1, funghi);
        m.addIngrediente(2, pomodoro);
        m.addIngrediente(3, formaggio);
        m.addIngrediente(4, uovo);

        //Aggiungeremo un costo fisso di manodopera per ogni pizza?
        Pizza p1=new Pizza("capricciosa");
        p1.setId(1);
        p1.addIngrediente(funghi);
        p1.addIngrediente(pomodoro);
        p1.addIngrediente(uovo);

        m.put(1, p1);
        Pizza p2=new Pizza("margherita");
        p2.setId(2);
        p2.addIngrediente(formaggio);

        m.put(2,p2);
    }



    public List<Cliente> getListaClienti() {
        return listaClienti;
    }

    public List<Comanda> getElencoComande() {
        return comande;
    }

    public Comanda getComandaCorrente() {
        return comandaCorrente;
    }

    public Menu getMenu() {
        return m;
    }

    public Cliente getCliente(int id){
        for (Cliente c:listaClienti){
            if(c.getId()==id) return c;
        }
        return null;
    }




    public Comanda nuovaComanda(Cliente cliente, String indirizzoConsegna){
        Date data_attuale = new Date();
        this.comandaCorrente = new Comanda(cliente, indirizzoConsegna, data_attuale);
        return this.comandaCorrente;
    }


    public HashMap<Integer,Pizza> elencoPizze(){
        HashMap<Integer,Pizza> pizzeDisponibili=m.getPizzeDisponibili();
        return pizzeDisponibili;
    }


    public Pizza selectPizza (int idPizza){
        pc=m.findPizza(idPizza);
        return pc;
    }

    public HashMap<Integer, Ingrediente> elencoIngredienti(Pizza pizza){
        HashMap<Integer, Ingrediente> elencoIngredienti = m.elencoIngredienti(pizza);
        return elencoIngredienti;

    }

    public void confermaPizzaComanda(Pizza pizza, int quantita){
        this.comandaCorrente.addPizza(pizza, quantita);
    }

    public float calcolaImporto(){

        return this.comandaCorrente.calcolaTotale();
    }


    public void confermaComanda(){

        this.comande.add(comandaCorrente);
    }

    public Ingrediente nuovoIngrediente(String nome, float prezzo){
        Ingrediente ingrediente = this.m.nuovoIngrediente(nome, prezzo);
        return  ingrediente;
    }

    public void addIngrediente(Integer codiceIngrediente, Ingrediente ingrediente){
        m.addIngrediente(codiceIngrediente, ingrediente);

    }

    public Pizza nuovaPizza(String nome){
        pc=new Pizza(nome);
        return pc;
    }

    public HashMap<Integer,Ingrediente> getIngredientiDisponibili(){
        return m.getIngredientiDisponibili();
    }

    public void addToPizza(Integer idIngrediente){
        try{
            Ingrediente ingrediente=m.findIngrediente(idIngrediente);
            pc.addIngrediente(ingrediente);
        }
        catch(Exception ex){
            System.out.println("pizza corrente non selezionata");
        }



    }

    public void confermaPizza(Integer codicePizza){
        m.addPizzaToMenu(codicePizza, pc);
    }

    public int registrazioneCliente (String nome, String cognome, String email, String indirizzo){
        Cliente cliente=new Cliente(nome, cognome, email, indirizzo);
        DAOFactory mysqlFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = mysqlFactory.getClienteDAO();
        int id=clienteDAO.createCliente(cliente);
        listaClienti.add(cliente);
        return id;
    }

    public boolean effettuaPagamento(String metodoPagamento, String[] infoPagamento) {
        return comandaCorrente.effettuaPagamento(metodoPagamento, infoPagamento);
    }

    public float associaSconto() {
            return comandaCorrente.associaSconto();

    }


    public void removeToPizza(Integer idIngrediente){
        try{
            pc.removeIngrediente(idIngrediente);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }



    }



}
