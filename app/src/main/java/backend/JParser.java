package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author Benjamin Young
 * 
 * Email: b.p.young1234@gmail.com
 */
public class JParser { 
    
    public String[] parseMessageData(String JString) throws Exception {
        JSONObject JObject = new JSONObject(JString);

        JSONArray JArray = JObject.getJSONArray("data");
        String returnArray[] = new String[JArray.length()];

        for (int i = 0; i < JArray.length(); i++) {
            JSONObject explrObject = JArray.getJSONObject(i);

            returnArray[i] = (HexToString(explrObject.getString("data")) + "," + EpochToDate(explrObject.getLong("time")));
        }

        return returnArray;
    }
    
    public String[] getValues(String JString, String target) throws Exception {
        JSONObject JObject = new JSONObject(JString);
        JSONArray JArray = JObject.getJSONArray("data");

        String returnArray[] = new String[JArray.length()];

        for (int i = 0; i < JArray.length(); i++){
            JSONObject explrObject = JArray.getJSONObject(i);

            if (!Arrays.asList(returnArray).contains(explrObject.getString(target))) {
                returnArray[i] = explrObject.getString(target);
            }
        }

        return returnArray;
    }
    
    private String HexToString(String Hex) {
        StringBuilder NewStr = new StringBuilder();

        for(int i = 0; i < Hex.length(); i += 2){
            String s = Hex.substring(i, i + 2);

            NewStr.append((char) Integer.parseInt(s, 16));
        }

        return NewStr.toString();
    }
    
    private String EpochToDate(Long epoch){
        Date date = new Date(epoch * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return(sdf.format(date));
    }
}
