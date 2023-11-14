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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class descripcionBD extends JPanel {
    private DefaultListModel<String> listModelTablas;
    private JList<String> listaTablas;
    private JButton describirTablaButton;
    private JButton ingresarDatosButton;
    private Paqueteria paqueteria;
    
    private Inicio inicio; // Referencia a la instancia de la clase Inicio

    public descripcionBD(Inicio inicio) {
        this.inicio = inicio;

        // Crear la instancia de Paqueteria y ajustar los valores de conexión según tus necesidades
        paqueteria = new Paqueteria("localhost", "root", "12345", 3306);

        // Panel derecho para mostrar las tablas
        JPanel panelDerecho = new JPanel();
        panelDerecho.setPreferredSize(new Dimension(550, 600));
        panelDerecho.setBackground(new Color(153, 153, 255)); // Color morado más oscuro

        // Crear un modelo de lista para las tablas
        listModelTablas = new DefaultListModel<>();
        listaTablas = new JList<>(listModelTablas);

        // Botón "Ingresar Datos"
        ingresarDatosButton = new JButton("Ingresar Datos");
        ingresarDatosButton.setEnabled(false); // Inicialmente deshabilitado

        // ActionListener para el botón "Ingresar Datos"
        ingresarDatosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedTable = listaTablas.getSelectedValue();
                if (selectedTable != null) {
                    String nombreBd = inicio.getSelectedDatabase();
                    // Abre la ventana que muestra registros existentes
                    tablaRegistros ventanaRegistros = new tablaRegistros(nombreBd, selectedTable);
                    ventanaRegistros.setVisible(true);
                    DefaultTableModel tableModel = new DefaultTableModel() {
    @Override
    public boolean isCellEditable(int row, int column) {
        // Habilita la edición en todas las celdas (puedes personalizar esto según tus necesidades)
        return true;
    }
};

                    JTable table = new JTable(tableModel);
table.setAutoCreateRowSorter(true);
tableModel.addTableModelListener(new TableModelListener() {
    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (e.getType() == TableModelEvent.UPDATE) {
            Object data = tableModel.getValueAt(row, column);
            // Aquí puedes procesar los datos ingresados por el usuario (data)
        }
    }
});

                }
            }
        });

        // Botón "Describir Tabla"
        describirTablaButton = new JButton("Describir Tabla");
        describirTablaButton.setEnabled(false); // Inicialmente deshabilitado

        // ActionListener para el botón "Describir Tabla"
        describirTablaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedTable = listaTablas.getSelectedValue();
                if (selectedTable != null) {
                    String nombreBd = inicio.getSelectedDatabase();
                    String descripcion = paqueteria.describirTabla(nombreBd, selectedTable);
                    // Mostrar la descripción en un panel emergente
                    JOptionPane.showMessageDialog(inicio, descripcion, "Descripción de la Tabla", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Activa el botón "Describir Tabla" y "Ingresar Datos" cuando se selecciona una tabla
        listaTablas.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    describirTablaButton.setEnabled(true);
                    ingresarDatosButton.setEnabled(true);
                }
            }
        });

        // Agregar los componentes al panel derecho
        panelDerecho.setLayout(new BorderLayout());
        panelDerecho.add(listaTablas, BorderLayout.CENTER);

        // Crear un panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(describirTablaButton);
        buttonPanel.add(ingresarDatosButton);

        panelDerecho.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar el panel derecho al contenido de la ventana
        add(panelDerecho, BorderLayout.CENTER);

        // Cargar las tablas al iniciar la aplicación
        cargarTablas();
    }

    private void cargarTablas() {
        listModelTablas.clear();

        String selectedDb = inicio.getSelectedDatabase();

        if (selectedDb != null) {
            List<String> tablas = paqueteria.listarTablas(selectedDb);

            for (String nombreTabla : tablas) {
                listModelTablas.addElement(nombreTabla);
            }
        }
    }
public void onDatabaseSelected(String selectedDb) {
        listModelTablas.clear();
        if (selectedDb != null) {
            List<String> tablas = paqueteria.listarTablas(selectedDb);
            for (String nombreTabla : tablas) {
                listModelTablas.addElement(nombreTabla);
            }
            describirTablaButton.setEnabled(false); // Inicialmente deshabilita el botón "Describir Tabla"
            ingresarDatosButton.setEnabled(false); // Inicialmente deshabilita el botón "Ingresar Datos"
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

        });
    }
}
