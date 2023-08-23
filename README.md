# ContarObjetos utilizando IntelliJ IDEA

Versión de Java:
- java 18.0.2.1 2022-08-18
- Java(TM) SE Runtime Environment (build 18.0.2.1+1-1)
- Java HotSpot(TM) 64-Bit Server VM (build 18.0.2.1+1-1, mixed mode, sharing)

Para configuración, sigue la guía en Medium: [Cómo configurar OpenCV en IntelliJ IDEA](https://medium.com/@aadimator/how-to-set-up-opencv-in-intellij-idea-6eb103c1d45c)

## Descripción del Algoritmo

El algoritmo general consta de los siguientes pasos:

1. **Lee y procesa imágenes en una carpeta:** Comienza cargando la biblioteca OpenCV y luego se dirige a una ruta específica. Itera a través de las imágenes en esa carpeta hasta un número n de imágenes determinado.

2. **Convierte imágenes al espacio de color HSV (Hue, Saturation, Value):** Las imágenes se convierten al espacio de color HSV, que se compone de tres componentes principales: matiz (color), saturación (intensidad del color) y valor (brillo). Esta conversión facilita la manipulación y segmentación de colores específicos.

3. **Crea máscaras individuales para cada color basadas en los umbrales de color definidos arbitrariamente:** Se establecen umbrales de color en el espacio HSV. Se crea una máscara individual para cada color, donde los píxeles que cumplen ciertos criterios (dentro del rango de umbrales) se marcan como blancos, mientras que los demás píxeles se marcan como negros.

4. **Encuentra contornos en las máscaras combinadas:** Las máscaras individuales se combinan en una sola máscara. Luego, se buscan los contornos en esta máscara combinada. Los contornos son bordes cerrados que rodean áreas de interés en la imagen.

5. **Clasifica los contornos en listas según el color:** Los contornos encontrados se clasifican en diferentes listas, una para cada color. Esto se basa en los criterios de color definidos en los umbrales. Cada contorno se verifica para determinar a qué color corresponde y se agrega a la lista correspondiente.

6. **Dibuja contornos y agrega información de conteo en la imagen original:** Los contornos clasificados se dibujan en la imagen original. Cada lista de contornos tiene su propio color de contorno. Además, se agrega texto en la imagen para mostrar la cantidad de contornos encontrados para cada color.

7. **Muestra las imágenes procesadas en ventanas emergentes:** Finalmente, se muestra la imagen original con los contornos y la información de conteo en una ventana emergente utilizando la biblioteca de interfaz gráfica de usuario Swing. Esto permite visualizar de manera interactiva los resultados del procesamiento de las imágenes.
