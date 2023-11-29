package mipropiomysql;

import packeteria.Paqueteria;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Inicio extends JFrame {
    private DefaultListModel<String> listModel;
    protected JList<String> listaBD;
    private Conectar conectarPanel;
    private consultas consultasBD;
    private crearBD crearBD;
    private descripcionBD descripcionBD;
    private CardLayout cardLayout;
    private ImageIcon inicioImage; // Imagen para la pantalla de inicio
    private JPanel panelDerecho;
    private tablas tablasPanel;
    private Paqueteria paqueteria;



    public Inicio() {
        
         getContentPane().setBackground(new Color(255, 0, 0));
        setTitle("Mi propio gestor de base de datos");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setPreferredSize(new Dimension(250, 600));
        panelIzquierdo.setBackground(new Color(0x557996));
                conectarPanel = new Conectar(this);

        listModel = new DefaultListModel<>();
        listaBD = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(listaBD);
        scrollPane.setPreferredSize(new Dimension(230, 500));

        panelIzquierdo.add(scrollPane);
                panelDerecho = new JPanel();

        cardLayout = new CardLayout();
        panelDerecho.setPreferredSize(new Dimension(600, 600));
panelDerecho.setBackground(Color.decode("#FFFFFF"));  // Puedes cambiar este valor hexadecimal según tu preferencia
        conectarPanel = new Conectar(this);
        crearBD = new crearBD(this, conectarPanel);
        descripcionBD = new descripcionBD(this);
    descripcionBD.setLocation(0, 0);
consultasBD = new consultas(this);
    tablasPanel = new tablas(this);


panelDerecho.setLayout(cardLayout);
panelDerecho.add(conectarPanel, "ConectarPanel");
panelDerecho.add(crearBD, "Crear_Base_DatosPanel");
panelDerecho.add(descripcionBD, "descripcionBDPanel");
        panelDerecho.add(consultasBD, "Consultas");
            panelDerecho.add(tablasPanel, "Crear_TablasPanel");


// ... (código existente)

        panelDerecho.remove(descripcionBD);
panelDerecho.add(descripcionBD, "Bases_Datos_descPanel");
panelDerecho.revalidate();
panelDerecho.repaint();

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("GBD.jpg"));
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        inicioImage = resizedIcon;




         JPanel inicioPanel = new JPanel();
         inicioPanel.setBackground(new Color(148,184,215));
        inicioPanel.setLayout(new BorderLayout());
        JLabel imageLabel = new JLabel(inicioImage);
        inicioPanel.add(imageLabel, BorderLayout.CENTER);

        // Agregar el nuevo panel al panelDerecho existente (en lugar de crear una nueva instancia)
        panelDerecho.add(inicioPanel, "Inicio");
        panelDerecho.setBackground(Color.decode("#FFFFFF"));  // Puedes cambiar este valor hexadecimal según tu preferencia

        // Crear un nuevo panel para mostrar la imagen
   
 try {
            ImageIcon icono = new ImageIcon(getClass().getResource("DB.png"));
            Image xd = icono.getImage();
            Image resizedImage2 = xd.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon2 = new ImageIcon(resizedImage2);

            // Configurar el ícono del JFrame (usando esta instancia real de Inicio)
            setIconImage(resizedImage2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Configurar el texto en el panel derecho
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText("<html><p style='font-size: 18px; color: black; text-align: center;'>Bienvenido a esta aplicación MySQL.</p></html>");
        textPane.setEditable(false);
        panelDerecho.add(textPane);

        cardLayout.show(panelDerecho, "Inicio"); // Mostrar el panel "Inicio" al inicio

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opciones");

        JMenuItem inicio = new JMenuItem("Inicio");
        inicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelDerecho, "Inicio");
            }
        });
        menu.add(inicio);
        
        JMenuItem conectarMenuItem = new JMenuItem("Conectar");
        conectarMenuItem.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        System.out.println("ConectarMenuItem ActionListener ejecutado."); // Mensaje de depuración
        cardLayout.show(panelDerecho, "ConectarPanel"); // Mostrar el panel Conectar
    }
});

        menu.add(conectarMenuItem);

        JMenuItem crearBdMenuItem = new JMenuItem("Crear Base de Datos");
        crearBdMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        cardLayout.show(panelDerecho, "Crear_Base_DatosPanel"); // Mostrar el panel Conectar
            }
        });
        menu.add(crearBdMenuItem);

       JMenuItem crearTablasMenuItem = new JMenuItem("Crear Tablas");
crearTablasMenuItem.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        mostrarPanelTablas(); // Llama al método para mostrar el TablasPanel
    }
});

        
        
  
        JMenuItem basesDeDatosMenuItem = new JMenuItem("Bases de Datos");
    basesDeDatosMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(panelDerecho, "Bases_Datos_descPanel");
        }
    });
        

menu.add(basesDeDatosMenuItem);


        menu.add(crearTablasMenuItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
JMenuItem consultasMenuItem = new JMenuItem("Consultas");
consultasMenuItem.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        cardLayout.show(panelDerecho, "Consultas");
    }
});

listaBD.addListSelectionListener(new ListSelectionListener() {
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedDb = getSelectedDatabase();
            // Llama al método onDatabaseSelected en Bases_Datos_desc para cargar las tablas y habilitar los botones
            descripcionBD.onDatabaseSelected(selectedDb);
            // Añade esta línea para actualizar el nombre de la base de datos en el JTextField
            tablasPanel.actualizarNombreBaseDeDatos(selectedDb);
        }
    }
});





        menu.add(consultasMenuItem);
        
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
    listaBD.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedDb = getSelectedDatabase();
                    consultasBD.onDatabaseSelected(selectedDb);
                }
            }
        });

    }
     public void mostrarPanelDescripcionBD() {
        cardLayout.show(panelDerecho, "Bases_Datos_descPanel");
        panelDerecho.revalidate();
        panelDerecho.repaint();
    }

public void mostrarPanelTablas() {
    cardLayout.show(panelDerecho, "Crear_TablasPanel");
    panelDerecho.revalidate();
    panelDerecho.repaint();
}

 public JPanel getPanelDerecho() {
        return panelDerecho;
    }
 
  public void cargarBasesDeDatos(Paqueteria paqueteria) {
        listModel.clear();

        List<String> basesDeDatos = paqueteria.listarBasesDeDatos();

        for (String nombreBd : basesDeDatos) {
            listModel.addElement(nombreBd);
        }

        // Establecer el estado de conexión
    }
  
 public Conectar getConectarPanel() {
        return conectarPanel;
    }

public String getSelectedDatabase() {
    return listaBD.getSelectedValue();
}

    
public void mostrarPanel() {
    System.out.println("Mostrando panel en DescripcionBD");
    System.out.println("cardLayout: " + cardLayout);
    System.out.println("panelDerecho: " + panelDerecho);

    if (panelDerecho != null) {
        // Mostrar el panel "Bases_Datos_descPanel" y ocultar los demás
        conectarPanel.setVisible(false);
        crearBD.setVisible(false);
        descripcionBD.setVisible(true);
        consultasBD.setVisible(false);

        // Asegurar que el panelDerecho se repinte
        panelDerecho.revalidate();
        panelDerecho.repaint();
    } else {
        System.out.println("panelDerecho es nulo");
    }
}


    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Inicio app = new Inicio();

        // Seleccionar la primera base de datos al iniciar la aplicación
        app.setVisible(true);
    });
}

}
