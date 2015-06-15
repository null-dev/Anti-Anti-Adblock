/**
 * Project: OpenSub
 * Created: 15/06/15
 * Author: nulldev
 */

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import java.util.*;

/**
 * NULL that ****ing adblocker buddy. **** it!
 */
public class NullBlockRenderer extends AbstractDocumentModifier {

    private static final ArrayList<String> FALSE_POSITIVES = new ArrayList<>(Arrays.asList(new String[]{
            "_ad.aspx","?adtype=","_adbrite","_ads.php?",
            "_banner_ad","_adspace",
            "google.com/promo_","interclick.",
            "js.worthathousandwords","hera.hardocp",
            "pro-market.","richmedia.yimg",".adbureau.",
            ".adbrite.com/ab",".atdmt.com/",
            ".projectwonderful.","http://ad.localhost",
            "http://ads.localhost","/ads/","/ajs.php?",
            ".doubleclick."}));

    private static final ArrayList<Character> ALPHABET = new ArrayList<>(Arrays.asList(boxCharArray((
            "abcdefghijklmnopqrstuvwxyz" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray())));

    public static final ArrayList<String> ATTS = new ArrayList<>(Arrays.asList(new String[]{
            "visibility: 'hidden'",
            "width: '1px'",
            "height: '1px'",
            "border: 'none'",
            "position: 'absolute'"
    }));

    public static final String RANDOM_STRING_BASE = "\r\n\t\r\n \t ";

    private static final String JS_FUNCTION = "";

    public static final int VAR_LENGTH = 5;

    @Override
    public Document render(Document document) {
        //Shuffle the false positives list
        Collections.shuffle(FALSE_POSITIVES);
        //Shuffle attribute list
        Collections.shuffle(ATTS);
        Random random = new Random();

        String falsePositive = FALSE_POSITIVES.get(0);
        //Generate all our variable names
        String[] varNames = new String[10];
        for(int i = 0; i < varNames.length; i++) {
            varNames[i] = getRandomVariableName(VAR_LENGTH);
        }

        String atts = "var " + varNames[4] + "= { " + StringUtils.join(ATTS, ", ") + " }; ";
        String[] cmds = new String[]{
                varNames[3] + "(" + varNames[1] + " .style," + varNames[4] + ");",
                varNames[1] + " .setAttribute ('src', '" + falsePositive + "'); ",
                "document .body .appendChild (" + varNames[1] + "); ",
                "function " + varNames[3] + "(" + varNames[4] + ", " + varNames[5] + ") { for ( var '" + varNames[6]
                        + " in " + varNames[5] + " ) " + varNames[4] + "[" + varNames[6] + "]="
                        + varNames[5] + "[" + varNames[6] + "];return " + varNames[4] + "; }; "
        };
        String toAppend = random.nextInt(2)==0?"img":"iframe";
        String[] pcmds = new String[]{
                varNames[1] + " = document .createElement ('" + toAppend + "');",
                atts
        };
        String[] calls = new String[]{
                varNames[1] + ".parentNode .removeChild(" + varNames[1] + ");",
                "if ( " + varNames[1] + ".style .display=='none' ) " + varNames[2] + " (); "
        };
        shuffleStringArray(cmds);
        shuffleStringArray(calls);
        String[] outputStringArray = ("<script type=\"text/javascript\">  " + "function " + varNames[0]
                + " (" + varNames[2] + ") {" + StringUtils.join(pcmds, " ") + StringUtils.join(cmds, " ")
                + "setTimeout ( function() { " + StringUtils.join("", calls) + " }, " + randInt(250, 750)
                + " ); }window .onload = function () {" + varNames[0] + "( function () { %%JSFN%% } ); };</script>").split(" ");
        StringBuilder ret = new StringBuilder();
        for(String bit : outputStringArray) {
            ret.append(bit);
            List<Character> tempCharArrayList = Arrays.asList(boxCharArray(RANDOM_STRING_BASE.toCharArray()));
            Collections.shuffle(tempCharArrayList);
            String shuffledRandomString = getStringRepresentation(tempCharArrayList);
            //Let's only do 7 for safety:P
            ret.append(StringUtils.substring(shuffledRandomString, 0, randInt(1, 7)));
        }
        document.head().append(ret.toString().replace("%%JSFN%%", JS_FUNCTION));
        return document;
    }

    //Get a variable name (fully random)
    public static String getRandomVariableName(int length) {
        Collections.shuffle(ALPHABET);
        Random random = new Random();
        StringBuilder varName = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char character = ALPHABET.get(random.nextInt(ALPHABET.size()));
            varName.append(character);
        }
        return varName.toString();
    }

    //Box a char array
    public static Character[] boxCharArray(char[] charArray) {
        Character[] boxedArray = new Character[charArray.length];
        int i = 0;
        for(char character : charArray) {
            boxedArray[i] = character;
            i++;
        }
        return boxedArray;
    }

    // Implementing Fisher–Yates shuffle (copied from http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array)
    public static void shuffleStringArray(String[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    /**
     * Copied from: http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
     *
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    //Copied from: http://stackoverflow.com/questions/6324826/converting-arraylist-of-characters-to-a-string
    public static String getStringRepresentation(List<Character> list)
    {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list)
        {
            builder.append(ch);
        }
        return builder.toString();
    }
}
