
package mipropiomysql;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import packeteria.Paqueteria;

public class tablas extends JPanel {
        private Inicio inicio;
    private JTextField bdComboBox;
    private JTextField nombreTablaTextField;
    private JSpinner columnasSpinner;
    private JButton crearTablaButton;
    private Font labelFont = new Font("Gotham Black", Font.ITALIC, 20);
    private String baseDeDatos; // Nueva variable de instancia

    public tablas(Inicio inicio) {
                this.inicio = inicio;

        // Configuración de la ventana "Administrar Tablas"
        setLayout(null); // Cambiar a null para usar setBounds

         int red = 148;
        int green = 184;
        int blue = 215;
        Color backgroundColor = new Color(red, green, blue);
        setBackground(backgroundColor);
        
        // ComboBox para seleccionar una base de datos
        JLabel bdLabel = new JLabel("Seleccionar Base de Datos:");
        bdLabel.setForeground(Color.WHITE);
        bdLabel.setBounds(10, 30, 300, 25);
        bdLabel.setFont(labelFont);
        bdComboBox = new JTextField(20);
        bdComboBox.setBounds(320, 30, 150, 25);
        

        // Obtén la lista de bases de datos desde la clase Paqueteria
       

        // TextField para ingresar el nombre de la tabla
        JLabel nombreTablaLabel = new JLabel("Nombre de la Tabla:");
        nombreTablaLabel.setForeground(Color.WHITE);
        nombreTablaTextField = new JTextField(20);
                nombreTablaTextField.setBounds(320, 80, 150, 25);
                nombreTablaLabel.setBounds(10, 80, 300, 25);
                nombreTablaLabel.setFont(labelFont);

        // Spinner para seleccionar la cantidad de columnas
        JLabel columnasLabel = new JLabel("Cantidad de Columnas:");
        columnasLabel.setForeground(Color.WHITE);
        columnasSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        columnasSpinner.setBounds(320, 130, 150, 25);
        columnasLabel.setBounds(10, 130, 300, 25);
        columnasLabel.setFont(labelFont);

        // Botón "Crear"
       crearTablaButton = new RoundButton("Crear", new Color(0x153f59), new Color(0x537491),Color.WHITE);
        crearTablaButton.setFont(labelFont);
        crearTablaButton.setForeground(Color.WHITE);
        crearTablaButton.setBounds(20, 190, 110, 50);

        add(new JLabel()); // Espacio en blanco
        add(crearTablaButton);

        // Agregar componentes al panel
        add(bdLabel);
        add(bdComboBox);
        add(nombreTablaLabel);
        add(nombreTablaTextField);
        add(columnasLabel);
        add(columnasSpinner);
        add(new JLabel()); // Espacio en blanco

        // ActionListener para el botón "Crear"
        crearTablaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearTabla();
            }
        });

       
    }

   private void crearTabla() {
    // Verificar si la conexión a la base de datos está establecida
    if (!inicio.getConectarPanel().isConexionEstablecida()) {
        JOptionPane.showMessageDialog(this, "Aún no mantienes una conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int cantidadColumnas = (int) columnasSpinner.getValue();
    String nombreTabla = nombreTablaTextField.getText();

    if (nombreTabla.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes ingresar un nombre para la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (inicio.getSelectedDatabase() == null || inicio.getSelectedDatabase().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes seleccionar una base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Obtener los valores seleccionados por el usuario
    StringBuilder createTableSQL = new StringBuilder("CREATE TABLE ");
    createTableSQL.append(inicio.getSelectedDatabase()).append(".").append(nombreTabla).append(" (");

    for (int i = 0; i < cantidadColumnas; i++) {
        if (i > 0) {
            createTableSQL.append(", ");
        }

        createTableSQL.append(getColumnDefinition(i));
    }

    createTableSQL.append(");");

    // Crear la tabla en la base de datos
    Connection conexion = inicio.getConectarPanel().getConexion();

    if (conexion != null) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate(createTableSQL.toString());

            // Muestra un mensaje emergente con el resultado
            String mensaje = "La tabla '" + nombreTabla + "' ha sido creada en la base de datos '" + inicio.getSelectedDatabase() + "'.";
            JOptionPane.showMessageDialog(this, mensaje, "Tabla Creada", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    private String getColumnDefinition(int columnNumber) {
        String nombreColumna = JOptionPane.showInputDialog(this, "Nombre de la Columna " + (columnNumber + 1) + ":");
        String tipoDato = (String) JOptionPane.showInputDialog(this, "Tipo de Dato Columna " + (columnNumber + 1) + ":", "Seleccionar Tipo de Dato",
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"INT", "VARCHAR", "FLOAT", "DATE"}, "INT");

        // Asegúrate de que el nombre de la columna no esté vacío
        if (nombreColumna.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un nombre para la columna.", "Error", JOptionPane.ERROR_MESSAGE);
            return getColumnDefinition(columnNumber); // Vuelve a pedir el nombre de la columna
        }

        if ("VARCHAR".equals(tipoDato)) {
            String longitud = JOptionPane.showInputDialog(this, "Longitud para VARCHAR (por defecto 20):");
            if (longitud.isEmpty()) {
                longitud = "20";  // Valor por defecto si no se ingresa nada
            }
            return nombreColumna + " " + tipoDato + "(" + longitud + ")";
        } else {
            return nombreColumna + " " + tipoDato;
        }
    }
public void actualizarNombreBaseDeDatos(String nombreBd) {
    bdComboBox.setText(nombreBd);
    bdComboBox.setEditable(false); // Bloquear la edición del JTextField
    baseDeDatos = nombreBd; // Almacena la base de datos seleccionada
}
   public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
            Inicio inicio = new Inicio();
            inicio.setVisible(true);
        });
    }
}
