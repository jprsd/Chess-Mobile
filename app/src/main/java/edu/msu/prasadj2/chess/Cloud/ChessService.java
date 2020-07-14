/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud;

import edu.msu.prasadj2.chess.Cloud.Models.GameLoadResult;
import edu.msu.prasadj2.chess.Cloud.Models.GameSaveResult;
import edu.msu.prasadj2.chess.Cloud.Models.LoginResult;
import edu.msu.prasadj2.chess.Cloud.Models.LogoutResult;
import edu.msu.prasadj2.chess.Cloud.Models.MatchmakingResult;
import edu.msu.prasadj2.chess.Cloud.Models.RegisterResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static edu.msu.prasadj2.chess.Cloud.Cloud.LOAD_PATH;
import static edu.msu.prasadj2.chess.Cloud.Cloud.LOGOUT_PATH;
import static edu.msu.prasadj2.chess.Cloud.Cloud.MATCHMAKING_PATH;
import static edu.msu.prasadj2.chess.Cloud.Cloud.REGISTER_PATH;
import static edu.msu.prasadj2.chess.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.prasadj2.chess.Cloud.Cloud.SAVE_PATH;

public interface ChessService {

    @FormUrlEncoded
    @POST(REGISTER_PATH)
    Call<RegisterResult> registerUser(@Field("xml") String xmlData);

    @FormUrlEncoded
    @POST(LOGIN_PATH)
    Call<LoginResult> loginUser(@Field("xml") String xmlData);

    @FormUrlEncoded
    @POST(LOGOUT_PATH)
    Call<LogoutResult> logoutUser(@Header("Cookie") String cookie,
                                  @Field("xml") String xmlData);

    @FormUrlEncoded
    @POST(MATCHMAKING_PATH)
    Call<MatchmakingResult> findOpponent(@Header("Cookie") String cookie,
                                         @Field("xml") String xmlData);

    @FormUrlEncoded
    @POST(LOAD_PATH)
    Call<GameLoadResult> loadGame(@Header("Cookie") String cookie,
                                  @Field("xml") String xmlData);

    @FormUrlEncoded
    @POST(SAVE_PATH)
    Call<GameSaveResult> saveGame(@Header("Cookie") String cookie,
                                  @Field("xml") String xmlData);

}
