import java.util.ArrayList;
import java.util.List;

public class CerberusCipher {
    private final SBox sBox;
    private final PBox pBox;

    public CerberusCipher() {
        this.sBox = new SBox(Configuration.CHARACTER_SET);
        this.pBox = new PBox();
    }

    // Helper method for key mixing (modular addition)
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

    // Helper method for key un-mixing (modular subtraction)
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

        KeyScheduler keyScheduler = new KeyScheduler(masterKey, Configuration.NUM_ROUNDS);
        List<String> roundKeys = keyScheduler.getRoundKeys();

        // Padding
        StringBuilder paddedText = new StringBuilder(plaintext);
        while (paddedText.length() % Configuration.BLOCK_SIZE != 0) {
            paddedText.append(Configuration.PAD_CHAR);
        }

        // Process in blocks
        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < paddedText.length(); i += Configuration.BLOCK_SIZE) {
            String currentBlock = paddedText.substring(i, i + Configuration.BLOCK_SIZE);

            // Initial Key Mixing
            currentBlock = mixWithKey(currentBlock, roundKeys.get(0));

            // Rounds
            for (int r = 0; r < Configuration.NUM_ROUNDS; r++) {
                currentBlock = sBox.substitute(currentBlock);
                currentBlock = pBox.permute(currentBlock);
                currentBlock = mixWithKey(currentBlock, roundKeys.get(r + 1));
            }
            ciphertext.append(currentBlock);
        }
        return ciphertext.toString();
    }

    public String decrypt(String ciphertext, String masterKey) {
        KeyScheduler keyScheduler = new KeyScheduler(masterKey, Configuration.NUM_ROUNDS);
        List<String> roundKeys = keyScheduler.getRoundKeys();

        // Process in blocks
        StringBuilder decryptedPaddedText = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += Configuration.BLOCK_SIZE) {
            String currentBlock = ciphertext.substring(i, i + Configuration.BLOCK_SIZE);

            // Rounds in reverse
            for (int r = Configuration.NUM_ROUNDS; r > 0; r--) {
                currentBlock = unmixWithKey(currentBlock, roundKeys.get(r));
                currentBlock = pBox.inversePermute(currentBlock);
                currentBlock = sBox.inverseSubstitute(currentBlock);
            }

            // Final Key Un-mixing
            currentBlock = unmixWithKey(currentBlock, roundKeys.get(0));
            decryptedPaddedText.append(currentBlock);
        }

        // Unpadding
        String paddedResult = decryptedPaddedText.toString();
        int lastRealChar = paddedResult.length() -1;
        while(lastRealChar >= 0 && paddedResult.charAt(lastRealChar) == Configuration.PAD_CHAR) {
            lastRealChar--;
        }

        return paddedResult.substring(0, lastRealChar + 1);
    }
}