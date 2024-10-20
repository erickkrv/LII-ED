package org.example;

public class DES {
    //Tabla de paridad
    private static final int[] ParityDropTable = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };
    //Tabla de desplazamiento
    private static final int[] ShiftTable = {
            1, 1, 2, 2, 2, 2, 2, 2,
            1, 2, 2, 2, 2, 2, 2, 1
    };
    //Tabla de compresión de llave
    private static final int[] KeyCompressionTable = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };
    //Tabla de permutación inicial
    private static final int[] InitialPermutationTable = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };
    //Tabla de expansión
    private static final int[] ExpansionPermutationTable = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };
    //SBox para la sustitución
    private static final int[][][] SBoxTable = {
            {
                    { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
                    { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
                    { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
                    { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }
            },
            {
                    { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
                    { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
                    { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
                    { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }
            },
            {
                    { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
                    { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
                    { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
                    { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }
            },
            {
                    { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
                    { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
                    { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
                    { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }
            },
            {
                    { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
                    { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
                    { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
                    { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }
            },
            {
                    { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
                    { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
                    { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
                    { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }
            },
            {
                    { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
                    { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
                    { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
                    { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }
            },
            {
                    { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
                    { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
                    { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
                    { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 }
            }
    };
    //PBox para la permutación
    private static final int[] PBoxPermutationTable = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };
    //Tabla de permutación final
    private static final int[] FinalPermutationTable = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };
    //Método para procesar el DES
    private static byte[] procesarDES(byte[] inputData, byte[] encKey, boolean asc){
        byte[] processedData = new byte[inputData.length]; //Aquí se guardarán los datos procesados
        int contBloque = inputData.length / 8; //Contador de bloques de 8 bytes
        byte[][] roundKeys = generarKey(encKey, asc); //Se generan las llaves de cada ronda a partir de la llave de cifrado
        byte[] blockBuffer = new byte[8]; //Buffer para almacenar bloque actual
        //Arreglo para mitad izquierda y luego derecha (4 bytes cada uno)
        byte[] leftHalf = new byte[4];
        byte[] rightHalf = new byte[4];
        //Arreglos para expansión, sustitución y temporal
        byte[] expandedRightHalf;
        byte[] subRightHalf = new byte[4];
        byte[] tempD;

        //Itera sobre cada bloque
        for(int blockNum = 0; blockNum < contBloque; blockNum++){
            System.arraycopy(inputData, blockNum * 8, blockBuffer, 0, 8); //Copia el bloque actual al buffer
            blockBuffer = permutar(blockBuffer, InitialPermutationTable); //Permuta el bloque actual con la tabla de permutación inicial
            //Se realizan las 16 rondas
            for(int round = 0; round < 16; round++){
                //Divide el bloque actual en dos mitades
                System.arraycopy(blockBuffer, 0, leftHalf, 0, 4);
                System.arraycopy(blockBuffer, 4, rightHalf, 0, 4);
                //Expande la mitad derecha
                expandedRightHalf = permutar(rightHalf, ExpansionPermutationTable);
                //XOR entre la mitad derecha expandida y la llave de la ronda actual
                expandedRightHalf = XOR(expandedRightHalf, roundKeys[round]);

                //Se itera sobre cada sección de 6 bits
                for(int section = 0; section < 8; section++){
                    //Se calcula la fila para la sustitución
                    int row = (obtenerBit(expandedRightHalf, section * 6) << 1 | obtenerBit(expandedRightHalf, section * 6 + 5));
                    //Se calcula la columna para la sustitución
                    int column = 0;
                    for(int bitIndex = 0; bitIndex < 4; bitIndex++){
                        column |= obtenerBit(expandedRightHalf, section * 6 + bitIndex + 1) << (3 - bitIndex);
                    }
                    //Se obtiene el valor de la SBox para la fila y columna calculadas
                    int sBoxValue = SBoxTable[section][row][column];
                    //Se asignan los bits de la sustitución a la mitad derecha sustituida
                    for(int bitIndex = 0; bitIndex < 4; bitIndex++){
                        colocarBit(subRightHalf, section * 4 + bitIndex, (sBoxValue >> (3 - bitIndex)) & 1);
                    }
                }

                //Se permuta la mitad derecha sustituida con la tabla de permutación PBox
                subRightHalf = permutar(subRightHalf, PBoxPermutationTable);
                //Se realiza el XOR entre la mitad izquierda y la mitad derecha sustituida
                tempD = XOR(leftHalf, subRightHalf);

                //Se intercambian las mitades, menos en la ultima ronda.
                if(round != 15){
                    System.arraycopy(rightHalf, 0, blockBuffer, 0, 4); //Se copia la mitad derecha a la mitad izquierda
                    System.arraycopy(tempD, 0, blockBuffer, 4, 4); // Se copia la mitad derecha sustituida a la mitad derecha
                }
                else{
                    //Si es la ultima ronda, no se intercambian
                    System.arraycopy(tempD, 0, blockBuffer, 0, 4); // Se copia la mitad derecha sustituida a la mitad izquierda
                    System.arraycopy(rightHalf, 0, blockBuffer, 4, 4); // Mitad derecha idéntica
                }
            }

            //Se permuta el bloque actual con la tabla de permutación final
            blockBuffer = permutar(blockBuffer, FinalPermutationTable);
            //Se copia el bloque procesado al arreglo de datos procesados
            System.arraycopy(blockBuffer, 0, processedData, blockNum * 8, 8);
        }
        //Se retorna el resultado final.
        return processedData;
    }

    //Método para encriptar
    public static byte[] encriptar(byte[] data, byte[] key, boolean addPadding){
        //Se verifica que la llave tenga 8 bytes
        if(key.length != 8){
            throw new IllegalArgumentException("La llave debe tener 8 bytes");
        }
        //Si se necesita padding, se agrega usando PKCS7
        if(addPadding){
            data = AddPkcs7Padding(data, 8);
        }
        //Se llama el método de retornar, el parámetro 'true' indica que es cifrado.
        return procesarDES(data, key, true);
    }

    //Método para desencriptar
    public static byte[] desencriptar(byte[] data, byte[] key, boolean removePadding){

        //Se verifica que la llave tenga 8 bytes
        if(key.length != 8){
            throw new IllegalArgumentException("La llave debe tener 8 bytes");
        }
        //Verifica que los datos sean múltiplos de 8
        if(data.length % 8 != 0){
            throw new IllegalArgumentException("Los datos no son múltiplos de 8");
        }
        //Se llama el método de retornar, el parámetro 'false' indica que es descifrado, y se almacenará en 'res'
        var res = procesarDES(data, key, false);
        //Si hay que quitar el padding, se llama al método correspondiente
        if(removePadding){
            res = RemovePkcs7Padding(res);
        }
        return res;
    }

    //Método para generar las llaves de cada ronda
    public static byte[][] generarKey(byte[] initialKey, boolean asc){
        byte[][] roundKeys = new byte[16][]; //Arreglo para almacenar las llaves de cada ronda
        byte[] permKey = permutar(initialKey, ParityDropTable); //Se permuta la llave inicial con la tabla de paridad
        //Se generan las llaves de cada ronda
        for(int round = 0; round < 16; round++){
            //Se divide la llave en dos mitades, cada una de 28 bits
            byte[] leftHalf = selBits(permKey, 0, 28);
            byte[] rightHalf = selBits(permKey, 28, 28);

            //Se desplazan las mitades a la izquierda según la tabla de desplazamiento
            leftHalf = LeftShift(leftHalf, 28, ShiftTable[round]);
            rightHalf = LeftShift(rightHalf, 28, ShiftTable[round]);

            byte[] combinedKey = JoinKey(leftHalf, rightHalf); //Se unen las mitades
            //Se permuta la llave combinada con la tabla de compresión de llave
            roundKeys[round] = permutar(combinedKey, KeyCompressionTable);
            permKey = combinedKey; //Se actualiza la llave para la siguiente ronda
        }
        if(!asc){ //Si 'asc' es falso (descifrado) se invierten las llaves
            byte[][] reversedKeys = new byte[16][]; //Se crea un arreglo para las llaves invertidas
            //Se invierten las llaves
            for(int i = 0; i < 16; i++){
                reversedKeys[i] = roundKeys[15 - i];
            }
            //Se retorna el arreglo de llaves invertidas
            return reversedKeys;
        }
        //Se retorna el arreglo de llaves si es cifrado.
        return roundKeys;
    }

    //Método para permutar según una tabla
    private static byte[] permutar(byte[] source, int[] table){
        int length = (table.length - 1) / 8 + 1; //Se calcula la longitud del arreglo resultante
        byte[] res = new byte[length]; //Se crea el arreglo resultante
        //Itera sobre cada posición de la tabla
        for(int i = 0; i < table.length; i++){
            colocarBit(res, i, obtenerBit(source, table[i] - 1)); //Se asigna el bit correspondiente
        }
        return res; //Retorna el arreglo resultante
    }

    //Método para realizar un desplazamiento a la izquierda
    private static byte[] LeftShift(byte[] data, int len, int sPos){
        byte[] outer = new byte[(len - 1) / 8 + 1]; //Se crea un arreglo para el resultado
        for(int i = 0; i < len; i++){ //Itera sobre cada bit
            int val = obtenerBit(data, (i + sPos) % len); //Obtiene el valor del bit en la posición desplazada
            colocarBit(outer, i, val); //Coloca el bit en 'outer'.
        }
        return outer; //Retorna el arreglo con bits desplazados.
    }

    //Método para realizar un XOR entre dos arreglos de bytes
    private static byte[] XOR(byte[] first, byte[] second){
        byte[] result = new byte[first.length]; //Se crea un arreglo para el resultado
        for(int i = 0; i < first.length; i++){ //Itera sobre cada byte del primer arreglo
            result[i] = (byte)(first[i] ^ second[i]); //Realiza el XOR entre los bytes y lo asigna al resultado
        }
        return result; //Retorna el resultado
    }

    //Método para obtener un bit
    private static int obtenerBit(byte[] data, int pos){
        //Se obtiene la posición del byte y el bit
        int posByte = pos / 8;
        int posBit = pos % 8;

        //Se retorna el bit en la posición correspondiente (se desplaza el byte a la derecha, y luego se hace un AND para obtener solo el valor de ese bit).
        return (data[posByte] >> (7 - posBit)) & 1;
    }

    //Método para colocar un bit en cierta posición de un arreglo.
    private static void colocarBit(byte[] data, int pos, int val){
        //Se calcula la posición del byte y del bit dentro de ese byte.
        int posByte = pos / 8;
        int posBit = pos % 8;
        //Si el bit a colocar es 1
        if(val == 1){
            data[posByte] |= (byte)(1 << (7 - posBit)); //Se hace un OR con una máscara que tiene un 1 en la posición correspondiente.
        }
        else{
            data[posByte] &= (byte)~(1 << (7 - posBit)); //Se hace un AND con una máscara que tiene un 0 en la posición correspondiente.
        }
    }

    //Arreglo para seleccionar un rango de bits en un arreglo de bytes.
    private static byte[] selBits(byte[] data, int start, int len){
        byte[] res = new byte[(len - 1) / 8 + 1]; //Se crea un array para almacenar los bits seleccionados.
        //Se itera sobre cada bit que se desea
        for(int i = 0; i < len; i++){
            int bit = obtenerBit(data, start + i); //Se obtiene el bit en la posición correspondiente
            colocarBit(res, i, bit); //Se coloca el bit en la posición correspondiente del arreglo resultante.
        }
        return res; //Se regresa el arreglo resultante.
    }

    //Método para combinar dos mitades de una clave en un solo array.
    private static byte[] JoinKey(byte[] left, byte[] right){
        byte[] res = new byte[7]; //Se crea un arreglo de 7 bytes para almacenar la clave combinada.
        //Se copian los tres primeros bytes en la parte izquierda del resultado
        for(int i = 0; i < 3; i++){
            res[i] = left[i];
        }
        //Se copian los cuatro últimos bits en la parte derecha del resultado
        for(int i = 0; i < 4; i++){
            int val = obtenerBit(left, 24 + i); //Se obtienen los bits de la posición 24 al 27 de la izquierda.
            colocarBit(res, 24 + i, val); //Se colocan los bits en la posición 24 al 27 del resultado.
        }
        //Se copian los 28 bits de la parte derecha en el resultado.
        for(int i = 0; i < 28; i++){
            int val = obtenerBit(right, i); //Obtiene los bits 0 al 27 de la derecha
            colocarBit(res, 28 + i, val); //Coloca esos bits en las posiciones 28 al 55 del resultado.
        }
        return res; //Se retorna el resultado.
    }

    //Método para agregar padding PKCS7 a los datos para que su longitud sea multiplo de 8.
    public static byte[] AddPkcs7Padding(byte[] data, int blockSize){
        //Se verifica que los datos no sean nulos
        if(data == null){
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }
        //Se verifica que el tamaño del bloque sea mayor a 0
        if(blockSize <= 0){
            throw new IllegalArgumentException("El tamaño del bloque debe ser mayor a 0");
        }
        int count = data.length; //Obtiene la longitud de los datos
        int paddingRemainder = count % blockSize; //Calcula el residuo de la división de la longitud de los datos entre el tamaño del bloque
        int paddingSize = blockSize - paddingRemainder; //Calcula el tamaño del padding que se necesita.

        //Si el tamaño del padding es 0, se agrega un bloque completo de padding
        if(paddingSize == 0){
            paddingSize = blockSize;
        }
        //Se crea un nuevo array para los datos con el padding puesto.
        byte[] paddedData = new byte[count + paddingSize];
        //Se copian los datos originales al nuevo array.
        System.arraycopy(data, 0, paddedData, 0, count);
        //El valor del byte del padding es IGUAL al tamaño del padding.
        byte paddingByte = (byte)paddingSize;
        //Se agrega el padding al final del array.
        for(int i = count; i < paddedData.length; i++){
            paddedData[i] = paddingByte;
        }
        return paddedData; //Se retornan los datos con el padding agregado.
    }

    //Método para eliminar el padding de un array de bytes.
    public static byte[] RemovePkcs7Padding(byte[] paddedByteArray){
        //Se verifica que los datos no sean nulos
        if(paddedByteArray == null){
            throw new IllegalArgumentException("Los datos no pueden ser nulos");
        }
        //Se verifica que los datos no estén vacíos
        if(paddedByteArray.length == 0){
            throw new IllegalArgumentException("Los datos no pueden estar vacíos");
        }

        //Se obtiene el tamaño del padding desde el último byte del array
        int paddingSize = paddedByteArray[paddedByteArray.length - 1];
        //Se verifica que el tamaño del padding sea válido
        if(paddingSize > paddedByteArray.length){
            throw new IllegalArgumentException("El tamaño del padding es mayor al tamaño de los datos");
        }
        //Se verifica que todos los bytes del padding sean iguales al tamaño del padding.
        for(int  i = paddedByteArray.length - paddingSize; i < paddedByteArray.length; i++){
            if(paddedByteArray[i] != paddingSize){
                throw new IllegalArgumentException("El padding no es válido");
            }
        }
        //Se calcula la longitud del resultado sin padding
        int resL = paddedByteArray.length - paddingSize;
        //Se crea un nuevo array con la longitud calculada (sin padding)
        byte[] res = new byte[resL];
        //Copia los datos originales al nuevo array.
        System.arraycopy(paddedByteArray, 0, res, 0, resL);
        //Devuelve el resultado
        return res;
    }
}
