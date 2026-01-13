package VistaInterfaces;

import Gestores.Simulador;
import Main.Aplicacion;
import Modelo.Ciudadano;
import Modelo.Dron;
import Modelo.EdificioInteligente;
import Modelo.Robot;
import javax.swing.*;
import java.awt.*;

/** Panel principal para la gestión de ciudadanos, robots, drones y simulación*/
public class PanelOperador extends JFrame {
    private Aplicacion aplicacion;

    public PanelOperador(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setTitle("Panel Operador - Neo-Urbe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

/** Inicialización de componentes */
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Gestión Ciudadanos", new PanelGestionCiudadanos(aplicacion));
        tabbedPane.addTab("Gestión Robots", new PanelGestionRobots(aplicacion));
        tabbedPane.addTab("Gestión Drones", new PanelGestionDrones(aplicacion));
        tabbedPane.addTab("Simulación", new PanelSimulacion(aplicacion));

        JButton btnVolver = new JButton("Volver al Menú Principal");
        btnVolver.addActionListener(e -> {
            new PantallaPrincipal(aplicacion).setVisible(true);
            dispose();
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(btnVolver, BorderLayout.SOUTH);

        add(mainPanel);
    }
}

/** Panel para la gestión de ciudadanos*/
class PanelGestionCiudadanos extends JPanel {
    private Aplicacion aplicacion;
    private JTextField txtId, txtNombre;
    private JComboBox<String> cmbEdificios;
    private JButton btnCrear, btnAsignarRobot, btnListar, btnEliminar; // Asegurar que btnAsignarRobot esté declarado
    private JTextArea txtResultados;
    private JButton btnMudar;
/** Constructor */
    public PanelGestionCiudadanos(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout(5, 5));
        initComponents();
    }

/** Inicialización de componentes */
    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Ciudadanos"));

        inputPanel.add(new JLabel("ID Ciudadano:"));
        txtId = new JTextField();
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        inputPanel.add(txtNombre);

        inputPanel.add(new JLabel("Edificio:"));
        cmbEdificios = new JComboBox<>();
        actualizarComboEdificios();
        inputPanel.add(cmbEdificios);

        btnCrear = new JButton("Crear Ciudadano");
        inputPanel.add(btnCrear);

        btnAsignarRobot = new JButton("Asignar Robot");
        inputPanel.add(btnAsignarRobot);

        btnMudar = new JButton("Mudar Ciudadano");
        inputPanel.add(btnMudar);

        // Panel de resultados
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Listado de Ciudadanos"));

        txtResultados = new JTextArea(15, 50);
        txtResultados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResultados);

        btnListar = new JButton("Actualizar Listado");
        btnEliminar = new JButton("Eliminar Ciudadano"); // Inicializar btnEliminar

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnListar);
        buttonPanel.add(btnEliminar);

        resultPanel.add(buttonPanel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Agregar listeners - TODOS LOS BOTONES DEBEN ESTAR INICIALIZADOS ANTES DE ESTO
        btnCrear.addActionListener(e -> crearCiudadano());
        btnAsignarRobot.addActionListener(e -> asignarRobot());
        btnListar.addActionListener(e -> listarCiudadanos());
        btnEliminar.addActionListener(e -> eliminarCiudadano()); // Agregar listener para eliminar
        btnMudar.addActionListener(e -> mudarCiudadano());
        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }

/** Actualiza el combo box de edificios */
    private void actualizarComboEdificios() {
        cmbEdificios.removeAllItems();
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            cmbEdificios.addItem(edificio.getNombre() + " (Cap: " + edificio.getCapacidadActual() + "/" + edificio.getCapacidadMaxima() + ")");
        }
    }

/** Crea un nuevo ciudadano */
    private void crearCiudadano() {
        String id = txtId.getText();
        String nombre = txtNombre.getText();
        int edificioIndex = cmbEdificios.getSelectedIndex();

        if (id.isEmpty() || nombre.isEmpty() || edificioIndex == -1) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        EdificioInteligente edificio = aplicacion.getGestorEdificios().getEdificios().get(edificioIndex);

        if (edificio.estaLleno()) {
            JOptionPane.showMessageDialog(this, "El edificio seleccionado está lleno");
            return;
        }


        if (aplicacion.getGestorCiudadanos().buscarCiudadanoId(id) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un ciudadano con este ID");
            return;
        }

        Ciudadano ciudadano = aplicacion.getGestorCiudadanos().crearCiudadano(id, nombre, edificio);
        if (ciudadano != null) {
            JOptionPane.showMessageDialog(this, "Ciudadano creado exitosamente");
            limpiarCampos();
            actualizarComboEdificios();
            listarCiudadanos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear ciudadano");
        }
    }

    /**Metodo para mudar a un ciudadano*/
    private void mudarCiudadano() {
        String idCiudadano = JOptionPane.showInputDialog(this, "ID del ciudadano a mudar:");
        if (idCiudadano == null || idCiudadano.isEmpty()) {
            return;
        }

        // Buscar el ciudadano
        Ciudadano ciudadano = aplicacion.getGestorCiudadanos().buscarCiudadanoId(idCiudadano);
        if (ciudadano == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ciudadano con ID: " + idCiudadano);
            return;
        }

        // Crear diálogo para seleccionar nuevo edificio
        JDialog dialog = new JDialog();
        dialog.setTitle("Seleccionar Nuevo Edificio");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JComboBox<String> cmbNuevosEdificios = new JComboBox<>();
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            // Excluir el edificio actual del ciudadano
            if (!edificio.equals(ciudadano.getEdificioAsignado()) && !edificio.estaLleno()) {
                cmbNuevosEdificios.addItem(edificio.getNombre() + " (Cap: " + edificio.getCapacidadActual() + "/" + edificio.getCapacidadMaxima() + ")");
            }
        }

        if (cmbNuevosEdificios.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay edificios disponibles con capacidad.");
            return;
        }

        JButton btnConfirmar = new JButton("Confirmar Mudanza");
        btnConfirmar.addActionListener(e -> {
            int selectedIndex = cmbNuevosEdificios.getSelectedIndex();
            if (selectedIndex >= 0) {
                // Encontrar el edificio seleccionado
                EdificioInteligente nuevoEdificio = null;
                int currentIndex = 0;
                for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
                    if (!edificio.equals(ciudadano.getEdificioAsignado()) && !edificio.estaLleno()) {
                        if (currentIndex == selectedIndex) {
                            nuevoEdificio = edificio;
                            break;
                        }
                        currentIndex++;
                    }
                }

                if (nuevoEdificio != null) {
                    // Realizar la mudanza
                    boolean exito = ciudadano.getEdificioAsignado().mudarse(nuevoEdificio, ciudadano);
                    if (exito) {
                        JOptionPane.showMessageDialog(this,
                                "Ciudadano " + ciudadano.getNombre() + " mudado exitosamente de " +
                                        ciudadano.getEdificioAsignado().getNombre() + " a " + nuevoEdificio.getNombre());
                        actualizarComboEdificios();
                        listarCiudadanos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al mudar el ciudadano.");
                    }
                }
            }
            dialog.dispose();
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Seleccione el nuevo edificio:"), BorderLayout.NORTH);
        panel.add(cmbNuevosEdificios, BorderLayout.CENTER);
        panel.add(btnConfirmar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

/** Asigna un robot a un ciudadano */
    private void asignarRobot() {
        String idCiudadano = JOptionPane.showInputDialog(this, "ID del ciudadano:");
        if (idCiudadano != null && !idCiudadano.isEmpty()) {
            // Verificar que el ciudadano existe antes de asignar
            if (aplicacion.getGestorCiudadanos().buscarCiudadanoId(idCiudadano) != null) {
                aplicacion.getGestorRobots().asignarRobot(aplicacion.getGestorCiudadanos(), idCiudadano);
                listarCiudadanos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el ciudadano con ID: " + idCiudadano);
            }
        }
    }

/**Elimina un ciudadano */
    private void eliminarCiudadano() {
        String id = JOptionPane.showInputDialog(this, "ID del ciudadano a eliminar:");
        if (id != null && !id.isEmpty()) {
            // Verificar que el ciudadano existe
            if (aplicacion.getGestorCiudadanos().buscarCiudadanoId(id) != null) {
                boolean exito = aplicacion.getGestorCiudadanos().eliminarCiudadano(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Ciudadano eliminado exitosamente");
                    actualizarComboEdificios();
                    listarCiudadanos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el ciudadano.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el ciudadano con ID: " + id);
            }
        }
    }

/** Lista todos los ciudadanos */
    private void listarCiudadanos() {
        StringBuilder sb = new StringBuilder();
        for (Ciudadano ciudadano : aplicacion.getGestorCiudadanos().listarCiudadanos()) {
            sb.append("ID: ").append(ciudadano.getIdCiudadano())
                    .append(" | Nombre: ").append(ciudadano.getNombre())
                    .append(" | Edificio: ").append(ciudadano.getEdificioAsignado().getNombre())
                    .append(" | Robots: ").append(ciudadano.cantidadRobotsAsignados())
                    .append("\n");
        }
        txtResultados.setText(sb.toString());
    }

/**  Limpia los campos de entrada */

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
    }
}

/** Panel para la gestión de robots */
class PanelGestionRobots extends JPanel {
    private Aplicacion aplicacion;
    private JTextField txtBateria;
    private JCheckBox chkActivo;
    private JButton btnCrear, btnListar, btnCrearLote, btnEliminar;
    private JTextArea txtListado;

/** Constructor */
    public PanelGestionRobots(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicialización de componentes */
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // Cambiado a 5 filas
        formPanel.setBorder(BorderFactory.createTitledBorder("Crear Robot"));

        formPanel.add(new JLabel("Batería Inicial (%):"));
        txtBateria = new JTextField("100");
        formPanel.add(txtBateria);

        formPanel.add(new JLabel("Activo:"));
        chkActivo = new JCheckBox("", true);
        formPanel.add(chkActivo);

        btnCrear = new JButton("Crear Robot Individual");
        formPanel.add(btnCrear);

        btnCrearLote = new JButton("Crear Lote (5 robots)");
        formPanel.add(btnCrearLote);

        btnEliminar = new JButton("Eliminar Robot");
        formPanel.add(btnEliminar);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Robots Existentes"));

        btnListar = new JButton("Listar Robots");
        txtListado = new JTextArea(15, 50);
        txtListado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtListado);

        listPanel.add(btnListar, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crearRobot());
        btnCrearLote.addActionListener(e -> crearLoteRobots());
        btnListar.addActionListener(e -> listarRobots());
        btnEliminar.addActionListener(e -> eliminarRobot()); // Agregar listener para eliminar

        add(formPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
    }

/** Crea un robot */
    private void crearRobot() {
        try {
            int bateria = Integer.parseInt(txtBateria.getText());
            boolean activo = chkActivo.isSelected();

            if (bateria < 0 || bateria > 100) {
                JOptionPane.showMessageDialog(this, "La batería debe estar entre 0 y 100");
                return;
            }

            // Ahora creamos el robot sin especificar procesador
            Robot robot = aplicacion.getGestorRobots().crearRobot(activo, bateria, null);
            JOptionPane.showMessageDialog(this, "Robot creado exitosamente: " + robot.getProcesador());
            listarRobots();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La batería debe ser un número válido");
        }
    }

/** Crea un lote de robots */
    private void crearLoteRobots() {
        int cantidad = 5;
        for (int i = 0; i < cantidad; i++) {
            int bateria = 20 + (int) (Math.random() * 80); // Entre 20 y 100
            aplicacion.getGestorRobots().crearRobot(true, bateria, null);
        }
        JOptionPane.showMessageDialog(this, "Lote de " + cantidad + " robots creado exitosamente");
        listarRobots();
    }

/**Elimina un robot */
    private void eliminarRobot() {
        String procesador = JOptionPane.showInputDialog(this, "Procesador del robot a eliminar:");
        if (procesador != null && !procesador.isEmpty()) {
            // Verificar que el robot existe
            Robot robot = aplicacion.getGestorRobots().buscarRobot(procesador);
            if (robot != null) {
                boolean exito = aplicacion.getGestorRobots().eliminarRobot(procesador);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Robot eliminado exitosamente");
                    listarRobots();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el robot. Verifique que no esté asignado a un ciudadano.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el robot con procesador: " + procesador);
            }
        }
    }

/** Lista de todos los robots */
    private void listarRobots() {
        StringBuilder sb = new StringBuilder();
        int total = 0;
        int asignados = 0;

        for (Robot robot : aplicacion.getGestorRobots().getRobots()) {
            sb.append("Procesador: ").append(robot.getProcesador())
                    .append(" | Batería: ").append(robot.getBateria()).append("%")
                    .append(" | Activo: ").append(robot.isActivo() ? "Sí" : "No")
                    .append(" | Asignado: ").append(robot.isAsignado() ? "Sí" : "No")
                    .append(" | Tareas: ").append(robot.getTareas().size())
                    .append("\n");

            total++;
            if (robot.isAsignado()) asignados++;
        }

        sb.append("\n--- RESUMEN ---\n");
        sb.append("Total robots: ").append(total).append("\n");
        sb.append("Asignados: ").append(asignados).append("\n");
        sb.append("Disponibles: ").append(total - asignados).append("\n");

        txtListado.setText(sb.toString());
    }
}

/** Panel de gestión de drones */
class PanelGestionDrones extends JPanel {
    private Aplicacion aplicacion;
    private JButton btnGenerarDrones, btnListarDrones;
    private JTextArea txtListado;

    public PanelGestionDrones(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicialización de componentes */
    private void initComponents() {
        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(BorderFactory.createTitledBorder("Gestión de Drones"));

        btnGenerarDrones = new JButton("Generar Drones Automáticamente");
        btnListarDrones = new JButton("Listar Drones");

        panelBotones.add(btnGenerarDrones);
        panelBotones.add(btnListarDrones);

        txtListado = new JTextArea(15, 50);
        txtListado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtListado);

        btnGenerarDrones.addActionListener(e -> generarDrones());
        btnListarDrones.addActionListener(e -> listarDrones());

        add(panelBotones, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

/** Genera drones automáticamente */
    private void generarDrones() {
        aplicacion.getGestorDrones().generarDrones(aplicacion.getGestorEdificios().getEdificios());
        JOptionPane.showMessageDialog(this, "Drones generados exitosamente");
        listarDrones();
    }

/** Lista de todos los drones */
    private void listarDrones() {
        StringBuilder sb = new StringBuilder();
        for (Dron dron : aplicacion.getGestorDrones().getDrones()) {
            sb.append("Procesador: ").append(dron.getProcesador())
                    .append(" | Batería: ").append(dron.getBateria()).append("%")
                    .append(" | Horas Vuelo: ").append(dron.getHorasVuelo())
                    .append(" | Estado: ").append(dron.getEstado())
                    .append(" | Edificio: ").append(dron.getEdificioAsignado() != null ? dron.getEdificioAsignado().getNombre() : "No asignado")
                    .append(" | En Alerta: ").append(dron.necesitaRecarga() ? "Sí" : "No")
                    .append("\n");
        }
        txtListado.setText(sb.toString());
    }
}

/** Panel para ejecutar la simulación completa */
class PanelSimulacion extends JPanel {
    private Aplicacion aplicacion;
    private JButton btnEjecutarSimulacion;
    private JTextArea txtLogSimulacion;

    public PanelSimulacion(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicialización de componentes */
    private void initComponents() {
        btnEjecutarSimulacion = new JButton("Ejecutar Simulación Completa");
        txtLogSimulacion = new JTextArea(20, 60);
        txtLogSimulacion.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLogSimulacion);

        btnEjecutarSimulacion.addActionListener(e -> ejecutarSimulacion());

        add(btnEjecutarSimulacion, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

/** Ejecuta la simulación completa */
    private void ejecutarSimulacion() {
        // Crear simulador y ejecutar
        Simulador simulador = new Simulador(
                aplicacion.getGestorCiudadanos(),
                aplicacion.getGestorDrones(),
                aplicacion.getGestorEstacion(),
                aplicacion.getGestorRobots(),
                aplicacion.getGestorEdificios(),
                aplicacion.getConsejoInteligencia()
        );

        txtLogSimulacion.setText("");
        simulador.ejecutarSimulacion();

        /**capturar la salida del simulador */
        txtLogSimulacion.setText("Simulación ejecutada exitosamente.\n\n");
        txtLogSimulacion.append("Revise la consola para ver los detalles completos de la simulación.\n\n");
        txtLogSimulacion.append("Resumen:\n");
        txtLogSimulacion.append("- Ciudadanos: " + aplicacion.getGestorCiudadanos().listarCiudadanos().size() + "\n");
        txtLogSimulacion.append("- Robots: " + aplicacion.getGestorRobots().getRobots().size() + "\n");
        txtLogSimulacion.append("- Drones: " + aplicacion.getGestorDrones().getDrones().size() + "\n");
        txtLogSimulacion.append("- Edificios: " + aplicacion.getGestorEdificios().getEdificios().size() + "\n");
        txtLogSimulacion.append("- Estaciones: " + aplicacion.getGestorEstacion().getEstaciones().size() + "\n");
    }
}