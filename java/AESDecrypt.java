/*
* Mark Thompson
* CS449 
* Homework 1
* Question 2
* 
* file: AESDecrypt.java
*/
 
import java.io.*;
import java.util.*;
import javax.crypto.*;
import java.math.*;
import java.lang.Object.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import javax.crypto.spec.*;
import java.util.Base64;
import javax.crypto.spec.IvParameterSpec;

public class AESDecrypt{

	public static String key;
	public static byte[] bkey;
	public static String iv;
	public static byte[] biv;

	public static final int cbc_IV_LENGTH = 12;
    public static final int cbc_TAG_LENGTH = 16;

    public static void main(String args[])
    {
    	if(argCheck(args)){
			// System.out.println("Formatted Correctly and files readable");
			if(extract(args)){
				System.out.println(key);
				System.out.println("Key length " + key.length());
				System.out.println(iv);
				System.out.println("IV length " + iv.length());
				try{
					byte[] encryptedMsg = process(args[1]);
					System.out.println("process worked");
					String dec = decrypt(encryptedMsg, key , iv );
					System.out.println(dec);	
				}
				catch(Exception e)
				{
					System.out.println("Unable to decrypt" + e);
				}
			}
			else{
				System.out.println("Unable to extract secure_channel");
			}

    	}
		
	}

	/* get key from String*/
	public static SecretKey keyFromString(String keyStr)
	{
		byte[] decodedKey = Base64.getDecoder().decode(keyStr);
		SecretKey sk = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return sk;
	}

	/* returns a byte array from the file passed to it. */
	public static byte[] process(String fname)
	{
		FileInputStream fin = null;
		byte[] buffer = new byte[16];
		try
		{
			File inpt = new File(fname);
			fin = new FileInputStream(inpt);
			buffer = new byte[(int)inpt.length()];
			fin.read(buffer);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found");
		}
		catch(IOException ioe){
			System.out.println("Unable to read file");
		}
		return buffer;
	}

	// public String toHex(String arg) {
	//     return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
	// }

/* decrypt */


	/*
	* Decrypt one block of cipher text.
	* credit for this function taken from ;
	* https://javainterviewpoint.com/java-aes-256-gcm-encryption-and-decryption/
	*/
	public static String decrypt(byte[] cipherText, String key, String iv) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(bkey, "AES");
        // Create ivSpec
        IvParameterSpec ivSpec = new IvParameterSpec(biv);
        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(Base64.getDecoder().decode(cipherText));   
        return new String(decryptedText);
    }

    public static byte[] toByteArray(String s)
    {
    	System.out.println("Reading : "+s);
    	int len = s.length();
    	byte[] data = new byte[len /2];
    	for(int i=0; i< len; i += 2){
    		System.out.print(s.charAt(i));
    		System.out.print(s.charAt(i+1)+" ");
    		data[i/2] = (byte) ((Character.digit(s.charAt(i),16) << 4) 
    			+ Character.digit(s.charAt(i+2),16));
    	}
    	return data;
    }

	/*
	* Extracts key and iv from secure_channel
	*/
	public static boolean extract(String args[])
	{
		Scanner sc;
		File sec = new File(args[5]);
		if(sec.canRead()){
			/*continue to extract*/
			try{
				sc = new Scanner(sec);
				while(sc.hasNextLine()){
					key = sc.nextLine();
					key = key.substring(4,key.length());
					bkey = toByteArray(key);
					iv = sc.nextLine();
					System.out.println("iv after sc.nextLine" + iv);
					iv = iv.substring(4,iv.length());
					System.out.println("iv after iv.substring" + iv);
					biv = toByteArray(iv);
				}
			}
			catch(Exception e){
				System.out.println("unable to open file.");
			}

			return true;
		}
		{
			/* unable to read secure_channel*/
			System.out.println("Unable to read secure_channel file");
			return false;
		}
	}

	/*
	* Check the arugments to make sure it's properly formatted
	*/
	public static boolean argCheck(String args[])
	{
		if(args.length != 6)
    	{
    		formatE();
    		return false;
    	}
    	else{
    		if(args[0].compareTo("-in") != 0 
    			|| args[2].compareTo("-out") != 0 
    			|| args[4].compareTo("-sec") != 0)
    		{
    			formatE();
    			return false;
    		}
    		else
    		{
    			File i = new File(args[1]);
		    	File s = new File(args[5]);
		    	if(i.canRead() && s.canRead())
		    	{
		    		return true;
		    	}
		    	else
		    	{
		    		System.out.println("Files not found or cannot be read");
		    		return false;
		    	}
    		}
    	}
	}

	/*
	* Prints an error message if the format of the arguments is invalid.
	*/
    public static void formatE()
    {
    	System.out.println("Invalid format");
    	System.out.println("java AESDecrypt -in ciphertext -out plaintext -sec secure_channel");
    }


}	