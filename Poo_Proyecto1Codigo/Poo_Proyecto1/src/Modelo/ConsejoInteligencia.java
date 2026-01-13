package Modelo;

import java.util.ArrayList;

public class ConsejoInteligencia {
    private ArrayList<Evento> eventosRegistrados;
    private ArrayList<EdificioInteligente> edificiosRegistrados;
    private ArrayList<EstacionRecarga> estacionesRegistradas;
    private ArrayList<String> accionesEjecutadas;
    private ArrayList<tipoEvento> tiposEventoConfigurados;
    private ArrayList<ArrayList<String>> accionesPorEvento;

    /** Reglas del sistema */
    private int minBateriaDrones = 15;
    private int minBateriaRobots = 15;

    public ConsejoInteligencia(ArrayList<EdificioInteligente> edificiosRegistrados, ArrayList<EstacionRecarga> estacionesRegistradas,
                               ArrayList<Evento> eventosRegistrados) {
        this.edificiosRegistrados =  new ArrayList<>();
        this.estacionesRegistradas =  new ArrayList<>();
        this.eventosRegistrados = new ArrayList<>();

        this.tiposEventoConfigurados = new ArrayList<>();
        this.accionesPorEvento = new ArrayList<>();

        configurarEventosPorDefecto();
    }


    public void agregarAccionEjecutada(String accion) {
        if (accionesEjecutadas == null) {
            accionesEjecutadas = new ArrayList<>();
        }
        accionesEjecutadas.add(accion);
    }


    /**Configura los diferentes eventos que pueden pasar en la ciudad, con sus respectivas acciones a
    * tomar para su respectiva solucion*/
    private void configurarEventosPorDefecto() {
        ArrayList<String> accionesIncendio = new ArrayList<>();
        accionesIncendio.add("Contactar a los bomberos");
        accionesIncendio.add("Llamar al 911");
        agregarConfiguracionEvento(tipoEvento.Incendio, accionesIncendio);

        ArrayList<String> accionesColision = new ArrayList<>();
        accionesColision.add("Contactar a oficiales de tránsito");
        accionesColision.add("Llamar al 911");
        agregarConfiguracionEvento(tipoEvento.Colision_Vehicular, accionesColision);

        ArrayList<String> accionesAccidente = new ArrayList<>();
        accionesAccidente.add("Convocar ambulancias");
        accionesAccidente.add("Llamar al 911");
        agregarConfiguracionEvento(tipoEvento.Accidente_Grave, accionesAccidente);

    }

    /**Agrega o actualiza una configuracion de un evento con una lista de acciones a ejecutar cuando suceda,
    * si el evento ya esta configurado se reemplaza y si no se agrega la configuracion y su accion*/
    public void agregarConfiguracionEvento(tipoEvento tipo, ArrayList<String> acciones) {
        int index = tiposEventoConfigurados.indexOf(tipo);
        if (index >= 0) {
            accionesPorEvento.set(index, acciones);
        } else {
            tiposEventoConfigurados.add(tipo);
            accionesPorEvento.add(acciones);
        }
    }


    /**Si el evento se encuentra configurado obtiene las acciones a tomar para ese evento en especifico,
    * si el evento no se encuentra retorna una lista vacia*/
    public ArrayList<String> obtenerAccionesParaEvento(tipoEvento tipo) {
        for (int i = 0; i < tiposEventoConfigurados.size(); i++) {
            if (tiposEventoConfigurados.get(i) == tipo) {
                return new ArrayList<>(accionesPorEvento.get(i)); /** Retorna copia para evitar modificación externa */
            }
        }
        return new ArrayList<>();
    }


    /**Procesa un evento sucedido en la ciudad y si tiene acciones asuciada a ese tipo de evento las ejecuta,
    * si no muestra un mensaje en consola*/
    public void procesarEvento(Evento evento) {
        if (evento == null) return;

        eventosRegistrados.add(evento);
        ArrayList<String> acciones = obtenerAccionesParaEvento(evento.getTipo());

        if (!acciones.isEmpty()) {
            ejecutarAcciones(evento, acciones);
        } else {
            System.out.println("No hay acciones configuradas para el evento: " + evento.getTipo());
        }
    }

    /**Ejecuta la accion asociada determinada al evento que esta sucediendo en una ubicacion de la ciudad
    * y hace una constancia de la accion utilizada*/
    private void ejecutarAcciones(Evento evento, ArrayList<String> acciones) {
        System.out.println("Procesando evento: " + evento.getTipo());
        System.out.println("Ubicación: Calle " + evento.getCalle() + ", Av. " + evento.getAvenida());

        for (String accion : acciones) {
            System.out.println(" Ejecutando: " + accion);
            evento.agregarAccionEjecutada(accion);
        }

        System.out.println("Evento procesado - " + acciones.size() + " acciones ejecutadas");
    }

    /**Actualiza la bateria minima del dron en rango de 0 a 100*/
    public void actualizarMinBateriaDrones(int nuevoMin) {
        if (nuevoMin >= 0 && nuevoMin <= 100) {
            this.minBateriaDrones = nuevoMin;
            System.out.println("Umbral de batería para drones actualizado a: " + nuevoMin + "%");
        }
    }

    /**Actualiza la bateria minima del robot en un rango de 0 a 100*/
    public void actualizarMinBateriaRobots(int nuevoMin) {
        if (nuevoMin >= 0 && nuevoMin <= 100) {
            this.minBateriaRobots = nuevoMin;
            System.out.println("Umbral de batería para robots actualizado a: " + nuevoMin + "%");
        }
    }

    public int contarEventosRegistrados() {
        return eventosRegistrados.size();
    }

    /**Cuenta la cantidad de eventos de un tipo que haya pasado en la ciudad*/
    public int contarEventosPorTipo(tipoEvento tipo) {
        int count = 0;
        for (Evento evento : eventosRegistrados) {
            if (evento.getTipo() == tipo) {
                count++;
            }
        }
        return count;
    }

    public int getMinBateriaDrones() {

        return minBateriaDrones;
    }

    public int getMinBateriaRobots() {

        return minBateriaRobots;
    }

    public ArrayList<Evento> getEventosRegistrados() {
        return new ArrayList<>(eventosRegistrados); /** Copia para evitar modificación externa */
    }

    public ArrayList<EdificioInteligente> getEdificiosRegistrados() {

        return new ArrayList<>(edificiosRegistrados);
    }

    public ArrayList<EstacionRecarga> getEstacionesRegistradas() {

        return new ArrayList<>(estacionesRegistradas);
    }

    public ArrayList<tipoEvento> getTiposEventoConfigurados() {

        return new ArrayList<>(tiposEventoConfigurados);
    }
}

