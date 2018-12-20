package us.fecytalk.trycambaru;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pentil on 11/09/18.
 */

public class ResponseApi {
    @SerializedName("msg")
    String mes;

    public String getValidation(){ return mes;}
}
