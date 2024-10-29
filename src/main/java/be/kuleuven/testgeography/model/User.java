package be.kuleuven.testgeography.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String myEmail;
    private String myPassword;
    private int averageScore;// this is a percentage
    private int totalNrQuestions;
    private int totalScore;
    private final String API_URL = "https://studev.groept.be/api/a21pt212/";

// TODO: average score doesn't work yet

    public User(String name, String email, String password){
        username = name;
        myEmail = email;
        myPassword = password;
        averageScore = 0;
        totalNrQuestions = 0;
        totalScore = 0;
    }

    public User(String name, String email, String password, int average, int nrQuizzes){
        username = name;
        myEmail = email;
        myPassword = password;
        averageScore = average;
        totalNrQuestions = nrQuizzes;
    }

    protected User(Parcel in) {
        username = in.readString();
        myEmail = in.readString();
        myPassword = in.readString();
        averageScore = in.readInt();
        totalNrQuestions = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername(){
        return username;
    }

    public int getAverageScore(){
        return averageScore;
        /*if (totalNrQuestions == 0){
            averageScore = 0;
        } else{
            averageScore = (totalScore*100) / (totalNrQuestions);
        }
        return averageScore;*/
    }

    public int getTotalNrQuestions(){
        return totalNrQuestions;
    }

    public void setUsername(String newUsername){
        this.username = newUsername;
    }

    public void setAverageScore(int average){
        averageScore = average;
    }

    public void setTotalScore(int nrCorrectAnswers){
        totalScore = nrCorrectAnswers;
    }

    public void setTotalNrQuestions(int nrQuizzes){
        totalNrQuestions = nrQuizzes;
    }

    public boolean setMyPassword(String oldPassword, String newPassword){
        if (oldPassword == myPassword){
            myPassword = newPassword;
            return true;
        } else{
            return false;
        }
    }

    public void updateAtEndOfQuiz(int score, int nrOfQuestions){
        totalScore += score;
        totalNrQuestions +=  nrOfQuestions;
    }

    public void updateAverageScore(){
        averageScore = totalScore / totalNrQuestions;
    }

    public String getMyPasswordURL(){
        String url = API_URL + "getUserPassword/" + myEmail;
        return url;
    }

    public String getAddUserURL(){
        String url = API_URL + "addUser/" + username + "/" + myPassword + "/" + myEmail;
        return url;
    }

    public String getSetUserScoresURL(){
        String url = API_URL + "setUserScores/" + totalScore + "/" + totalNrQuestions + "/" + myEmail;
        return url;
    }

    public String getCheckEmailExistsURL(){
        String url = API_URL + "checkExistingEmail/" + myEmail;
        return url;
    }

    /** checkUsernameInDB checks through API if the username of User object exists in the database
     * @return true if username exists in the database, false if username does not exist in the database
     */
    public boolean checkUsernameInDB(){
        return true;
    }

    /** getEmailFromDB checks through API if the user exists in the database and then queries for the email
     * @return email address from the DB if the user exists in the database
     */
    public String getEmailFromDB(){
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(myEmail);
        parcel.writeString(myPassword);
        parcel.writeInt(averageScore);
        parcel.writeInt(totalNrQuestions);
    }
}
