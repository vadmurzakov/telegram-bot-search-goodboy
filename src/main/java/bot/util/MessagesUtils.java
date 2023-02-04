package bot.util;

public final class MessagesUtils {

    /**
     * Склонение существительного по числителю.
     *
     * @param count числитель
     * @param arr   массив существительных в формате: ["книга", "книги", "книг"]
     * @return просклоненное существительное.
     */
    public static String declensionOfNumbers(int count, String... arr) {
        String result;
        int mod100 = count % 100;
        if (mod100 > 4 && mod100 < 21) {
            result = arr[2];
        } else {
            int mod10 = mod100 % 10;
            if (mod10 == 1) {
                result = arr[0];
            } else if (mod10 > 1 && mod10 < 5) {
                result = arr[1];
            } else {
                result = arr[2];
            }
        }
        return result;
    }

}
