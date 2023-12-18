import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SecretPhraseGenerator {

    private static final List<String> WORD_LIST = Arrays.asList(
            "apple", "banana", "cherry", "dog", "elephant", "fire", "giraffe", "happiness", "icecream", "jazz",
            "kangaroo", "lemon", "mango", "notebook", "ocean", "penguin", "queen", "rainbow", "sunset", "tiger",
            "umbrella", "volcano", "watermelon", "xylophone", "yoga", "zeppelin",
            "almond", "butterfly", "candle", "dragon", "enigma", "flamingo", "guitar", "horizon", "island", "jungle",
            "kiwi", "lighthouse", "moonlight", "nebula", "orchid", "paradise", "quasar", "rose", "serenade", "tornado",
            "unity", "vortex", "whisper", "xerox", "yarn", "zephyr",
            "alchemy", "breeze", "cascade", "diamond", "echo", "falcon", "gondola", "harmony", "infinity", "jubilee",
            "kaleidoscope", "lullaby", "marathon", "nirvana", "opulent", "pandemonium", "quintessence", "rhapsody", "silhouette", "tranquil",
            "utopia", "vivid", "wanderlust", "xanadu", "yearning", "zealot",
            "abyss", "bazaar", "clarity", "daffodil", "effervescent", "fiasco", "galaxy", "hologram", "impulse", "jigsaw",
            "krypton", "limelight", "mystique", "nectar", "obsidian", "paradox", "quokka", "reverie", "sapphire", "triumph",
            "uplifting", "velvet", "whimsical", "xenon", "yonder", "zest", "avalanche", "biscuit", "clandestine", "dynamo",
            "effigy", "fractal", "gossamer", "havoc", "incognito", "juxtapose", "kaleidoscopic", "labyrinth", "mellifluous", "nautical",
            "oblivion", "panorama", "quicksilver", "ravishing", "serendipity", "talisman", "universe", "vanguard", "whirlwind", "xylitol",
            "yearn", "zenith"
    );

    public static String generateSecretPhase(){
        Collections.shuffle(WORD_LIST, new SecureRandom());

        List<String> secretPhraseWords = WORD_LIST.subList(0, 12);

        String secretPhrase = String.join(" ", secretPhraseWords);

        return secretPhrase;
    }

}
