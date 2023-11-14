/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mipropiomysql;
import packeteria.Paqueteria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class consultas extends JPanel {
    private JComboBox<String> tablaComboBox;
    private JPanel panelAtributos;
    private Paqueteria paqueteria;
    private Inicio inicio;
    private JTextArea resultadoConsulta;
    private JButton generarConsultaButton;
    private JComboBox<String> atributoComboBox;
    private JTable resultadoEjecucion;
private DefaultTableModel tableModel;

    public consultas(Inicio inicio) {
        this.inicio = inicio;
        paqueteria = new Paqueteria("localhost", "root", "12345", 3306);

        // Nuevo componente para seleccionar la tabla
        tablaComboBox = new JComboBox<>();

        // Agregar el componente al panel en la parte superior
        add(tablaComboBox, BorderLayout.NORTH);
        
        // Nuevo panel para los CheckBox de los atributos
        panelAtributos = new JPanel();
        panelAtributos.setLayout(new BoxLayout(panelAtributos, BoxLayout.Y_AXIS));

        // Agregar el panel al panel izquierdo
        add(panelAtributos, BorderLayout.WEST);

        // Nuevo componente para mostrar el resultado de la consulta
        resultadoConsulta = new JTextArea();
        resultadoConsulta.setEditable(false);

        // Agregar el componente al panel en la parte media
add(new JScrollPane(resultadoConsulta), BorderLayout.NORTH);

        // Nuevo botón para generar y ejecutar la consulta
        generarConsultaButton = new JButton("Generar y Ejecutar Consulta");
        generarConsultaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarYExecutarConsulta();
            }
        });
     

        // Agregar el componente al panel en la parte inferior
add(new JScrollPane(resultadoEjecucion), BorderLayout.CENTER);

        // Agregar el botón al panel en la parte inferior derecha
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(generarConsultaButton);
        add(panelBoton, BorderLayout.SOUTH);
        tableModel = new DefaultTableModel();
resultadoEjecucion = new JTable(tableModel);
add(new JScrollPane(resultadoEjecucion), BorderLayout.CENTER);
        // Manejar eventos de selección de la tabla
        tablaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarAtributos();
            }
        });


        // Cargar las tablas al iniciar la aplicación
        cargarTablas();
    }

    private void cargarTablas() {
        tablaComboBox.removeAllItems();

        String selectedDb = inicio.getSelectedDatabase();

        if (selectedDb != null) {
            List<String> tablas = paqueteria.listarTablas(selectedDb);

            for (String nombreTabla : tablas) {
                tablaComboBox.addItem(nombreTabla);
            }
        }
    }

    private void cargarAtributos() {
        panelAtributos.removeAll();

        String selectedDb = inicio.getSelectedDatabase();
        String selectedTable = (String) tablaComboBox.getSelectedItem();

        if (selectedDb != null && selectedTable != null) {
            List<String> atributos = paqueteria.listarAtributos(selectedDb, selectedTable);

            for (String atributo : atributos) {
                JCheckBox checkBoxAtributo = new JCheckBox(atributo);
                panelAtributos.add(checkBoxAtributo);
            }

            panelAtributos.revalidate();
            panelAtributos.repaint();
        }
    }


private void mostrarResultadosEnTable(ResultSet resultSet) {
    try {
        // Limpiar el modelo de la tabla antes de agregar nuevos datos
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        // Obtener los nombres de las columnas
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Agregar nombres de columnas al modelo de la tabla
        for (int i = 1; i <= columnCount; i++) {
            tableModel.addColumn(metaData.getColumnName(i));
        }

        // Obtener los datos de las filas
        while (resultSet.next()) {
            List<String> fila = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                fila.add(resultSet.getString(i));
            }
            // Agregar datos de filas al modelo de la tabla
            tableModel.addRow(fila.toArray());
        }
    } catch (SQLException e) {
        e.printStackTrace();
        resultadoConsulta.setText("Error al mostrar resultados en la tabla: " + e.getMessage());
    }
}


private void ejecutarConsulta(String consulta, String selectedDb, String selectedTable) {
    try (Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/" + selectedDb,
            "root",
            "12345")) {
        System.out.println(consulta);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(consulta);

        // Mostrar los resultados de la ejecución en la tabla
        mostrarResultadosEnTable(resultSet);

    } catch (SQLException e) {
        e.printStackTrace();
        
        // Mostrar mensaje de error en resultadoConsulta
        resultadoConsulta.setText("Error al ejecutar la consulta: " + e.getMessage());
    }
}

   private void generarYExecutarConsulta() {
    String selectedDb = inicio.getSelectedDatabase();
    String selectedTable = (String) tablaComboBox.getSelectedItem();

    // Verificar si se ha seleccionado una tabla
    if (selectedDb != null && selectedTable != null) {
        // Obtener los nombres de los atributos seleccionados
        List<String> atributosSeleccionados = new ArrayList<>();
        Component[] components = panelAtributos.getComponents();
        boolean alMenosUnAtributoSeleccionado = false; // Nueva variable para verificar la selección

        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    atributosSeleccionados.add(checkBox.getText());
                    alMenosUnAtributoSeleccionado = true; // Marcamos que al menos un atributo está seleccionado
                }
            }
        }

        // Verificar si al menos un JCheckBox está seleccionado
        if (!alMenosUnAtributoSeleccionado) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar al menos una casilla para ejecutar la Query", "Mensaje de Emergencia", JOptionPane.ERROR_MESSAGE);
            return; // Salir del método sin ejecutar la consulta
        }

        // Construir la consulta con los atributos seleccionados
        StringBuilder consultaBuilder = new StringBuilder("SELECT ");
        for (String atributo : atributosSeleccionados) {
            consultaBuilder.append(atributo).append(", ");
        }
        consultaBuilder.delete(consultaBuilder.length() - 2, consultaBuilder.length()); // Eliminar la última coma
        consultaBuilder.append(" FROM ").append(selectedTable);

        String consulta = consultaBuilder.toString();

        // Mostrar la consulta generada en el JTextArea
        resultadoConsulta.setText("Consulta generada: " + consulta);

        // Ejecutar la consulta
        ejecutarConsulta(consulta, selectedDb, selectedTable);
    }
}




    public void onDatabaseSelected(String selectedDb) {
        System.out.println("Selected Database: " + selectedDb);
        cargarTablas();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Puedes probar la interfaz aquí si es necesario
        });
    }
}
