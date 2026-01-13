package VistaInterfaces;

import Main.Aplicacion;
import Modelo.*;
import Modelo.Robot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Utilidades para gráficos de texto coloreado */
class GraficoUtil {

    public static class DataPoint {
        private final String label;
        private final double value;

        public DataPoint(String label, double value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public double getValue() { return value; }
    }

/** Gráfico de barras en JTextPane con colores egún valor */
    public static void actualizarGraficoTextoGenerico(JTextPane graficoPane, List<DataPoint> datos, int maxBoxes, String title) {
        if (graficoPane == null) return;
        StyledDocument doc = graficoPane.getStyledDocument();
        try {
            if (doc.getLength() > 0) doc.remove(0, doc.getLength());


            // estilos
            Style labelStyle = doc.addStyle("label", null);
            StyleConstants.setFontFamily(labelStyle, Font.MONOSPACED);
            StyleConstants.setBold(labelStyle, true);
            StyleConstants.setFontSize(labelStyle, 12);
            StyleConstants.setForeground(labelStyle, Color.DARK_GRAY);

            Style percStyle = doc.addStyle("perc", null);
            StyleConstants.setFontFamily(percStyle, Font.MONOSPACED);
            StyleConstants.setFontSize(percStyle, 11);
            StyleConstants.setForeground(percStyle, Color.DARK_GRAY);

            Style sGreen = doc.addStyle("green", null);
            StyleConstants.setFontFamily(sGreen, Font.MONOSPACED);
            StyleConstants.setFontSize(sGreen, 12);
            StyleConstants.setForeground(sGreen, new Color(0, 153, 0));

            Style sOrange = doc.addStyle("orange", null);
            StyleConstants.setFontFamily(sOrange, Font.MONOSPACED);
            StyleConstants.setFontSize(sOrange, 12);
            StyleConstants.setForeground(sOrange, new Color(255, 140, 0));

            Style sRed = doc.addStyle("red", null);
            StyleConstants.setFontFamily(sRed, Font.MONOSPACED);
            StyleConstants.setFontSize(sRed, 12);
            StyleConstants.setForeground(sRed, Color.RED);

            Style sEmpty = doc.addStyle("empty", null);
            StyleConstants.setFontFamily(sEmpty, Font.MONOSPACED);
            StyleConstants.setFontSize(sEmpty, 12);
            StyleConstants.setForeground(sEmpty, Color.LIGHT_GRAY);

            // título
            if (title != null && !title.isEmpty()) {
                Style tit = doc.addStyle("title", null);
                StyleConstants.setFontFamily(tit, Font.SANS_SERIF);
                StyleConstants.setFontSize(tit, 13);
                StyleConstants.setBold(tit, true);
                doc.insertString(doc.getLength(), title + "\n\n", tit);
            }

            //doc.insertString(doc.getLength(), "Leyenda: ", labelStyle);
            doc.insertString(doc.getLength(), "■■■■■■■", sGreen);
            doc.insertString(doc.getLength(), " ≥7  ", percStyle);
            doc.insertString(doc.getLength(), "■■■■", sOrange);
            doc.insertString(doc.getLength(), " 5-6  ", percStyle);
            doc.insertString(doc.getLength(), "■■■", sRed);
            doc.insertString(doc.getLength(), " ≤4\n\n", percStyle);

            int labelWidth = 20; // ancho para alinear etiquetas

            if (datos == null || datos.isEmpty()) {
                doc.insertString(doc.getLength(), "Sin datos", percStyle);
                return;
            }

            for (DataPoint dp : datos) {
                String nombre = dp.getLabel();
                double pct = dp.getValue();

                int boxes = (int) Math.round(pct * maxBoxes / 100.0);
                if (boxes > maxBoxes) boxes = maxBoxes;
                if (boxes < 0) boxes = 0;

                Style boxStyle;
                if (boxes >= 7) boxStyle = sGreen;
                else if (boxes > 4) boxStyle = sOrange;
                else boxStyle = sRed;

                String label = nombre.length() > labelWidth ? nombre.substring(0, labelWidth - 3) + "..." : nombre;
                label = String.format("%-" + labelWidth + "s", label);
                doc.insertString(doc.getLength(), label + " | ", labelStyle);

                for (int i = 0; i < boxes; i++) doc.insertString(doc.getLength(), "■", boxStyle);
                for (int i = boxes; i < maxBoxes; i++) doc.insertString(doc.getLength(), "□", sEmpty);

                String info = String.format(" | %5.1f%% (%d/%d)\n", pct, boxes, maxBoxes);
                doc.insertString(doc.getLength(), info, percStyle);
            }


        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
/** Panel General principal con pestañas para cada área */
public class PanelGeneral extends JFrame {
    private Aplicacion aplicacion;

/** Constructor */
    public PanelGeneral(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setTitle("Dashboard General - Neo-Urbe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        initComponents();
    }

/**  Inicialización de componentes */
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Energía", new PanelEnergia(aplicacion));
        tabbedPane.addTab("Infraestructura", new PanelInfraestructura(aplicacion));
        tabbedPane.addTab("Seguridad", new PanelSeguridad(aplicacion));
        tabbedPane.addTab("Bienestar Ciudadano", new PanelBienestar(aplicacion));

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

/** Panel de Energía */
class PanelEnergia extends JPanel {
    private Aplicacion aplicacion;
    private JProgressBar progressRobotsAlerta, progressDronesAlerta;
    private JLabel lblRobotsAlerta, lblDronesAlerta, lblEdificiosAlerta, lblCiudadanosAfectados;
    private JTable tablaEdificios;
    private JTextArea alertasArea;


    private JTextPane graficoPane;
    private int maxBoxesGrafico = 10;

/** Constructor */
    public PanelEnergia(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        actualizarDatos();
    }

/** Inicializar componentes*/
    private void initComponents() {
        // Panel de KPIs (Norte)
        JPanel panelKPIs = crearPanelKPIs();

        // Panel central con desgloses y gráficos
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));

        // Tabla de edificios (Desglose útil)
        panelCentral.add(crearPanelTablaEdificios());

        // Gráfico de barras (texto coloreado dinámico)
        panelCentral.add(crearPanelGrafico());

        // Panel de alertas (Sur)
        JPanel panelAlertas = crearPanelAlertas();

        add(panelKPIs, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelAlertas, BorderLayout.SOUTH);
    }

/** Panel de KPIs */
    private JPanel crearPanelKPIs() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("KPIs de Energía"));

        // KPI 1: Robots en alerta
        JPanel kpi1 = new JPanel(new BorderLayout());
        kpi1.setBorder(BorderFactory.createTitledBorder("Robots en Alerta"));
        lblRobotsAlerta = new JLabel("0/0 (0%)", JLabel.CENTER);
        lblRobotsAlerta.setFont(new Font("Arial", Font.BOLD, 16));
        progressRobotsAlerta = new JProgressBar(0, 100);
        progressRobotsAlerta.setStringPainted(true);
        kpi1.add(lblRobotsAlerta, BorderLayout.CENTER);
        kpi1.add(progressRobotsAlerta, BorderLayout.SOUTH);

        // KPI 2: Drones en alerta
        JPanel kpi2 = new JPanel(new BorderLayout());
        kpi2.setBorder(BorderFactory.createTitledBorder("Drones en Alerta"));
        lblDronesAlerta = new JLabel("0/0 (0%)", JLabel.CENTER);
        lblDronesAlerta.setFont(new Font("Arial", Font.BOLD, 16));
        progressDronesAlerta = new JProgressBar(0, 100);
        progressDronesAlerta.setStringPainted(true);
        kpi2.add(lblDronesAlerta, BorderLayout.CENTER);
        kpi2.add(progressDronesAlerta, BorderLayout.SOUTH);

        // KPI 3: Edificios con alerta
        JPanel kpi3 = new JPanel(new BorderLayout());
        kpi3.setBorder(BorderFactory.createTitledBorder("Edificios con Alerta"));
        lblEdificiosAlerta = new JLabel("0/0 (0%)", JLabel.CENTER);
        lblEdificiosAlerta.setFont(new Font("Arial", Font.BOLD, 16));
        kpi3.add(lblEdificiosAlerta, BorderLayout.CENTER);

        // KPI 4: Ciudadanos afectados
        JPanel kpi4 = new JPanel(new BorderLayout());
        kpi4.setBorder(BorderFactory.createTitledBorder("Ciudadanos Afectados"));
        lblCiudadanosAfectados = new JLabel("0", JLabel.CENTER);
        lblCiudadanosAfectados.setFont(new Font("Arial", Font.BOLD, 16));
        kpi4.add(lblCiudadanosAfectados, BorderLayout.CENTER);

        panel.add(kpi1);
        panel.add(kpi2);
        panel.add(kpi3);
        panel.add(kpi4);

        return panel;
    }

/**Crea el panel de Edicios */
    private JPanel crearPanelTablaEdificios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Desglose por Edificio"));

        String[] columnNames = {"Edificio", "Robots Total", "En Alerta", "% Alerta", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        tablaEdificios = new JTable(model);
        tablaEdificios.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tablaEdificios);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/**Crea el panel grafico */
    private JPanel crearPanelGrafico() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gráfico - Robots por Edificio"));

        graficoPane = new JTextPane();
        graficoPane.setEditable(false);
        graficoPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        graficoPane.setBackground(new Color(0xFCFCFC));
        graficoPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JScrollPane scrollPane = new JScrollPane(graficoPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        // llenado inicial
        List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoEnergia();
        GraficoUtil.actualizarGraficoTextoGenerico(graficoPane, datos, maxBoxesGrafico, "Robots en Alerta por Edificio");

        return panel;
    }

/**Crea el panel de alertas */
    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de Energía"));

        alertasArea = new JTextArea(5, 50);
        alertasArea.setEditable(false);
        alertasArea.setForeground(Color.RED);
        alertasArea.setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(alertasArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/**Actutliza los datos de los Kpis */
    private void actualizarDatos() {
        // Calcular KPIs
        int totalRobots = aplicacion.getGestorRobots().getRobots().size();
        int robotsAlerta = calcularRobotsEnAlerta();
        int totalDrones = aplicacion.getGestorDrones().getDrones().size();
        int dronesAlerta = calcularDronesEnAlerta();
        int edificiosConAlerta = calcularEdificiosConAlerta();
        int totalEdificios = aplicacion.getGestorEdificios().getEdificios().size();
        int ciudadanosAfectados = calcularCiudadanosAfectados();

        // Actualizar KPIs
        double porcentajeRobots = totalRobots > 0 ? (robotsAlerta * 100.0 / totalRobots) : 0;
        double porcentajeDrones = totalDrones > 0 ? (dronesAlerta * 100.0 / totalDrones) : 0;
        double porcentajeEdificios = totalEdificios > 0 ? (edificiosConAlerta * 100.0 / totalEdificios) : 0;

        lblRobotsAlerta.setText(robotsAlerta + "/" + totalRobots + " (" + String.format("%.1f", porcentajeRobots) + "%)");
        lblDronesAlerta.setText(dronesAlerta + "/" + totalDrones + " (" + String.format("%.1f", porcentajeDrones) + "%)");
        lblEdificiosAlerta.setText(edificiosConAlerta + "/" + totalEdificios + " (" + String.format("%.1f", porcentajeEdificios) + "%)");
        lblCiudadanosAfectados.setText(String.valueOf(ciudadanosAfectados));

        progressRobotsAlerta.setValue((int) porcentajeRobots);
        progressDronesAlerta.setValue((int) porcentajeDrones);

        // Colorear según el porcentaje
        progressRobotsAlerta.setForeground(porcentajeRobots > 30 ? Color.RED : porcentajeRobots > 10 ? Color.ORANGE : Color.GREEN);
        progressDronesAlerta.setForeground(porcentajeDrones > 30 ? Color.RED : porcentajeDrones > 10 ? Color.ORANGE : Color.GREEN);

        // Actualizar tabla de edificios
        actualizarTablaEdificios();

        // Actualizar alertas
        actualizarAlertas();

        // refrescar grafico (se pinta inmediatamente con datos actuales)
        SwingUtilities.invokeLater(() -> {
            List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoEnergia();
            GraficoUtil.actualizarGraficoTextoGenerico(graficoPane, datos, maxBoxesGrafico, "Robots en Alerta por Edificio");
        });
    }

    // Calculo de KPIs
/** Robots en alerta */
    private int calcularRobotsEnAlerta() {
        int count = 0;
        for (Robot robot : aplicacion.getGestorRobots().getRobots()) {
            if (robot.necesitaRecarga()) {
                count++;
            }
        }
        return count;
    }

/** Drones en alerta */
    private int calcularDronesEnAlerta() {
        int count = 0;
        for (Dron dron : aplicacion.getGestorDrones().getDrones()) {
            if (dron.necesitaRecarga()) {
                count++;
            }
        }
        return count;
    }

/** Edificios en alerta */
    private int calcularEdificiosConAlerta() {
        int count = 0;
        int umbral = aplicacion.getConsejoInteligencia().getMinBateriaRobots();
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            if (edificio.contarRobotsEnAlerta(umbral) > 0) {
                count++;
            }
        }
        return count;
    }

/** Ciudadanos afectados */
    private int calcularCiudadanosAfectados() {
        int count = 0;
        int umbral = aplicacion.getConsejoInteligencia().getMinBateriaRobots();
        for (Ciudadano ciudadano : aplicacion.getGestorCiudadanos().listarCiudadanos()) {
            if (ciudadano.tieneRobotsEnAlerta(umbral)) {
                count++;
            }
        }
        return count;
    }

/** Actualización de tabla y alertas */
    private void actualizarTablaEdificios() {
        DefaultTableModel model = (DefaultTableModel) tablaEdificios.getModel();
        model.setRowCount(0);

        int umbral = aplicacion.getConsejoInteligencia().getMinBateriaRobots();

        // Rellenar filas
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            int robotsTotales = 0;
            int robotsAlerta = 0;

            for (Ciudadano ciudadano : edificio.getListaResidentes()) {
                robotsTotales += ciudadano.cantidadRobotsAsignados();
                robotsAlerta += ciudadano.cantidadRobotsEnAlerta(umbral);
            }

            double porcentajeAlerta = robotsTotales > 0 ? (robotsAlerta * 100.0 / robotsTotales) : 0;
            String estado = porcentajeAlerta > 30 ? "CRÍTICO" : porcentajeAlerta > 10 ? "ADVERTENCIA" : "NORMAL";

            model.addRow(new Object[]{
                    edificio.getNombre(),
                    robotsTotales,
                    robotsAlerta,
                    String.format("%.1f%%", porcentajeAlerta),
                    estado
            });
        }
    }

/** Actualización de alertas */
    private void actualizarAlertas() {
        StringBuilder alertas = new StringBuilder();
        int umbralAlerta = 30; // Umbral configurable

        // Verificar edificios con más del umbral de robots en alerta
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            int robotsAlerta = edificio.contarRobotsEnAlerta(aplicacion.getConsejoInteligencia().getMinBateriaRobots());
            int robotsTotales = 0;
            for (Ciudadano ciudadano : edificio.getListaResidentes()) {
                robotsTotales += ciudadano.cantidadRobotsAsignados();
            }

            // Alerta si el porcentaje supera el umbral
            if (robotsTotales > 0) {
                double porcentaje = (robotsAlerta * 100.0 / robotsTotales);
                if (porcentaje > umbralAlerta) {
                    alertas.append("ALERTA: ").append(edificio.getNombre())
                            .append(" tiene ").append(String.format("%.1f", porcentaje))
                            .append("% de robots en alerta (").append(robotsAlerta)
                            .append("/").append(robotsTotales).append(")\n");
                }
            }
        }

        if (alertas.length() == 0) {
            alertas.append("No hay alertas críticas de energía");
            alertasArea.setForeground(Color.BLACK);
        } else {
            alertasArea.setForeground(Color.RED);
        }

        alertasArea.setText(alertas.toString());
    }

/** Obtener datos para gráfico */
    private List<GraficoUtil.DataPoint> obtenerDatosGraficoEnergia() {
        List<GraficoUtil.DataPoint> datos = new ArrayList<>();
        int umbral = aplicacion.getConsejoInteligencia().getMinBateriaRobots();
        for (EdificioInteligente e : aplicacion.getGestorEdificios().getEdificios()) {
            int tot = 0, alerta = 0;
            for (Ciudadano c : e.getListaResidentes()) {
                tot += c.cantidadRobotsAsignados();
                alerta += c.cantidadRobotsEnAlerta(umbral);
            }
            double pct = tot > 0 ? (alerta * 100.0 / tot) : 0;
            datos.add(new GraficoUtil.DataPoint(e.getNombre(), pct));
        }
        if (datos.isEmpty()) datos.add(new GraficoUtil.DataPoint("Sin edificios", 0));
        return datos;
    }


}

/** Panel de Infraestructura */
class PanelInfraestructura extends JPanel {
    private Aplicacion aplicacion;
    private JLabel lblEstacionesDisponibles, lblOcupacionInstantanea, lblDemandaRecarga;
    private JTable tablaEstaciones;
    private JTextArea alertasArea;


    // gráfico
    private JTextPane graficoPaneInfra;
    private int maxBoxesGraficoInfra = 10;

/** Constructor */
    public PanelInfraestructura(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        actualizarDatos();
    }

/**Inicializa componentes */
    private void initComponents() {
        // Panel de KPIs
        JPanel panelKPIs = crearPanelKPIs();

        // Panel central
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentral.add(crearPanelTablaEstaciones());
        panelCentral.add(crearPanelGraficoEstaciones());

        // Panel de alertas
        JPanel panelAlertas = crearPanelAlertas();

        add(panelKPIs, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelAlertas, BorderLayout.SOUTH);
    }

/** Panel de KPIs */
    private JPanel crearPanelKPIs() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("KPIs de Infraestructura"));

        // KPI 1: Estaciones disponibles
        JPanel kpi1 = new JPanel(new BorderLayout());
        kpi1.setBorder(BorderFactory.createTitledBorder("Estaciones Disponibles"));
        lblEstacionesDisponibles = new JLabel("0/0 (0%)", JLabel.CENTER);
        lblEstacionesDisponibles.setFont(new Font("Arial", Font.BOLD, 16));
        kpi1.add(lblEstacionesDisponibles, BorderLayout.CENTER);

        // KPI 2: Ocupación instantánea
        JPanel kpi2 = new JPanel(new BorderLayout());
        kpi2.setBorder(BorderFactory.createTitledBorder("Ocupación Instantánea"));
        lblOcupacionInstantanea = new JLabel("0%", JLabel.CENTER);
        lblOcupacionInstantanea.setFont(new Font("Arial", Font.BOLD, 16));
        kpi2.add(lblOcupacionInstantanea, BorderLayout.CENTER);

        // KPI 3: Demanda de recarga
        JPanel kpi3 = new JPanel(new BorderLayout());
        kpi3.setBorder(BorderFactory.createTitledBorder("Demanda de Recarga"));
        lblDemandaRecarga = new JLabel("0", JLabel.CENTER);
        lblDemandaRecarga.setFont(new Font("Arial", Font.BOLD, 16));
        kpi3.add(lblDemandaRecarga, BorderLayout.CENTER);

        panel.add(kpi1);
        panel.add(kpi2);
        panel.add(kpi3);

        return panel;
    }

/** Panel de tabla de estaciones */
    private JPanel crearPanelTablaEstaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Desglose por Estación"));

        String[] columnNames = {"ID", "Ubicación", "Capacidad", "Ocupados", "% Ocupación", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        tablaEstaciones = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tablaEstaciones);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Gráfico de ocupación por estación */
    private JPanel crearPanelGraficoEstaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gráfico - Ocupación por Estación"));

        graficoPaneInfra = new JTextPane();
        graficoPaneInfra.setEditable(false);
        graficoPaneInfra.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        graficoPaneInfra.setBackground(new Color(0xFCFCFC));
        graficoPaneInfra.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JScrollPane scrollPane = new JScrollPane(graficoPaneInfra);
        panel.add(scrollPane, BorderLayout.CENTER);

        List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoInfraestructura();
        GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneInfra, datos, maxBoxesGraficoInfra, "Ocupación (%) por Estación");

        return panel;
    }

/** Panel de alertas */
    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de Infraestructura"));

        alertasArea = new JTextArea(4, 50);
        alertasArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(alertasArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Actualización de datos */
    private void actualizarDatos() {
        // Calcular KPIs
        int totalEstaciones = aplicacion.getGestorEstacion().getEstaciones().size();
        int estacionesDisponibles = calcularEstacionesDisponibles();
        double ocupacionPromedio = calcularOcupacionPromedio();
        int demandaTotal = calcularDemandaTotal();

        // Actualizar KPIs
        lblEstacionesDisponibles.setText(estacionesDisponibles + "/" + totalEstaciones +
                " (" + String.format("%.1f", totalEstaciones > 0 ? estacionesDisponibles * 100.0 / totalEstaciones : 0) + "%)");
        lblOcupacionInstantanea.setText(String.format("%.1f%%", ocupacionPromedio));
        lblDemandaRecarga.setText(String.valueOf(demandaTotal));

        // Actualizar tabla
        actualizarTablaEstaciones();

        // Actualizar alertas
        actualizarAlertasInfraestructura();

        // refrescar grafico
        SwingUtilities.invokeLater(() -> {
            List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoInfraestructura();
            GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneInfra, datos, maxBoxesGraficoInfra, "Ocupación (%) por Estación");
        });
    }

/** Cálculo de KPIs */
    private int calcularEstacionesDisponibles() {
        int count = 0;
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            if (estacion.estaOperativa()) {
                count++;
            }
        }
        return count;
    }

/** Ocupación promedio */
    private double calcularOcupacionPromedio() {
        if (aplicacion.getGestorEstacion().getEstaciones().isEmpty()) return 0;
        double total = 0;
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            total += estacion.getPorcentajeOcupacion();
        }
        return total / aplicacion.getGestorEstacion().getEstaciones().size();
    }

/** Demanda total ( robots y drones que necesitan recarga) */
    private int calcularDemandaTotal() {
        int demanda = 0;
        // Robots en alerta
        for (Robot robot : aplicacion.getGestorRobots().getRobots()) {
            if (robot.necesitaRecarga()) demanda++;
        }
        // Drones en alerta
        for (Dron dron : aplicacion.getGestorDrones().getDrones()) {
            if (dron.necesitaRecarga()) demanda++;
        }
        return demanda;
    }

/** Actualización de tabla y alertas */
    private void actualizarTablaEstaciones() {
        DefaultTableModel model = (DefaultTableModel) tablaEstaciones.getModel();
        model.setRowCount(0);

        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            model.addRow(new Object[]{
                    estacion.getId(),
                    "Calle " + estacion.getCalle() + ", Av " + estacion.getAvenida(),
                    estacion.getCapacidadMaxima(),
                    estacion.getOcupados(),
                    String.format("%.1f%%", estacion.getPorcentajeOcupacion()),
                    estacion.getEstado().toString()
            });
        }
    }

/** Actualización de alertas */
    private void actualizarAlertasInfraestructura() {
        StringBuilder alertas = new StringBuilder();

        // Verificar estaciones en mantenimiento con ocupación
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            if (estacion.getEstado() != Estado.DISPONIBLE && estacion.getOcupados() > 0) {
                alertas.append("ALERTA: Estación ").append(estacion.getId())
                        .append(" está en ").append(estacion.getEstado())
                        .append(" pero tiene ").append(estacion.getOcupados())
                        .append(" dispositivos ocupados\n");
            }
        }

        // Verificar demanda vs capacidad
        int capacidadTotal = 0;
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            if (estacion.estaOperativa()) {
                capacidadTotal += estacion.obtenerEspaciosDisponibles();
            }
        }
        int demanda = calcularDemandaTotal();
        if (demanda > capacidadTotal) {
            alertas.append("ALERTA: Demanda (").append(demanda)
                    .append(") excede capacidad disponible (").append(capacidadTotal).append(")\n");
        }

        if (alertas.length() == 0) {
            alertas.append("No hay alertas críticas de infraestructura");
            alertasArea.setForeground(Color.BLACK);
        } else {
            alertasArea.setForeground(Color.RED);
        }

        alertasArea.setText(alertas.toString());
    }

/** Obtener datos para gráfico */
    private List<GraficoUtil.DataPoint> obtenerDatosGraficoInfraestructura() {
        List<GraficoUtil.DataPoint> datos = new ArrayList<>();
        for (EstacionRecarga estacion : aplicacion.getGestorEstacion().getEstaciones()) {
            double pct = estacion.getPorcentajeOcupacion();
            String label = "Est " + estacion.getId();
            datos.add(new GraficoUtil.DataPoint(label, pct));
        }
        if (datos.isEmpty()) datos.add(new GraficoUtil.DataPoint("Sin estaciones", 0));
        return datos;
    }


}

/** Panel de Seguridad*/
class PanelSeguridad extends JPanel {
    private Aplicacion aplicacion;
    private JLabel lblIncidentes24h, lblIncidentesSinAccion, lblEdificiosImpactados, lblCiudadanosPotenciales;
    private JTable tablaIncidentes;
    private JTextArea alertasArea;


    // gráfico
    private JTextPane graficoPaneSeguridad;
    private int maxBoxesGraficoSeg = 10;

/** Constructor */
    public PanelSeguridad(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        actualizarDatos();
    }

/**Inicializar componentes */
    private void initComponents() {
        // Panel de KPIs
        JPanel panelKPIs = crearPanelKPIs();

        // Panel central
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentral.add(crearPanelTablaIncidentes());
        panelCentral.add(crearPanelGraficoSeguridad());

        // Panel de alertas
        JPanel panelAlertas = crearPanelAlertasSeguridad();

        add(panelKPIs, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelAlertas, BorderLayout.SOUTH);
    }

/** Panel de KPIs */
    private JPanel crearPanelKPIs() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("KPIs de Seguridad"));

        // Simulamos datos para incidentes (en una app real vendrían de la base de datos)
        int incidentes24h = 8;
        int incidentesSinAccion = 2;
        int edificiosImpactados = 3;
        int ciudadanosPotenciales = 45;

        JPanel kpi1 = crearPanelKPIIndividual("Incidentes 24h", String.valueOf(incidentes24h));
        JPanel kpi2 = crearPanelKPIIndividual("Incidentes sin Acción", String.valueOf(incidentesSinAccion));
        JPanel kpi3 = crearPanelKPIIndividual("Edificios Impactados", String.valueOf(edificiosImpactados));
        JPanel kpi4 = crearPanelKPIIndividual("Ciudadanos Potenciales", String.valueOf(ciudadanosPotenciales));

        panel.add(kpi1);
        panel.add(kpi2);
        panel.add(kpi3);
        panel.add(kpi4);

        return panel;
    }

/** Crear panel individual para cada KPI */
    private JPanel crearPanelKPIIndividual(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        JLabel label = new JLabel(valor, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

/** Panel de tabla de incidentes */
    private JPanel crearPanelTablaIncidentes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Desglose de Incidentes"));

        String[] columnNames = {"Tipo", "Edificio", "Acciones", "Hora", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        tablaIncidentes = new JTable(model);

        // Datos de ejemplo (se mantienen)
        model.addRow(new Object[]{"Incendio", "Torre Norte", "Bomberos, 911", "14:30", "Resuelto"});
        model.addRow(new Object[]{"Colisión", "Residencial Este", "Tránsito", "15:45", "En proceso"});
        model.addRow(new Object[]{"Accidente Grave", "Condominio Sur", "Ambulancias", "16:20", "Resuelto"});
        model.addRow(new Object[]{"Presencia Humo", "Torre Norte", "Bomberos", "17:10", "Pendiente"});

        JScrollPane scrollPane = new JScrollPane(tablaIncidentes);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Panel de gráfico de tipos de incidentes */
    private JPanel crearPanelGraficoSeguridad() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gráfico - Tipos de Incidentes"));

        graficoPaneSeguridad = new JTextPane();
        graficoPaneSeguridad.setEditable(false);
        graficoPaneSeguridad.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        graficoPaneSeguridad.setBackground(new Color(0xFCFCFC));
        graficoPaneSeguridad.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JScrollPane scrollPane = new JScrollPane(graficoPaneSeguridad);
        panel.add(scrollPane, BorderLayout.CENTER);

        List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoSeguridad();
        GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneSeguridad, datos, maxBoxesGraficoSeg, "Incidentes (normalizados)");

        return panel;
    }

/** Panel de alertas de seguridad */
    private JPanel crearPanelAlertasSeguridad() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de Seguridad"));

        alertasArea = new JTextArea(4, 50);
        alertasArea.setEditable(false);
        alertasArea.setText("ALERTA: Torre Norte tiene 3 incidentes reportados en las últimas 24h (mayor reincidencia)\n" +
                "ALERTA: 2 incidentes pendientes de acción");

        JScrollPane scrollPane = new JScrollPane(alertasArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Actualización de datos */
    private void actualizarDatos() {
        // En una implementación real, aquí se actualizarían los datos reales
        // Por ahora usamos datos de ejemplo en KPIs
        // refrescar grafico (pintado inmediato)
        SwingUtilities.invokeLater(() -> {
            List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoSeguridad();
            GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneSeguridad, datos, maxBoxesGraficoSeg, "Incidentes (normalizados)");
        });
    }

/** Conteo de tipos SIN usar Map: usamos dos listas paralelas */
    private List<GraficoUtil.DataPoint> obtenerDatosGraficoSeguridad() {
        List<String> tipos = new ArrayList<>();
        List<Integer> conteos = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) tablaIncidentes.getModel();
        for (int r = 0; r < model.getRowCount(); r++) {
            String tipo = String.valueOf(model.getValueAt(r, 0));
            int idx = tipos.indexOf(tipo);
            if (idx >= 0) {
                conteos.set(idx, conteos.get(idx) + 1);
            } else {
                tipos.add(tipo);
                conteos.add(1);
            }
        }

        // Normalizar y preparar datos
        List<GraficoUtil.DataPoint> datos = new ArrayList<>();
        if (tipos.isEmpty()) {
            datos.add(new GraficoUtil.DataPoint("Sin datos", 0));
            return datos;
        }

        int max = 1;
        for (int c : conteos) if (c > max) max = c;

        for (int i = 0; i < tipos.size(); i++) {
            double pct = conteos.get(i) * 100.0 / max; // normaliza 0..100
            datos.add(new GraficoUtil.DataPoint(tipos.get(i), pct));
        }
        return datos;
    }


}

/** Panel de Bienestar*/
class PanelBienestar extends JPanel {
    private Aplicacion aplicacion;
    private JLabel lblOcupacionPromedio, lblEdificiosCriticos, lblCoberturaRobots, lblRatioRobotsCiudadano;
    private JTable tablaEdificios;
    private JTextArea alertasArea;


    // gráfico
    private JTextPane graficoPaneBienestar;
    private int maxBoxesGraficoBien = 10;

    public PanelBienestar(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        actualizarDatos();
    }

/**Inicializar componentes */
    private void initComponents() {
        // Panel de KPIs
        JPanel panelKPIs = crearPanelKPIs();

        // Panel central
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentral.add(crearPanelTablaBienestar());
        panelCentral.add(crearPanelGraficoBienestar());

        // Panel de alertas
        JPanel panelAlertas = crearPanelAlertasBienestar();

        add(panelKPIs, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelAlertas, BorderLayout.SOUTH);
    }

/**Crear panel de KPIS */
    private JPanel crearPanelKPIs() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("KPIs de Bienestar"));

        // KPI 1: Ocupación promedio
        JPanel kpi1 = new JPanel(new BorderLayout());
        kpi1.setBorder(BorderFactory.createTitledBorder("Ocupación Promedio"));
        lblOcupacionPromedio = new JLabel("0%", JLabel.CENTER);
        lblOcupacionPromedio.setFont(new Font("Arial", Font.BOLD, 16));
        kpi1.add(lblOcupacionPromedio, BorderLayout.CENTER);

        // KPI 2: Edificios críticos
        JPanel kpi2 = new JPanel(new BorderLayout());
        kpi2.setBorder(BorderFactory.createTitledBorder("Edificios al 90%+"));
        lblEdificiosCriticos = new JLabel("0", JLabel.CENTER);
        lblEdificiosCriticos.setFont(new Font("Arial", Font.BOLD, 16));
        kpi2.add(lblEdificiosCriticos, BorderLayout.CENTER);

        // KPI 3: Cobertura de robots
        JPanel kpi3 = new JPanel(new BorderLayout());
        kpi3.setBorder(BorderFactory.createTitledBorder("Ciudadanos con Robot"));
        lblCoberturaRobots = new JLabel("0%", JLabel.CENTER);
        lblCoberturaRobots.setFont(new Font("Arial", Font.BOLD, 16));
        kpi3.add(lblCoberturaRobots, BorderLayout.CENTER);

        // KPI 4: Ratio robots/ciudadano
        JPanel kpi4 = new JPanel(new BorderLayout());
        kpi4.setBorder(BorderFactory.createTitledBorder("Robots por Ciudadano"));
        lblRatioRobotsCiudadano = new JLabel("0.0", JLabel.CENTER);
        lblRatioRobotsCiudadano.setFont(new Font("Arial", Font.BOLD, 16));
        kpi4.add(lblRatioRobotsCiudadano, BorderLayout.CENTER);

        panel.add(kpi1);
        panel.add(kpi2);
        panel.add(kpi3);
        panel.add(kpi4);

        return panel;
    }

/**Panel de tabla de edificios y su bienestar */
    private JPanel crearPanelTablaBienestar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Balance por Edificio"));

        String[] columnNames = {"Edificio", "Capacidad", "Residentes", "Ocupación%", "Con Robot", "Sin Robot", "Robots Total", "Robots/Ciudadano", "Robots Alerta"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        tablaEdificios = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tablaEdificios);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Gráfico de ocupación por edificio */
    private JPanel crearPanelGraficoBienestar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gráficos de Bienestar"));

        graficoPaneBienestar = new JTextPane();
        graficoPaneBienestar.setEditable(false);
        graficoPaneBienestar.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        graficoPaneBienestar.setBackground(new Color(0xFCFCFC));
        graficoPaneBienestar.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JScrollPane scrollPane = new JScrollPane(graficoPaneBienestar);
        panel.add(scrollPane, BorderLayout.CENTER);

        List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoBienestar();
        GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneBienestar, datos, maxBoxesGraficoBien, "Ocupación (%) por Edificio");

        return panel;
    }

/** Obtener datos para gráfico */
    private JPanel crearPanelAlertasBienestar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de Bienestar"));

        alertasArea = new JTextArea(4, 50);
        alertasArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(alertasArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

/** Obtener datos para gráfico */
    private void actualizarDatos() {
        // Calcular KPIs
        double ocupacionPromedio = calcularOcupacionPromedio();
        int edificiosCriticos = calcularEdificiosCriticos();
        double coberturaRobots = calcularCoberturaRobots();
        double ratioRobotsCiudadano = calcularRatioRobotsCiudadano();

        // Actualizar KPIs
        lblOcupacionPromedio.setText(String.format("%.1f%%", ocupacionPromedio));
        lblEdificiosCriticos.setText(String.valueOf(edificiosCriticos));
        lblCoberturaRobots.setText(String.format("%.1f%%", coberturaRobots));
        lblRatioRobotsCiudadano.setText(String.format("%.1f", ratioRobotsCiudadano));

        // Actualizar tabla
        actualizarTablaBienestar();

        // Actualizar alertas
        actualizarAlertasBienestar();

        // refrescar grafico (pintado inmediato)
        SwingUtilities.invokeLater(() -> {
            List<GraficoUtil.DataPoint> datos = obtenerDatosGraficoBienestar();
            GraficoUtil.actualizarGraficoTextoGenerico(graficoPaneBienestar, datos, maxBoxesGraficoBien, "Ocupación (%) por Edificio");
        });
    }

/** Cálculo de KPIs */
    private double calcularOcupacionPromedio() {
        if (aplicacion.getGestorEdificios().getEdificios().isEmpty()) return 0;
        double total = 0;
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            total += edificio.calcularPorcentajeOcupacion();
        }
        return total / aplicacion.getGestorEdificios().getEdificios().size();
    }

/** Edificios al más del 90% de ocupación */
    private int calcularEdificiosCriticos() {
        int count = 0;
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            if (edificio.calcularPorcentajeOcupacion() >= 90) {
                count++;
            }
        }
        return count;
    }

/** Cobertura de robots (% de ciudadanos con al menos un robot) */
    private double calcularCoberturaRobots() {
        int totalCiudadanos = aplicacion.getGestorCiudadanos().listarCiudadanos().size();
        if (totalCiudadanos == 0) return 0;
        int conRobot = 0;
        for (Ciudadano ciudadano : aplicacion.getGestorCiudadanos().listarCiudadanos()) {
            if (ciudadano.tieneRobotsAsignados()) {
                conRobot++;
            }
        }
        return (conRobot * 100.0) / totalCiudadanos;
    }

/** Ratio de robots por ciudadano */
    private double calcularRatioRobotsCiudadano() {
        int totalCiudadanos = aplicacion.getGestorCiudadanos().listarCiudadanos().size();
        if (totalCiudadanos == 0) return 0;
        int totalRobots = 0;
        for (Ciudadano ciudadano : aplicacion.getGestorCiudadanos().listarCiudadanos()) {
            totalRobots += ciudadano.cantidadRobotsAsignados();
        }
        return (double) totalRobots / totalCiudadanos;
    }

/** Actualización de tabla y alertas de bienestar */
    private void actualizarTablaBienestar() {
        DefaultTableModel model = (DefaultTableModel) tablaEdificios.getModel();
        model.setRowCount(0);

        int umbral = aplicacion.getConsejoInteligencia().getMinBateriaRobots();

        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            int residentes = edificio.getCapacidadActual();
            int capacidad = edificio.getCapacidadMaxima();
            double ocupacion = edificio.calcularPorcentajeOcupacion();

            int conRobot = 0;
            int sinRobot = 0;
            int robotsTotal = 0;
            int robotsAlerta = 0;

            for (Ciudadano ciudadano : edificio.getListaResidentes()) {
                if (ciudadano.tieneRobotsAsignados()) {
                    conRobot++;
                    robotsTotal += ciudadano.cantidadRobotsAsignados();
                    robotsAlerta += ciudadano.cantidadRobotsEnAlerta(umbral);
                } else {
                    sinRobot++;
                }
            }

            double robotsPorCiudadano = residentes > 0 ? (double) robotsTotal / residentes : 0;

            model.addRow(new Object[]{
                    edificio.getNombre(),
                    capacidad,
                    residentes,
                    String.format("%.1f%%", ocupacion),
                    conRobot,
                    sinRobot,
                    robotsTotal,
                    String.format("%.1f", robotsPorCiudadano),
                    robotsAlerta
            });
        }
    }

/** Actualización de alertas */
    private void actualizarAlertasBienestar() {
        StringBuilder alertas = new StringBuilder();

        // Verificar cobertura mínima (< 60%)
        double cobertura = calcularCoberturaRobots();
        if (cobertura < 60) {
            alertas.append("ALERTA: Cobertura de robots es solo ").append(String.format("%.1f", cobertura))
                    .append("% (mínimo recomendado: 60%)\n");
        }

        // Verificar edificios con alta ocupación y baja cobertura
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            double ocupacion = edificio.calcularPorcentajeOcupacion();
            int conRobot = 0;
            for (Ciudadano ciudadano : edificio.getListaResidentes()) {
                if (ciudadano.tieneRobotsAsignados()) conRobot++;
            }
            double coberturaEdificio = edificio.getCapacidadActual() > 0 ?
                    (conRobot * 100.0 / edificio.getCapacidadActual()) : 0;

            if (ocupacion > 80 && coberturaEdificio < 50) {
                alertas.append("ALERTA: ").append(edificio.getNombre())
                        .append(" tiene alta ocupación (").append(String.format("%.1f", ocupacion))
                        .append("%) pero baja cobertura de robots (").append(String.format("%.1f", coberturaEdificio))
                        .append("%)\n");
            }
        }

        if (alertas.length() == 0) {
            alertas.append("No hay alertas críticas de bienestar");
            alertasArea.setForeground(Color.BLACK);
        } else {
            alertasArea.setForeground(Color.RED);
        }

        alertasArea.setText(alertas.toString());
    }

/** Obtener datos para el gráfico */
    private List<GraficoUtil.DataPoint> obtenerDatosGraficoBienestar() {
        List<GraficoUtil.DataPoint> datos = new ArrayList<>();
        for (EdificioInteligente edificio : aplicacion.getGestorEdificios().getEdificios()) {
            double ocup = edificio.calcularPorcentajeOcupacion();
            datos.add(new GraficoUtil.DataPoint(edificio.getNombre(), ocup));
        }
        if (datos.isEmpty()) datos.add(new GraficoUtil.DataPoint("Sin edificios", 0));
        return datos;
    }


}

