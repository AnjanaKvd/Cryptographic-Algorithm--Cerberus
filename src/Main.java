import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- USER INPUT ---
        System.out.print("Enter the plaintext: ");
        String plaintext = scanner.nextLine();

        System.out.print("Enter the master key: ");
        String masterKey = scanner.nextLine();

        // --- ENCRYPTION / DECRYPTION ---
        CerberusCipher cipher = new CerberusCipher();

        // --- ENCRYPTION ---
        String ciphertext = cipher.encrypt(plaintext, masterKey);

        // --- DECRYPTION ---
        String recoveredPlaintext = cipher.decrypt(ciphertext, masterKey);

        // --- FINAL OUTPUT ---
        System.out.printf("\nFinal Plaintext : '%s'\n", plaintext);
        System.out.printf("Final Ciphertext: '%s'\n", ciphertext);
        System.out.printf("Recovered Plaintext: '%s'\n", recoveredPlaintext);

        // --- VERIFICATION ---
        System.out.println("\n--- VERIFICATION ---");
        if (plaintext.equals(recoveredPlaintext)) {
            System.out.println("SUCCESS: The recovered plaintext matches the original plaintext.");
        } else {
            System.out.println("FAILURE: The recovered plaintext does NOT match the original.");
        }

        scanner.close();
    }
}
