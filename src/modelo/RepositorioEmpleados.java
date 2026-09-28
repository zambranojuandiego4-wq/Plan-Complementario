package modelo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Guarda los empleados en memoria. Es el "archivador" de la empresa.
 */
public class RepositorioEmpleados {
    // HashMap: clave = cédula, valor = el objeto empleado
    private final HashMap<String, EmpleadoBase> empleados = new HashMap<>();

    public boolean agregar(EmpleadoBase empleado) {
        if (empleados.containsKey(empleado.getCedula())) {
            return false; // ya existe: no se permiten cédulas duplicadas
        }
        empleados.put(empleado.getCedula(), empleado);
        return true;
    }

    public EmpleadoBase buscar(String cedula) {
        return empleados.get(cedula); // devuelve null si no existe
    }

    public boolean actualizar(EmpleadoBase empleado) {
        if (!empleados.containsKey(empleado.getCedula())) {
            return false; // no se puede actualizar algo que no existe
        }
        empleados.put(empleado.getCedula(), empleado); // reemplaza el anterior
        return true;
    }

    public boolean eliminar(String cedula) {
        return empleados.remove(cedula) != null;
    }

    public ArrayList<EmpleadoBase> listarTodos() {
        // Copiamos los valores del mapa en una lista para poder recorrerlos en orden
        return new ArrayList<>(empleados.values());
    }
}
