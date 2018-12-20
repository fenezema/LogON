package us.fecytalk.trycambaru;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by pentil on 11/09/18.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/sendImg/")
    Call<ResponseApi> kirim (@Field("idUser") String nrp,
                             @Field("password") String password,
                             @Field("image") String image);
    @FormUrlEncoded
    @POST("/doTrain/")
    Call<ResponseApi> kirimTrain (@Field("idUser") String nrp,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("/doPredict/")
    Call<ResponseApi> kirimAbsen
            (   @Field("idUser") String idUser,
                @Field("password") String password,
                @Field("image")String image);

    @FormUrlEncoded
    @POST("/signin/")
    Call<ResponseApi> signin
            (   @Field("idUser") String idUser,
                @Field("password") String password,
                @Field("image")String image,
                @Field("Lat") String lat,
                @Field("Lon")String lon,
                @Field("idAgenda")String hehe);

    @FormUrlEncoded
    @POST("/sendSignature/")
    Call<ResponseApi> sendTTD (@Field("idUser") String nrp,
                                @Field("password") String password,
                               @Field("image") String image);

    @FormUrlEncoded
    @POST("/doTrain_TTD/")
    Call<ResponseApi> trainTTD (@Field("idUser") String nrp,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("/doPredict_TTD/")
    Call<ResponseApi> predictTTD
            (   @Field("idUser") String idUser,
                @Field("password") String password,
                @Field("image")String image);

    @FormUrlEncoded
    @POST("/signin_TTD/")
    Call<ResponseApi> signinTTD
            (   @Field("idUser") String idUser,
                @Field("password") String password,
                @Field("image")String image,
                @Field("idAgenda")String hehe,
                @Field("Lat") String lat,
                @Field("Lon")String lon);
}
