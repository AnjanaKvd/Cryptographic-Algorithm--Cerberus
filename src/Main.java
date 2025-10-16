public class Main {
    public static void main(String[] args) {
        String plaintext = "ATTACK!!";
        String masterKey = "SECUREKY";

        CerberusCipher cipher = new CerberusCipher();

        // --- ENCRYPTION ---
        String ciphertext = cipher.encrypt(plaintext, masterKey);

        // --- DECRYPTION ---
        String recoveredPlaintext = cipher.decrypt(ciphertext, masterKey);

        // --- FINAL SUMMARY ---
        System.out.printf("\n\nFinal Plaintext : '%s'\n", plaintext);
        System.out.printf("Final Ciphertext: '%s'\n", ciphertext);
        System.out.printf("\n\nRecovered Plaintext: '%s'\n", recoveredPlaintext);

        // --- VERIFICATION ---
        System.out.println("\n--- VERIFICATION ---");
        if (plaintext.equals(recoveredPlaintext)) {
            System.out.println("SUCCESS: The recovered plaintext matches the original plaintext.");
        } else {
            System.out.println("FAILURE: The recovered plaintext does NOT match the original.");
        }
    }
}