import java.util.*;
import java.io.*;
import java.math.BigInteger;

public class RSA_Encrypt {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner console = new Scanner(System.in);
		
		System.out.print("Please enter the name of the file you want to encrypt: ");
		String inputFile = console.nextLine().trim();
		Scanner input = new Scanner(new File(inputFile));
		System.out.print("Please enter the name of the file you want to put the encrypted text into: ");
		String encryptedFile = console.nextLine().trim();
		PrintStream encryptedOutput = new PrintStream(new File(encryptedFile));
		PrintStream keyOutput = new PrintStream(new File(encryptedFile + "_key"));
		encryptFile(input, encryptedOutput, keyOutput);
		
		
		
//		System.out.print("Please enter the text message you want to encrypt: ");
//		String input = console.nextLine();
//		
//		ArrayList<Map<String, BigInteger>> encryptionList = encryption(input);
//		System.out.print("Encrypted Message: ");
//		printEncryptionMessage(encryptionList);
//		System.out.print("Decrypted Message: ");
//		printDecryptionMessage(encryptionList);
	}
	
	public static void encryptFile(Scanner input, PrintStream encryptedOutput, PrintStream keyOutput) {
		while (input.hasNextLine()) {
			String line = input.nextLine();
			ArrayList<Map<String, BigInteger>> encryptionList = encryption(line);
			printEncryptionMessage(encryptionList, encryptedOutput);
			printKey(encryptionList, keyOutput);
			keyOutput.println("*");
		}
	}
	
	
	public static void printEncryptionMessage(ArrayList<Map<String, BigInteger>> encryptionList, PrintStream output) {
		for (Map<String, BigInteger> map : encryptionList) {
			output.println(map.get("c"));
			output.println(map.get("n"));
		}
	}
	
	public static void printKey(ArrayList<Map<String, BigInteger>> encryptionList, PrintStream output) {
		for (Map<String, BigInteger> map : encryptionList) {
			output.println(map.get("d"));
		}
	}
	
	public static void printDecryptionMessage(ArrayList<Map<String, BigInteger>> encryptionList) {
		for (Map<String, BigInteger> map : encryptionList) {
			BigInteger bigC = map.get("c");
			BigInteger bigD = map.get("d");
			BigInteger bigN = map.get("n");
			BigInteger result = bigC.modPow(bigD, bigN);
			System.out.print((char)(result.intValue()));
		}
		System.out.println();
	}
	
	public static ArrayList<Map<String, BigInteger>> encryption(String input) {
		ArrayList<Map<String, BigInteger>> result = new ArrayList<>();
		for (int i = 0; i < input.length(); i++) {
			result.add(encryptChar(input.charAt(i)));
		}
		return result;
	}
	
	public static Map<String, BigInteger> encryptChar(char inputChar) {
		List<BigInteger> prime = new ArrayList<>();
		Map<String, BigInteger> result = new TreeMap<>();
		Random random = new Random();
		
		int count = 0;
		while (count < 2) {
			BigInteger bigNum = new BigInteger(15, random);
			if (isPrime(bigNum)) {
				count++;
				prime.add(bigNum);
			}
		}
		
		int m = (int) inputChar;
		BigInteger bigM = new BigInteger(Integer.toString(m));
		BigInteger bigN = prime.get(0).multiply(prime.get(1));
		BigInteger bigPhi = (prime.get(0).subtract(BigInteger.ONE)).multiply((prime.get(1).subtract(BigInteger.ONE)));
		BigInteger bigE = generateE(bigPhi);
		BigInteger bigD = generateD(bigPhi, bigE);
		BigInteger bigC = bigM.modPow(bigE, bigN);
		result.put("c", bigC);
		result.put("d", bigD);
		result.put("n", bigN);
		return result;
	}
	
	public static BigInteger generateD(BigInteger bigPhi, BigInteger bigE) {
		boolean isFound = false;
		BigInteger bigK = BigInteger.ONE;
		BigInteger bigD = BigInteger.ONE;
		while (!isFound) {
			if ((BigInteger.ONE.add(bigK.multiply(bigPhi))).mod(bigE).compareTo(BigInteger.ZERO) == 0) {
				bigD = (BigInteger.ONE.add(bigK.multiply(bigPhi))).divide(bigE);
				isFound = true;
			} else {
				bigK = bigK.add(BigInteger.ONE);
			}
		}
		return bigD;
	}
	
	public static boolean isPrime(BigInteger bigNum) {
		if (bigNum.compareTo(BigInteger.ZERO) == 0 || bigNum.compareTo(BigInteger.ONE) == 0) {
			return false;
		}
		for (BigInteger bi = BigInteger.TWO; bi.compareTo(bigNum) < 0; bi = bi.add(BigInteger.ONE)) {
			if (bigNum.mod(bi).compareTo(BigInteger.ZERO) == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static BigInteger generateE(BigInteger bigPhi) {
		BigInteger bigE = BigInteger.TWO;
		boolean isFound = false;
		while (!isFound && bigE.compareTo(bigPhi) < 0) {
			if (bigE.mod(BigInteger.TWO).compareTo(BigInteger.ONE) == 0 
				&& gcd(bigPhi, bigE).compareTo(BigInteger.ONE) == 0) {
				isFound = true;
			} else {
				bigE = bigE.add(BigInteger.ONE);
			}
		}
		return bigE;
	}
	
	public static BigInteger gcd(BigInteger num1, BigInteger num2) {
		if (num2.compareTo(BigInteger.ZERO) == 0) {
			return num1;
		} else {
			return gcd(num2, num1.mod(num2));
		}
	}
}
