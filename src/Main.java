public class Main {
    public static void main(String[] args) {
        String plaintext = "Cerberus Algorithm";
        String masterKey = "SPKSecre"; // Must be 8 characters

        System.out.println("Original Plaintext: " + plaintext);
        System.out.println("Master Key: " + masterKey);

        CerberusCipher cipher = new CerberusCipher();

        // --- ENCRYPTION ---
        String ciphertext = cipher.encrypt(plaintext, masterKey);
        System.out.println("\nEncrypted Ciphertext: " + ciphertext);

        // --- DECRYPTION ---
        String recoveredPlaintext = cipher.decrypt(ciphertext, masterKey);
        System.out.println("Recovered Plaintext: " + recoveredPlaintext);

        // --- VERIFICATION ---
        System.out.println("\n--- VERIFICATION ---");
        if (plaintext.equals(recoveredPlaintext)) {
            System.out.println("SUCCESS: The recovered plaintext matches the original.");
        } else {
            System.out.println("FAILURE: The recovered plaintext does NOT match the original.");
        }
    }
}