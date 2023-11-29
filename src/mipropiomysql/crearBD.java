package mipropiomysql;
import java.util.List;
import packeteria.Paqueteria;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;

public class crearBD extends JPanel {
    private JTextField txtNombreBd;
    private Conectar conexionPanel;
    private Inicio inicio;

    public crearBD(Inicio inicio, Conectar conexionPanel) {
        this.conexionPanel = conexionPanel;
        this.inicio = inicio;
        setLayout(null);

        int red = 148;
        int green = 184;
        int blue = 215;

        Color backgroundColor = new Color(red, green, blue);
        setBackground(backgroundColor);

        JLabel lblNombreBd = new JLabel("Nombre de la Base de Datos:");
        lblNombreBd.setFont(new Font("Gotham Black", Font.BOLD, 22));
        lblNombreBd.setForeground(Color.white);
        txtNombreBd = new JTextField(20);
        lblNombreBd.setBounds(50, 50, 400, 30);
        txtNombreBd.setBounds(50, 120, 300, 30);
        add(lblNombreBd);
        add(txtNombreBd);

RoundButton btnCrearBd = new RoundButton("Crear", new Color(0x153f59), new Color(0x537491), Color.WHITE);
        btnCrearBd.setFont(new Font("Gotham Black", Font.BOLD, 20));
        btnCrearBd.setForeground(Color.WHITE);
        btnCrearBd.setBounds(50, 180, 120, 40);
        add(btnCrearBd);

        btnCrearBd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombreBd = txtNombreBd.getText();

                if (!conexionPanel.isConexionEstablecida()) {
                    JOptionPane.showMessageDialog(crearBD.this, "No hay una conexión establecida.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conexion = conexionPanel.getConexion();
                if (conexion == null) {
                    JOptionPane.showMessageDialog(crearBD.this, "La conexión no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (nombreBd.isEmpty()) {
                    JOptionPane.showMessageDialog(crearBD.this, "Por favor, coloca el nombre de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Paqueteria paqueteria = new Paqueteria(conexionPanel.getHost(), conexionPanel.getUsuario(), conexionPanel.getContraseña(), conexionPanel.getPuerto());

                    if (paqueteria.getConexion() == null) {
                        JOptionPane.showMessageDialog(crearBD.this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    List<String> basesDeDatosExistente = paqueteria.obtenerBasesDeDatos();
                    if (basesDeDatosExistente.contains(nombreBd)) {
                        JOptionPane.showMessageDialog(crearBD.this, "El nombre '" + nombreBd + "' ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    paqueteria.crearBd(nombreBd);

                    JOptionPane.showMessageDialog(crearBD.this, "La base de datos '" + nombreBd + "' ha sido creada.", "Base de Datos Creada", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(crearBD.this, "Error al crear la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        });
    }
}
