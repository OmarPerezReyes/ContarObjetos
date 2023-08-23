// Paso 3: Función Main
public class Main {
    public static void main(String[] args) {
        // Crear el gestor de figuras
        GestorFiguras<Figura> gestor = new GestorFiguras<>();

        // Crear instancias de figuras
        Figura.Circulo circulo = new Figura.Circulo(5.0);
        Figura.Rectangulo rectangulo = new Figura.Rectangulo(4.0, 3.0);

        // Agregar figuras al gestor
        gestor.agregarFigura(circulo);
        gestor.agregarFigura(rectangulo);

        // Mostrar información de todas las figuras
        System.out.println("Información de todas las figuras:");
        gestor.mostrarInfoTodasFiguras();

        // Calcular el área total de todas las figuras
        double areaTotal = gestor.calcularAreaTotal();
        System.out.println("Área total de todas las figuras: " + areaTotal);
    }
}
