package group01.mytunes.utility;

public class MyTunesUtility {

    public static String timeFormatConverter(double inputSeconds){
        int min = 0;
        int sec = 0;
        int total = (int) inputSeconds;

        String minut = "";
        String seconds = "";

        min = total/60;
        sec = total%60;

        if (min < 10) {
            minut = "0"+min;
        } else  {
            minut = min + "";
        }

        if (sec < 10) {
            seconds = "0"+sec;
        } else {
            seconds = sec + "";
        }

        return minut + ":" + seconds;
    }
}
