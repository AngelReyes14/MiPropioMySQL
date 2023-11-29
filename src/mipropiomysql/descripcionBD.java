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
         setBackground(new Color(230, 204, 255));

        // Crear la instancia de Paqueteria y ajustar los valores de conexión según tus necesidades
        paqueteria = new Paqueteria("localhost", "root", "12345", 3306);
        int red = 148;
        int green = 184;
        int blue = 215;

        // Panel derecho para mostrar las tablas
        JPanel panelDerecho = new JPanel();
        panelDerecho.setPreferredSize(new Dimension(550, 600));
         // Crear un objeto Color con los valores RGB
        Color backgroundColor = new Color(red, green, blue);

        // Establecer el color de fondo del panel
        setBackground(backgroundColor);

        // Crear un modelo de lista para las tablas
        listModelTablas = new DefaultListModel<>();
        listaTablas = new JList<>(listModelTablas);

        // Botón "Ingresar Datos"
ingresarDatosButton = new RoundButton("Ingresar Datos", new Color(0x153f59), new Color(0x537491), Color.WHITE);
ingresarDatosButton.setEnabled(false);
ingresarDatosButton.setForeground(Color.WHITE); // Establecer color de texto


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
       describirTablaButton = new RoundButton("Describir Tabla", new Color(0x153f59), new Color(0x537491), Color.WHITE);
describirTablaButton.setEnabled(false);
describirTablaButton.setForeground(Color.WHITE); // Establecer color de texto


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
           // Colores para el fondo del panel de botones
int red2 = 148;
int green2 = 184;
int blue2 = 215;
Color buttonPanelColor = new Color(red2, green2, blue2);

// Crear un panel para los botones
JPanel buttonPanel = new JPanel();
buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
buttonPanel.setBackground(buttonPanelColor); // Establecer el color de fondo

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
