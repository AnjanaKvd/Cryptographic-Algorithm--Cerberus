import java.util.ArrayList;
import java.util.List;

public class KeyScheduler {
    private final List<String> roundKeys;

    public KeyScheduler(String masterKey, int numRounds) {
        if (masterKey.length() != Configuration.BLOCK_SIZE) {
            throw new IllegalArgumentException("Master key must be " + Configuration.BLOCK_SIZE + " characters long.");
        }
        this.roundKeys = new ArrayList<>();
        generateRoundKeys(masterKey, numRounds);
    }

    private void generateRoundKeys(String masterKey, int numRounds) {
        this.roundKeys.add(masterKey);
        String currentKey = masterKey;
        for (int i = 0; i < numRounds; i++) {
            // Cyclic left shift
            currentKey = currentKey.substring(1) + currentKey.charAt(0);
            this.roundKeys.add(currentKey);
        }
    }

    public List<String> getRoundKeys() {
        return this.roundKeys;
    }
}