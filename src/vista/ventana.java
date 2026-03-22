
package vista;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import perfil.TipoUsuario;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import Invetario.Articulo;
import venta.Atencion;
import Invetario.Empenho;
import venta.TipoAtencion;
import perfil.Usuario;


public class ventana extends javax.swing.JFrame {
    private ArrayList<Usuario> misUsuarios;
    private ArrayList<Articulo> misArticulos;
    private ArrayList<Atencion> misVentas;
    private ArrayList<Empenho> misEmpenhos;
    private int idAsignableArticulo = 0;
    private int idAsignableVentas = 0;
    private int idAsignableEmpenhos = 0;
    
    public ventana() {
        initAtributes();
        initComponents();
        cargarArticulos();
        cargarAtenciones();
        initComboBox();
        initListaDevolucion();
        initInventario();
    }
    
    private void initInventario(){
        String linea = ""; 
        DefaultTableModel modelo = (DefaultTableModel) inventario.getModel();
        
        while(modelo.getRowCount() > 0) modelo.removeRow(0);
        
        for(Articulo a : misArticulos){
            Object[] fila = {String.valueOf(a.getId()), a.getNombre(), String.valueOf(a.getPrecio()), String.valueOf(a.getCantidad())};
            modelo.addRow(fila);
        }
    }
    
    public void initComboBox(){
        DefaultComboBoxModel vendibles = (DefaultComboBoxModel) articulosVendibles.getModel();
        DefaultComboBoxModel empenhables = (DefaultComboBoxModel) articulosEmpenhables.getModel();
        
        for(Articulo a : misArticulos){
            System.out.println(a.isVendible());
            if(a.getCantidad() > 0){
                if(a.isVendible()){
                    vendibles.addElement(a.getNombre());
                    System.out.println("Agregado "+a.getNombre());
                }
                empenhables.addElement(a.getNombre());
            }
        }
    }
    
    private void initAtributes(){
        misUsuarios = new ArrayList<>();
        misArticulos = new ArrayList<>();
        misVentas = new ArrayList<>();
        misEmpenhos = new ArrayList<>();
    }
    
    private void cerrarSesion(){
        admin.setSelectedIndex(0);
        atencion.setSelectedIndex(0);
        
        id.setText("");
        clave.setText("");
        vistas.setSelectedIndex(0);
    }
    
    private void cargarUsuarios() {                                    
        File f = new File("usuarios.txt");
        FileReader fr = null;
        BufferedReader br = null;

        try{
            fr = new FileReader(f);
            br = new BufferedReader(fr);
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Error al conectarse con la base de datos.");
        }

        String linea = ""; 
        DefaultTableModel modelo = (DefaultTableModel) listaUsuarios.getModel();
        
        while(modelo.getRowCount() > 0) modelo.removeRow(0);
        
        try {
            while((linea = br.readLine()) != null){
                String[] usuario = linea.split(";");
                System.out.println(linea);
                Object[] fila = {usuario[0], (Integer.parseInt(usuario[2]) == TipoUsuario.ADMIN.ordinal()? "Administrador" : "Atención al cliente" )};
                modelo.addRow(fila);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
     private void initListaVentas() {
        
        String linea = "", fechaRegistro = "", cliente = ""; 
        double valorTotal = 0.0;
        boolean vencido = false, devuelto = false;
        int id = 0;
        DefaultTableModel modelo = (DefaultTableModel) ventasPorFecha.getModel();
        
        while(modelo.getRowCount() > 0) modelo.removeRow(0);
        
        
        for(int i = 0; i < idAsignableVentas; i++){
            valorTotal = 0;
            for(int j = 0; j < misVentas.size(); j++){
                if(i == misVentas.get(j).getId()){
                    valorTotal += misVentas.get(j).getPrecio() * misVentas.get(j).getCantidad();
                    cliente = misVentas.get(j).getCliente();
                    fechaRegistro = misVentas.get(j).getFecha();
                    id = misVentas.get(j).getId();
                }
                
                
            }
            Object[] fila = {String.valueOf(i), cliente, fechaRegistro,String.valueOf(valorTotal)};
            modelo.addRow(fila);
            
            pack();
        }
    }
    
    private void initListaDevolucion() {
        
        String linea = "", fechaRegistro = "", fechaDevolucion = "", cliente = ""; 
        double valorTotal = 0.0;
        boolean vencido = false, devuelto = false;
        DefaultTableModel modelo = (DefaultTableModel) listaDevolucion.getModel();
        DefaultTableModel modeloEmpenhos = (DefaultTableModel) empenhosPorFecha.getModel();
        DefaultTableModel ModeloVencidos = (DefaultTableModel) empenhadosVencidos.getModel();
        
        while(modelo.getRowCount() > 0) modelo.removeRow(0);
        while(ModeloVencidos.getRowCount() > 0) ModeloVencidos.removeRow(0);
        while(modeloEmpenhos.getRowCount() > 0) modeloEmpenhos.removeRow(0);
        
        
        for(int i = 0; i < idAsignableEmpenhos; i++){
            valorTotal = 0;
            for(int j = 0; j < misEmpenhos.size(); j++){
                if(i == misEmpenhos.get(j).getId()){
                    valorTotal += misEmpenhos.get(j).getPrecio() * misEmpenhos.get(j).getCantidad();
                    cliente = misEmpenhos.get(j).getCliente();
                    fechaDevolucion = misEmpenhos.get(j).getFechaDevolucion();
                    fechaRegistro = misEmpenhos.get(j).getFecha();
                    vencido = misEmpenhos.get(j).isVencido();
                    devuelto = misEmpenhos.get(j).isDevuelto();
                }
                
                
                if(vencido){
                    Object[] f = {misEmpenhos.get(i).getArticulo(), misEmpenhos.get(j).getPrecio(), misEmpenhos.get(j).getCantidad()};
                    ModeloVencidos.addRow(f);
                }
                
            }
            Object[] fila = {String.valueOf(i), cliente, fechaRegistro, fechaDevolucion, String.valueOf(valorTotal),
                (vencido? "SI" : "NO" ), (devuelto? "SI" : "NO" )};
            
           
            
            modelo.addRow(fila);
            modeloEmpenhos.addRow(fila);
        }
        
        pack();
        initListaVentas();
    }
    
    private void cargarArticulos() {                                    
        File f = new File("articulos.txt");
        FileReader fr = null;
        BufferedReader br = null;

        try{
            fr = new FileReader(f);
            br = new BufferedReader(fr);
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Error al conectarse con la base de datos.");
        }

       
        String linea = "";
        try {
            while((linea = br.readLine()) != null){
                String[] articulo = linea.split(";");
                System.out.println(linea);
                idAsignableArticulo = Integer.parseInt(articulo[0]) + 1;
                misArticulos.add(new Articulo(Integer.parseInt(articulo[0]), articulo[1], Integer.parseInt(articulo[2]), Double.parseDouble(articulo[3]), Boolean.parseBoolean(articulo[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void cargarAtenciones() {                                    
        File f = new File("ventas.txt");
        FileReader fr = null;
        BufferedReader br = null;

        try{
            fr = new FileReader(f);
            br = new BufferedReader(fr);
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Error al conectarse con la base de datos.");
        }

        //id ; nombre ; cantidad ; precio
        String linea = "";
        try {
            while((linea = br.readLine()) != null){
                String[] atencion = linea.split(";");
                System.out.println("VENTA: "+linea);
                idAsignableVentas = Integer.parseInt(atencion[0]) + 1;
                System.out.println("ID DE VENTAS: "+idAsignableVentas);
                
                misVentas.add(new Atencion(Integer.parseInt(atencion[0]), TipoAtencion.VENDER,
                        atencion[2],   atencion[3], Integer.parseInt(atencion[4]), Double.parseDouble(atencion[5]), atencion[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        File f2 = new File("empenhos.txt");
        FileReader fr2 = null;
        BufferedReader br2 = null;

        try{
            fr2 = new FileReader(f2);
            br2 = new BufferedReader(fr2);
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Error al conectarse con la base de datos.");
        }
        
        try {
            while((linea = br2.readLine()) != null){
                String[] atencion = linea.split(";");
                System.out.println("EMPENHO: "+linea);
                idAsignableEmpenhos = Integer.parseInt(atencion[0]) + 1;
                
                misEmpenhos.add(new Empenho(Integer.parseInt(atencion[0]), TipoAtencion.EMPENHAR,
                        atencion[2],   atencion[3], Integer.parseInt(atencion[4]), Double.parseDouble(atencion[5]), atencion[6] ,
                        atencion[7], Boolean.parseBoolean(atencion[8]), Boolean.parseBoolean(atencion[9]) ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private boolean existeUsuario(String id) {
        DefaultTableModel modelo = (DefaultTableModel) listaUsuarios.getModel();
        
        for(int i = 0; i < modelo.getRowCount(); i++){
            if(modelo.getValueAt(i, 0).toString().equalsIgnoreCase(id))
                return true;
        }
        
        return false;
    }
    
    private int existeProductoVendible(String id) {
        DefaultTableModel modelo = (DefaultTableModel) listaVender.getModel();
        
        for(int i = 0; i < modelo.getRowCount(); i++){
            if(modelo.getValueAt(i, 0).toString().equalsIgnoreCase(id))
                return i;
        }
        
        return -1;
    }
    
    private int existeProductoEmpenhable(String id) {
        DefaultTableModel modelo = (DefaultTableModel) listaEmpenhar.getModel();
        
        for(int i = 0; i < modelo.getRowCount(); i++){
            if(modelo.getValueAt(i, 0).toString().equalsIgnoreCase(id))
                return i;
        }
        
        return -1;
    }
    
    private boolean existeArticulo(String nombre) {
        for(Articulo a : misArticulos){
            if(a.getNombre().equalsIgnoreCase(nombre))
                return true;
        }
        
        return false;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vistas = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        entrar = new javax.swing.JButton();
        id = new javax.swing.JTextField();
        clave = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        salir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        admin = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        cerrar1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        reportes = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        saludo1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel42 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        idCrear = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnCrear = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        claveCrear = new javax.swing.JPasswordField();
        jLabel19 = new javax.swing.JLabel();
        tipoCrear = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaUsuarios = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        usuarios = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cerrar2 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        saludo2 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel31 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        inventario = new javax.swing.JTable();
        jPanel33 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        empenhadosVencidos = new javax.swing.JTable();
        jPanel43 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        ventasPorFecha = new javax.swing.JTable();
        jPanel44 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        empenhosPorFecha = new javax.swing.JTable();
        atencion = new javax.swing.JTabbedPane();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        cerrar3 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        comprar = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        empenhar1 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        vender1 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        saludo3 = new javax.swing.JLabel();
        comprarArticulo = new javax.swing.JButton();
        cantidad = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        precio = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        articulo = new javax.swing.JTextField();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel30 = new javax.swing.JLabel();
        vendible = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        cerrar4 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        comprar2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        empenhar2 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        vender2 = new javax.swing.JLabel();
        vender = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        saludo4 = new javax.swing.JLabel();
        btnCarritoVender = new javax.swing.JButton();
        btnRegistrarVenta = new javax.swing.JButton();
        cantidadVender = new javax.swing.JTextField();
        jSeparator17 = new javax.swing.JSeparator();
        jSeparator18 = new javax.swing.JSeparator();
        precioVender = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jSeparator19 = new javax.swing.JSeparator();
        clienteVender = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        listaVender = new javax.swing.JTable();
        articulosVendibles = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        unidadesDisponiblesVender = new javax.swing.JTextField();
        jSeparator20 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        empenhar = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        cerrar5 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        comprar3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        empenhar3 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        vender3 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        jPanel40 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        saludo5 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel45 = new javax.swing.JPanel();
        btnRegistrarEmpenho = new javax.swing.JButton();
        jLabel50 = new javax.swing.JLabel();
        articulosEmpenhables = new javax.swing.JComboBox<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        listaEmpenhar = new javax.swing.JTable();
        jSeparator25 = new javax.swing.JSeparator();
        cantidadEmpenhar = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jSeparator26 = new javax.swing.JSeparator();
        precioEmpenhar = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jSeparator21 = new javax.swing.JSeparator();
        unidadesDisponiblesEmpenhar = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        btnCarritoEmpenhar = new javax.swing.JButton();
        clienteEmpenhar = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        clienteEmpenhar1 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jSeparator24 = new javax.swing.JSeparator();
        jLabel51 = new javax.swing.JLabel();
        jSeparator27 = new javax.swing.JSeparator();
        fechaDevolucion = new javax.swing.JFormattedTextField();
        jPanel46 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listaDevolucion = new javax.swing.JTable();
        jLabel52 = new javax.swing.JLabel();
        idEmpenho = new javax.swing.JTextField();
        jSeparator28 = new javax.swing.JSeparator();
        btnRegistrarDevolucion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Ventum");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(1019, 576));
        setMinimumSize(new java.awt.Dimension(1019, 576));
        setPreferredSize(new java.awt.Dimension(1019, 576));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vistas.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        vistas.setMaximumSize(new java.awt.Dimension(1129, 565));
        vistas.setMinimumSize(new java.awt.Dimension(1129, 565));
        vistas.setPreferredSize(new java.awt.Dimension(1129, 565));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/store_shop_building_ecommerce_icon_124608.png"))); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, 280, 320));

        jLabel1.setFont(new java.awt.Font("Chalkduster", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(21, 101, 192));
        jLabel1.setText("Venta");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 160, 60));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 520, 530));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Nombre de usuario");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 210, 300, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Contraseña");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 320, 300, 40));

        entrar.setBackground(new java.awt.Color(255, 255, 255));
        entrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        entrar.setText("Iniciar sesión");
        entrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                entrarMouseClicked(evt);
            }
        });
        jPanel1.add(entrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 440, -1, -1));

        id.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        id.setForeground(new java.awt.Color(21, 101, 192));
        id.setBorder(null);
        id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idActionPerformed(evt);
            }
        });
        jPanel1.add(id, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 240, 310, 30));

        clave.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clave.setForeground(new java.awt.Color(21, 101, 192));
        clave.setBorder(null);
        jPanel1.add(clave, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 350, 310, 30));

        jSeparator1.setForeground(new java.awt.Color(21, 101, 192));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, 310, 10));

        jSeparator2.setForeground(new java.awt.Color(21, 101, 192));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 270, 310, 10));

        salir.setBackground(new java.awt.Color(255, 255, 255));
        salir.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        salir.setText("Salir");
        salir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salirMouseClicked(evt);
            }
        });
        jPanel1.add(salir, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 440, 140, -1));

        jLabel2.setFont(new java.awt.Font("Chalkduster", 0, 36)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/conversation_chat_deal_agreement_icon_124665.png"))); // NOI18N
        jLabel2.setText("BIENVENIDO");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, 430, 140));

        vistas.addTab("log", jPanel1);

        admin.setTabPlacement(javax.swing.JTabbedPane.RIGHT);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setBackground(new java.awt.Color(30, 136, 229));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cerrar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cerrar1.setForeground(new java.awt.Color(255, 255, 255));
        cerrar1.setText("     Cerrar sesión");
        cerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrar1MouseClicked(evt);
            }
        });
        jPanel16.add(cerrar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel3.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 509, 170, 40));

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("          Usuarios");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel8.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 40));

        jPanel5.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 40));

        jPanel9.setBackground(new java.awt.Color(172, 172, 195));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        reportes.setBackground(new java.awt.Color(30, 136, 229));
        reportes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportes.setForeground(new java.awt.Color(204, 204, 204));
        reportes.setText("          Reportes");
        reportes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        reportes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportesMouseClicked(evt);
            }
        });
        jPanel9.add(reportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        jPanel5.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 40));

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("      Administrador");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 170, 50));

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 550));

        jPanel6.setBackground(new java.awt.Color(227, 242, 253));
        jPanel6.setToolTipText("Administración de usuarios");
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setBackground(new java.awt.Color(227, 242, 253));
        jLabel8.setFont(new java.awt.Font("Chalkduster", 1, 36)); // NOI18N
        jLabel8.setText("PERFILES");
        jPanel10.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 240, -1));

        saludo1.setBackground(new java.awt.Color(227, 242, 253));
        saludo1.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        saludo1.setForeground(new java.awt.Color(21, 101, 192));
        jPanel10.add(saludo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 30));

        jTabbedPane1.setBackground(new java.awt.Color(102, 102, 102));
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jPanel42.setBackground(new java.awt.Color(204, 204, 204));
        jPanel42.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel42.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator3.setForeground(new java.awt.Color(21, 101, 192));
        jPanel42.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 250, 10));

        idCrear.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        idCrear.setForeground(new java.awt.Color(21, 101, 192));
        idCrear.setBorder(null);
        idCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idCrearActionPerformed(evt);
            }
        });
        jPanel42.add(idCrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 250, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Tipo de usuario");
        jPanel42.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 20, 190, 40));

        btnCrear.setBackground(new java.awt.Color(255, 255, 255));
        btnCrear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCrear.setText("Crear usuario");
        btnCrear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCrearMouseClicked(evt);
            }
        });
        jPanel42.add(btnCrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 160, 40));

        jSeparator4.setForeground(new java.awt.Color(21, 101, 192));
        jPanel42.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 250, 10));

        claveCrear.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        claveCrear.setForeground(new java.awt.Color(21, 101, 192));
        claveCrear.setBorder(null);
        jPanel42.add(claveCrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 250, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Contraseña");
        jPanel42.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 140, 40));

        tipoCrear.setBackground(new java.awt.Color(187, 222, 251));
        tipoCrear.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tipoCrear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Usuario", "Administrador" }));
        tipoCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoCrearActionPerformed(evt);
            }
        });
        jPanel42.add(tipoCrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 70, 180, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setText("Nombre de usuario");
        jPanel42.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 190, 40));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/person_account_file_folder_document_icon_124634.png"))); // NOI18N
        jPanel42.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 120, 260, 240));

        jTabbedPane1.addTab("Crear", jPanel42);

        jPanel41.setBackground(new java.awt.Color(102, 102, 102));
        jPanel41.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listaUsuarios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre de usuario", "Tipo de usuario"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        listaUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaUsuarios.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane1.setViewportView(listaUsuarios);

        jPanel41.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 770, 340));

        jTabbedPane1.addTab("Usuarios registrados", jPanel41);

        jPanel10.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 820, 420));

        jPanel6.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 550));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 840, 550));

        admin.addTab("m", jPanel3);

        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(153, 153, 153));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel12.setBackground(new java.awt.Color(172, 172, 195));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usuarios.setBackground(new java.awt.Color(30, 136, 229));
        usuarios.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        usuarios.setForeground(new java.awt.Color(227, 242, 253));
        usuarios.setText("          Usuarios");
        usuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usuariosMouseClicked(evt);
            }
        });
        jPanel12.add(usuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        jPanel11.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 40));

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setBackground(new java.awt.Color(30, 136, 229));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("          Reportes");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel13.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel11.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 40));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(227, 242, 253));
        jLabel12.setText("    Administrador");
        jPanel11.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 170, 50));

        jPanel4.setBackground(new java.awt.Color(30, 136, 229));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cerrar2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cerrar2.setForeground(new java.awt.Color(227, 242, 253));
        cerrar2.setText("     Cerrar sesión");
        cerrar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrar2MouseClicked(evt);
            }
        });
        jPanel4.add(cerrar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel11.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 509, 170, 40));

        jPanel7.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 550));

        jPanel14.setBackground(new java.awt.Color(227, 242, 253));
        jPanel14.setToolTipText("Administración de usuarios");
        jPanel14.setOpaque(false);
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(204, 204, 204));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setBackground(new java.awt.Color(227, 242, 253));
        jLabel13.setFont(new java.awt.Font("Chalkduster", 1, 36)); // NOI18N
        jLabel13.setText("  Reportes");
        jPanel15.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 320, -1));

        saludo2.setBackground(new java.awt.Color(227, 242, 253));
        saludo2.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        saludo2.setForeground(new java.awt.Color(21, 101, 192));
        jPanel15.add(saludo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 30));

        jTabbedPane2.setBackground(new java.awt.Color(187, 222, 251));
        jTabbedPane2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jPanel31.setBackground(new java.awt.Color(102, 102, 102));
        jPanel31.setForeground(new java.awt.Color(21, 101, 192));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inventario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        inventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Artículo", "Precio", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        inventario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        inventario.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane6.setViewportView(inventario);
        if (inventario.getColumnModel().getColumnCount() > 0) {
            inventario.getColumnModel().getColumn(0).setHeaderValue("Id");
        }

        jPanel31.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 340));

        jTabbedPane2.addTab("Inventario", jPanel31);

        jPanel33.setBackground(new java.awt.Color(102, 102, 102));
        jPanel33.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        empenhadosVencidos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        empenhadosVencidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Precio", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        empenhadosVencidos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        empenhadosVencidos.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane9.setViewportView(empenhadosVencidos);

        jPanel33.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 340));

        jTabbedPane2.addTab("Articulos empeñados vencidos", jPanel33);

        jPanel43.setBackground(new java.awt.Color(102, 102, 102));
        jPanel43.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ventasPorFecha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ventasPorFecha.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id.", "Cliente", "Fecha de empeño", "Valor total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        ventasPorFecha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ventasPorFecha.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane12.setViewportView(ventasPorFecha);
        if (ventasPorFecha.getColumnModel().getColumnCount() > 0) {
            ventasPorFecha.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel43.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 340));

        jTabbedPane2.addTab("Ventas por fechas", jPanel43);

        jPanel44.setBackground(new java.awt.Color(102, 102, 102));
        jPanel44.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        empenhosPorFecha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        empenhosPorFecha.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id.", "Cliente", "Fecha de empeño", "Fecha de evolución", "Valor total", "Vencido", "Devuelto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        empenhosPorFecha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        empenhosPorFecha.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane11.setViewportView(empenhosPorFecha);
        if (empenhosPorFecha.getColumnModel().getColumnCount() > 0) {
            empenhosPorFecha.getColumnModel().getColumn(2).setResizable(false);
            empenhosPorFecha.getColumnModel().getColumn(3).setHeaderValue("Fecha de evolución");
            empenhosPorFecha.getColumnModel().getColumn(5).setHeaderValue("Vencido");
            empenhosPorFecha.getColumnModel().getColumn(6).setHeaderValue("Devuelto");
        }

        jPanel44.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 770, 340));

        jTabbedPane2.addTab("Empeños por fecha", jPanel44);

        jPanel15.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 810, 410));

        jPanel14.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 550));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 840, 550));

        admin.addTab("r", jPanel7);

        vistas.addTab("ad", admin);

        atencion.setTabPlacement(javax.swing.JTabbedPane.RIGHT);

        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(30, 136, 229));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cerrar3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cerrar3.setForeground(new java.awt.Color(227, 242, 253));
        cerrar3.setText("     Cerrar sesión");
        cerrar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrar3MouseClicked(evt);
            }
        });
        jPanel18.add(cerrar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel17.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 509, 170, 40));

        jPanel19.setBackground(new java.awt.Color(153, 153, 153));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel20.setBackground(new java.awt.Color(204, 204, 204));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        comprar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        comprar.setText("Comprar Artículo");
        comprar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel20.add(comprar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel19.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 40));

        jLabel10.setBackground(new java.awt.Color(21, 101, 192));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Usuario");
        jPanel19.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 50));

        jPanel21.setBackground(new java.awt.Color(172, 172, 195));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        empenhar1.setBackground(new java.awt.Color(30, 136, 229));
        empenhar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        empenhar1.setForeground(new java.awt.Color(227, 242, 253));
        empenhar1.setText("Empeñar Artículo");
        empenhar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        empenhar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                empenhar1MouseClicked(evt);
            }
        });
        jPanel21.add(empenhar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel19.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 40));

        jPanel24.setBackground(new java.awt.Color(172, 172, 195));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vender1.setBackground(new java.awt.Color(30, 136, 229));
        vender1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        vender1.setForeground(new java.awt.Color(227, 242, 253));
        vender1.setText("Vender Artículo");
        vender1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        vender1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vender1MouseClicked(evt);
            }
        });
        jPanel24.add(vender1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel19.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 40));

        jPanel17.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 550));

        jPanel22.setBackground(new java.awt.Color(227, 242, 253));
        jPanel22.setToolTipText("Administración de usuarios");
        jPanel22.setOpaque(false);
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel23.setBackground(new java.awt.Color(204, 204, 204));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setBackground(new java.awt.Color(227, 242, 253));
        jLabel14.setFont(new java.awt.Font("Chalkduster", 1, 24)); // NOI18N
        jLabel14.setText(" Comprar Artículo");
        jPanel23.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, 360, -1));

        saludo3.setBackground(new java.awt.Color(227, 242, 253));
        saludo3.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        saludo3.setForeground(new java.awt.Color(21, 101, 192));
        jPanel23.add(saludo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 30));

        comprarArticulo.setBackground(new java.awt.Color(21, 101, 192));
        comprarArticulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        comprarArticulo.setText("Registrar Artículo");
        comprarArticulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                comprarArticuloMouseClicked(evt);
            }
        });
        jPanel23.add(comprarArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 500, 220, -1));

        cantidad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cantidad.setForeground(new java.awt.Color(21, 101, 192));
        cantidad.setBorder(null);
        cantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantidadActionPerformed(evt);
            }
        });
        jPanel23.add(cantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, 190, 30));

        jSeparator9.setForeground(new java.awt.Color(21, 101, 192));
        jPanel23.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 330, 190, 10));

        jSeparator10.setForeground(new java.awt.Color(21, 101, 192));
        jPanel23.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 440, 210, 10));

        precio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        precio.setForeground(new java.awt.Color(21, 101, 192));
        precio.setBorder(null);
        precio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precioActionPerformed(evt);
            }
        });
        jPanel23.add(precio, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, 210, 30));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setText("Precio");
        jPanel23.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, 190, 40));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setText("Cantidad");
        jPanel23.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 190, 40));

        articulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        articulo.setForeground(new java.awt.Color(21, 101, 192));
        articulo.setBorder(null);
        articulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                articuloActionPerformed(evt);
            }
        });
        jPanel23.add(articulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 190, 30));

        jSeparator12.setForeground(new java.awt.Color(21, 101, 192));
        jPanel23.add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 190, 10));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel30.setText("Artículo");
        jPanel23.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 190, 40));

        vendible.setBackground(new java.awt.Color(227, 242, 253));
        vendible.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        vendible.setText("Vendible");
        jPanel23.add(vendible, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 440, 180, -1));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/person_account_profile_graph_chart_arrow_icon_124633.png"))); // NOI18N
        jPanel23.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 60, 270, 240));

        jPanel22.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 550));

        jPanel17.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 840, 550));

        atencion.addTab("c", jPanel17);

        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel26.setBackground(new java.awt.Color(30, 136, 229));
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cerrar4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cerrar4.setForeground(new java.awt.Color(227, 242, 253));
        cerrar4.setText("     Cerrar sesión");
        cerrar4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrar4MouseClicked(evt);
            }
        });
        jPanel26.add(cerrar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel25.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 509, 170, 40));

        jPanel27.setBackground(new java.awt.Color(153, 153, 153));
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel28.setBackground(new java.awt.Color(172, 172, 195));
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        comprar2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        comprar2.setForeground(new java.awt.Color(227, 242, 253));
        comprar2.setText("Comprar Artículo");
        comprar2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        comprar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                comprar2MouseClicked(evt);
            }
        });
        jPanel28.add(comprar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 40));

        jPanel27.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 40));

        jLabel15.setBackground(new java.awt.Color(21, 101, 192));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(227, 242, 253));
        jLabel15.setText("Usuario");
        jPanel27.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 120, 50));

        jPanel29.setBackground(new java.awt.Color(172, 172, 195));
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        empenhar2.setBackground(new java.awt.Color(30, 136, 229));
        empenhar2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        empenhar2.setForeground(new java.awt.Color(227, 242, 253));
        empenhar2.setText("Empeñar Artículo");
        empenhar2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        empenhar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                empenhar2MouseClicked(evt);
            }
        });
        jPanel29.add(empenhar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel27.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 40));

        jPanel30.setBackground(new java.awt.Color(204, 204, 204));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vender2.setBackground(new java.awt.Color(30, 136, 229));
        vender2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        vender2.setText("Vender Artículo");
        vender2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        vender2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vender2MouseClicked(evt);
            }
        });
        jPanel30.add(vender2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel27.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 40));

        jPanel25.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 550));

        vender.setBackground(new java.awt.Color(227, 242, 253));
        vender.setToolTipText("Administración de usuarios");
        vender.setOpaque(false);
        vender.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel32.setBackground(new java.awt.Color(204, 204, 204));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setBackground(new java.awt.Color(227, 242, 253));
        jLabel16.setFont(new java.awt.Font("Chalkduster", 1, 36)); // NOI18N
        jLabel16.setText(" Vender Artículo");
        jPanel32.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 390, -1));

        saludo4.setBackground(new java.awt.Color(227, 242, 253));
        saludo4.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        saludo4.setForeground(new java.awt.Color(21, 101, 192));
        jPanel32.add(saludo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 30));

        btnCarritoVender.setBackground(new java.awt.Color(255, 255, 255));
        btnCarritoVender.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCarritoVender.setText("Agregar");
        btnCarritoVender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCarritoVenderMouseClicked(evt);
            }
        });
        jPanel32.add(btnCarritoVender, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 250, 110, -1));

        btnRegistrarVenta.setBackground(new java.awt.Color(255, 255, 255));
        btnRegistrarVenta.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrarVenta.setText("Registrar Venta");
        btnRegistrarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarVentaMouseClicked(evt);
            }
        });
        jPanel32.add(btnRegistrarVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(311, 490, 230, -1));

        cantidadVender.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cantidadVender.setForeground(new java.awt.Color(21, 101, 192));
        cantidadVender.setBorder(null);
        cantidadVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantidadVenderActionPerformed(evt);
            }
        });
        jPanel32.add(cantidadVender, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 250, 110, 30));

        jSeparator17.setForeground(new java.awt.Color(21, 101, 192));
        jPanel32.add(jSeparator17, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 280, 110, 10));

        jSeparator18.setForeground(new java.awt.Color(21, 101, 192));
        jPanel32.add(jSeparator18, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 280, 160, 10));

        precioVender.setEditable(false);
        precioVender.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        precioVender.setForeground(new java.awt.Color(21, 101, 192));
        precioVender.setBorder(null);
        precioVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precioVenderActionPerformed(evt);
            }
        });
        jPanel32.add(precioVender, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 250, 160, 30));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setText("Precio");
        jPanel32.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 160, 40));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel37.setText("Cantidad");
        jPanel32.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, 110, 40));

        jSeparator19.setForeground(new java.awt.Color(21, 101, 192));
        jPanel32.add(jSeparator19, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, 380, 10));

        clienteVender.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clienteVender.setForeground(new java.awt.Color(21, 101, 192));
        clienteVender.setBorder(null);
        clienteVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteVenderActionPerformed(evt);
            }
        });
        jPanel32.add(clienteVender, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 380, 30));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel38.setText("Cliente");
        jPanel32.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 190, 40));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel40.setText("Artículo");
        jPanel32.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 190, 40));

        listaVender.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listaVender.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Precio", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        listaVender.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaVender.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane5.setViewportView(listaVender);

        jPanel32.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 770, 180));

        articulosVendibles.setBackground(new java.awt.Color(187, 222, 251));
        articulosVendibles.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        articulosVendibles.setForeground(new java.awt.Color(21, 101, 192));
        articulosVendibles.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                articulosVendiblesItemStateChanged(evt);
            }
        });
        articulosVendibles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                articulosVendiblesActionPerformed(evt);
            }
        });
        jPanel32.add(articulosVendibles, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 170, -1));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel39.setText("Unid. Disponibles");
        jPanel32.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 210, 160, 40));

        unidadesDisponiblesVender.setEditable(false);
        unidadesDisponiblesVender.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        unidadesDisponiblesVender.setForeground(new java.awt.Color(21, 101, 192));
        unidadesDisponiblesVender.setBorder(null);
        unidadesDisponiblesVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unidadesDisponiblesVenderActionPerformed(evt);
            }
        });
        jPanel32.add(unidadesDisponiblesVender, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 250, 160, 30));

        jSeparator20.setForeground(new java.awt.Color(21, 101, 192));
        jPanel32.add(jSeparator20, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 280, 160, 10));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buy_click_button_hand_gesture_icon_124675.png"))); // NOI18N
        jPanel32.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 150, 140));

        vender.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 550));

        jPanel25.add(vender, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 840, 550));

        atencion.addTab("v", jPanel25);

        empenhar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel34.setBackground(new java.awt.Color(30, 136, 229));
        jPanel34.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cerrar5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cerrar5.setForeground(new java.awt.Color(227, 242, 253));
        cerrar5.setText("     Cerrar sesión");
        cerrar5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrar5MouseClicked(evt);
            }
        });
        jPanel34.add(cerrar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        empenhar.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 509, 170, 40));

        jPanel35.setBackground(new java.awt.Color(153, 153, 153));
        jPanel35.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel36.setBackground(new java.awt.Color(172, 172, 195));
        jPanel36.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        comprar3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        comprar3.setForeground(new java.awt.Color(227, 242, 253));
        comprar3.setText("  Comprar Artículo");
        comprar3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        comprar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                comprar3MouseClicked(evt);
            }
        });
        jPanel36.add(comprar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 40));

        jPanel35.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 170, 40));

        jLabel17.setBackground(new java.awt.Color(21, 101, 192));
        jLabel17.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(227, 242, 253));
        jLabel17.setText("Usuario");
        jPanel35.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 110, 50));

        jPanel37.setBackground(new java.awt.Color(204, 204, 204));
        jPanel37.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        empenhar3.setBackground(new java.awt.Color(30, 136, 229));
        empenhar3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        empenhar3.setText("  Empeñar Artículo");
        empenhar3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        empenhar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                empenhar3MouseClicked(evt);
            }
        });
        jPanel37.add(empenhar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 40));

        jPanel35.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 170, 40));

        jPanel38.setBackground(new java.awt.Color(172, 172, 195));
        jPanel38.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        vender3.setBackground(new java.awt.Color(30, 136, 229));
        vender3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        vender3.setForeground(new java.awt.Color(227, 242, 253));
        vender3.setText("    Vender Artículo");
        vender3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        vender3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vender3MouseClicked(evt);
            }
        });
        jPanel38.add(vender3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 40));

        jPanel35.add(jPanel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 170, 40));

        empenhar.add(jPanel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 550));

        jPanel39.setBackground(new java.awt.Color(227, 242, 253));
        jPanel39.setToolTipText("Administración de usuarios");
        jPanel39.setOpaque(false);
        jPanel39.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel40.setBackground(new java.awt.Color(204, 204, 204));
        jPanel40.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setBackground(new java.awt.Color(227, 242, 253));
        jLabel18.setFont(new java.awt.Font("Chalkduster", 1, 24)); // NOI18N
        jLabel18.setText("  Empeñar Artículo");
        jPanel40.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 350, -1));

        saludo5.setBackground(new java.awt.Color(227, 242, 253));
        saludo5.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        saludo5.setForeground(new java.awt.Color(21, 101, 192));
        jPanel40.add(saludo5, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 30));

        jPanel45.setBackground(new java.awt.Color(153, 153, 153));
        jPanel45.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnRegistrarEmpenho.setBackground(new java.awt.Color(255, 255, 255));
        btnRegistrarEmpenho.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrarEmpenho.setText("Registrar Empeño");
        btnRegistrarEmpenho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarEmpenhoMouseClicked(evt);
            }
        });
        jPanel45.add(btnRegistrarEmpenho, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, 230, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel50.setText("Artículo");
        jPanel45.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 190, 40));

        articulosEmpenhables.setBackground(new java.awt.Color(187, 222, 251));
        articulosEmpenhables.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        articulosEmpenhables.setForeground(new java.awt.Color(21, 101, 192));
        articulosEmpenhables.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                articulosEmpenhablesItemStateChanged(evt);
            }
        });
        articulosEmpenhables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                articulosEmpenhablesActionPerformed(evt);
            }
        });
        jPanel45.add(articulosEmpenhables, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 170, -1));

        listaEmpenhar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listaEmpenhar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artículo", "Precio", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        listaEmpenhar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaEmpenhar.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane7.setViewportView(listaEmpenhar);
        if (listaEmpenhar.getColumnModel().getColumnCount() > 0) {
            listaEmpenhar.getColumnModel().getColumn(0).setResizable(false);
            listaEmpenhar.getColumnModel().getColumn(1).setHeaderValue("Fecha de evolución");
        }

        jPanel45.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 770, 180));

        jSeparator25.setForeground(new java.awt.Color(21, 101, 192));
        jPanel45.add(jSeparator25, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, 110, 10));

        cantidadEmpenhar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cantidadEmpenhar.setForeground(new java.awt.Color(21, 101, 192));
        cantidadEmpenhar.setBorder(null);
        cantidadEmpenhar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantidadEmpenharActionPerformed(evt);
            }
        });
        jPanel45.add(cantidadEmpenhar, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 110, 30));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel47.setText("Cantidad");
        jPanel45.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, 110, 40));

        jSeparator26.setForeground(new java.awt.Color(21, 101, 192));
        jPanel45.add(jSeparator26, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, 160, 10));

        precioEmpenhar.setEditable(false);
        precioEmpenhar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        precioEmpenhar.setForeground(new java.awt.Color(21, 101, 192));
        precioEmpenhar.setBorder(null);
        precioEmpenhar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precioEmpenharActionPerformed(evt);
            }
        });
        jPanel45.add(precioEmpenhar, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, 160, 30));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel46.setText("Precio");
        jPanel45.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 80, 160, 40));

        jSeparator21.setForeground(new java.awt.Color(21, 101, 192));
        jPanel45.add(jSeparator21, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, 160, 10));

        unidadesDisponiblesEmpenhar.setEditable(false);
        unidadesDisponiblesEmpenhar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        unidadesDisponiblesEmpenhar.setForeground(new java.awt.Color(21, 101, 192));
        unidadesDisponiblesEmpenhar.setBorder(null);
        unidadesDisponiblesEmpenhar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unidadesDisponiblesEmpenharActionPerformed(evt);
            }
        });
        jPanel45.add(unidadesDisponiblesEmpenhar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 120, 160, 30));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel41.setText("Unid. Disponibles");
        jPanel45.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, 160, 40));

        btnCarritoEmpenhar.setBackground(new java.awt.Color(255, 255, 255));
        btnCarritoEmpenhar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCarritoEmpenhar.setText("Agregar");
        btnCarritoEmpenhar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCarritoEmpenharMouseClicked(evt);
            }
        });
        jPanel45.add(btnCarritoEmpenhar, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 120, 110, -1));

        clienteEmpenhar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clienteEmpenhar.setForeground(new java.awt.Color(21, 101, 192));
        clienteEmpenhar.setBorder(null);
        clienteEmpenhar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteEmpenharActionPerformed(evt);
            }
        });
        jPanel45.add(clienteEmpenhar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 420, 30));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel48.setText("Cliente");
        jPanel45.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 420, 40));

        clienteEmpenhar1.setBackground(new java.awt.Color(227, 242, 253));
        clienteEmpenhar1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clienteEmpenhar1.setForeground(new java.awt.Color(21, 101, 192));
        clienteEmpenhar1.setBorder(null);
        clienteEmpenhar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteEmpenhar1ActionPerformed(evt);
            }
        });
        jPanel45.add(clienteEmpenhar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 420, 30));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(21, 101, 192));
        jLabel49.setText("Cliente");
        jPanel45.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 420, 40));

        jSeparator24.setForeground(new java.awt.Color(21, 101, 192));
        jPanel45.add(jSeparator24, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 420, 10));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(204, 204, 204));
        jLabel51.setText("Fecha (YYYY/MM/DD)");
        jPanel45.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 0, 210, 40));

        jSeparator27.setForeground(new java.awt.Color(21, 101, 192));
        jPanel45.add(jSeparator27, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 60, 210, 10));

        fechaDevolucion.setBorder(null);
        fechaDevolucion.setForeground(new java.awt.Color(21, 101, 192));
        fechaDevolucion.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel45.add(fechaDevolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 210, 30));

        jTabbedPane3.addTab("Registrar empeño", jPanel45);

        jPanel46.setBackground(new java.awt.Color(153, 153, 153));
        jPanel46.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listaDevolucion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listaDevolucion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id.", "Cliente", "Fecha de empeño", "Fecha de evolución", "Valor total", "Vencido", "Devuelto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        listaDevolucion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        listaDevolucion.setSelectionBackground(new java.awt.Color(30, 136, 229));
        jScrollPane8.setViewportView(listaDevolucion);
        if (listaDevolucion.getColumnModel().getColumnCount() > 0) {
            listaDevolucion.getColumnModel().getColumn(2).setResizable(false);
            listaDevolucion.getColumnModel().getColumn(3).setHeaderValue("Fecha de evolución");
            listaDevolucion.getColumnModel().getColumn(5).setHeaderValue("Vencido");
            listaDevolucion.getColumnModel().getColumn(6).setHeaderValue("Devuelto");
        }

        jPanel46.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 770, 260));

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel52.setText("Id. del empeño");
        jPanel46.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, 250, 40));

        idEmpenho.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        idEmpenho.setForeground(new java.awt.Color(21, 101, 192));
        idEmpenho.setBorder(null);
        idEmpenho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idEmpenhoActionPerformed(evt);
            }
        });
        jPanel46.add(idEmpenho, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, 250, 30));

        jSeparator28.setForeground(new java.awt.Color(21, 101, 192));
        jPanel46.add(jSeparator28, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 370, 250, 10));

        btnRegistrarDevolucion.setBackground(new java.awt.Color(255, 255, 255));
        btnRegistrarDevolucion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRegistrarDevolucion.setText("Registrar Devolución");
        btnRegistrarDevolucion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarDevolucionMouseClicked(evt);
            }
        });
        jPanel46.add(btnRegistrarDevolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 340, 230, -1));

        jTabbedPane3.addTab("Registrar devolución", jPanel46);

        jPanel40.add(jTabbedPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 800, 440));

        jPanel39.add(jPanel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, 550));

        empenhar.add(jPanel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 840, 550));

        atencion.addTab("e", empenhar);

        vistas.addTab("at", atencion);

        getContentPane().add(vistas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1110, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idActionPerformed

    private void entrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_entrarMouseClicked
        if(id.getText().length() == 0 || clave.getText().length() == 0){
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        }else{
            File f = new File("usuarios.txt");
            FileReader fr = null;
            BufferedReader br = null;
            
            try{
                fr = new FileReader(f);
                br = new BufferedReader(fr);
            }catch(FileNotFoundException e){
                JOptionPane.showMessageDialog(null, "Error al conectarse con la base de datos.");
            }
            
            String linea = ""; 
            boolean encontrado = false, esAdministrador = false;
            try {
                while((linea = br.readLine()) != null){
                    String[] usuario = linea.split(";");
                    System.out.println(linea);
                    System.out.println(usuario[2] + " " + TipoUsuario.ADMIN.ordinal());
                            
                    
                    if(encontrado = (id.getText().equalsIgnoreCase(usuario[0]) && clave.getText().equals(usuario[1])) ){
                        //Podemos iniciar sesion, validamos el tipo de usuario
                        saludo1.setText("Hola, "+usuario[0]);
                        saludo2.setText("Hola, "+usuario[0]);
                        saludo3.setText("Hola, "+usuario[0]);
                        saludo4.setText("Hola, "+usuario[0]);
                        saludo5.setText("Hola, "+usuario[0]);
                        esAdministrador = Integer.parseInt(usuario[2]) == TipoUsuario.ADMIN.ordinal();
                        break;
                    }
                }
                
                if(encontrado){
                    if(esAdministrador){ 
                        cargarUsuarios();
                        
                        
                    }else{

                    }
                       
                    vistas.setSelectedIndex(esAdministrador? 1 : 2 );
                }
                else JOptionPane.showMessageDialog(null, "Nombre de usuario o clave incorrectos!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }//GEN-LAST:event_entrarMouseClicked

    private void reportesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportesMouseClicked
        // TODO add your handling code here:
        admin.setSelectedIndex(1);
    }//GEN-LAST:event_reportesMouseClicked

    private void usuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usuariosMouseClicked
        // TODO add your handling code here:
        admin.setSelectedIndex(0);
    }//GEN-LAST:event_usuariosMouseClicked

    private void cerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrar1MouseClicked
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrar1MouseClicked

    private void cerrar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrar2MouseClicked
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrar2MouseClicked

    private void cerrar3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrar3MouseClicked
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrar3MouseClicked

    private void idCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idCrearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idCrearActionPerformed

    private void btnCrearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCrearMouseClicked
        // TODO add your handling code here:
        if(idCrear.getText().isEmpty() || claveCrear.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        }else{
            if(!existeUsuario(idCrear.getText())){
                
                File f = new File("usuarios.txt");
               
                
                
                
                try (FileWriter fw = new FileWriter("usuarios.txt", true);
                     BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter pw = new PrintWriter(bw)){
                    pw.println(idCrear.getText()+";"+claveCrear.getText()+";"+tipoCrear.getSelectedIndex());
                    
                    JOptionPane.showMessageDialog(null, "Usuario creado exitosamente");
                    cargarUsuarios();
                    
                    DefaultTableModel modelo = (DefaultTableModel) listaUsuarios.getModel();
                        
                    Object[] fila = {idCrear.getText(), (tipoCrear.getSelectedIndex() == TipoUsuario.ADMIN.ordinal()? "Administrador" : "Atención al cliente" )};
                    modelo.addRow(fila);
                    idCrear.setText("");
                    claveCrear.setText("");

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
                    e.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(null, "El nombre de usuario \""+idCrear.getText()+"\" ya está en uso.");
            }
        }
    }//GEN-LAST:event_btnCrearMouseClicked

    private void actualizarProductos() { 



        try (FileWriter fw = new FileWriter("articulos.txt", false);
             BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw)){
            
            for(Articulo a :  misArticulos){
                pw.println(a.getId()+";"+a.getNombre()+";"+a.getCantidad()+";"+a.getPrecio()+";"+(a.isVendible()? "true" : "false"));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
            e.printStackTrace();
        }
    }  
    
    private void tipoCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoCrearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoCrearActionPerformed

    private void empenhar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_empenhar1MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(2);
    }//GEN-LAST:event_empenhar1MouseClicked

    private void vender1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vender1MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(1);
    }//GEN-LAST:event_vender1MouseClicked

    private void cerrar4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrar4MouseClicked
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrar4MouseClicked

    private void empenhar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_empenhar2MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(2);
    }//GEN-LAST:event_empenhar2MouseClicked

    private void vender2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vender2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_vender2MouseClicked

    private void cantidadVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cantidadVenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantidadVenderActionPerformed

    private void precioVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precioVenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precioVenderActionPerformed

    private void clienteVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteVenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteVenderActionPerformed

    private void cerrar5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrar5MouseClicked
        // TODO add your handling code here:
        cerrarSesion();
    }//GEN-LAST:event_cerrar5MouseClicked

    private void empenhar3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_empenhar3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_empenhar3MouseClicked

    private void vender3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vender3MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(1);
    }//GEN-LAST:event_vender3MouseClicked

    public boolean fechaValida(String x){
        if(x.length() == 10){
            for(int i = 0; i < 10; i++){
                if(i == 4 || i == 7){
                    if(x.charAt(i) != '/') return false;
                }else if(!(x.charAt(i) >= '0' && x.charAt(i) <= '9')){
                    return false;
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(x.trim());
            } catch (ParseException pe) {
                return false;
            }
            return true;
        }else return false;
        
        
    }
    
    private void btnRegistrarEmpenhoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarEmpenhoMouseClicked
        // TODO add your handling code here:
        DefaultTableModel modelo = (DefaultTableModel) listaEmpenhar.getModel();
        
        if(clienteEmpenhar.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "El nombre del cliente es obligatorio");
        }else if(modelo.getRowCount() == 0){
            JOptionPane.showMessageDialog(null, "El carrito se encuentra vacio.");
        }else if(!fechaValida(fechaDevolucion.getText())){
            JOptionPane.showMessageDialog(null, "La fecha de devolución no cumple el formato.");
        }
        else{
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String fecha = dateFormat.format(date);

            try (FileWriter fw = new FileWriter("empenhos.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw)){


                for(int i = 0; i < modelo.getRowCount(); i++){
                    pw.println(idAsignableEmpenhos+";1;"+clienteEmpenhar.getText()+";"+modelo.getValueAt(i, 0).toString()+";"+modelo.getValueAt(i, 2).toString()+";"+
                            modelo.getValueAt(i, 1).toString()+";"+fecha+";"+fechaDevolucion.getText()+";false;false");
                            misEmpenhos.add(new Empenho(idAsignableEmpenhos,TipoAtencion.EMPENHAR,clienteEmpenhar.getText(), modelo.getValueAt(i, 0).toString(),
                                    Integer.parseInt(modelo.getValueAt(i, 2).toString()), Double.parseDouble(modelo.getValueAt(i, 1).toString()),fecha,fechaDevolucion.getText(), false, false));
                }
                
               
                
                idAsignableEmpenhos++;
                int cantidad = 0;
                //Actualizacion de la lista de productos
                for(int i = 0; i < modelo.getRowCount(); i++){
                    for(int j = 0; j < misArticulos.size(); j++){
                        if(misArticulos.get(j).getNombre().equalsIgnoreCase(modelo.getValueAt(i, 0).toString())){
                            cantidad = misArticulos.get(j).getCantidad();
                            misArticulos.get(j).setCantidad(cantidad - Integer.parseInt(modelo.getValueAt(i, 2).toString()));
                        }
                    }
                }
                                
                actualizarProductos();
                initListaDevolucion();

                while(modelo.getRowCount() > 0) modelo.removeRow(0);
                
                clienteEmpenhar.setText("");
                
                articulosEmpenhablesItemStateChanged(null);
                pack();

                JOptionPane.showMessageDialog(null, "Empeño registrado exitosamente");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnRegistrarEmpenhoMouseClicked

    private void cantidadEmpenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cantidadEmpenharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantidadEmpenharActionPerformed

    private void precioEmpenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precioEmpenharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precioEmpenharActionPerformed

    private void comprar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_comprar2MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(0);
    }//GEN-LAST:event_comprar2MouseClicked

    private void comprar3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_comprar3MouseClicked
        // TODO add your handling code here:
        atencion.setSelectedIndex(0);
    }//GEN-LAST:event_comprar3MouseClicked

    private void comprarArticuloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_comprarArticuloMouseClicked
        // TODO add your handling code here:
        if(articulo.getText().isEmpty() || cantidad.getText().isEmpty() || precio.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        }else{
            if(Double.parseDouble(precio.getText()) > 0){
                File f = new File("usuarios.txt");
              

                try (FileWriter fw = new FileWriter("articulos.txt", true);
                     BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter pw = new PrintWriter(bw)){

                    
                    pw.println(idAsignableArticulo+";"+articulo.getText()+";"+cantidad.getText()+";"+precio.getText()+";"+ (vendible.isSelected()? "true" : "false"));
                    misArticulos.add(new Articulo(idAsignableArticulo, articulo.getText(), Integer.parseInt(cantidad.getText()), Double.parseDouble(precio.getText()), Boolean.parseBoolean((vendible.isSelected()? "true" : "false"))));
                    idAsignableArticulo++;
                    JOptionPane.showMessageDialog(null, "Articulo registrado exitosamente");

                    
                                        
                    DefaultComboBoxModel vendibles = (DefaultComboBoxModel) articulosVendibles.getModel();
                    DefaultComboBoxModel empenhables = (DefaultComboBoxModel) articulosEmpenhables.getModel();
                    
                    empenhables.addElement(articulo.getText());
                    if(vendible.isSelected())
                        vendibles.addElement(articulo.getText());
                    
                    articulo.setText("");
                    cantidad.setText("");
                    precio.setText("");
                    vendible.setSelected(false);
                    
                    pack();

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
                    e.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(null, "El precio debe ser mayor a 0.");
            }
        }
    }//GEN-LAST:event_comprarArticuloMouseClicked

    private void articuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_articuloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_articuloActionPerformed

    private void precioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precioActionPerformed

    private void cantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantidadActionPerformed

    private void btnCarritoVenderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCarritoVenderMouseClicked
        // TODO add your handling code here:
        if(cantidadVender.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        }else{
            if(Integer.parseInt(cantidadVender.getText()) > 0){
                DefaultTableModel modelo = (DefaultTableModel) listaVender.getModel();
        
                int x = articulosVendibles.getSelectedIndex(), contador = -1;


                for(int i = 0; i < misArticulos.size(); i++){
                    if(misArticulos.get(i).isVendible()){
                        contador++;
                        if(contador == x){
                            precioVender.setText( String.valueOf(misArticulos.get(i).getPrecio())  );
                            int r = existeProductoVendible(misArticulos.get(i).getNombre());
                            
                            if(r > -1){
                                String f = String.valueOf(Integer.parseInt(modelo.getValueAt(r, 2).toString()) + Integer.parseInt(cantidadVender.getText()));
                                if(Integer.parseInt(f) <= misArticulos.get(i).getCantidad())
                                    modelo.setValueAt(f, r, 2);
                                else
                                    JOptionPane.showMessageDialog(null, "Solo existen "+misArticulos.get(i).getCantidad()+" unidad(es) del producto "+misArticulos.get(i).getNombre());
                            }else{
                                if(Integer.parseInt(cantidadVender.getText()) <= misArticulos.get(i).getCantidad()){
                                    Object[] fila = {misArticulos.get(i).getNombre(), String.valueOf(precioVender.getText()), String.valueOf(cantidadVender.getText())};
                                    modelo.addRow(fila);
                                }else
                                    JOptionPane.showMessageDialog(null, "Solo existen "+misArticulos.get(i).getCantidad()+" unidad(es) del producto "+misArticulos.get(i).getNombre());
                                
                            }
                            

                            return;
                        }
                    }
                }
                pack();
            }else JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
        }
        
    }//GEN-LAST:event_btnCarritoVenderMouseClicked

    private void btnRegistrarVentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarVentaMouseClicked
        // TODO add your handling code here:
        DefaultTableModel modelo = (DefaultTableModel) listaVender.getModel();
        
        if(clienteVender.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "El nombre del cliente es obligatorio");
        }else if(modelo.getRowCount() == 0){
            JOptionPane.showMessageDialog(null, "El carrito se encuentra vacio.");
        }else{
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String fecha = dateFormat.format(date);

            try (FileWriter fw = new FileWriter("ventas.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw)){


                for(int i = 0; i < modelo.getRowCount(); i++){
                    pw.println(idAsignableVentas+";0;"+clienteVender.getText()+";"+modelo.getValueAt(i, 0).toString()+";"+modelo.getValueAt(i, 2).toString()+";"+
                            modelo.getValueAt(i, 1).toString()+";"+fecha);
                }
                
                idAsignableVentas++;
                int cantidad = 0;
                //Actualizacion de la lista de productos
                for(int i = 0; i < modelo.getRowCount(); i++){
                    for(int j = 0; j < misArticulos.size(); j++){
                        if(misArticulos.get(j).getNombre().equalsIgnoreCase(modelo.getValueAt(i, 0).toString())){
                            cantidad = misArticulos.get(j).getCantidad();
                            misArticulos.get(j).setCantidad(cantidad - Integer.parseInt(modelo.getValueAt(i, 2).toString()));
                        }
                    }
                }
                
                actualizarProductos();

                while(modelo.getRowCount() > 0) modelo.removeRow(0);
                
                clienteVender.setText("");
                
                articulosVendiblesItemStateChanged(null);
                pack();

                JOptionPane.showMessageDialog(null, "Venta registrada exitosamente");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
                e.printStackTrace();
            }
        }
        
    }//GEN-LAST:event_btnRegistrarVentaMouseClicked

    private void btnCarritoEmpenharMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCarritoEmpenharMouseClicked
        // TODO add your handling code here:
        if(cantidadEmpenhar.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        }else{
            if(Integer.parseInt(cantidadEmpenhar.getText()) > 0){
                DefaultTableModel modelo = (DefaultTableModel) listaEmpenhar.getModel();
        
                int x = articulosEmpenhables.getSelectedIndex();


                for(int i = 0; i < misArticulos.size(); i++){
                    if(x == i){
                        precioEmpenhar.setText( String.valueOf(misArticulos.get(i).getPrecio())  );
                        int r = existeProductoEmpenhable(misArticulos.get(i).getNombre());

                        if(r > -1){
                            String f = String.valueOf(Integer.parseInt(modelo.getValueAt(r, 2).toString()) + Integer.parseInt(cantidadEmpenhar.getText()));
                            if(Integer.parseInt(f) <= misArticulos.get(i).getCantidad())
                                modelo.setValueAt(f, r, 2);
                            else
                                JOptionPane.showMessageDialog(null, "Solo existen "+misArticulos.get(i).getCantidad()+" unidad(es) del producto "+misArticulos.get(i).getNombre());
                        }else{
                            if(Integer.parseInt(cantidadEmpenhar.getText()) <= misArticulos.get(i).getCantidad()){
                                Object[] fila = {misArticulos.get(i).getNombre(), String.valueOf(precioEmpenhar.getText()), String.valueOf(cantidadEmpenhar.getText())};
                                modelo.addRow(fila);
                            }else
                                JOptionPane.showMessageDialog(null, "Solo existen "+misArticulos.get(i).getCantidad()+" unidad(es) del producto "+misArticulos.get(i).getNombre());

                        }


                        return;
                    }
                }
                pack();
            }else JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
        }
        
    }//GEN-LAST:event_btnCarritoEmpenharMouseClicked

    private void articulosVendiblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_articulosVendiblesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_articulosVendiblesActionPerformed

    private void articulosEmpenhablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_articulosEmpenhablesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_articulosEmpenhablesActionPerformed

    private void articulosEmpenhablesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_articulosEmpenhablesItemStateChanged
        // TODO add your handling code here:
        precioEmpenhar.setText(String.valueOf(misArticulos.get(articulosEmpenhables.getSelectedIndex()).getPrecio()));
        unidadesDisponiblesEmpenhar.setText(String.valueOf(misArticulos.get(articulosEmpenhables.getSelectedIndex()).getCantidad()));
    }//GEN-LAST:event_articulosEmpenhablesItemStateChanged

    private void articulosVendiblesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_articulosVendiblesItemStateChanged
        // TODO add your handling code here:
        int x = articulosVendibles.getSelectedIndex(), contador = -1;
        
        
        for(int i = 0; i < misArticulos.size(); i++){
            if(misArticulos.get(i).isVendible()){
                contador++;
                if(contador == x){
                    precioVender.setText( String.valueOf(misArticulos.get(i).getPrecio())  );
                    unidadesDisponiblesVender.setText(String.valueOf(misArticulos.get(i).getCantidad()));
                    if(misArticulos.get(i).getCantidad() > 0){
                        btnCarritoEmpenhar.setEnabled(true);
                    }else btnCarritoEmpenhar.setEnabled(false);
                    return;
                }
            }
        }
    }//GEN-LAST:event_articulosVendiblesItemStateChanged

    private void unidadesDisponiblesVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unidadesDisponiblesVenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unidadesDisponiblesVenderActionPerformed

    private void unidadesDisponiblesEmpenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unidadesDisponiblesEmpenharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unidadesDisponiblesEmpenharActionPerformed

    private void clienteEmpenhar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteEmpenhar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteEmpenhar1ActionPerformed

    private void clienteEmpenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteEmpenharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteEmpenharActionPerformed

    private void idEmpenhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idEmpenhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idEmpenhoActionPerformed
    
    private boolean devolucionVencida(int id){
        DefaultTableModel modelo = (DefaultTableModel) listaDevolucion.getModel();
        
        for(int i = 0; i < modelo.getRowCount(); i++){
            if(Integer.parseInt(modelo.getValueAt(i, 0).toString()) == id){
                Date d1 = new Date(modelo.getValueAt(i, 3).toString()), d2 = new Date();
                return d1.before(d2);
            }
        }
        
        return false;
    }
    
    private void btnRegistrarDevolucionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarDevolucionMouseClicked
        // TODO add your handling code here:
        
        if(idEmpenho.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "El identificador del empeño es obligatorio es obligatorio");
        }else{
            int id = Integer.parseInt(idEmpenho.getText());
            
            if(id >= 0 && id < idAsignableEmpenhos){
                DefaultTableModel modelo = (DefaultTableModel) listaDevolucion.getModel();
                if(modelo.getValueAt(id, 6).toString().equalsIgnoreCase("NO")){
                    if(modelo.getValueAt(id, 5).toString().equalsIgnoreCase("NO")){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        String fecha = dateFormat.format(date);



                        boolean v = devolucionVencida(Integer.parseInt(idEmpenho.getText()));

                        for(Empenho e : misEmpenhos){
                            if(e.getId() == id){
                                e.setDevuelto(true);
                                e.setVencido(v);
                            }
                        }


                        try (FileWriter fw = new FileWriter("empenhos.txt", true);
                             BufferedWriter bw = new BufferedWriter(fw);
                                PrintWriter pw = new PrintWriter(bw)){

                            for(Empenho e : misEmpenhos){
                                pw.println(e.getId()+";1;"+e.getCliente()+";"+e.getArticulo()+";"+e.getCantidad()+";"+e.getPrecio()+";"+e.getFecha()+";"+
                                            e.getFechaDevolucion()+";"+(e.isVencido()? "true" : "false")+";"+(e.isDevuelto()? "true" : "false"));
                            }

                            initListaDevolucion();


                            idEmpenho.setText("");

                            pack();

                            JOptionPane.showMessageDialog(null, "Empeño devuelto exitosamente");
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Error al escribir en el archivo");
                            e.printStackTrace();
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Este empeño no puedo ser devuelto.");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Este empeño ya fue devuelto.");
                }
                
            }else{
                JOptionPane.showMessageDialog(null, "No existe un registro con este identificador.");
            }
            
        }
    }//GEN-LAST:event_btnRegistrarDevolucionMouseClicked

    private void salirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salirMouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_salirMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane admin;
    private javax.swing.JTextField articulo;
    private javax.swing.JComboBox<String> articulosEmpenhables;
    private javax.swing.JComboBox<String> articulosVendibles;
    private javax.swing.JTabbedPane atencion;
    private javax.swing.JButton btnCarritoEmpenhar;
    private javax.swing.JButton btnCarritoVender;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnRegistrarDevolucion;
    private javax.swing.JButton btnRegistrarEmpenho;
    private javax.swing.JButton btnRegistrarVenta;
    private javax.swing.JTextField cantidad;
    private javax.swing.JTextField cantidadEmpenhar;
    private javax.swing.JTextField cantidadVender;
    private javax.swing.JLabel cerrar1;
    private javax.swing.JLabel cerrar2;
    private javax.swing.JLabel cerrar3;
    private javax.swing.JLabel cerrar4;
    private javax.swing.JLabel cerrar5;
    private javax.swing.JPasswordField clave;
    private javax.swing.JPasswordField claveCrear;
    private javax.swing.JTextField clienteEmpenhar;
    private javax.swing.JTextField clienteEmpenhar1;
    private javax.swing.JTextField clienteVender;
    private javax.swing.JLabel comprar;
    private javax.swing.JLabel comprar2;
    private javax.swing.JLabel comprar3;
    private javax.swing.JButton comprarArticulo;
    private javax.swing.JTable empenhadosVencidos;
    private javax.swing.JPanel empenhar;
    private javax.swing.JLabel empenhar1;
    private javax.swing.JLabel empenhar2;
    private javax.swing.JLabel empenhar3;
    private javax.swing.JTable empenhosPorFecha;
    private javax.swing.JButton entrar;
    private javax.swing.JFormattedTextField fechaDevolucion;
    private javax.swing.JTextField id;
    private javax.swing.JTextField idCrear;
    private javax.swing.JTextField idEmpenho;
    private javax.swing.JTable inventario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator24;
    private javax.swing.JSeparator jSeparator25;
    private javax.swing.JSeparator jSeparator26;
    private javax.swing.JSeparator jSeparator27;
    private javax.swing.JSeparator jSeparator28;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable listaDevolucion;
    private javax.swing.JTable listaEmpenhar;
    private javax.swing.JTable listaUsuarios;
    private javax.swing.JTable listaVender;
    private javax.swing.JTextField precio;
    private javax.swing.JTextField precioEmpenhar;
    private javax.swing.JTextField precioVender;
    private javax.swing.JLabel reportes;
    private javax.swing.JButton salir;
    private javax.swing.JLabel saludo1;
    private javax.swing.JLabel saludo2;
    private javax.swing.JLabel saludo3;
    private javax.swing.JLabel saludo4;
    private javax.swing.JLabel saludo5;
    private javax.swing.JComboBox<String> tipoCrear;
    private javax.swing.JTextField unidadesDisponiblesEmpenhar;
    private javax.swing.JTextField unidadesDisponiblesVender;
    private javax.swing.JLabel usuarios;
    private javax.swing.JPanel vender;
    private javax.swing.JLabel vender1;
    private javax.swing.JLabel vender2;
    private javax.swing.JLabel vender3;
    private javax.swing.JCheckBox vendible;
    private javax.swing.JTable ventasPorFecha;
    private javax.swing.JTabbedPane vistas;
    // End of variables declaration//GEN-END:variables
}
