import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.common.io.LittleEndianDataInputStream;

public class TEST_BINARY_FILE {
	
	static String fileName = "bancada-a320-conf-3100-sol101.op2";
	//static String fileName = "tocho-interno-loop-03.op2";
	//static String fileName = "bdf-and-op2.op2";

	
	private static LittleEndianDataInputStream fileInputStream = null;
	static FileInputStream inputStream;
	private static boolean finDeArchivo = false;
	private static int valor;
	private static String dataBlockName;
	
	
	public static void main(String[] args) {
		try {
			inputStream = new FileInputStream(fileName);
		
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File f = new File(fileName);
		System.out.println("TAMAÑO DEL ARCHIVO\t"+f.length());
		fileInputStream = new LittleEndianDataInputStream(inputStream);
		try {
			readOP2Header();
			while(!finDeArchivo){
				finDeArchivo = leeDataBlock();
			}
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static boolean leeDataBlock() {
		boolean finDataBlock = false;
		while(!finDataBlock){
			valor = readTri(fileInputStream);
			if(valor == 0)return true;
			finDataBlock = leeDataRecord(valor);
		}
		System.out.println("\n");
		return false;
	}
	private static boolean leeDataRecord(int valor2) {
		System.out.print("Data Record\t"+valor2+"\t");
		int numeroDeDatos;
		if(valor2 == 2){
			dataBlockName = leeCabeceroDataBlock();
			return false;
		}
		else if(valor2 <0){
			readTri(fileInputStream);//1
			readTri(fileInputStream);//0
			numeroDeDatos = readTri(fileInputStream);//4  num bytes*4  4
			System.out.print(numeroDeDatos*4 +"\t");
			if(numeroDeDatos == 0)return true;
			procesarDatos(valor2, numeroDeDatos, dataBlockName);
			return false;
		}
		else{
			procesarDatos(valor2);
			return false;
		}
	}

	private static void procesarDatos(int numeroDeDatos) {
		try {
			fileInputStream.skipBytes(numeroDeDatos*4 + 8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void procesarDatos(int valor, int numeroDeDatos, String dataBlockName2) {
		if(valor == -3 && dataBlockName2.startsWith("O")){
			System.out.println("Es el principio de OFP table, dentro de la palabra de configuracion...");
			
			try {
				fileInputStream.skipBytes(4);//
				System.out.println("Approach code???\t"+fileInputStream.readInt());
				System.out.println("Table code???\t"+fileInputStream.readInt());
				fileInputStream.skipBytes(numeroDeDatos*4-4);
				//fileInputStream.skipBytes(numeroDeDatos*4 + 8);//ESTO ERA EL ORIGINAL QUE FUNCIONA
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				fileInputStream.skipBytes(numeroDeDatos*4 + 8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}

	private static String leeCabeceroDataBlock() {
		String cadena1 = null;
		try {
			fileInputStream.readInt();//8
			cadena1 = readCHAR8(fileInputStream);
			//fileInputStream.skipBytes(4);
			fileInputStream.readInt();//8
			fileInputStream.skipBytes(60);//4 -1 4 4 7 4 28 28b 28 
			System.out.println("\nData Block Name:\t" + cadena1);
		} catch (IOException e) {
			e.printStackTrace();
		}//4
		return cadena1;
		
	}

	private static String readCHAR8(LittleEndianDataInputStream myStream){
		byte[] micadena = new byte[8];
		StringBuilder cadena = new StringBuilder();
		try {
			
			myStream.read(micadena, 0, 8);
			for (int i = 0; i < 8; i++ ){
				cadena.append((char)micadena[i]);
				//System.out.println((char)micadena[i]);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return cadena.toString();
	}
	public static void readOP2Header(){
		byte[] 	myByteDate 	= new byte[12];
		int k;
		try {
			System.out.println("Reading op2 header...");
			fileInputStream.readInt();//4
			fileInputStream.readInt();//3
			System.out.println("FECHA");
			fileInputStream.skipBytes(8);//4 y 12
			fileInputStream.read(myByteDate, 0, 12);
			System.out.println(myByteDate[0]+"/"+myByteDate[4]+"/"+myByteDate[8]);//Fecha5/7/15 (12 bytes)
			System.out.println(fileInputStream.readInt());//12
			
		} catch (IOException e) {
			e.printStackTrace();
		}
       
		k=readTri(fileInputStream);//4 7 4
		readChars(fileInputStream, k);//28 28bytes 28 	----- NASTRAN FORT TAPE ID CODE - 
		k=readTri(fileInputStream);//4 2 4
		readChars(fileInputStream, k);// 8 8bytes 8		----- XXXXXXXXX
		k=readTri(fileInputStream);//4 -1 4
  		k=readTri(fileInputStream);//4 0 4 
  		System.out.println("End of op2 header.");
  		System.out.println("********************************************************************************");
	}
	private static int readTri(LittleEndianDataInputStream myStream){
		Integer j = null;
		try {
			myStream.skipBytes(4);
			j = myStream.readInt();
			myStream.skipBytes(4);
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return j.intValue();
		
	}
	private static String readChars(LittleEndianDataInputStream myStream, int numberInts){
		byte[] micadena = new byte[numberInts*4];
		StringBuilder cadena = new StringBuilder();
		try {
			myStream.readInt();
			myStream.read(micadena, 0, numberInts*4);
			for (int i = 0; i < numberInts*4; i++ ){
				cadena.append((char)micadena[i]);
				//System.out.println((char)micadena[i]);
			}
			System.out.println(cadena);
			myStream.readInt();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return cadena.toString().trim();
		
	}
}
