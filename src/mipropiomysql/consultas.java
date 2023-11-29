package mipropiomysql;

import packeteria.Paqueteria;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class consultas extends JPanel {

    private JComboBox<String> tablaComboBox;
    private JPanel panelAtributos;
    private Paqueteria paqueteria;
    private Inicio inicio;
    private JTextArea resultadoConsulta;
    private JButton generarConsultaButton;
    private JTable resultadoEjecucion;
    private DefaultTableModel tableModel;
    private JTextField consultaTextField;

    public consultas(Inicio inicio) {
        this.inicio = inicio;
        paqueteria = new Paqueteria("localhost", "root", "12345", 3306);

        setLayout(new BorderLayout());
        setBackground(new Color(148, 184, 215)); 

        // Panel para los componentes en la línea superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(148, 184, 215)); // Azul real
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Texto para la consulta
        consultaTextField = new JTextField();
        consultaTextField.setFont(new Font("Arial", Font.BOLD, 16));
        consultaTextField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        consultaTextField.setHorizontalAlignment(JTextField.CENTER);
        consultaTextField.setBackground(new Color(255, 255, 255));
        consultaTextField.setEditable(false);
        consultaTextField.setForeground(Color.black);
        topPanel.add(consultaTextField, BorderLayout.CENTER);

        // Componentes para seleccionar la tabla
        tablaComboBox = new JComboBox<>();
        tablaComboBox.setBackground(new Color(255, 255, 255));
        tablaComboBox.setForeground(Color.black);
        tablaComboBox.setPreferredSize(new Dimension(150, 30));
        topPanel.add(tablaComboBox, BorderLayout.SOUTH);

        // Agregar el panel superior al BorderLayout
        add(topPanel, BorderLayout.NORTH);

        // Panel para los CheckBox de los atributos
        panelAtributos = new JPanel();
        panelAtributos.setBackground(new Color(148, 184, 215));
        panelAtributos.setLayout(new BoxLayout(panelAtributos, BoxLayout.Y_AXIS));
        panelAtributos.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(panelAtributos, BorderLayout.WEST);

        // Componente para mostrar el resultado de la consulta
        resultadoConsulta = new JTextArea();
        resultadoConsulta.setEditable(false);
        resultadoConsulta.setBackground(new Color(148, 184, 215)); // Fondo gris claro
        resultadoConsulta.setForeground(Color.black);
        resultadoConsulta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(resultadoConsulta), BorderLayout.CENTER);

        // Botón para generar y ejecutar la consulta
        // Cambia la declaración del botón a utilizar RoundButton
    // Botón para generar y ejecutar la consulta
generarConsultaButton = new RoundButton("Generar Consulta", new Color(0x153f59), new Color(0x537491), Color.WHITE);
generarConsultaButton.setFont(new Font("Arial", Font.BOLD, 16));
generarConsultaButton.setForeground(Color.white);
generarConsultaButton.setBorderPainted(false);
generarConsultaButton.setFocusPainted(false);
generarConsultaButton.setBounds(450, 400, 200, 40);

// Agrega el botón al panel
JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
panelBoton.add(generarConsultaButton);
add(panelBoton, BorderLayout.SOUTH);


    // Inicializar otras variables y componentes
    tableModel = new DefaultTableModel();
    resultadoEjecucion = new JTable(tableModel);

    // Luego, llamar a configurarEventos
    configurarEventos();
        // Componente para mostrar el resultado de la ejecución en una tabla
        tableModel = new DefaultTableModel();
        resultadoEjecucion = new JTable(tableModel) {
            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension d = super.getPreferredSize();
                        d.height = 40; // Altura del encabezado
                        return d;
                    }
                };
            }
        };
        JTableHeader header = resultadoEjecucion.getTableHeader();
        header.setBackground(new Color(0x557996)); // Azul real más oscuro
        header.setForeground(Color.BLACK);

        // Establecer el color de fondo y de letra para las celdas de la tabla
        resultadoEjecucion.setBackground(new Color(255,255,255)); // Blanco
        resultadoEjecucion.setForeground(Color.black);

        // Establecer el color de fondo y de letra para la selección de celdas
        resultadoEjecucion.setSelectionBackground(new Color(204, 153, 204)); // Azul pálido
        resultadoEjecucion.setSelectionForeground(Color.black);

        // Establecer el color de fondo y de letra para el texto del encabezado de las columnas
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(new Color(0x557996)); // Azul real más oscuro
                setForeground(Color.black);
                setFont(new Font("Arial", Font.BOLD, 15));
            }
        });

        // Agregar el componente al panel en la parte inferior
        add(new JScrollPane(resultadoEjecucion), BorderLayout.CENTER);

        // Manejar eventos de selección de la tabla
        tablaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarAtributos();
            }
        });

        cargarTablas();
        configurarEventos();
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
            String[] atributos = paqueteria.listarAtributos(selectedDb, selectedTable);

            for (String atributo : atributos) {
                JCheckBox checkBoxAtributo = new JCheckBox(atributo);
                checkBoxAtributo.setBackground(new Color(148, 184, 215));  // Azul real
                checkBoxAtributo.setForeground(Color.black);  // Color de letra
                panelAtributos.add(checkBoxAtributo);
            }

            panelAtributos.revalidate();
            panelAtributos.repaint();
        }
    }

    private void mostrarResultadosEnTable(ResultSet resultSet) {
        try {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                List<String> fila = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    fila.add(resultSet.getString(i));
                }
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
            consultaTextField.setText(consulta);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            mostrarResultadosEnTable(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
            resultadoConsulta.setText("Error al ejecutar la consulta: " + e.getMessage());
        }
    }

    private void generarYExecutarConsulta() {
        String selectedDb = inicio.getSelectedDatabase();
        String selectedTable = (String) tablaComboBox.getSelectedItem();

        if (selectedDb != null && selectedTable != null) {
            List<String> atributosSeleccionados = new ArrayList<>();
            Component[] components = panelAtributos.getComponents();
            boolean alMenosUnAtributoSeleccionado = false;

            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        atributosSeleccionados.add(checkBox.getText());
                        alMenosUnAtributoSeleccionado = true;
                    }
                }
            }

            if (!alMenosUnAtributoSeleccionado) {
                JOptionPane.showMessageDialog(this, "Consulta realizada", "Mensaje de Emergencia", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder consultaBuilder = new StringBuilder("SELECT ");
            for (String atributo : atributosSeleccionados) {
                consultaBuilder.append(atributo).append(", ");
            }
            consultaBuilder.delete(consultaBuilder.length() - 2, consultaBuilder.length());
            consultaBuilder.append(" FROM ").append(selectedTable);

            String consulta = consultaBuilder.toString();

            consultaTextField.setText(consulta);

            ejecutarConsulta(consulta, selectedDb, selectedTable);
        }
    }

    private void configurarEventos() {
        generarConsultaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarYExecutarConsulta();
                cargarAtributos(); // Agregar esta línea para cargar atributos al presionar el botón
            }
        });

        // Agregar el botón al panel en la parte inferior derecha
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(generarConsultaButton);
        add(panelBoton, BorderLayout.SOUTH);

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