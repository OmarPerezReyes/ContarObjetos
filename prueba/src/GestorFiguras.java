// 2. Implementación de una Clase para Gestionar Figuras
import java.util.ArrayList;
import java.util.List;

// Definición de la clase GestorFiguras que utiliza generacidad
class GestorFiguras<T extends Figura> {
    private List<T> figuras; // Lista para almacenar las figuras

    // Constructor de la clase GestorFiguras
    public GestorFiguras() {
        figuras = new ArrayList<>(); // Inicialización de la lista de figuras
    }

    // Método para agregar una figura a la lista
    public void agregarFigura(T figura) {
        figuras.add(figura); // Agregar la figura a la lista
    }

    // Método para mostrar información de todas las figuras
    public void mostrarInfoTodasFiguras() {
        for (T figura : figuras) {
            System.out.println("Tipo de figura: " + figura.getClass().getSimpleName());
            System.out.println("Área: " + figura.calcularArea());
            System.out.println("Perímetro: " + figura.calcularPerimetro());
            System.out.println("----------------------");
        }
    }

    // Método para calcular el área total de todas las figuras
    public double calcularAreaTotal() {
        double areaTotal = 0;
        for (T figura : figuras) {
            areaTotal += figura.calcularArea(); // Acumular áreas
        }
        return areaTotal; // Devolver el área total
    }
}
