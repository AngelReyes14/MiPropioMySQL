/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mipropiomysql;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import packeteria.Paqueteria;

public class tablas extends JFrame {
    private JComboBox<String> bdComboBox;
    private JTextField nombreTablaTextField;
    private JSpinner columnasSpinner;
    private JButton crearTablaButton;

    public tablas() {
        // Configuración de la ventana "Administrar Tablas"
        setTitle("Administrar Tablas");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear un panel para los componentes
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        // ComboBox para seleccionar una base de datos
        JLabel bdLabel = new JLabel("Seleccionar Base de Datos:");
        bdComboBox = new JComboBox<>();

        // Obtén la lista de bases de datos desde la clase Paqueteria
        Paqueteria paqueteria = new Paqueteria("localhost", "root", "12345", 3306); // Ajusta estos valores
        DefaultComboBoxModel<String> bdComboBoxModel = new DefaultComboBoxModel<>();
        for (String bd : paqueteria.listarBasesDeDatos()) {
            bdComboBoxModel.addElement(bd);
        }
        bdComboBox.setModel(bdComboBoxModel);

        // TextField para ingresar el nombre de la tabla
        JLabel nombreTablaLabel = new JLabel("Nombre de la Tabla:");
        nombreTablaTextField = new JTextField(20);

        // Spinner para seleccionar la cantidad de columnas
        JLabel columnasLabel = new JLabel("Cantidad de Columnas:");
        columnasSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));

        // Botón "Crear"
        crearTablaButton = new JButton("Crear");

        // Agregar componentes al panel
        panel.add(bdLabel);
        panel.add(bdComboBox);
        panel.add(nombreTablaLabel);
        panel.add(nombreTablaTextField);
        panel.add(columnasLabel);
        panel.add(columnasSpinner);
        panel.add(new JLabel()); // Espacio en blanco
        panel.add(crearTablaButton);

        // ActionListener para el botón "Crear"
        crearTablaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearTabla();
            }
        });

        // Agregar el panel al contenido de la ventana
        add(panel);
    }

    private void crearTabla() {
        int cantidadColumnas = (int) columnasSpinner.getValue();
        String nombreTabla = nombreTablaTextField.getText();

        if (nombreTabla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un nombre para la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener los valores seleccionados por el usuario
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE ");
        createTableSQL.append(nombreTabla).append(" (");

        for (int i = 0; i < cantidadColumnas; i++) {
            if (i > 0) {
                createTableSQL.append(", ");
            }

            createTableSQL.append(getColumnDefinition(i));
        }

        createTableSQL.append(");");

        // Crear la tabla en la base de datos
        String baseDeDatos = bdComboBox.getSelectedItem().toString();
        Paqueteria paqueteria = new Paqueteria("localhost", "root", "12345", 3306); // Ajusta estos valores
        Connection conexion = paqueteria.getConexion();

        if (conexion != null) {
            try {
                Statement statement = conexion.createStatement();
                statement.executeUpdate("USE " + baseDeDatos); // Seleccionar la base de datos
                statement.executeUpdate(createTableSQL.toString());

                // Muestra un mensaje emergente con el resultado
                String mensaje = "La tabla '" + nombreTabla + "' ha sido creada en la base de datos '" + baseDeDatos + "'.";
                JOptionPane.showMessageDialog(this, mensaje, "Tabla Creada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al crear la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                paqueteria.desconectar(conexion);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            tablas tablas = new tablas();
            tablas.setVisible(true);
        });
    }
}
