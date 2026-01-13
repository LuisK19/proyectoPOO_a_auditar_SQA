package VistaInterfaces;

import Main.Aplicacion;
import Modelo.*;
import Gestores.GestorEdificios;
import Gestores.GestorEstacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


/**Clase principal del panel de administración para el sistema Neo-Urbe*/
public class PanelAdministrador extends JFrame {
    private Aplicacion aplicacion;

    /** Constructor: inicializa el panel administrador con la instancia de la aplicación */
    public PanelAdministrador(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setTitle("Panel Administrador - Neo-Urbe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

/** Inicializa los componentes gráficos y organiza los paneles internos*/
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Agrega los paneles de gestión al tabbedPane
        tabbedPane.addTab("Gestión Edificios", new PanelGestionEdificios(aplicacion));
        tabbedPane.addTab("Gestión Estaciones", new PanelGestionEstaciones(aplicacion));
        tabbedPane.addTab("Gestión Eventos", new PanelGestionEventos(aplicacion));
        tabbedPane.addTab("Configuración Reglas", new PanelConfiguracionReglas(aplicacion));

        // Botón para volver al menú principal
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

/** Panel para la gestión de edificios inteligentes*/
class PanelGestionEdificios extends JPanel {
    private Aplicacion aplicacion;
    private JTextField txtId, txtNombre, txtCalle, txtAvenida, txtCapacidad;
    private JButton btnCrear, btnListar;
    private JTextArea txtListado;

/** Constructor: recibe la instancia de la aplicación*/
    public PanelGestionEdificios(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicializa los componentes del formulario y la lista de edificios*/
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Crear Nuevo Edificio"));

        formPanel.add(new JLabel("ID:"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Calle:"));
        txtCalle = new JTextField();
        formPanel.add(txtCalle);

        formPanel.add(new JLabel("Avenida:"));
        txtAvenida = new JTextField();
        formPanel.add(txtAvenida);

        formPanel.add(new JLabel("Capacidad Máxima:"));
        txtCapacidad = new JTextField();
        formPanel.add(txtCapacidad);

        btnCrear = new JButton("Crear Edificio");
        formPanel.add(btnCrear);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Edificios Existentes"));

        btnListar = new JButton("Listar Edificios");
        txtListado = new JTextArea(15, 50);
        txtListado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtListado);

        listPanel.add(btnListar, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);


        btnCrear.addActionListener(e -> crearEdificio());
        btnListar.addActionListener(e -> listarEdificios());

        add(formPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
    }

/** Crea un nuevo edificio a partir de los datos ingresados*/
    private void crearEdificio() {
        try {
            String id = txtId.getText();
            String nombre = txtNombre.getText();
            int calle = Integer.parseInt(txtCalle.getText());
            int avenida = Integer.parseInt(txtAvenida.getText());
            int capacidad = Integer.parseInt(txtCapacidad.getText());

            // Validación de campos obligatorios
            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID y Nombre son obligatorios");
                return;
            }
            // Verifica que el ID no esté repetido
            for (EdificioInteligente edificio: aplicacion.getGestorEdificios().getEdificios()) {
                if (edificio.getId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "El id no puede repetirse");
                    return;
                }
            }
            // Crea el edificio y actualiza la lista
            EdificioInteligente edificio = aplicacion.getGestorEdificios().crearEdificio(id, nombre, calle, avenida, capacidad);
            JOptionPane.showMessageDialog(this, "Edificio creado exitosamente: " + edificio.getNombre());
            limpiarCampos();
            listarEdificios();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Calle, Avenida y Capacidad deben ser números válidos");
        }
    }

/** Lista todos los edificios existentes en el área de texto */
    private void listarEdificios() {
        StringBuilder sb = new StringBuilder();
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            sb.append("ID: ").append(edificio.getId())
                    .append(" | Nombre: ").append(edificio.getNombre())
                    .append(" | Ubicación: Calle ").append(edificio.getCalle())
                    .append(", Av. ").append(edificio.getAvenida())
                    .append(" | Capacidad: ").append(edificio.getCapacidadActual())
                    .append("/").append(edificio.getCapacidadMaxima())
                    .append(" | Ocupación: ").append(String.format("%.1f", edificio.calcularPorcentajeOcupacion()))
                    .append("%\n");
        }
        txtListado.setText(sb.toString());
    }

/** Limpia los campos del formulario */
    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtCalle.setText("");
        txtAvenida.setText("");
        txtCapacidad.setText("");
    }
}

/** Panel para las gestión de estaciones de recarga */
class PanelGestionEstaciones extends JPanel {
    private Aplicacion aplicacion;
    private JTextField txtAvenida, txtCalle, txtCapacidad;
    private JComboBox<Estado> cmbEstado;
    private JButton btnCrear, btnListar;
    private JTextArea txtListado;

    public PanelGestionEstaciones(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicializa los componente del formulario y la lista de estaciones */
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Crear Estación de Recarga"));

        // Campos de entrada para los datos de la estación
        formPanel.add(new JLabel("Avenida:"));
        txtAvenida = new JTextField();
        formPanel.add(txtAvenida);

        formPanel.add(new JLabel("Calle:"));
        txtCalle = new JTextField();
        formPanel.add(txtCalle);

        formPanel.add(new JLabel("Capacidad:"));
        txtCapacidad = new JTextField();
        formPanel.add(txtCapacidad);

        formPanel.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(Estado.values());
        formPanel.add(cmbEstado);

        btnCrear = new JButton("Crear Estación");
        formPanel.add(btnCrear);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Estaciones de Recarga"));

        btnListar = new JButton("Listar Estaciones");
        txtListado = new JTextArea(15, 50);
        txtListado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtListado);

        listPanel.add(btnListar, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crearEstacion());
        btnListar.addActionListener(e -> listarEstaciones());

        add(formPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
    }

/** Crea una nueva estación de recarga con los datos ingresados */
    private void crearEstacion() {
        try {
            int avenida = Integer.parseInt(txtAvenida.getText());
            int calle = Integer.parseInt(txtCalle.getText());
            int capacidad = Integer.parseInt(txtCapacidad.getText());
            Estado estado = (Estado) cmbEstado.getSelectedItem();

            EstacionRecarga estacion = aplicacion.getGestorEstacion().crearEstacion(avenida, calle, capacidad, estado);
            JOptionPane.showMessageDialog(this, "Estación creada exitosamente (ID: " + estacion.getId() + ")");
            limpiarCampos();
            listarEstaciones();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Avenida, Calle y Capacidad deben ser números válidos");
        }
    }

/** Lista todas las estaciones de recarga en el área de texto */
    private void listarEstaciones() {
        StringBuilder sb = new StringBuilder();
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            sb.append("ID: ").append(estacion.getId())
                    .append(" | Ubicación: Calle ").append(estacion.getCalle())
                    .append(", Av. ").append(estacion.getAvenida())
                    .append(" | Capacidad: ").append(estacion.getCapacidadMaxima())
                    .append(" | Estado: ").append(estacion.getEstado())
                    .append(" | Ocupación: ").append(estacion.getOcupados())
                    .append("/").append(estacion.getCapacidadMaxima())
                    .append("\n");
        }
        txtListado.setText(sb.toString());
    }

    private void limpiarCampos() {
        txtAvenida.setText("");
        txtCalle.setText("");
        txtCapacidad.setText("");
        cmbEstado.setSelectedIndex(0);
    }
}

/** Panel para la gestión de eventos y sus acciones asociadas */
class PanelGestionEventos extends JPanel {
    private Aplicacion aplicacion;
    private JComboBox<tipoEvento> cmbTipoEvento;
    private JButton btnAgregarAccion, btnListarAcciones, btnConfigurar;
    private JTextArea txtAcciones;
    private JTextField txtNuevaAccion;

    public PanelGestionEventos(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout());
        initComponents();
    }

/** Inicializa los componentes del formulario y la lista de acciones por evento */
    private void initComponents() {
        JPanel panelSuperior = new JPanel(new GridLayout(3, 2, 5, 5));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Configurar Acciones por Evento"));

        /** ComboBox para seleccionar el tipo de evento */
        panelSuperior.add(new JLabel("Tipo de Evento:"));
        cmbTipoEvento = new JComboBox<>(tipoEvento.values());
        panelSuperior.add(cmbTipoEvento);

        panelSuperior.add(new JLabel("Nueva Acción:"));
        txtNuevaAccion = new JTextField();
        panelSuperior.add(txtNuevaAccion);

        btnAgregarAccion = new JButton("Agregar Acción");
        panelSuperior.add(btnAgregarAccion);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Acciones Configuradas"));

        btnListarAcciones = new JButton("Listar Acciones para Evento");
        txtAcciones = new JTextArea(15, 50);
        txtAcciones.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAcciones);

        btnConfigurar = new JButton("Configurar Eventos por Defecto");

        panelCentral.add(btnListarAcciones, BorderLayout.NORTH);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(btnConfigurar, BorderLayout.SOUTH);

        btnAgregarAccion.addActionListener(e -> agregarAccion());
        btnListarAcciones.addActionListener(e -> listarAcciones());
        btnConfigurar.addActionListener(e -> configurarEventosPorDefecto());

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
    }

/** Agrega una nueva acción para el evento selecionado */
    private void agregarAccion() {
        tipoEvento tipo = (tipoEvento) cmbTipoEvento.getSelectedItem();
        String accion = txtNuevaAccion.getText();

        if (accion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una acción");
            return;
        }

        ArrayList<String> accionesActuales = aplicacion.getConsejoInteligencia().obtenerAccionesParaEvento(tipo);
        accionesActuales.add(accion);
        aplicacion.getConsejoInteligencia().agregarConfiguracionEvento(tipo, accionesActuales);

        JOptionPane.showMessageDialog(this, "Acción agregada para " + tipo);
        txtNuevaAccion.setText("");
        listarAcciones();
    }

    private void listarAcciones() {
        tipoEvento tipo = (tipoEvento) cmbTipoEvento.getSelectedItem();
        ArrayList<String> acciones = aplicacion.getConsejoInteligencia().obtenerAccionesParaEvento(tipo);

        StringBuilder sb = new StringBuilder();
        sb.append("Acciones para ").append(tipo).append(":\n");
        for (int i = 0; i < acciones.size(); i++) {
            sb.append(i + 1).append(". ").append(acciones.get(i)).append("\n");
        }
        txtAcciones.setText(sb.toString());
    }

    private void configurarEventosPorDefecto() {
        // Esto ya está hecho en el constructor de ConsejoInteligencia, pero podemos reforzarlo
        JOptionPane.showMessageDialog(this, "Eventos por defecto configurados");
        listarAcciones();
    }
}

/** Panel para la configuración de reglas del sistema */
class PanelConfiguracionReglas extends JPanel {
    private Aplicacion aplicacion;
    private JTextField txtMinBateriaDrones, txtMinBateriaRobots;
    private JButton btnActualizarReglas;

    public PanelConfiguracionReglas(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new GridLayout(3, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Configuración de Reglas"));

        // Campos para las reglas de batería mínima
        add(new JLabel("Mínimo Batería Drones (%):"));
        txtMinBateriaDrones = new JTextField(String.valueOf(aplicacion.getConsejoInteligencia().getMinBateriaDrones()));
        add(txtMinBateriaDrones);

        add(new JLabel("Mínimo Batería Robots (%):"));
        txtMinBateriaRobots = new JTextField(String.valueOf(aplicacion.getConsejoInteligencia().getMinBateriaRobots()));
        add(txtMinBateriaRobots);

        btnActualizarReglas = new JButton("Actualizar Reglas");
        add(btnActualizarReglas);

        btnActualizarReglas.addActionListener(e -> actualizarReglas());
    }

/** Actualiza las reglas de batería mínima para drones y robots */
    private void actualizarReglas() {
        try {
            int minDrones = Integer.parseInt(txtMinBateriaDrones.getText());
            int minRobots = Integer.parseInt(txtMinBateriaRobots.getText());

            if (minDrones < 0 || minDrones > 100 || minRobots < 0 || minRobots > 100) {
                JOptionPane.showMessageDialog(this, "Los valores deben estar entre 0 y 100");
                return;
            }

            aplicacion.getConsejoInteligencia().actualizarMinBateriaDrones(minDrones);
            aplicacion.getConsejoInteligencia().actualizarMinBateriaRobots(minRobots);

            JOptionPane.showMessageDialog(this, "Reglas actualizadas exitosamente");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
        }
    }
}
