package cordova.plugin.freestylelibre;

import android.support.annotation.NonNull;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlgorithmUtil {

    public static final boolean DEBUG = true; // global debug output flag (don't have anywhere better to put it)

    private static final double TREND_UP_DOWN_LIMIT = 1;
    private static final double TREND_SLIGHT_UP_DOWN_LIMIT = 0.5;
    private static final int CONFIDENCE_LIMIT = 1;

    public enum TrendArrow {
        UNKNOWN,
        DOWN,
        SLIGHTLY_DOWN,
        FLAT,
        SLIGHTLY_UP,
        UP
    }

    private static final int MINUTE = 60000;

    private static final int PREDICTION_TIME = 15;

    private static final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");

    public static String format(Date date) {
        return mFormat.format(date);
    }

    private static int getGlucose(byte[] bytes) {
        //return ((256 * (bytes[0] & 0xFF) + (bytes[1] & 0xFF)) & 0x0FFF) / 10; // should be discussed/tested
        return ((256 * (bytes[0] & 0xFF) + (bytes[1] & 0xFF)) & 0x3FFF) / 10;
    }

    private static int getGlucoseRaw(byte[] bytes) {
        //return ((256 * (bytes[0] & 0xFF) + (bytes[1] & 0xFF)) & 0x0FFF); // should be discussed/tested
        return ((256 * (bytes[0] & 0xFF) + (bytes[1] & 0xFF)) & 0x3FFF);
    }

    

    public static JSONArray parseData(int attempt, String tagId, byte[] data) {
        long watchTime = System.currentTimeMillis();

        int indexTrend = data[26] & 0xFF;

        int indexHistory = data[27] & 0xFF;

        final int sensorTime = 256 * (data[317] & 0xFF) + (data[316] & 0xFF);

        long sensorStartTime = watchTime - sensorTime * MINUTE;

        //ArrayList<GlucoseData> historyList = new ArrayList<>();
        JSONArray historyList = new JSONArray();

        // loads history values (ring buffer, starting at index_trent. byte 124-315)
        for (int index = 0; index < 32; index++) {
            int i = indexHistory - index - 1;
            if (i < 0) i += 32;
            //GlucoseData glucoseData = new GlucoseData();
            JSONObject glucoseData = new JSONObject();
            glucoseData.put("glucoseLevel",
                    getGlucose(new byte[]{data[(i * 6 + 125)], data[(i * 6 + 124)]}));

            glucoseData.put("glucoseLevelRaw",
                    getGlucoseRaw(new byte[]{data[(i * 6 + 125)], data[(i * 6 + 124)]}));

            int time = Math.max(0, Math.abs((sensorTime - 3) / 15) * 15 - index * 15);

            glucoseData.put("realDate", sensorStartTime + time * MINUTE);
            glucoseData.put("sensorId", tagId);
            glucoseData.put("sensorTime", time);
            historyList.put(glucoseData);
        }


        //ArrayList<GlucoseData> trendList = new ArrayList<>();
        JSONArray trendList = new JSONArray();
        // loads trend values (ring buffer, starting at index_trent. byte 28-123)
        for (int index = 0; index < 16; index++) {
            int i = indexTrend - index - 1;
            if (i < 0) i += 16;
            //GlucoseData glucoseData = new GlucoseData();
            JSONObject glucoseData = new JSONObject();
            glucoseData.put("glucoseLevel",
                    getGlucose(new byte[]{data[(i * 6 + 29)], data[(i * 6 + 28)]}));

            glucoseData.put("glucoseLevelRaw",
                    getGlucoseRaw(new byte[]{data[(i * 6 + 29)], data[(i * 6 + 28)]}));
            int time = Math.max(0, sensorTime - index);

            glucoseData.put("realDate", sensorStartTime + time * MINUTE);
            glucoseData.put("sensorId", tagId);
            glucoseData.put("sensorTime", time);
            trendList.put(glucoseData);
        }
        JSONObject result = new JSONObject();
        result.put("Glucose", historyList);
        result.put("Trend", trendList);
        return result;
        //PredictionData predictedGlucose = getPredictionData(attempt, tagId, trendList);
        //return new ReadingData(predictedGlucose, trendList, historyList);

    }

    // @NonNull
    // private static String getPredictionData(int attempt, String tagId, ArrayList<GlucoseData> trendList) {
    //     PredictionData predictedGlucose = new PredictionData();
    //     SimpleRegression regression = new SimpleRegression();
    //     for (int i = 0; i < trendList.size(); i++) {
    //         regression.addData(trendList.size() - i, (trendList.get(i)).glucoseLevel);
    //     }
    //     predictedGlucose.glucoseLevel = (int)regression.predict(15 + PREDICTION_TIME);
    //     predictedGlucose.trend = regression.getSlope();
    //     predictedGlucose.confidence = regression.getSlopeConfidenceInterval();
    //     predictedGlucose.errorCode = PredictionData.Result.OK;
    //     predictedGlucose.realDate = trendList.get(0).realDate;
    //     predictedGlucose.sensorId = tagId;
    //     predictedGlucose.attempt = attempt;
    //     predictedGlucose.sensorTime = trendList.get(0).sensorTime;
    //     return predictedGlucose;
    // }
}