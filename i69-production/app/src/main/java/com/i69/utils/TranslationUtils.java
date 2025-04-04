package com.i69.utils;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationUtils extends AsyncTask<Void, Void, String> {

    ResultCallBack resultCallBack;
    String inputWord;
    String srcLangCode;
    String trLangCode;

    public TranslationUtils(ResultCallBack resultCallBack, String inputWord, String srcLangCode, String trLangCode) {
        this.resultCallBack = resultCallBack;
        this.inputWord = inputWord;
        this.srcLangCode = srcLangCode;
        this.trLangCode = trLangCode;
    }

    public void StopBackground(){
        this.cancel(true);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return callUrlAndParseResult(srcLangCode, trLangCode, inputWord);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (resultCallBack != null)
            if (s!=null)
            resultCallBack.onReceiveResult(s);
            else
                resultCallBack.onFailedResult();
    }

    public interface ResultCallBack {
        void onReceiveResult(String result);
        void onFailedResult();
    }

    private String callUrlAndParseResult(String langFrom, String langTo,
                                         String word) throws Exception {

        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            if (!isCancelled())
            response.append(inputLine);
            else return null;
        }
        in.close();

        return  parseResult(response.toString(),word);
    }

    private String parseResult(String inputJson, String word) throws Exception {
        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        String outputWord= "";
        for (int i = 0; i < jsonArray2.length(); i++) {
            if (!isCancelled()) {
                JSONArray parseResult = (JSONArray) jsonArray2.get(i);
                String result = parseResult.get(0).toString();
                if (result != null && !result.contains("null")) {
                    outputWord += result;
                }
            }
            else {
                return null;
            }
        }

        return outputWord;
//        setConversation(outputWord);
    }
}
