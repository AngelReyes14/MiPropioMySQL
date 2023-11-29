package mipropiomysql;

import java.awt.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import packeteria.Paqueteria;
public class Conectar extends JPanel {
    private Inicio inicio;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;
    private String selectedDatabase;
    private Font labelFont = new Font("Gotham Black", Font.ITALIC, 20);
private Connection conexion; // Asegúrate de tener esta declaración
private boolean conexionEstablecida = false; // Variable para verificar si ya hay una conexión establecida


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

        setTextFieldRound(txtHost);
        setTextFieldRound(txtPuerto);
        setTextFieldRound(txtUsuario);
        setTextFieldRound(txtContraseña);
        
        addFormField(this, "Host:", txtHost, 350, 40, 200, 30, Color.WHITE);
        addFormField(this, "Puerto:", txtPuerto, 350, 120, 200, 30, Color.WHITE);
        addFormField(this, "Usuario:", txtUsuario, 350, 200, 200, 30, Color.WHITE);
        addFormField(this, "Contraseña:", txtContraseña, 350, 280, 200, 30, Color.WHITE);

RoundButton btnConectar = new RoundButton("Conectar", new Color(0x153f59), new Color(0x537491), Color.WHITE);
btnConectar.setFont(labelFont);
btnConectar.setForeground(Color.WHITE);
btnConectar.setBounds(450, 400, 200, 40);
  
 

btnConectar.addActionListener(e -> {
    // Verificar si ya hay una conexión establecida
   if (conexionEstablecida) {
        JOptionPane.showMessageDialog(this, "Ya mantienes una conexión establecida.", "Información", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Verificar si los campos están vacíos
    if (txtHost.getText().isEmpty() || txtPuerto.getText().isEmpty() || txtUsuario.getText().isEmpty() || new String(txtContraseña.getPassword()).isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tiene que llenar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Salir del método si hay campos vacíos
    }

    // Verificar si los datos ingresados son válidos
    if (!esDatoValido(txtHost.getText(), "Host") || !esDatoValido(txtPuerto.getText(), "Puerto") || !esDatoValido(txtUsuario.getText(), "Usuario") || !esDatoValido(new String(txtContraseña.getPassword()), "Contraseña")) {
        return; // Salir del método si hay datos inválidos
    }

    String host = txtHost.getText();
    int puerto = Integer.parseInt(txtPuerto.getText());
    String usuario = txtUsuario.getText();
    String contraseña = new String(txtContraseña.getPassword());

    // Store the selected database
    selectedDatabase = inicio.getSelectedDatabase();

    Paqueteria paqueteria = new Paqueteria(host, usuario, contraseña, puerto);
    conexion = paqueteria.getConexion();
    
     if (conexion != null) {
            JOptionPane.showMessageDialog(this, "Conexión exitosa a la base de datos MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            inicio.cargarBasesDeDatos(paqueteria);

            // Habilitar opciones después de una conexión exitosa
            
            // Establecer la bandera de conexión establecida a true
            conexionEstablecida = true;
        } else {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

        add(btnConectar);
    }
private boolean esDatoValido(String dato, String nombreCampo) {
    // Implementar la lógica de validación según tus criterios
    // En este ejemplo, simplemente verifica que el campo no sea "123"
    if (dato.equals("123")) {
        JOptionPane.showMessageDialog(this, "El campo " + nombreCampo + " no coincide.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    return true;
}
    private void addFormField(Container container, String label, JComponent component, int x, int y, int width, int height, Color backgroundColor) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(x, y, width, height);

        container.add(lbl);

        x += width + 5; // Separación entre la etiqueta y el componente
        component.setBackground(backgroundColor);
        component.setBounds(x, y, width, height);

        container.add(component);
    }
     public Connection getConexion() {
        return conexion;
    }
     private void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setTextFieldRound(JTextField textField) {
        // Crear un borde con esquinas curveadas y sin líneas de contorno
        Border roundedBorder = new LineBorder(Color.WHITE, 5, true); // Puedes personalizar el grosor
        
        // Establecer el borde con esquinas curveadas para el JTextField
        textField.setBorder(roundedBorder);
    }


      private void conectarABaseDeDatos() {
        try {
            String host = txtHost.getText();
            int puerto = Integer.parseInt(txtPuerto.getText());
            String usuario = txtUsuario.getText();
            String contraseña = new String(txtContraseña.getPassword());

            String url = "jdbc:mysql://" + host + ":" + puerto + "/";
            conexion = DriverManager.getConnection(url, usuario, contraseña);

            // ... (resto del código para manejar la conexión)
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        private void desconectarDeBaseDeDatos() {
        cerrarConexion();
    }
         @Override
    protected void finalize() throws Throwable {
        cerrarConexion();
        super.finalize();
    }
 public boolean isConexionEstablecida() {
        return conexion != null; // Devuelve true si la conexión está establecida, false si no lo está
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Inicio inicio = new Inicio();
            inicio.setVisible(true);
        });
    }

    public String getHost() {
        return txtHost.getText();
    }

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public String getContraseña() {
        return new String(txtContraseña.getPassword());
    }
    public String getSelectedDatabase() {
    return selectedDatabase;
}


    public int getPuerto() {
        // Manejar la conversión a entero y posiblemente manejar errores aquí
        try {
            return Integer.parseInt(txtPuerto.getText());
        } catch (NumberFormatException e) {
            // Manejar el error, como mostrar un mensaje y devolver un valor por defecto
            JOptionPane.showMessageDialog(this, "Error en el puerto.", "Error", JOptionPane.ERROR_MESSAGE);
            return 0; // O cualquier valor por defecto que desees
        }
    }
    
}
