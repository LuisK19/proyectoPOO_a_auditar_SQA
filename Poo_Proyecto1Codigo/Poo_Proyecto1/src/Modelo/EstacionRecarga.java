package Modelo;

import java.util.ArrayList;

public class EstacionRecarga {
    private static int ids_a_Asignar = 0;
    private int id;
    private int calle;
    private int avenida;
    private int capacidadMaxima;
    private int ocupados;
    private Estado estado;
    private ArrayList<Dispositivos> listaAtendidos;
    private ArrayList<Object> dispositivosEnRecarga;
    private ArrayList<RegistroRecarga> registrosRecarga;

    public EstacionRecarga(){}

    public EstacionRecarga(int avenida, int calle, int capacidadMaxima, Estado estado) {
        this.calle = calle;
        this.avenida = avenida;
        this.id = ids_a_Asignar++;
        this.capacidadMaxima = capacidadMaxima;
        this.estado = estado;
        this.ocupados = 0;
        this.listaAtendidos = new ArrayList<>();
        this.dispositivosEnRecarga = new ArrayList<>();
        this.registrosRecarga = new ArrayList<>();
    }

    /** Verifica si la estacion esta Disponible y si la cuenta con espacio, si esto pasa puede atender
    * a los drones y robots*/
    public boolean puedeAtender() {
        return ocupados < capacidadMaxima && estado == Estado.DISPONIBLE;
    }

    /** Verifica si la estacion puede atender el dispositivo en este caso un dron o un robot y si este no
    * esta siendo cargado, si esto se cumple la estacion recarga su bateria y realiza un registro de recarga
    * del dispositivo ya sea dron o robot con sus respectivos detalles*/
    public boolean atenderDispositivo(Object dispositivo) {
        if (puedeAtender() && dispositivo != null) {
            if (!dispositivosEnRecarga.contains(dispositivo)) {
                int bateriaAntes = -1;
                String idDispositivo = "UNKNOWN";
                Dispositivos tipo = null;

                if (dispositivo instanceof Robot) {
                    Robot r = (Robot) dispositivo;
                    bateriaAntes = r.getBateria();
                    r.recargarBateriaCompleta();
                    listaAtendidos.add(Dispositivos.ROBOT);
                    tipo = Dispositivos.ROBOT;
                    idDispositivo = r.getProcesador();
                } else if (dispositivo instanceof Dron) {
                    Dron d = (Dron) dispositivo;
                    bateriaAntes = d.getBateria();
                    d.recargarBateria();
                    listaAtendidos.add(Dispositivos.DRON);
                    tipo = Dispositivos.DRON;
                    idDispositivo = d.getProcesador();
                } else {
                    /** no es robot ni dron */
                    return false;
                }

                dispositivosEnRecarga.add(dispositivo);
                ocupados++;

                int bateriaDespues = 100;
                registrosRecarga.add(new RegistroRecarga(tipo, idDispositivo, bateriaAntes, bateriaDespues));
                return true;
            }
        }
        return false;
    }

    /** Libera el dispositivo ya que este se encuentra recargado y disminuye la cantidad de ocupados de esa estacion*/
    public void liberarDispositivo(Object dispositivo) {
        if (dispositivosEnRecarga.remove(dispositivo)) {
            ocupados--;
        }
    }

    public ArrayList<RegistroRecarga> getRegistrosRecarga(){ return new ArrayList<>(registrosRecarga); }


    public int obtenerEspaciosDisponibles() {
        return capacidadMaxima - ocupados;
    }

    public double getPorcentajeOcupacion() {
        return (double) ocupados / capacidadMaxima * 100;
    }

    public boolean estaOperativa() {
        return estado == Estado.DISPONIBLE;
    }

    public void atenderRobot(Robot robot){
        atenderDispositivo(robot);
    }
    public void atenderDron(Dron dron){
        atenderDispositivo(dron);
    }

    /** getters y setters */
    public int getCapacidadMaxima(){ return capacidadMaxima;}
    public int getAvenida() { return avenida; }
    public void setAvenida(int avenida) { this.avenida = avenida; }
    public int getCalle() { return calle; }
    public void setCalle(int calle) { this.calle = calle; }
    public int getId(){ return id;}
    public void setId(int id){this.id = id;}
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public int getOcupados() { return ocupados; }
    public void setOcupados(int ocupados) { this.ocupados = ocupados; }
    public ArrayList<Dispositivos> getListaAtendidos() { return listaAtendidos; }
    public void setListaAtendidos(ArrayList<Dispositivos> listaAtendidos) { this.listaAtendidos = listaAtendidos; }
}

