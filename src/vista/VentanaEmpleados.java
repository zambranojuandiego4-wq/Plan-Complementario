package vista;

import controlador.EmpleadoControlador;
import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;
import modelo.EmpleadoComercial;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La ventana del sistema. Solo muestra información y captura lo que el usuario hace;
 * todas las decisiones se las delega al controlador.
 */
public class VentanaEmpleados extends JFrame {
    private final EmpleadoControlador controlador;

    // Campos del formulario
    private final JTextField txtCedula = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtSalario = new JTextField();
    private final JTextField txtBonificacion = new JTextField();
    private final JComboBox<String> cmbTipo = new JComboBox<>(EmpleadoControlador.TIPOS_EMPLEADO);

    // Botones de acción (Incluye el botón de Estadísticas del Bonus)
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnHistorial = new JButton("Historial");
    private final JButton btnEstadisticas = new JButton("Estadísticas");

    // Tabla y resumen
    private DefaultTableModel datosTabla;
    private final JLabel lblResumen = new JLabel();

    public VentanaEmpleados(EmpleadoControlador controlador) {
        super("Sistema CRUD de Talento Humano");
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(lblResumen, BorderLayout.SOUTH);
        conectarEventos();
        refrescarTabla();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 540);
        setLocationRelativeTo(null); // centra la ventana en la pantalla
    }

    private JPanel construirFormulario() {
        JPanel campos = new JPanel(new GridLayout(5, 2, 8, 8));
        campos.add(new JLabel("Cédula:"));
        campos.add(txtCedula);
        campos.add(new JLabel("Nombre completo:"));
        campos.add(txtNombre);
        campos.add(new JLabel("Salario base:"));
        campos.add(txtSalario);
        campos.add(new JLabel("Tipo de empleado:"));
        campos.add(cmbTipo);
        campos.add(new JLabel("Bonificación / Comisión %:")); // Etiqueta combinada exigida en el reto
        campos.add(txtBonificacion);
        txtBonificacion.setEnabled(false); // arranca desactivado porque inicia en "Operativo"

        // Array de botones para agregarlos en bucle
        JPanel botones = new JPanel(new FlowLayout());
        JButton[] listaBotones = {btnAgregar, btnBuscar, btnActualizar, btnEliminar, btnLimpiar, btnHistorial, btnEstadisticas};
        for (JButton boton : listaBotones) {
            botones.add(boton);
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.add(campos, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private String texto(JTextField campo) {
        return campo.getText().trim();
    }

    private String tipoSeleccionado() {
        return (String) cmbTipo.getSelectedItem();
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Cédula", "Nombre", "Tipo", "Salario base", "Salario total"};
        datosTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // la tabla es solo de lectura
            }
        };
        JTable tabla = new JTable(datosTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Empleados registrados"));
        return scroll;
    }

    private void refrescarTabla() {
        datosTabla.setRowCount(0); // borra las filas anteriores
        for (EmpleadoBase empleado : controlador.obtenerEmpleados()) {
            Object[] fila = {
                    empleado.getCedula(),
                    empleado.getNombre(),
                    empleado.getTipo(),
                    formatoPesos(empleado.getSalarioBase()),
                    formatoPesos(empleado.calcularSalarioTotal())
            };
            datosTabla.addRow(fila);
        }
        lblResumen.setText("Empleados: " + datosTabla.getRowCount()
                + " | Total nómina: "
                + formatoPesos(controlador.calcularTotalNomina()));
    }

    private String formatoPesos(double valor) {
        return String.format("$ %,.0f", valor);
    }

    private void conectarEventos() {
        // Habilita el campo adicional tanto para Administrativos como para Comerciales
        cmbTipo.addActionListener(e -> {
            String tipo = tipoSeleccionado();
            if (tipo.equals("Administrativo") || tipo.equals("Comercial")) {
                txtBonificacion.setEnabled(true);
            } else {
                txtBonificacion.setEnabled(false);
                txtBonificacion.setText("");
            }
        });

        btnAgregar.addActionListener(e -> mostrarResultado(controlador.agregarEmpleado(
                texto(txtCedula), texto(txtNombre), texto(txtSalario),
                tipoSeleccionado(), texto(txtBonificacion))));

        btnActualizar.addActionListener(e -> mostrarResultado(controlador.actualizarEmpleado(
                texto(txtCedula), texto(txtNombre), texto(txtSalario),
                tipoSeleccionado(), texto(txtBonificacion))));

        btnBuscar.addActionListener(e -> buscar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnHistorial.addActionListener(e -> mostrarHistorial());

        // Evento para el botón Estadísticas (Bonus 1)
        btnEstadisticas.addActionListener(e -> {
            HashMap<String, Integer> stats = controlador.obtenerEstadisticas();
            String mensaje = "Cantidad de empleados por tipo:\n" +
                    "Operativos: " + stats.get("Operativo") + "\n" +
                    "Administrativos: " + stats.get("Administrativo") + "\n" +
                    "Comerciales: " + stats.get("Comercial");
            JOptionPane.showMessageDialog(this, mensaje, "Estadísticas Generales", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void mostrarResultado(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
        refrescarTabla();
    }

    private void buscar() {
        String cedula = texto(txtCedula);
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe una cédula para buscar.");
            return;
        }
        EmpleadoBase empleado = controlador.buscarEmpleado(cedula);
        if (empleado == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún empleado con la cédula " + cedula + ".");
            return;
        }
        txtNombre.setText(empleado.getNombre());
        txtSalario.setText(String.format("%.0f", empleado.getSalarioBase()));
        cmbTipo.setSelectedItem(empleado.getTipo());

        // Carga de forma correcta los datos según el tipo de objeto encontrado
        if (empleado instanceof EmpleadoAdministrativo) {
            EmpleadoAdministrativo adm = (EmpleadoAdministrativo) empleado;
            txtBonificacion.setText(String.format("%.0f", adm.getBonificacion()));
        } else if (empleado instanceof EmpleadoComercial) {
            EmpleadoComercial comer = (EmpleadoComercial) empleado;
            txtBonificacion.setText(String.format("%.0f", comer.getPorcentajeComision())); // Carga la comisión
        } else {
            txtBonificacion.setText("");
        }
    }

    private void eliminar() {
        String cedula = texto(txtCedula);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas eliminar al empleado con cédula " + cedula + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            mostrarResultado(controlador.eliminarEmpleado(cedula));
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtSalario.setText("");
        txtBonificacion.setText("");
        cmbTipo.setSelectedIndex(0);
        txtCedula.requestFocus();
    }

    private void mostrarHistorial() {
        ArrayList<String> historial = controlador.obtenerHistorial();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aún no hay operaciones registradas.");
            return;
        }
        String texto = "";
        for (int i = 0; i < historial.size(); i++) {
            texto += (i + 1) + ". " + historial.get(i) + "\n";
        }
        JOptionPane.showMessageDialog(this, texto, "Historial de operaciones", JOptionPane.INFORMATION_MESSAGE);
    }
}
