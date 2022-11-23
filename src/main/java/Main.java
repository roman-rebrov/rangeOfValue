import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {


    private static List<FutureTask<Integer>> threadList = new ArrayList<>();


    private static Callable<Integer> getThread(String text) {
        return () -> {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);

            return maxSize;
        };
    }


    private static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
            final FutureTask<Integer> integerFutureTask = new FutureTask<>(getThread(texts[i]));
            final Thread thread = new Thread(integerFutureTask);
            thread.start();
            threadList.add(integerFutureTask);
        }

        long startTs = System.currentTimeMillis(); // start time
        int maxSize = 0;

        for (FutureTask<Integer> task : threadList) {
            int res = task.get();

            if (maxSize < res) {
                maxSize = res;
            }

        }
        System.out.println("Max: " + maxSize);
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }
}