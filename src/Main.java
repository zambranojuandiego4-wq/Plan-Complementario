import controlador.EmpleadoControlador;
import vista.VentanaEmpleados;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing recomienda crear las ventanas dentro de su propio hilo gráfico
        SwingUtilities.invokeLater(() -> {
            EmpleadoControlador controlador = new EmpleadoControlador(); // + Modelo
            VentanaEmpleados ventana = new VentanaEmpleados(controlador); // Vista
            ventana.setVisible(true);
        });
    }
}
