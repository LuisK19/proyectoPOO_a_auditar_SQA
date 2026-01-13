package VistaInterfaces;

import Main.Aplicacion;
//import VistaInterfaces.PanelAdministrador;
//import VistaInterfaces.PanelGeneral;
//import VistaInterfaces.PanelOperador;

import javax.swing.*;
import java.awt.*;

/** Pantalla primcipal con opciones para diferentes perfiles de usuario */
public class PantallaPrincipal extends JFrame {
    private Aplicacion aplicacion;

/** Constructor */
    public PantallaPrincipal(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        setTitle("Neo-Urbe - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        initComponents();
    }

/** Inicializar componentes de la interfaz */
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Crear botones para diferentes perfiles de usuario
        JButton btnAdministrador = new JButton("Perfil Administrador");
        JButton btnOperador = new JButton("Perfil Operador");
        JButton btnGeneral = new JButton("Perfil General");

        JButton btnGenerarDatosPrueba = new JButton("Generar Datos");
        btnGenerarDatosPrueba.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea generar datos de prueba?\nEsta acción creará 4 edificios, 20 ciudadanos, 15 robots y drones.",
                    "Generar Datos de Prueba",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                aplicacion.crearDatosPrueba();
                JOptionPane.showMessageDialog(this, "Datos de prueba generados exitosamente.");
            }
        });

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        btnAdministrador.setFont(buttonFont);
        btnOperador.setFont(buttonFont);
        btnGeneral.setFont(buttonFont);
        btnGenerarDatosPrueba.setFont(buttonFont);
        panel.add(btnAdministrador);
        panel.add(btnOperador);
        panel.add(btnGeneral);
        panel.add(btnGenerarDatosPrueba);
        add(panel);

        // Configurar acciones
        btnAdministrador.addActionListener(e -> {
            new PanelAdministrador(aplicacion).setVisible(true);
            dispose();
        });

        btnOperador.addActionListener(e -> {
            new PanelOperador(aplicacion).setVisible(true);
            dispose();
        });

        btnGeneral.addActionListener(e -> {
            new PanelGeneral(aplicacion).setVisible(true);
            dispose();
        });
    }
}
