package controlador;

import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;
import modelo.EmpleadoComercial; // Importamos comercial
import modelo.RepositorioEmpleados;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * El "cerebro" del sistema: recibe lo que el usuario escribe en la ventana,
 * lo valida y decide qué hacer con los datos.
 */
public class EmpleadoControlador {
    // Array: lista FIJA de tipos de empleado modificada con "Comercial" en la tercera opción
    public static final String[] TIPOS_EMPLEADO = {"Operativo", "Administrativo", "Comercial"};
    private final RepositorioEmpleados repositorio;
    private final ArrayList<String> historial; // ArrayList: crece con cada operación

    public EmpleadoControlador() {
        repositorio = new RepositorioEmpleados();
        historial = new ArrayList<>();
        cargarDatosDePrueba();
    }

    // Carga 4 empleados de ejemplo usando arrays paralelos y un ciclo for
    private void cargarDatosDePrueba() {
        String[] cedulas = {"1001", "1002", "1003", "1004"};
        String[] nombres = {"Ana Torres", "Luis Gómez", "Marta Ríos", "Pedro Cano"};
        double[] salarios = {1800000, 2500000, 1750000, 3200000};

        for (int i = 0; i < cedulas.length; i++) {
            EmpleadoBase empleado;
            if (i % 2 == 0) {
                empleado = new EmpleadoBase(cedulas[i], nombres[i], salarios[i]);
            } else {
                empleado = new EmpleadoAdministrativo(cedulas[i], nombres[i], salarios[i], 300000);
            }
            repositorio.agregar(empleado);
        }
    }

    // Revisa carácter por carácter que el texto sea un número positivo válido
    private boolean esNumeroValido(String texto) {
        if (texto.isEmpty() || texto.equals(".")) {
            return false;
        }
        int puntos = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.') {
                puntos++;
            } else if (!Character.isDigit(c)) {
                return false; // letra, signo, espacio... no es válido
            }
        }
        return puntos <= 1; // máximo un punto decimal
    }

    // Devuelve un mensaje de error, o null si todo está correcto
    private String validar(String cedula, String nombre, String salario, String tipo, String bonificacion) {
        if (cedula.isEmpty() || nombre.isEmpty()) {
            return "La cédula y el nombre son obligatorios.";
        }
        if (!esNumeroValido(salario)) {
            return "El salario debe ser un número positivo (sin puntos de miles).";
        }
        if (tipo.equals("Administrativo") && !esNumeroValido(bonificacion)) {
            return "La bonificación debe ser un número positivo.";
        }

        // Validación obligatoria del reto: la comisión comercial no puede ser mayor a 50
        if (tipo.equals("Comercial")) {
            if (!esNumeroValido(bonificacion)) {
                return "La comisión debe ser un número positivo.";
            }
            double comi = Double.parseDouble(bonificacion);
            if (comi > 50) {
                return "El porcentaje de comisión no puede ser mayor a 50%."; // Mensaje claro exigido
            }
        }
        return null;
    }

    // Fábrica de empleados: decide qué clase instanciar según el tipo
    private EmpleadoBase construirEmpleado(String cedula, String nombre, String salario, String tipo, String bonificacion) {
        double salarioBase = Double.parseDouble(salario);
        if (tipo.equals("Administrativo")) {
            double bono = Double.parseDouble(bonificacion);
            return new EmpleadoAdministrativo(cedula, nombre, salarioBase, bono);
        }
        // Si seleccionan Comercial, construimos la nueva clase comercial
        if (tipo.equals("Comercial")) {
            double comi = Double.parseDouble(bonificacion);
            return new EmpleadoComercial(cedula, nombre, salarioBase, comi);
        }
        return new EmpleadoBase(cedula, nombre, salarioBase);
    }

    // ======================= OPERACIONES CRUD =======================
    public String agregarEmpleado(String cedula, String nombre, String salario, String tipo, String bonificacion) {
        String error = validar(cedula, nombre, salario, tipo, bonificacion);
        if (error != null) {
            return error;
        }
        EmpleadoBase nuevo = construirEmpleado(cedula, nombre, salario, tipo, bonificacion);
        if (repositorio.agregar(nuevo)) {
            historial.add("AGREGADO: " + cedula + " - " + nombre);
            return "Empleado agregado correctamente.";
        }
        return "Ya existe un empleado con la cédula " + cedula + ".";
    }

    public EmpleadoBase buscarEmpleado(String cedula) {
        historial.add("BÚSQUEDA: " + cedula);
        return repositorio.buscar(cedula);
    }

    public String actualizarEmpleado(String cedula, String nombre, String salario, String tipo, String bonificacion) {
        String error = validar(cedula, nombre, salario, tipo, bonificacion);
        if (error != null) {
            return error;
        }
        EmpleadoBase actualizado = construirEmpleado(cedula, nombre, salario, tipo, bonificacion);
        if (repositorio.actualizar(actualizado)) {
            historial.add("ACTUALIZADO: " + cedula + " - " + nombre);
            return "Empleado actualizado correctamente.";
        }
        return "No existe ningún empleado con la cédula " + cedula + ".";
    }

    public String eliminarEmpleado(String cedula) {
        if (repositorio.eliminar(cedula)) {
            historial.add("ELIMINADO: " + cedula);
            return "Empleado eliminado correctamente.";
        }
        return "No existe ningún empleado con la cédula " + cedula + ".";
    }

    // BONUS 2: Hace que la tabla devuelva los empleados ordenados alfabéticamente
    public ArrayList<EmpleadoBase> obtenerEmpleados() {
        ArrayList<EmpleadoBase> lista = repositorio.listarTodos();
        lista.sort((e1, e2) -> e1.getNombre().compareToIgnoreCase(e2.getNombre()));
        return lista;
    }

    // Polimorfismo en acción: incluye automáticamente a los comerciales sin cambiar la estructura
    public double calcularTotalNomina() {
        double total = 0;
        for (EmpleadoBase empleado : repositorio.listarTodos()) {
            total += empleado.calcularSalarioTotal();
        }
        return total;
    }

    public ArrayList<String> obtenerHistorial() {
        return historial;
    }

    // BONUS 1: Conteo de empleados usando HashMap<String, Integer> mostrado en un JOptionPane
    public HashMap<String, Integer> obtenerEstadisticas() {
        HashMap<String, Integer> conteo = new HashMap<>();
        conteo.put("Operativo", 0);
        conteo.put("Administrativo", 0);
        conteo.put("Comercial", 0);

        for (EmpleadoBase emp : repositorio.listarTodos()) {
            String tipo = emp.getTipo();
            int actual = conteo.get(tipo);
            conteo.put(tipo, actual + 1);
        }
        return conteo;
    }
}
