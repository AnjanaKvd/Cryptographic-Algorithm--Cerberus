import java.util.List;

public class CerberusCipher {
    private final SBox sBox;
    private final PBox pBox;

    public CerberusCipher() {
        this.sBox = new SBox(Configuration.CHARACTER_SET);
        this.pBox = new PBox();
    }

    //key mixing (modular addition)
    private String mixWithKey(String block, String key) {
        StringBuilder mixedBlock = new StringBuilder();
        for (int i = 0; i < block.length(); i++) {
            int blockCharIdx = Configuration.CHAR_TO_INDEX.get(block.charAt(i));
            int keyCharIdx = Configuration.CHAR_TO_INDEX.get(key.charAt(i));
            int newIdx = (blockCharIdx + keyCharIdx) % Configuration.CHAR_SET_SIZE;
            mixedBlock.append(Configuration.INDEX_TO_CHAR.get(newIdx));
        }
        return mixedBlock.toString();
    }

    //key un-mixing (modular subtraction)
    private String unmixWithKey(String block, String key) {
        StringBuilder unmixedBlock = new StringBuilder();
        for (int i = 0; i < block.length(); i++) {
            int blockCharIdx = Configuration.CHAR_TO_INDEX.get(block.charAt(i));
            int keyCharIdx = Configuration.CHAR_TO_INDEX.get(key.charAt(i));
            int newIdx = (blockCharIdx - keyCharIdx + Configuration.CHAR_SET_SIZE) % Configuration.CHAR_SET_SIZE;
            unmixedBlock.append(Configuration.INDEX_TO_CHAR.get(newIdx));
        }
        return unmixedBlock.toString();
    }

    public String encrypt(String plaintext, String masterKey) {
        // Input validation
        for (char c : plaintext.toCharArray()) {
            if (!Configuration.CHARACTER_SET.contains(c)) {
                throw new IllegalArgumentException("Plaintext contains invalid character: " + c);
            }
        }

        System.out.println("========================= ENCRYPTION PROCESS =========================");

        // Padding
        StringBuilder paddedText = new StringBuilder(plaintext);
        while (paddedText.length() % Configuration.BLOCK_SIZE != 0) {
            paddedText.append(Configuration.PAD_CHAR);
        }
        System.out.printf("\n[1] Original Plaintext : '%s'\n", plaintext);
        System.out.printf("[2] Padded Plaintext   : '%s' (%d bytes)\n", paddedText, paddedText.length());

        // Key Generation
        KeyScheduler keyScheduler = new KeyScheduler(masterKey, Configuration.NUM_ROUNDS);
        List<String> roundKeys = keyScheduler.getRoundKeys();
        System.out.println("\n[3] Generated Round Keys:");
        for (int i = 0; i < roundKeys.size(); i++) {
            System.out.printf("    Round Key %d: '%s'\n", i, roundKeys.get(i));
        }

        // Process in blocks
        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < paddedText.length(); i += Configuration.BLOCK_SIZE) {
            String block = paddedText.substring(i, i + Configuration.BLOCK_SIZE);
            int blockNum = (i / Configuration.BLOCK_SIZE) + 1;
            System.out.printf("\n--- Processing Block %d: '%s' ---\n", blockNum, block);

            String currentData = block;

            // Initial Key Mixing
            System.out.println("\n  [Initial Step] Key Mixing");
            String initialMixedData = mixWithKey(currentData, roundKeys.get(0));
            System.out.printf("    - Input  : '%s'\n    - Mix Key: '%s'\n    - Output : '%s'\n",
                    currentData, roundKeys.get(0), initialMixedData);
            currentData = initialMixedData;

            // Rounds
            for (int r = 0; r < Configuration.NUM_ROUNDS; r++) {
                System.out.printf("\n  [Round %d]\n", r + 1);
                String dataBeforeSub = currentData;
                String substitutedData = sBox.substitute(dataBeforeSub);
                System.out.printf("    - (a) Substitution : '%s' -> '%s'\n", dataBeforeSub, substitutedData);

                String permutedData = pBox.permute(substitutedData);
                System.out.printf("    - (b) Permutation  : '%s' -> '%s'\n", substitutedData, permutedData);

                String roundMixedData = mixWithKey(permutedData, roundKeys.get(r + 1));
                System.out.printf("    - (c) Key Mixing   : '%s'\n      - Mix Key      : '%s'\n      - Round Output : '%s'\n",
                        permutedData, roundKeys.get(r + 1), roundMixedData);
                currentData = roundMixedData;
            }
            ciphertext.append(currentData);
            System.out.printf("--- Finished Block %d, Current Ciphertext: '%s' ---\n", blockNum, ciphertext);
        }
        System.out.println("\n======================== ENCRYPTION COMPLETE =========================");
        return ciphertext.toString();
    }

    public String decrypt(String ciphertext, String masterKey) {
        System.out.println("\n========================= DECRYPTION PROCESS =========================");

        // Key Generation
        KeyScheduler keyScheduler = new KeyScheduler(masterKey, Configuration.NUM_ROUNDS);
        List<String> roundKeys = keyScheduler.getRoundKeys();
        System.out.println("\n[1] Generated Round Keys (will be used in reverse order):");
        for (int i = 0; i < roundKeys.size(); i++) {
            System.out.printf("    Round Key %d: '%s'\n", i, roundKeys.get(i));
        }

        // Process in blocks
        StringBuilder decryptedPaddedText = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += Configuration.BLOCK_SIZE) {
            String block = ciphertext.substring(i, i + Configuration.BLOCK_SIZE);
            int blockNum = (i / Configuration.BLOCK_SIZE) + 1;
            System.out.printf("\n--- Processing Block %d: '%s' ---\n", blockNum, block);
            String currentData = block;

            // Rounds in reverse
            for (int r = Configuration.NUM_ROUNDS; r > 0; r--) {
                System.out.printf("\n  [Round %d] (Reversed)\n", r);
                String dataBeforeUnmix = currentData;
                String unmixedData = unmixWithKey(dataBeforeUnmix, roundKeys.get(r));
                System.out.printf("    - (a) Key Un-Mixing: '%s'\n      - Mix Key      : '%s'\n      - Output       : '%s'\n",
                        dataBeforeUnmix, roundKeys.get(r), unmixedData);

                String unpermutedData = pBox.inversePermute(unmixedData);
                System.out.printf("    - (b) Inv Permute  : '%s' -> '%s'\n", unmixedData, unpermutedData);

                String unsubstitutedData = sBox.inverseSubstitute(unpermutedData);
                System.out.printf("    - (c) Inv Substitute : '%s' -> '%s'\n", unpermutedData, unsubstitutedData);
                currentData = unsubstitutedData;
            }

            // Final Key Un-mixing
            System.out.println("\n  [Final Step] Key Un-Mixing");
            String finalUnmixedData = unmixWithKey(currentData, roundKeys.get(0));
            System.out.printf("    - Input  : '%s'\n    - Mix Key: '%s'\n    - Output : '%s'\n",
                    currentData, roundKeys.get(0), finalUnmixedData);
            currentData = finalUnmixedData;

            decryptedPaddedText.append(currentData);
            System.out.printf("--- Finished Block %d, Current Decrypted Text: '%s' ---\n", blockNum, decryptedPaddedText);
        }

        // Unpadding
        String paddedResult = decryptedPaddedText.toString();
        int lastRealChar = paddedResult.length() - 1;
        while (lastRealChar >= 0 && paddedResult.charAt(lastRealChar) == Configuration.PAD_CHAR) {
            lastRealChar--;
        }
        String unpaddedResult = paddedResult.substring(0, lastRealChar + 1);

        System.out.printf("\n[3] Decrypted (Padded) : '%s'\n", paddedResult);
        System.out.printf("[4] Decrypted (Unpadded) : '%s'\n", unpaddedResult);

        System.out.println("\n======================== DECRYPTION COMPLETE =========================");
        return unpaddedResult;
    }
}