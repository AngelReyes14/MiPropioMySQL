/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mipropiomysql;

import java.awt.*;
import javax.swing.*;
import java.sql.Connection;
import packeteria.Paqueteria;

public class Conectar extends JPanel {
    private Inicio inicio;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;
    private Font labelFont = new Font("Arial", Font.BOLD, 18);

    public Conectar(Inicio inicio) {
        this.inicio = inicio;

        setLayout(null); // Usar un diseño nulo para poder usar setBounds
        int red = 148;
        int green = 184;
        int blue = 215;

        // Crear un objeto Color con los valores RGB
        Color backgroundColor = new Color(red, green, blue);

        // Establecer el color de fondo del panel
        setBackground(backgroundColor);

        txtHost = new JTextField("127.0.0.1");
        txtPuerto = new JTextField("3306");
        txtUsuario = new JTextField("root");
        txtContraseña = new JPasswordField("12345");

        addFormField(this, "Host:", txtHost, 350, 40, 200, 30, Color.WHITE);
        addFormField(this, "Puerto:", txtPuerto, 350, 120, 200, 30, Color.WHITE);
        addFormField(this, "Usuario:", txtUsuario, 350, 200, 200, 30, Color.WHITE);
        addFormField(this, "Contraseña:", txtContraseña, 350, 280, 200, 30, Color.WHITE);

        JButton btnConectar = new JButton("Conectar");
        btnConectar.setFont(labelFont);
        btnConectar.setBackground(new Color(0, 128, 0));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setBounds(450, 400, 200, 30);
        btnConectar.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        // Cambiar color al pasar el mouse sobre el botón
        btnConectar.setBackground(new Color(0, 200, 0));
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
        // Restaurar color cuando el mouse sale del botón
        btnConectar.setBackground(new Color(0, 128, 0));
    }
    
});
    btnConectar.addActionListener(e -> {
    // Verificar si los campos están vacíos
    if (txtHost.getText().isEmpty() || txtPuerto.getText().isEmpty() || txtUsuario.getText().isEmpty() || new String(txtContraseña.getPassword()).isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tiene que llenar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Salir del método si hay campos vacíos
    }    
            String host = txtHost.getText();
            int puerto = Integer.parseInt(txtPuerto.getText());
            String usuario = txtUsuario.getText();
            String contraseña = new String(txtContraseña.getPassword());

            Paqueteria paqueteria = new Paqueteria(host, usuario, contraseña, puerto);
            Connection conexion = paqueteria.getConexion();

            if (conexion != null) {
                JOptionPane.showMessageDialog(this, "Conexión exitosa a la base de datos MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                inicio.cargarBasesDeDatos(paqueteria);
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(btnConectar);
    }

    private void addFormField(Container container, String label, JComponent component, int x, int y, int width, int height, Color backgroundColor) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(Color.BLACK);
        lbl.setBounds(x, y, width, height);

        container.add(lbl);

        x += width + 5; // Separación entre la etiqueta y el componente
        component.setBackground(backgroundColor);
        component.setBounds(x, y, width, height);

        container.add(component);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Inicio inicio = new Inicio();
            inicio.setVisible(true);
        });
    }
}
