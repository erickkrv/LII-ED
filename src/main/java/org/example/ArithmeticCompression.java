package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ArithmeticCompression {
    private LinkedHashMap<Character, Range> probabilidades;
    private String source;
    private long underflow_bits;

    private static final int default_low = 0;
    private static final int default_high = 0xffff;

    private static final int MSD = 0x8000;

    private static final int SSD = 0x4000;

    private int scale;

    public ArithmeticCompression(String source){
        this.source = source;
        this.probabilidades = new LinkedHashMap<Character, Range>();
        CalcularProbabilidades();
    }
    public ArithmeticCompression(LinkedHashMap<Character, Range> probabilidades, int scale){
        this.source = "";
        this.probabilidades = probabilidades;
        this.scale = scale;
    }
    private void CalcularProbabilidades() {
        //Identificar que tanto se repite cada símbolo
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char symbol : source.toCharArray()) {
            frequencies.put(symbol, frequencies.getOrDefault(symbol, 0) + 1);
        }

        //Ordenar las frecuencias por valor y luego por clave
        Map<Character, Integer> sortedFrequencies = frequencies.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        //Almacenar la escala de la cadena
        scale = source.length();

        int low = 0;

        //Iterar a través de cada símbolo y calcular su rango
        for (Map.Entry<Character, Integer> entry : sortedFrequencies.entrySet()) {
            int high = low + entry.getValue();
            probabilidades.put(entry.getKey(), new Range(low, high));
            low = high;
        }

        // Imprimir probabilidades (debug)
//        for (Map.Entry<Character, Range> entry : probabilidades.entrySet()) {
//            System.out.println("Symbol: " + entry.getKey() + " - Low: " + entry.getValue().getLow() + " - High: " + entry.getValue().getHigh());
//        }
    }

    public ByteArrayOutputStream Compress(String input) {
        //Inicializar low y high
        int low, high;

        //Inicializar el buffer de salida
        BitWriter output_stream = new BitWriter();

        //Inicializar los valores de low y high
        low = default_low;
        high = default_high;
        underflow_bits = 0;
        long range;
        String output = "";

        //Iterar a través de cada símbolo de la cadena de entrada
        for (char symbol : input.toCharArray()) {
            //Letra a evaluar (debug)
//            System.out.println("SIMBOLO EVALUADO: " + symbol);
            //Calcular el nuevo rango
            range = (long)(high - low) + 1;
            //Calcular el nuevo low y high
            high = (int)(low + range * probabilidades.get(symbol).getHigh() / scale - 1);
            low = (int)(low + range * probabilidades.get(symbol).getLow() / scale);

            // Normalizar los rangos si es necesario
            while (true) {
                //Si los bits más significativos de low y high son iguales
                if ((high & MSD) == (low & MSD)) {
                    //Escribir el bit más significativo en el buffer de salida
                    output_stream.WriteBit((high & MSD) != 0);
                    output += (high & MSD) != 0 ? "1" : "0";
                    //Debug
//                    System.out.println("Quedan bits? " + (underflow_bits > 0));
                    //Escribir los bits de underflow si es necesario
                    while (underflow_bits > 0) {
                        //Escribir el complemento del bit más significativo en el buffer de salida
                        output_stream.WriteBit((high & MSD) == 0);
                        output += (~high & MSD) == 0 ? "0" : "1"; //Agregar el bit al string de salida
                        underflow_bits--; //Decrementar el número de bits de underflow
                        //Debug
//                        System.out.println("Output Compressed String with Underflow: " + output);
                    }
                } else{
                    //Si el bit más significativo de low es 1 y el de high es 0
                    if ((low & SSD) != 0 && (high & SSD) == 0) {
                        //Incrementar el número de bits de underflow
                        underflow_bits++;
                        //Normalizar los rangos
                        low = low & 0x3fff; //Mantener los 14 bits menos significativos de low
                        high = high | 0x4000; //Establecer el bit más significativo de high en 1
                        //Debug
//                        System.out.println("Underflow: " + underflow_bits);
                    } else {
                        break;
                    }
                }
//                //Debug
//                System.out.println("Antes de mover: ");
//                System.out.println("Low: " + low + " - " + Integer.toBinaryString(low) + " - High: " + high + " - " + Integer.toBinaryString(high));
                //Mover bits a la izquierda
                low = (low << 1) & 0xFFFF; //Desplazar low un bit a la izquierda y mantener los 16 bits
                high = (high << 1) & 0xFFFF; //Desplazar high un bit a la izquierda y mantener los 16 bits
                high = high | 1; //Establecer el bit menos significativo de high en 1
                //Debug
//                System.out.println("Luego de mover: ");
//                System.out.println("Low: " + low + " - " + Integer.toBinaryString(low) + " - High: " + high + " - " + Integer.toBinaryString(high));
//                System.out.println("Output Compressed Binary: " + output);
            }
//            System.out.println("---------------------------------------------");
        }

        //Escribir el último byte en el buffer de salida
        output_stream.WriteBit((low & 0x4000) != 0); //Escribir el bit más significativo de low
        output += (low & 0x4000) != 0 ? "1" : "0"; //Agregar el bit al string de salida
        underflow_bits++; //Incrementar el número de bits de underflow

        while (underflow_bits-- > 0) {  //Escribir los bits de underflow, si hay
            output_stream.WriteBit((low & 0x4000) == 0); //Escribir el complemento del bit más significativo de low
            output += (~low & 0x4000) == 0 ? "0" : "1"; //Agregar el bit al string de salida
        }
        //Debug
        //System.out.println("Output Compressed Binary: " + output);

        // Devolver el buffer de salida
        return output_stream.Flush();
    }
    public static String GetBinaryString(ByteArrayOutputStream stream){
        //Obtener el string binario de un ByteArrayOutputStream
        int originalPosition = stream.size();
        //Obtener los bytes del stream
        byte[] bytes = stream.toByteArray();
        //Inicializar un StringBuilder
        StringBuilder binary = new StringBuilder();
        for(byte b : bytes){
            //Convertir cada byte a un string binario de 8 bits
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binary.append(binaryStr);
        }
        //Devolver el string binario
        return binary.toString();
    }
    public String Decompress(ByteArrayInputStream input, int size){
        char c;
        int scaledValue, low, high;
        String retval = "";
        int code = 0;

        // Crear un lector de bits a partir de la entrada
        BitReader input_buffer = new BitReader(input);
        // Leer los primeros 16 bits de la entrada
        for(int i = 0; i < 16; i++){
            code = (code << 1);
            code |= (input_buffer.ReadBit() ? 1 : 0);
        }

        // Inicializar los valores de low y high
        low = default_low;
        high = default_high;

        // Iterar hasta que se alcance el tamaño especificado
        for(int i = 0; ;){
            // Calcular el rango
            long range = (long)(high - low) + 1;
            // Calcular el valor escalado
            scaledValue = (int)(((code - low + 1) * scale - 1) / range);

            // Buscar el símbolo correspondiente al valor escalado
            c = '\0';
            for(Map.Entry<Character, Range> entry: probabilidades.entrySet()){
                if(scaledValue >= entry.getValue().getLow() && scaledValue < entry.getValue().getHigh()){
                    c = entry.getKey();
                    break;
                }
            }
            // Si no se encontró un símbolo, lanzar una excepción
            if(c == '\0'){
                throw new IllegalStateException("Symbol not found");
            }
            // Agregar el símbolo a la salida
            retval += c;

            // Si se ha alcanzado el tamaño especificado, terminar el bucle
            if(++i == size){
                break;
            }

            // Calcular el nuevo rango
            range = (long)(high - low) + 1;
            // Calcular los nuevos valores de low y high
            high = (int)(low + range * probabilidades.get(c).getHigh() / scale - 1);
            low = (int)(low + range * probabilidades.get(c).getLow() / scale);

            // Normalizar los rangos
            while(true){
                if((high & MSD) == (low & MSD)){
                    // Si los bits más significativos de low y high son iguales, no hacer nada
                } else {
                    // Si el bit más significativo de low es 1 y el de high es 0
                    if((low & SSD) == 0x4000 && (high & SSD) == 0){
                        // Aplicar la operación XOR a code y mover los rangos
                        code = code ^ 0x4000; // XOR
                        low = low & 0x3fff; // Mantener los 14 bits menos significativos de low
                        high = high | 0x4000; // Establecer el bit más significativo de high en 1
                    }else{
                        break;
                    }
                }
                // Mover los bits a la izquierda
                low = (low << 1) & 0xFFFF;
                high = (high << 1) & 0xFFFF;
                high = high | 1;
                // Leer el siguiente bit de la entrada
                code = (code << 1) & 0xFFFF;
                code = code | (input_buffer.ReadBit() ? 1 : 0);
            }
        }
        // Devolver la cadena de salida
        return retval;
    }
}
