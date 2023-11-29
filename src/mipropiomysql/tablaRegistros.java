
package mipropiomysql;

import packeteria.Paqueteria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class tablaRegistros extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;
    private Paqueteria paqueteria;
    private String nombreBd;
    private String nombreTabla;
    private JLabel consultaLabel;

    public tablaRegistros(String nombreBd, String nombreTabla) {
        this.nombreBd = nombreBd;
        this.nombreTabla = nombreTabla;
        Color colorFondo = new Color(180, 212, 239);

        setTitle("Registros de " + nombreTabla);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        paqueteria = new Paqueteria("localhost", "root", "12345", 3306); // Ajusta estos valores

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);

        // Crear un panel para los botones
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(148, 184, 215));  // Cambia los valores RGB según tu preferencia
        panel.add(scrollPane, BorderLayout.CENTER);

        // Crear un panel para los botones
        JPanel botonesPanel = new JPanel();

        // Agregar los botones al panel de botones
        botonesPanel.add(crearBotonRedondo("Ingresar Registro", e -> mostrarDialogoIngresarRegistro()));
botonesPanel.add(crearBotonRedondo("Eliminar Registro", e -> eliminarRegistroSeleccionado()));
botonesPanel.add(crearBotonRedondo("Actualizar Registro", e -> mostrarDialogoActualizarRegistro()));

        // Agregar el panel de botones al panel principal
        panel.add(botonesPanel, BorderLayout.SOUTH);

        // Agregar el panel principal al JFrame
        add(panel);
        

        // Agrega los encabezados al modelo de la tabla una sola vez
        String[] columnNames = paqueteria.obtenerNombresColumnas(nombreBd, nombreTabla);
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setForeground(Color.WHITE);  // Cambia el color según tus preferencias
tableHeader.setBackground(new Color(0x557996)); // Reemplaza 255, 0, 0 con los componentes RGB del color deseado
table.setBackground(colorFondo);
        // Cargar los datos automáticamente al crear la ventana
        cargarDatos();
        configurarEventos();
    }

   private RoundButton crearBotonRedondo(String texto, ActionListener listener) {
    Color backgroundColor = new Color(0x153f59);
    Color hoverColor = new Color(0x537491);
    Color textColor = Color.WHITE;

    RoundButton button = new RoundButton(texto, backgroundColor, hoverColor, textColor);
    button.addActionListener(listener);
    return button;
}

    private void cargarDatos() {
        // Obtener registros existentes y agregarlos a la tabla
        List<String[]> registros = paqueteria.obtenerRegistros(nombreBd, nombreTabla);
        for (String[] registro : registros) {
            tableModel.addRow(registro);
        }
    }

    private void configurarEventos() {
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionAllowed(false);
                table.setColumnSelectionInterval(col, col);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    mostrarMenuContextual(e);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (row >= 0 && col >= 0) {
                        table.setColumnSelectionAllowed(false);
                        table.setRowSelectionAllowed(true);
                        table.setRowSelectionInterval(row, row);
                    }
                }
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // Agrega una nueva fila en la tabla
                    tableModel.addRow(new Object[tableModel.getColumnCount()]);
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // Obtener los datos de la última fila
                    int rowCount = tableModel.getRowCount();
                    if (rowCount > 0) {
                        String[] rowData = new String[tableModel.getColumnCount()];
                        for (int col = 0; col < tableModel.getColumnCount(); col++) {
                            rowData[col] = (String) tableModel.getValueAt(rowCount - 1, col);
                        }

                        // Guardar los datos en la base de datos
                        paqueteria.insertarRegistro(nombreBd, nombreTabla, rowData);
                    }
                }
            }
        });
    }

    private void mostrarMenuContextual(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem ordenarDeMayorAMenor = new JMenuItem("Ordenar de mayor a menor");
        JMenuItem ordenarDeMenorAMayor = new JMenuItem("Ordenar de menor a mayor");

        ordenarDeMayorAMenor.addActionListener(actionEvent -> ordenarTabla(true));
        ordenarDeMenorAMayor.addActionListener(actionEvent -> ordenarTabla(false));

        menu.add(ordenarDeMayorAMenor);
        menu.add(ordenarDeMenorAMayor);
        menu.show(table.getTableHeader(), e.getX(), e.getY());
    }

    private void ordenarTabla(boolean deMayorAMenor) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        sorter.setComparator(0, new AlphanumericComparator());
        sorter.sort();
    }
    // Método para mostrar el cuadro de diálogo de ingreso de registro
   private void mostrarDialogoIngresarRegistro() {
    JDialog dialog = new JDialog(this, "Ingresar Registro", true);
    dialog.setSize(400, 200);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setLocationRelativeTo(this);

    JPanel dialogPanel = new JPanel(new GridLayout(0, 2));

    // Crea campos de texto para ingresar datos
    String[] columnNames = paqueteria.obtenerNombresColumnas(nombreBd, nombreTabla);
    JTextField[] textFields = new JTextField[columnNames.length];

    for (int i = 0; i < columnNames.length; i++) {
        dialogPanel.add(new JLabel(columnNames[i] + ":"));
        textFields[i] = new JTextField();
        dialogPanel.add(textFields[i]);
    }

    JButton guardarButton = new JButton("Guardar");
    guardarButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] rowData = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                rowData[i] = textFields[i].getText();
            }

            // Mostrar la consulta SQL antes de realizar la inserción
            String query = paqueteria.generarInsertQuery(nombreTabla, columnNames, rowData);
            int confirmacion = JOptionPane.showConfirmDialog(dialog, "¿Seguro que quieres ingresar este registro?\nConsulta SQL:\n" + query, "Confirmar Registro", JOptionPane.YES_NO_OPTION);

            // Realizar la inserción si el usuario elige "Sí"
            if (confirmacion == JOptionPane.YES_OPTION) {
                tableModel.addRow(rowData);
                paqueteria.insertarRegistro(nombreBd, nombreTabla, rowData);
                dialog.dispose();
            }
        }
    });

    dialogPanel.add(guardarButton);
    dialog.add(dialogPanel);
    dialog.setVisible(true);
}

   
    // Método para eliminar el registro seleccionado
  private void eliminarRegistroSeleccionado() {
    int selectedRow = table.getSelectedRow();

    if (selectedRow >= 0) {
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String[] rowData = new String[tableModel.getColumnCount()];
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            rowData[col] = (String) tableModel.getValueAt(modelRow, col);
        }

        // Mostrar la consulta SQL antes de realizar la eliminación
        String query = paqueteria.generarDeleteQuery(nombreTabla, paqueteria.obtenerNombresColumnas(nombreBd, nombreTabla), rowData);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar el registro?\nConsulta SQL:\n" + query,
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exitoso = paqueteria.eliminarRegistro(nombreBd, nombreTabla, rowData);

            if (exitoso) {
                tableModel.removeRow(modelRow);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

 // Método para mostrar el cuadro de diálogo de actualización de registro
private void mostrarDialogoActualizarRegistro() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String[] rowData = new String[tableModel.getColumnCount()];
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            rowData[col] = (String) tableModel.getValueAt(modelRow, col);
        }

        // Obtener los nombres de las columnas
        String[] columnNames = paqueteria.obtenerNombresColumnas(nombreBd, nombreTabla);

        // Crear un array de Strings para almacenar los nuevos valores
        String[] newValues = new String[columnNames.length];

        // Solicitar al usuario que ingrese los nuevos valores
        for (int i = 0; i < columnNames.length; i++) {
            String newValue = JOptionPane.showInputDialog("Ingrese el nuevo valor para " + columnNames[i]);
            newValues[i] = newValue;
        }

        // Construir la condición WHERE (puedes personalizar según tus necesidades)
        String condition = columnNames[0] + " = '" + rowData[0] + "'";

        // Actualizar el registro en la base de datos
       // Actualizar el registro en la base de datos
// Actualizar el registro en la base de datos
boolean exitoso = paqueteria.actualizarRegistro(nombreBd, nombreTabla, columnNames, newValues, condition);

if (exitoso) {
    // Actualizar los datos en la tabla
    for (int col = 0; col < tableModel.getColumnCount(); col++) {
        tableModel.setValueAt(newValues[col], modelRow, col);
    }

    // Construir la consulta de actualización
    String updateQuery = paqueteria.construirConsultaUpdate(nombreTabla, columnNames, newValues, condition);

    // Mostrar mensaje de éxito con la consulta SQL
    int confirmacion = JOptionPane.showConfirmDialog(this, 
            "Registro actualizado correctamente.\n¿Seguro que deseas actualizar estos datos?\nConsulta SQL:\n" + updateQuery, 
            "Éxito", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE);

    // Si el usuario elige "Sí", imprimir la consulta SQL en la consola
    if (confirmacion == JOptionPane.YES_OPTION) {
        System.out.println("Consulta SQL:\n" + updateQuery);
    }
} else {
    // Mostrar mensaje de error
    JOptionPane.showMessageDialog(this, "Error al actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
}

    }
}





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            tablaRegistros ventana = new tablaRegistros("NombreBaseDeDatos", "NombreTabla");
            ventana.setVisible(true);
        });
    }
}
